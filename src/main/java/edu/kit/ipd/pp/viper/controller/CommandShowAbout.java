package edu.kit.ipd.pp.viper.controller;

import java.awt.Image;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import edu.kit.ipd.pp.viper.view.GUIComponentID;

/**
 * Command for showing an about window for further information about the project.
 */
public class CommandShowAbout extends Command implements Observer, WindowListener {    
    private static final String ICON_PATH = "/icons_png/viper-icon.png";
    private boolean isOpened;

    private JFrame frame;
    private JLabel labelTitle;
    private JLabel labelAuthors;
    private JLabel labelLibraries;
    private JLabel labelIcon;

    /**
     * Initializes a new show about command.
     */
    public CommandShowAbout() {
        super();

        this.isOpened = false;

        ImageIcon icon = new ImageIcon(this.getClass().getResource(CommandShowAbout.ICON_PATH));
        icon.setImage(icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH));
        this.labelIcon = new JLabel(icon);

        LanguageManager.getInstance().addObserver(this);
    }

    private void setText() {
        LanguageManager langman = LanguageManager.getInstance();

        this.frame.setTitle(langman.getString(LanguageKey.MENU_ABOUT));

        this.labelTitle.setText("<html><center><b>" + langman.getString(LanguageKey.ABOUT_NAME) + "</b><br/>"
                + langman.getString(LanguageKey.ABOUT_DESCRIPTION) + "</center><br/></html>");

        this.labelAuthors.setText("<html><b>" + langman.getString(LanguageKey.ABOUT_AUTHORS) + "</b>"
                + "<ul>"
                + "  <li>Lukas Brocke</li>"
                + "  <li>Paul Brinkmeier</li>"
                + "  <li>Jannik Koch</li>"
                + "  <li>Aaron Maier</li>"
                + "  <li>Christian Oder</li>"
                + "</ul></html>");

        this.labelLibraries.setText("<html><b>" + langman.getString(LanguageKey.ABOUT_LIBRARIES) + "</b>"
                + "<ul>"
                + "  <li><b>Apache Batik</b><br/>https://xmlgraphics.apache.org/batik/</li>"
                + "  <li><b>Graphviz-Java</b><br/>https://github.com/nidi3/graphviz-java</li>"
                + "  <li><b>Fifesoft RSyntaxTextArea</b><br/>https://github.com/bobbylight/RSyntaxTextArea</li>"
                + "  <li><b>Liberation Serif 2.00.1</b><br/>https://pagure.io/liberation-fonts</li>"
                + "</ul></html>");
    }

    @Override
    public void execute() {
        if (!this.isOpened) {
            this.frame = new JFrame();
            this.frame.setName(GUIComponentID.FRAME_ABOUT.toString());
            this.frame.setLocationRelativeTo(null);
            this.frame.setResizable(false);
            this.frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            this.frame.setVisible(true);

            this.labelTitle = new JLabel();
            this.labelTitle.setVisible(true);
            this.labelTitle.setHorizontalAlignment(SwingConstants.CENTER);

            this.labelAuthors = new JLabel();
            this.labelAuthors.setVisible(true);
            this.labelAuthors.setHorizontalAlignment(SwingConstants.LEFT);

            this.labelLibraries = new JLabel();
            this.labelLibraries.setVisible(true);
            this.labelLibraries.setHorizontalAlignment(SwingConstants.LEFT);            

            this.setText();

            GroupLayout layout = new GroupLayout(this.frame.getContentPane());
            layout.setAutoCreateContainerGaps(true);
            layout.setAutoCreateGaps(true);

            layout.setHorizontalGroup(
                layout.createParallelGroup()
                  .addComponent(this.labelTitle)
                  .addGroup(layout.createSequentialGroup()
                      .addComponent(this.labelIcon)
                      .addComponent(this.labelAuthors)
                      .addComponent(this.labelLibraries))
            );

            layout.setVerticalGroup(
                layout.createSequentialGroup()
                    .addComponent(this.labelTitle)
                    .addGroup(layout.createParallelGroup()
                            .addComponent(this.labelIcon)
                            .addComponent(this.labelAuthors)
                            .addComponent(this.labelLibraries))
            );

            this.frame.getContentPane().setLayout(layout);
            this.frame.pack();
            this.frame.addWindowListener(this);
            this.isOpened = true;
        }

        this.frame.requestFocus();
        this.frame.toFront();
    }
    
    /**
     * Returns whether the popup is opened
     * 
     * @return boolean value describing whether the popup is opened
     */
    public boolean isOpened() {
        return this.isOpened;
    }

    @Override
    public void update(Observable obs, Object obj) {
        if (this.isOpened) {
            this.setText();
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
        return;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        this.isOpened = false;
    }

    @Override
    public void windowClosed(WindowEvent e) {
        return;
    }

    @Override
    public void windowIconified(WindowEvent e) {
        return;
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        return;
    }

    @Override
    public void windowActivated(WindowEvent e) {
        return;
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        return;
    }
}
