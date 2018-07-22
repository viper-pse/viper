package edu.kit.ipd.pp.viper.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.ButtonGroup;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import edu.kit.ipd.pp.viper.controller.CommandExportImage;
import edu.kit.ipd.pp.viper.controller.CommandExportTikz;
import edu.kit.ipd.pp.viper.controller.CommandOpen;
import edu.kit.ipd.pp.viper.controller.CommandSave;
import edu.kit.ipd.pp.viper.controller.CommandSetLang;
import edu.kit.ipd.pp.viper.controller.CommandToggleLib;
import edu.kit.ipd.pp.viper.controller.ImageFormat;
import edu.kit.ipd.pp.viper.controller.LanguageKey;
import edu.kit.ipd.pp.viper.controller.LanguageManager;
import edu.kit.ipd.pp.viper.controller.SaveType;

/**
 * Represents a menu bar containing all available options and functionalities of
 * the program.
 */
public class MenuBar extends JMenuBar implements HasClickable {
    /**
     * Serial UID
     */
    private static final long serialVersionUID = -8638583278667065231L;

    /**
     * Instance of main window
     */
    private final MainWindow main;

    private MenuItem itemNew;
    private MenuItem itemOpen;
    private MenuItem itemSave;
    private MenuItem itemSaveAs;
    private MenuItem itemExit;
    private MenuItem itemParse;
    private MenuItem itemFormat;
    private MenuItem itemExportPNG;
    private MenuItem itemExportSVG;
    private MenuItem itemExportTikZ;
    private CheckBoxMenuItem itemToggleSTD;
    
    private Menu recentlyUsedMenu;

    /**
     * The constructor initialises all menus in the menu bar
     * 
     * @param gui Instance of main window
     */
    public MenuBar(MainWindow gui) {
        this.main = gui;

        this.addFileMenu();
        this.addProgramMenu();
        this.addExportMenu();
        this.addSettingsMenu();
    }

    /**
     * Adds the "file" menu
     */
    private void addFileMenu() {
        Menu menu = new Menu(LanguageKey.MENU_FILE);

        this.addNewItem(menu);
        this.addOpenItem(menu);
        this.addSaveItem(menu);
        this.addSaveAsItem(menu);
        menu.addSeparator();
        this.addRecentlyOpenedMenu(menu);
        menu.addSeparator();
        this.addExitItem(menu);

        this.resetRecentlyOpenedMenu();
        this.add(menu);
    }

    /**
     * Adds the "file > new" item
     * 
     * @param menu Menu to attach to
     */
    private void addNewItem(Menu menu) {
        itemNew = new MenuItem(LanguageKey.MENU_NEW, this.main.getCommandNew());

        menu.add(itemNew);
    }

    /**
     * Adds the "file > open" item
     * 
     * @param menu Menu to attach to
     */
    private void addOpenItem(Menu menu) {
        itemOpen = new MenuItem(LanguageKey.MENU_OPEN, this.main.getCommandOpen());

        menu.add(itemOpen);
    }

    /**
     * Adds the "file > save" item
     * 
     * @param menu Menu to attach to
     */
    private void addSaveItem(Menu menu) {
        itemSave = new MenuItem(LanguageKey.MENU_SAVE, this.main.getCommandSave());

        menu.add(itemSave);
    }

    /**
     * Adds the "file > save as" item
     * 
     * @param menu Menu to attach to
     */
    private void addSaveAsItem(Menu menu) {
        itemSaveAs = new MenuItem(LanguageKey.MENU_SAVEAS, new CommandSave(this.main.getConsolePanel(),
                this.main.getEditorPanel(), SaveType.SAVE_AS, this.main::setWindowTitle));


        menu.add(itemSaveAs);
    }

    /**
     * Adds the "file > recently opened" item
     * 
     * @param menu Menu to attach to
     */
    private void addRecentlyOpenedMenu(Menu menu) {
        recentlyUsedMenu = new Menu(LanguageKey.MENU_RECENT);
        menu.add(recentlyUsedMenu);
    }
    
