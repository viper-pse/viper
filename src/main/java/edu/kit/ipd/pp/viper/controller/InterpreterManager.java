package edu.kit.ipd.pp.viper.controller;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;

import edu.kit.ipd.pp.viper.model.ast.Functor;
import edu.kit.ipd.pp.viper.model.ast.FunctorGoal;
import edu.kit.ipd.pp.viper.model.ast.Goal;
import edu.kit.ipd.pp.viper.model.ast.KnowledgeBase;
import edu.kit.ipd.pp.viper.model.ast.Rule;
import edu.kit.ipd.pp.viper.model.ast.Term;
import edu.kit.ipd.pp.viper.model.ast.Variable;
import edu.kit.ipd.pp.viper.model.interpreter.Environment;
import edu.kit.ipd.pp.viper.model.interpreter.Interpreter;
import edu.kit.ipd.pp.viper.model.interpreter.StepResult;
import edu.kit.ipd.pp.viper.model.interpreter.Substitution;
import edu.kit.ipd.pp.viper.model.parser.ParseException;
import edu.kit.ipd.pp.viper.model.parser.PrologParser;
import edu.kit.ipd.pp.viper.model.visualisation.GraphvizMaker;
import edu.kit.ipd.pp.viper.view.ClickableState;
import edu.kit.ipd.pp.viper.view.ConsolePanel;
import edu.kit.ipd.pp.viper.view.LogType;
import edu.kit.ipd.pp.viper.view.MainWindow;
import edu.kit.ipd.pp.viper.view.VisualisationPanel;
import guru.nidi.graphviz.model.Graph;

/**
 * Manager class for interpreters. This class holds references to all
 * visualisation graphs created and serves as an interface for commands to control the
 * interpretation and parse new programs.
 */
public class InterpreterManager {
    private static final String STANDARD_LIBRARY_PATH = "/stdlib.pl";

    private Optional<KnowledgeBase> knowledgeBase;
    private Optional<Goal> query;
    private Optional<Interpreter> interpreter;
    private List<Graph> visualisations;
    private List<StepResult> results;
    private int current;
    private Optional<Set<Variable>> variables;
    private boolean useStandardLibrary = true;
    private final Optional<KnowledgeBase> standardLibrary;
    private boolean running = false;
    private final Consumer<ClickableState> toggleStateFunc;
    private Optional<Thread> continueThread = Optional.empty();

    /**
     * Initializes an interpreter manager. This method calls reset() internally.
     * 
     * @param toggleStateFunc Consumer function that switches the state of clickable
     *        elements in the GUI
     */
    public InterpreterManager(Consumer<ClickableState> toggleStateFunc) {
        this.toggleStateFunc = toggleStateFunc;
        this.standardLibrary = this.loadStandardLibrary();
        this.reset();
    }

    /**
     * Resets the instance to make it ready for a new interpreter.
     */
    public void reset() {
        if (this.running) {
            this.cancel();
        }
        this.knowledgeBase = Optional.empty();
        this.query = Optional.empty();
        this.interpreter = Optional.empty();
        this.variables = Optional.empty();
        this.visualisations = new ArrayList<Graph>();
        this.results = new ArrayList<StepResult>();
        this.current = 0;
    }

    /**
     * Loads the standard library.
     * The standard library gets loaded from a file and is directly parsed into a KnowledgeBase.
     * This method is meant to be called exactly once - at the start of the program.
     *
     * @return an optional KnowledgeBase object representing the standard library
     */
    private Optional<KnowledgeBase> loadStandardLibrary() {
        try {
            InputStream input = this.getClass().getResourceAsStream(InterpreterManager.STANDARD_LIBRARY_PATH);
            String stdlibSource = new String(IOUtils.toByteArray(input));

            return Optional.of(new PrologParser(stdlibSource).parse());
        } catch (IOException e) {
            return Optional.empty();
        } catch (ParseException e) {
            return Optional.empty();
        }
    }

    /**
     * Parses a knowledgebase and remembers it.
     * Also combines the parsed knowledgebase with the stdlib, if desired.
     * This method also checks for conflicting rules in the given program and the standard library.
     *
     * @param kbSource source code to parse
     * @throws ParseException if the source code is malformed
     * @return set of conflicting rules in the given program and the standard library
     */
    public Set<Rule> parseKnowledgeBase(String kbSource) throws ParseException {
        KnowledgeBase kb = new PrologParser(kbSource).parse();
        Set<Rule> conflictingRules = Collections.emptySet();

        if (this.useStandardLibrary && this.standardLibrary.isPresent()) {
            conflictingRules = kb.getConflictingRules(this.standardLibrary.get());
            kb = kb.combine(this.standardLibrary.get());
        }

        this.knowledgeBase = Optional.of(kb);
        return conflictingRules;
    }

