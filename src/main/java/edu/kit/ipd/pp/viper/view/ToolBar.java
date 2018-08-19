package edu.kit.ipd.pp.viper.view;

import java.awt.Cursor;

import javax.swing.Box;
import javax.swing.JLabel;

import javax.swing.JToolBar;

import edu.kit.ipd.pp.viper.controller.LanguageKey;

/**
 * Represents a toolbar containing a bunch of button with icons. This toolbar
 * can be attached to the main window of the program to represent "shortcuts"
 * for different menu options.
 */
public class ToolBar extends JToolBar implements HasClickable {
    /**
     * Serial UID
     */
    private static final long serialVersionUID = 7709171094564025321L;

    /**
     * Path to icons used by {@link ToolBarButton}s of this toolbar. All paths need
     * to begin with a slash, otherwise Java will look for the file inside the
     * <code>edu.kit.ipd.pp.viper.view</code> package, not inside
     * <code>src/main/resources/</code>.
     */
    private static final String ICON_NEW = "/icons_png/icon_newfile.png";
    private static final String ICON_OPEN = "/icons_png/icon_openfile.png";
    private static final String ICON_SAVE = "/icons_png/icon_savefile.png";
    private static final String ICON_PARSE = "/icons_png/icon_parsefile.png";
    private static final String ICON_FORMAT = "/icons_png/icon_formatfile.png";
    private static final String ICON_CANCEL = "/icons_png/icon_cancel.png";
    private static final String ICON_NEXTSTEP = "/icons_png/icon_nextstep.png";
    private static final String ICON_NEXTSOLUTION = "/icons_png/icon_nextsolution.png";
    private static final String ICON_PREVIOUSSTEP = "/icons_png/icon_previousstep.png";
    private static final String ICON_ZOOM_IN = "/icons_png/icon_zoom_in.png";
    private static final String ICON_ZOOM_OUT = "/icons_png/icon_zoom_out.png";
    
    private ToolBarButton buttonNew;
    private ToolBarButton buttonOpen;
    private ToolBarButton buttonSave;
    private ToolBarButton buttonParse;
    private ToolBarButton buttonFormat;
    private ToolBarButton buttonBack;
    private ToolBarButton buttonStep;
    private ToolBarButton buttonSolution;
    private ToolBarButton buttonCancel;
    
    private JLabel labelZoom;
    private ToolBarButton buttonZoomIn;
    private ToolBarButton buttonZoomOut;

    /**
     * Reference to main class
     */
    private final MainWindow main;

    /**
     * Creates a new toolbar that can be added to the main window
     * 
     * @param gui Reference to main window. Necessary because the commands executed
     *        by clicking on buttons in this toolbar need the different panels
     *        available via getters in the main class.
     */
    public ToolBar(MainWindow gui) {
        this.main = gui;

        this.setFloatable(false);
        this.setRollover(true);
        this.addButtons();
    }

