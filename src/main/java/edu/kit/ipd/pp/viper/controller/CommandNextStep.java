package edu.kit.ipd.pp.viper.controller;

import java.awt.Color;

import edu.kit.ipd.pp.viper.model.interpreter.StepResult;
import edu.kit.ipd.pp.viper.model.visualisation.GraphvizMaker;
import edu.kit.ipd.pp.viper.view.ConsolePanel;
import edu.kit.ipd.pp.viper.view.VisualisationPanel;
import guru.nidi.graphviz.model.Graph;

/**
 * Command for executing a single interpreter step.
 */
public class CommandNextStep extends Command {
    private ConsolePanel console;
    private VisualisationPanel visualisation;
    private InterpreterManager interpreterManager;

    /**
     * Initializes a new step command.
     * 
     * @param console Panel of the console area
     * @param visualisation Panel of the visualisation area
     * @param interpreterManager Interpreter manager with a reference to the current
     * interpreter
     */
    public CommandNextStep(ConsolePanel console, VisualisationPanel visualisation,
            InterpreterManager interpreterManager) {
        this.console = console;
        this.visualisation = visualisation;
        this.interpreterManager = interpreterManager;
    }

    /**
     * Executes the command.
     */
    public void execute() {
        final StepResult res = interpreterManager.step();
        if (res == StepResult.SOLUTION_FOUND) {
            final String prefix = LanguageManager.getInstance().getString(LanguageKey.SOLUTION_FOUND);
            console.printLine(prefix + interpreterManager.getSolutionString(), Color.BLACK);
        }

        Graph graph = GraphvizMaker.createGraph(interpreterManager.getCurrentState());
        visualisation.setFromGraph(graph);
    }
}
