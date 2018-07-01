package edu.kit.ipd.pp.viper.controller;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FilenameUtils;

import edu.kit.ipd.pp.viper.view.ConsolePanel;
import edu.kit.ipd.pp.viper.view.VisualisationPanel;

/**
 * Command for exporting the visualisation to TikZ for LaTex.
 * */
public class CommandExportTikz extends Command {
    private static final String KEY_TIKZ_FILES = "key_tikz_files";
    private static final String KEY_EXPORT_FILE_ERROR = "key_export_file_error";
    private static final String KEY_EXPORT_FILE_SUCCESS = "key_export_file_success";
    
    private ConsolePanel console;
    private VisualisationPanel visualisation;
    
    /**
     * Initializes a new TikZ export command.
     * 
     * @param console               Panel of the console area
     * @param visualisation         Panel of the visualisation area
     */
    public CommandExportTikz(ConsolePanel console, VisualisationPanel visualisation,
            InterpreterManager interpreterManager) {
        this.console = console;
        this.visualisation = visualisation;
    }

    /**
     * Executes the command.
     */
    public void execute() {
        FileFilter filter = new FileFilter() {
            @Override
            public String getDescription() {
                return LanguageManager.getInstance().getString(KEY_TIKZ_FILES);
            }

            @Override
            public boolean accept(File f) {
                return FilenameUtils.getExtension(f.getName()).toLowerCase().equals("tikz");
            }
        };

        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(filter);
        int rv = chooser.showSaveDialog(null);

        if (rv == JFileChooser.APPROVE_OPTION) {
            try {
                FileOutputStream out = new FileOutputStream(chooser.getSelectedFile());
                out.write(visualisation.getTikZ().getBytes());
                out.flush();
                out.close();

                String msg = LanguageManager.getInstance().getString(KEY_EXPORT_FILE_SUCCESS);
                console.printLine(msg + ": " + chooser.getSelectedFile().getAbsolutePath(), Color.BLACK);
            } catch (FileNotFoundException e) {
                String err = LanguageManager.getInstance().getString(KEY_EXPORT_FILE_ERROR);
                console.printLine(err + ": " + chooser.getSelectedFile().getAbsolutePath(), Color.RED);
                e.printStackTrace();
            } catch (IOException e) {
                String err = LanguageManager.getInstance().getString(KEY_EXPORT_FILE_ERROR);
                console.printLine(err + ": " + chooser.getSelectedFile().getAbsolutePath(), Color.RED);
                e.printStackTrace();
            }
        }
    }
}
