package edu.kit.ipd.pp.viper.controller;

import edu.kit.ipd.pp.viper.view.ConsolePanel;
import edu.kit.ipd.pp.viper.view.EditorPanel;
import edu.kit.ipd.pp.viper.view.VisualisationPanel;

/**
 * Command for resetting the zoom on all zoomable elements.
 */
public class CommandResetZoom extends Command {
    private VisualisationPanel visualisation;
    private ConsolePanel console;
    private EditorPanel editor;

    /**
     * Initializes a new reset zoom command.
     * 
     * @param visualisation Panel of the visualisation area
     * @param console Panel of the console area
     * @param editor Panel of the editor area
     */
    public CommandResetZoom(VisualisationPanel visualisation, ConsolePanel console, EditorPanel editor) {
        this.visualisation = visualisation;
        this.console = console;
        this.editor = editor;
    }
    
    @Override
    public void execute() {
        this.visualisation.resetZoom();
        this.console.resetZoom();
        this.editor.resetZoom();
    }
}