    /**
     * Clears the recently used menu and adds the current items to it
     */
    public void resetRecentlyOpenedMenu() {
        recentlyUsedMenu.removeAll();
        for (File f : this.main.getEditorPanel().getFileReferenceList()) {
            JMenuItem item = new JMenuItem(f.getAbsolutePath());
            CommandOpen command = new CommandOpen(f.getAbsolutePath(), this.main.getConsolePanel(),
                    this.main.getEditorPanel(), this.main.getVisualisationPanel(), this.main::setWindowTitle,
                    this.main::switchClickableState, this.main.getCommandSave());

            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    command.execute();
                }
            });
            recentlyUsedMenu.add(item);
        }
    }

    /**
     * Adds the "file > exit" item
     * 
     * @param menu Menu to attach to
     */
    private void addExitItem(Menu menu) {
        itemExit = new MenuItem(LanguageKey.MENU_EXIT, this.main.getCommandExit());

        menu.add(itemExit);
    }

    /**
     * Adds the "program" menu
     */
    private void addProgramMenu() {
        Menu menu = new Menu(LanguageKey.MENU_PROGRAM);

        this.addParseItem(menu);
        this.addFormatItem(menu);

        this.add(menu);
    }

    /**
     * Adds the "program > parse" item
     * 
     * @param menu Menu to attach to
     */
    private void addParseItem(Menu menu) {
        itemParse = new MenuItem(LanguageKey.MENU_PARSE, this.main.getCommandParse());

        menu.add(itemParse);
    }

    /**
     * Adds the "program > format" item
     * 
     * @param menu Menu to attach to
     */
    private void addFormatItem(Menu menu) {
        itemFormat = new MenuItem(LanguageKey.MENU_FORMAT, this.main.getCommandFormat());

        menu.add(itemFormat);
    }

    /**
     * Adds the "export" menu
     */
    private void addExportMenu() {
        Menu menu = new Menu(LanguageKey.MENU_EXPORT);

        itemExportPNG = new MenuItem(LanguageKey.MENU_EXPORT_PNG, new CommandExportImage(this.main.getConsolePanel(),
                ImageFormat.PNG, this.main.getInterpreterManager()));

        itemExportSVG = new MenuItem(LanguageKey.MENU_EXPORT_SVG, new CommandExportImage(this.main.getConsolePanel(),
                ImageFormat.SVG, this.main.getInterpreterManager()));

        itemExportTikZ = new MenuItem(LanguageKey.MENU_EXPORT_TIKZ,
                new CommandExportTikz(this.main.getConsolePanel(), this.main.getInterpreterManager()));

        menu.add(itemExportPNG);
        menu.add(itemExportSVG);
        menu.add(itemExportTikZ);

        this.add(menu);
    }

    /**
     * Adds the "settings" menu
     */
    private void addSettingsMenu() {
        Menu menu = new Menu(LanguageKey.MENU_SETTINGS);

        itemToggleSTD = new CheckBoxMenuItem(LanguageKey.MENU_STDLIB, new CommandToggleLib(this.main,
                this.main::switchClickableState));
        if (this.main.getPreferencesManager().isStandardLibEnabled())
            itemToggleSTD.setSelected(true);
        else
            // standard lib is set to enabled by default, turn off here
            this.main.getInterpreterManager().toggleStandardLibrary();

        menu.add(itemToggleSTD);
        this.addLanguageSwitchMenu(menu);

        this.add(menu);
    }

    /**
     * Adds the "swttings > language" item
     * 
     * @param menu Menu to attach to
     */
    private void addLanguageSwitchMenu(Menu menu) {
        Menu languageMenu = new Menu(LanguageKey.MENU_LANGUAGE);

        ButtonGroup languageGroup = new ButtonGroup();

        // get the currently set locale from the preferences manager
        Locale configLocale = this.main.getPreferencesManager().getLanguage();

        Iterator<Locale> iter = LanguageManager.getInstance().getSupportedLocales().iterator();
        while (iter.hasNext()) {
            Locale locale = iter.next();

            LanguageCheckBoxMenuItem item = new LanguageCheckBoxMenuItem(locale,
                    new CommandSetLang(this.main.getConsolePanel(), this.main.getVisualisationPanel(), locale,
                            this.main.getInterpreterManager(), this.main.getPreferencesManager()));
            languageGroup.add(item);

            if (locale.getLanguage().equals(configLocale.getLanguage()))
                item.setSelected(true);

            languageMenu.add(item);
        }

        menu.add(languageMenu);
    }

    @Override
    public void switchClickableState(ClickableState state) {
        switch (state) {
        case NOT_PARSED_YET:
        case PARSED_PROGRAM:
            this.itemNew.setEnabled(true);
            this.itemOpen.setEnabled(true);
            this.itemSave.setEnabled(true);
            this.itemSaveAs.setEnabled(true);
            this.itemExit.setEnabled(true);
            this.itemParse.setEnabled(true);
            this.itemFormat.setEnabled(true);
            this.itemExportPNG.setEnabled(false);
            this.itemExportSVG.setEnabled(false);
            this.itemExportTikZ.setEnabled(false);
            this.itemToggleSTD.setEnabled(true);
            break;
        case PARSED_QUERY:
        case FIRST_STEP:
        case LAST_STEP:
        case NO_MORE_SOLUTIONS:
            this.itemNew.setEnabled(true);
            this.itemOpen.setEnabled(true);
            this.itemSave.setEnabled(true);
            this.itemSaveAs.setEnabled(true);
            this.itemExit.setEnabled(true);
            this.itemParse.setEnabled(true);
            this.itemFormat.setEnabled(true);
            this.itemExportPNG.setEnabled(true);
            this.itemExportSVG.setEnabled(true);
            this.itemExportTikZ.setEnabled(true);
            this.itemToggleSTD.setEnabled(true);
            break;
        default:
            break;
        }
    }
}
