package edu.kit.ipd.pp.viper.controller;

import edu.kit.ipd.pp.viper.view.ConsolePanel;
import edu.kit.ipd.pp.viper.view.VisualisationPanel;

/**
 * Command for continuously executing interpreter-steps until the next solution
 * is found. Once a solution is found, it is printed out through the console and
 * the visualisation adapts to the respective interpreter state.
 */
public class CommandFinishQuery extends Command {
    private ConsolePanel console;
    private VisualisationPanel visualisation;
    private InterpreterManager interpreterManager;

    /**
     * Initializes a new continue command.
     * 
     * @param console Panel of the console area
     * @param visualisation Panel of the visualisation area
     * @param interpreterManager Interpreter manager with a reference to the current
     *        interpreter
     */
    public CommandFinishQuery(ConsolePanel console, VisualisationPanel visualisation,
            InterpreterManager interpreterManager) {
        this.console = console;
        this.visualisation = visualisation;
        this.interpreterManager = interpreterManager;
    }

    @Override
    public void execute() {
        this.interpreterManager.cancel();
        this.interpreterManager.finishQuery(this.console, this.visualisation);
    }
}