    /**
     * Parses a query and initializes an interpreter, if a parsed knowledgebase is
     * available.
     *
     * @param querySource source code of the query to parse
     * @throws ParseException if the source code is malformed
     */
    public void parseQuery(String querySource) throws ParseException {
        if (!this.knowledgeBase.isPresent()) {
            throw new ParseException(LanguageManager.getInstance().getString(LanguageKey.NO_KNOWLEDGEBASE_PRESENT));
        }

        KnowledgeBase knowledgeBase = this.knowledgeBase.get();

        List<Goal> goals = new PrologParser(querySource).parseQuery();

        // goals.size may never be 0 at this point
        // if that were to happen the parser would fail
        if (goals.size() == 1) {
            this.query = Optional.of(goals.get(0));
        } else {
            // TODO: use a Set for this
            List<Term> solveFor = new ArrayList<>();

            for (Goal goal : goals) {
                for (Variable var : goal.getVariables()) {
                    if (!solveFor.contains(var)) {
                        solveFor.add(var);
                    }
                }
            }

            Functor head = new Functor("main", solveFor);
            this.query = Optional.of(new FunctorGoal(head));
            knowledgeBase = knowledgeBase.withRule(new Rule(head, goals));
        }

        // Reset visualisations from previous query
        this.visualisations = new ArrayList<Graph>();
        this.results = new ArrayList<StepResult>();
        this.current = 0;

        this.interpreter = Optional.of(new Interpreter(knowledgeBase, this.query.get()));
        this.variables = Optional.of(this.query.get().getVariables());
        this.visualisations.add(GraphvizMaker.createGraph(this.interpreter.get()));
        this.results.add(StepResult.STEPS_REMAINING);
    }

    /**
     * Takes an interpreter step. If this method is called after a call to
     * {@link #reset()} (that includes the constructor) and before calls to
     * {@link #parseKnowledgeBase(String)} and {@link #parseQuery(String)}, i.e.
     * before an interpreter instance has been created, it will have no effect and
     * return.
     * Also takes care of the console output.
     *
     * @param console The console panel of the main window
     */
    public void nextStep(ConsolePanel console) {
        if (!this.interpreter.isPresent()) {
            return;
        }
        
        if (!this.running) {
            this.toggleStateFunc.accept(ClickableState.PARSED_QUERY);
        }

        if (this.current < this.visualisations.size() - 1) {
            if (this.results.get(this.current + 1) == StepResult.NO_MORE_SOLUTIONS) {
                this.current++;
                this.toggleStateFunc.accept(ClickableState.LAST_STEP);
                return;
            }

            this.current++;
            return;
        }

        this.results.add(this.interpreter.get().step());
        this.visualisations.add(GraphvizMaker.createGraph(this.interpreter.get()));
        this.current++;

        if (this.results.get(this.current) == StepResult.SOLUTION_FOUND) {
            String prefix = LanguageManager.getInstance().getString(LanguageKey.SOLUTION_FOUND);
            List<Substitution> solution = this.getSolution();

            String solutionString = solution.isEmpty()
                    ? "  " + LanguageManager.getInstance().getString(LanguageKey.SOLUTION_YES)
                    : solution.stream().map(sol -> "  " + sol.toString()).collect(joining(",\n"));

            console.printLine(String.format("%s:\n%s.", prefix, solutionString), LogType.SUCCESS);
        }

        if (this.results.get(this.current) == StepResult.NO_MORE_SOLUTIONS) {
            console.printLine(LanguageManager.getInstance().getString(LanguageKey.NO_MORE_SOLUTIONS),
                    LogType.INFO);
            this.toggleStateFunc.accept(ClickableState.LAST_STEP);
        }
    }

    /**
     * Shows a previously generated and saved visualisation
     */
    public void previousStep() {
        if (this.current > 0) {
            this.current--;
        }

        this.toggleStateFunc.accept(this.current == 0 ? ClickableState.FIRST_STEP : ClickableState.PARSED_QUERY);
    }

    /**
     * Runs the interpreter until the next solution is found. This is done in a
     * separate thread to ensure the GUI is still responsive and the execution can
     * be canceled if it's going on for too long. Because of that the Thread has to
     * set the visualisation and the console output
     * 
     * @param console The console panel of the gui
     * @param visualisation The visualisation panel of the gui
     */
    public void nextSolution(ConsolePanel console, VisualisationPanel visualisation) {
        if (!this.running) {
            this.setThreadRunning(true);
            this.assignNextSolutionThread(console, visualisation);
        }
    }

    /**
     * Actually initializes and starts a thread that runs until the next solution is
     * found and also takes care of the console output and the visualization 
     * inside the thread
     * 
     * @param console The console panel of the gui
     * @param visualisation The visualisation panel of the gui
     */
    public void assignNextSolutionThread(ConsolePanel console, VisualisationPanel visualisation) {
        if (this.continueThread.isPresent()) {
            this.setThreadRunning(false);
            return;
        }

        this.continueThread = Optional.of(new Thread(() -> {
            if (this.results.get(this.current) == StepResult.NO_MORE_SOLUTIONS) {
                this.setThreadRunning(false);
                return;
            }

            while (this.running) {
                this.nextStep(console);

                if (this.results.get(this.current) == StepResult.SOLUTION_FOUND) {
                    this.setThreadRunning(false);
                    this.toggleStateFunc.accept(ClickableState.PARSED_QUERY);
                }

                if (this.results.get(this.current) == StepResult.NO_MORE_SOLUTIONS) {
                    this.setThreadRunning(false);
                    this.toggleStateFunc.accept(ClickableState.LAST_STEP);
                }
            }

            visualisation.setFromGraph(this.getCurrentVisualisation());

            return;
        }));

        this.continueThread.get().start();
    }