    /**
     * Adds all buttons to the toolbar
     */
    private void addButtons() {
        this.buttonNew = new ToolBarButton(GUIComponentID.BUTTON_NEW,
                                           ToolBar.ICON_NEW, LanguageKey.TOOLTIP_NEW,
                                           this.main.getCommandNew());
        
        this.buttonOpen = new ToolBarButton(GUIComponentID.BUTTON_OPEN,
                                           ToolBar.ICON_OPEN, LanguageKey.TOOLTIP_OPEN,
                                           this.main.getCommandOpen());
        
        this.buttonSave = new ToolBarButton(GUIComponentID.BUTTON_SAVE,
                                           ToolBar.ICON_SAVE, LanguageKey.TOOLTIP_SAVE,
                                           this.main.getCommandSave());

        this.buttonParse = new ToolBarButton(GUIComponentID.BUTTON_PARSE,
                                           ToolBar.ICON_PARSE, LanguageKey.TOOLTIP_PARSE,
                                           this.main.getCommandParse());
        
        this.buttonFormat = new ToolBarButton(GUIComponentID.BUTTON_FORMAT,
                                           ToolBar.ICON_FORMAT, LanguageKey.TOOLTIP_FORMAT,
                                           this.main.getCommandFormat());

        this.buttonBack = new ToolBarButton(GUIComponentID.BUTTON_STEPBACK,
                                           ToolBar.ICON_PREVIOUSSTEP, LanguageKey.TOOLTIP_PREVIOUSSTEP,
                                           this.main.getCommandPreviousStep());
        
        this.buttonStep = new ToolBarButton(GUIComponentID.BUTTON_STEP,
                                           ToolBar.ICON_NEXTSTEP, LanguageKey.TOOLTIP_NEXTSTEP,
                                           this.main.getCommandNextStep());
        
        this.buttonSolution = new ToolBarButton(GUIComponentID.BUTTON_NEXT_SOLUTION,
                                           ToolBar.ICON_NEXTSOLUTION, LanguageKey.TOOLTIP_NEXTSOLUTION,
                                           this.main.getCommandContinue());
        
        this.buttonCancel = new ToolBarButton(GUIComponentID.BUTTON_CANCEL,
                                           ToolBar.ICON_CANCEL, LanguageKey.TOOLTIP_CANCEL,
                                           this.main.getCommandCancel());

        this.buttonZoomIn = new ToolBarButton(GUIComponentID.BUTTON_ZOOM_IN,
                                           ToolBar.ICON_ZOOM_IN, LanguageKey.TOOLTIP_ZOOM_IN,
                                           this.main.getCommandZoomTextIn());

        this.buttonZoomOut = new ToolBarButton(GUIComponentID.BUTTON_ZOOM_OUT,
                                           ToolBar.ICON_ZOOM_OUT, LanguageKey.TOOLTIP_ZOOM_OUT,
                                           this.main.getCommandZoomTextOut());
        
        this.labelZoom = new JLabel("Text-Zoom:");
        this.add(this.buttonNew);
        this.add(this.buttonOpen);
        this.add(this.buttonSave);
        this.addSeparator();
        this.add(this.buttonParse);
        this.add(this.buttonFormat);
        this.addSeparator();
        this.add(this.buttonBack);
        this.add(this.buttonStep);
        this.add(this.buttonSolution);
        this.add(this.buttonCancel);

        this.add(Box.createHorizontalGlue());

        // Arbitrary space between the zoom button and the label
        final int padding = 10;
        
        this.add(this.labelZoom);
        this.add(Box.createHorizontalStrut(padding));
        this.add(this.buttonZoomIn);
        this.add(this.buttonZoomOut);
    }

    @Override
    public void switchClickableState(ClickableState state) {
        switch (state) {
        case NOT_PARSED_YET:
        case PARSED_PROGRAM:
        case NO_MORE_SOLUTIONS:
            this.buttonNew.setEnabled(true);
            this.buttonOpen.setEnabled(true);
            this.buttonSave.setEnabled(true);
            this.buttonParse.setEnabled(true);
            this.buttonFormat.setEnabled(true);
            this.buttonBack.setEnabled(false);
            this.buttonStep.setEnabled(false);
            this.buttonSolution.setEnabled(false);
            this.buttonCancel.setEnabled(false);
            break;
        case PARSED_QUERY:
            this.buttonNew.setEnabled(true);
            this.buttonOpen.setEnabled(true);
            this.buttonSave.setEnabled(true);
            this.buttonParse.setEnabled(true);
            this.buttonFormat.setEnabled(true);
            this.buttonBack.setEnabled(true);
            this.buttonStep.setEnabled(true);
            this.buttonSolution.setEnabled(true);
            this.buttonCancel.setEnabled(false);
            break;
        case FIRST_STEP:
            this.buttonNew.setEnabled(true);
            this.buttonOpen.setEnabled(true);
            this.buttonSave.setEnabled(true);
            this.buttonParse.setEnabled(true);
            this.buttonFormat.setEnabled(true);
            this.buttonBack.setEnabled(false);
            this.buttonStep.setEnabled(true);
            this.buttonSolution.setEnabled(true);
            this.buttonCancel.setEnabled(false);
            break;
        case LAST_STEP:
            this.buttonNew.setEnabled(true);
            this.buttonOpen.setEnabled(true);
            this.buttonSave.setEnabled(true);
            this.buttonParse.setEnabled(true);
            this.buttonFormat.setEnabled(true);
            this.buttonBack.setEnabled(true);
            this.buttonStep.setEnabled(false);
            this.buttonSolution.setEnabled(false);
            this.buttonCancel.setEnabled(false);
            break;
        case NEXT_SOLUTION_RUNNING:
            this.buttonCancel.setEnabled(true);
            this.main.setGlobalCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            break;
        case NEXT_SOLUTION_ABORTED:
            this.buttonCancel.setEnabled(false);
            this.main.setGlobalCursor(Cursor.getDefaultCursor());
            break;
        default:
            break;
        }
    }
}
