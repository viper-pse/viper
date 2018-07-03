package edu.kit.ipd.pp.viper.controller;

import javax.swing.JOptionPane;

import edu.kit.ipd.pp.viper.view.ConsolePanel;
import edu.kit.ipd.pp.viper.view.EditorPanel;
import edu.kit.ipd.pp.viper.view.VisualisationPanel;

/**
 * Command for setting the editor up with a new file. This clears the editor and
 * removes any reference to previously saved files.
 */
public class CommandNew extends Command {
    private ConsolePanel console;
    private EditorPanel editor;
    private VisualisationPanel visualisation;

    /**
     * @param console Panel of the console area
     * @param visualisation Panel of the visualisation area
     * @param editor Panel of the editor area
     */
    public CommandNew(ConsolePanel console, EditorPanel editor, VisualisationPanel visualisation) {
        this.console = console;
        this.editor = editor;
        this.visualisation = visualisation;
    }

    /**
     * Executes the command.
     */
    public void execute() {
        if (editor.hasChanged()) {
            LanguageManager langman = LanguageManager.getInstance();
            Object options[] = {langman.getString(LanguageKey.KEY_YES), langman.getString(LanguageKey.KEY_NO),
                    langman.getString(LanguageKey.KEY_CANCEL)};
            int rv = JOptionPane.showOptionDialog(null, langman.getString(LanguageKey.KEY_CONFIRMATION),
                    langman.getString(LanguageKey.KEY_CONFIRMATION_TITLE), JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, options, options[2]);

            switch (rv) {
            // Yes option
            case 0:
                CommandSave save = new CommandSave(console, editor, SaveType.SAVE);
                save.execute();
                break;
            // No option
            case 1:
                break;
            // Cancel option
            default:
                return;
            }
        }

        editor.setSourceText("");
        editor.setHasChanged(false);
        console.clearAll();
        visualisation.clearVisualization();
    }
}