    /**
     * Waits for the thread to die. This is used for testing the continue and
     * finish query functionalities.
     */
    public void waitForThread() {
        if (this.continueThread.isPresent()) {
            try {
                this.continueThread.get().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }            
        }
    }

    /**
     * Runs the interpreter until there are no more solutions. This is done in a
     * separate Thread to ensure that the GUI is still responsive and the Thread can
     * be stopped if it takes to long or if the query is a recursive loop.
     *
     * @param console Panel of the console area
     * @param visualisation Panel of the visualisation area
     */
    public void finishQuery(ConsolePanel console, VisualisationPanel visualisation) {
        if (!this.running) {
           this.setThreadRunning(true);
           this.assignFinishQueryThread(console, visualisation); 
        }
    }
    /**
     * Actually initializes and starts a thread that runs until there are no more 
     * solutions and also takes care of the console output and the visualization
     * inside the thread
     * 
     * @param console The console panel of the gui
     * @param visualisation The visualisation panel of the gui
     */

    public void assignFinishQueryThread(ConsolePanel console, VisualisationPanel visualisation) {
        if (this.continueThread.isPresent()) {
            this.setThreadRunning(false);
            return;
        }

        this.continueThread = Optional.of(new Thread(() -> {
            if (this.results.get(this.current) == StepResult.NO_MORE_SOLUTIONS) {
                this.setThreadRunning(false);
                return;
            }

            while (this.running) {
                this.nextStep(console);

                if (this.results.get(this.current) == StepResult.NO_MORE_SOLUTIONS) {
                    this.setThreadRunning(false);
                    this.toggleStateFunc.accept(ClickableState.LAST_STEP);
                }
            }

            visualisation.setFromGraph(this.getCurrentVisualisation());

            return;
        }));

        this.continueThread.get().start();
    }

    /**
     * Cancels any currently running search for the next solution invoked by calling
     * runUntilNextSolution() by stopping the thread. The currently executed
     * interpretation step is finished before the process is stopped. The
     * visualisation gets updated to the respective current step.
     */
    public void cancel() {
        this.setThreadRunning(false);

        if (!this.continueThread.isPresent()) {
            return;
        }

        try {
            this.continueThread.get().join();
        } catch (InterruptedException e) {
            if (MainWindow.inDebugMode()) {
                e.printStackTrace();
            }
        }

        this.continueThread = Optional.empty();
    }

    /**
     * Returns a solution for the query. This method assumes that on the current
     * interpreter instance, interpreter.getCurrent().isPresent() holds. This means
     * that at least one step must have been executed.
     * 
     * @return string list of substitutions that form a solution
     */
    public List<Substitution> getSolution() {
        Environment currentEnv = this.interpreter.get().getCurrent().get().getEnvironment();

        List<Substitution> solution = this.variables.get().stream().map(variable -> {
            return new Substitution(variable, currentEnv.applyAllSubstitutions(variable));
        }).collect(toList());

        return solution;
    }

    /**
     * Getter-Method for the current visualisation of the interpretation
     * 
     * @return Current visualisation of the interpretation
     */
    public Graph getCurrentVisualisation() {
        return this.visualisations.get(this.current);
    }

    /**
     * Toggles the standard library on or off.
     */
    public void toggleStandardLibrary() {
        this.useStandardLibrary = !this.useStandardLibrary;
    }

    /**
     * Returns whether the standard library is enabled.
     * 
     * @return boolean value describing whether the standard library is enabled
     */
    public boolean isStandardEnabled() {
        return this.useStandardLibrary;
    }

    /**
     * Handles thread start/stop actions and disables/enables the pause button accordingly.
     * @param running describing whether the thread is running
     */
    private void setThreadRunning(boolean running) {
        this.running = running;
        if (running) {
            this.toggleStateFunc.accept(ClickableState.NEXT_SOLUTION_RUNNING);
        } else {
            this.toggleStateFunc.accept(ClickableState.NEXT_SOLUTION_ABORTED);
        }
    }
    
    /**
     * Returns whether the interpreter manager is currently
     * searching for solutions.
     * 
     * @return whether the interpreter manager is searching for solutions
     */
    public boolean isSearchingForSolutions() {
        return this.running;
    }
    
    /**
     * Getter-Method for the code used in the standard library
     * 
     * @return the standard library code
     */
    public String getStandardLibraryCode() {
        if (!this.standardLibrary.isPresent()) {
            // TODO: better error handling, i.e. return Optional or throw exception
            return "";
        }

        return this.standardLibrary.get().toString();
    }
}
