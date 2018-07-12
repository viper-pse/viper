package edu.kit.ipd.pp.viper.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFileChooser;

import edu.kit.ipd.pp.viper.view.ConsolePanel;
import edu.kit.ipd.pp.viper.view.EditorPanel;
import edu.kit.ipd.pp.viper.view.LogType;
import edu.kit.ipd.pp.viper.view.MainWindow;

/**
 * Command for saving the editor content to disk as a Prolog file.
 */
public class CommandSave extends Command {    
    private ConsolePanel console;
    private EditorPanel editor;
    private SaveType saveType;

    /**
     * Initializes a new save command.
     * 
     * @param console Panel of the console area
     * @param editor Panel of the editor area
     * @param saveType Save type (either save or save as new)
     */
    public CommandSave(ConsolePanel console, EditorPanel editor, SaveType saveType) {
        this.console = console;
        this.editor = editor;
        this.saveType = saveType;
    }

    /**
     * Executes the command.
     */
    public void execute() {
        if (this.saveType == SaveType.SAVE && this.editor.hasFileReference())
            save();
        else
            saveAs();
    }

    private void save() {
        File file = this.editor.getFileReference();
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(this.editor.getSourceText().getBytes());
            out.flush();
            out.close();
            this.editor.setHasChanged(false);
        } catch (IOException e) {
            String err = LanguageManager.getInstance().getString(LanguageKey.SAVE_FILE_ERROR);
            this.console.printLine(err + ": " + file.getAbsolutePath(), LogType.ERROR);

            if (MainWindow.inDebugMode()) {
                e.printStackTrace();
            }
        }
    }

    private void saveAs() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(FileFilters.PL_FILTER);
        int rv = chooser.showSaveDialog(null);

        if (rv == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String filePath = file.getAbsolutePath();
            if (!filePath.endsWith(".pl")) {
                file = new File(filePath + ".pl");
            }

            try {
                FileOutputStream out = new FileOutputStream(file);

                out.write(this.editor.getSourceText().getBytes());
                out.flush();
                out.close();
                this.editor.setHasChanged(false);
                this.editor.setFileReference(file);

                String msg = LanguageManager.getInstance().getString(LanguageKey.SAVE_FILE_SUCCESS);
                this.console.printLine(msg + ": " + file.getAbsolutePath(), LogType.INFO);
            } catch (IOException e) {
                String err = LanguageManager.getInstance().getString(LanguageKey.SAVE_FILE_ERROR);
                this.console.printLine(err + ": " + file.getAbsolutePath(), LogType.ERROR);

                if (MainWindow.inDebugMode()) {
                    e.printStackTrace();
                }
            }
        }
    }
}
