import dk.brics.automaton.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.table.*;
import java.util.*;
import java.io.*;

/**
 *
 *
 */
public class GUI {
    public GUI() {
        GUI.currentGUI = this;
        addTabs(frame);
        addEvents(frame);
        frame.setVisible(true);
        Image ic = Toolkit.getDefaultToolkit().getImage("./images/HouseFire.png");
        frame.setIconImage(ic);
        frame.setSize(640,480);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    private void updateTps() {
        if (updater == null) {
            Runnable r = new Runnable() {
                public void run() {
                    try {
                        while (true) {
                            long ktps = HashDispatcher.getKtps();
                            tps.setText("kT/s: " + ktps);
                            Thread.sleep(500);
                        }
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            };
            updater = new Thread(r);
            updater.start();
        }
    }

    private void stopUpdateTps() {
        updater.interrupt();
        updater = null;
        tps.setText("Your house is currently not on fire.");
    }

    private class RunAction extends AbstractAction {
        public void actionPerformed(ActionEvent ae) {
            Automaton aut = Automaton.makeEmpty();
            Iterator<String> lines =
                Util.readLinesFromString(regTextPane.getText());
            while (lines.hasNext()) {
                try {
                    String s = lines.next();
                    RegExp re = new RegExp(s);
                    Automaton a = re.toAutomaton();
                    aut = aut.union(a);
                } catch (IllegalArgumentException e) {
                }
                aut.minimize();
            }
            if (aut.isEmpty() || aut.isEmptyString()) {
                ErrorFrame.spawnError("Your regular expression" +
                        " does not match any string.");
            } else if (!Util.isPossible4chanAutomaton(aut)) {
                ErrorFrame.spawnError("Your regular expression does not " +
                        "match any 4chan trip codes");
            } else {
                this.setEnabled(false);
                int threads = 0;
                try {
                    String val = (String)coreDropDown.getSelectedItem();
                    threads = Integer.parseInt(val);
                } catch (NumberFormatException nfe) {
                    threads = 8;
                }
                stopAction.setEnabled(true);
                RunAutomaton ra = new RunAutomaton(aut);
                HashDispatcher.dispatch(ra, threads);
                updateTps();
            }
        }
    }

    public static void addTrip(String pass, String trip) {
        GUI.currentGUI.model.addRow(new Object[]{pass, trip});
    }

    private class StopAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            HashDispatcher.killAll();
            this.setEnabled(false);
            runAction.setEnabled(true);
            stopUpdateTps();
        }
    }

    private class CloseAction extends AbstractAction {
        public void actionPerformed(ActionEvent event) {
            // Update the contents of regexps.txt
            try {
                Util.writeTextToFile(regTextPane.getText(), "./doc/regexps.txt");
            } catch (Exception e) {
            }
            // Shut down safely.
            WindowEvent we = new WindowEvent(frame, WindowEvent.WINDOW_CLOSING);
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(we);
            frame.setVisible(false);
            frame.dispose();
            System.exit(0);
        }
    }

    private JLabel createLabel(String text, Color fg, Font font, int align) {
        JLabel l = new JLabel(text, align);
        l.setFont(font);
        l.setForeground(fg);
        l.setBackground(Color.white);
        l.setVerticalAlignment(JLabel.TOP);
        return l;
    }

    private JPanel getAdditionalSettingsArea() {
        JPanel p = new JPanel();
        p.setLayout(null);
        p.setBackground(Util.BG_COLOR);
        JLabel coresText =
            createLabel("Number of cores to use:",
                    new Color(0, 0, 0),
                    new Font(Font.SANS_SERIF, Font.PLAIN, 12),
                    JLabel.LEFT);
        coresText.setSize(200, 25);
        coresText.setLocation(5, 5);
        p.add(coresText);
        String[] comboBoxOptions = {
            "1", "2", "3", "4", "5", "6", "7", "8",
            "9", "10", "11", "12", "13", "14", "15", "16"
        };
        coreDropDown = new JComboBox<String>(comboBoxOptions);
        coreDropDown.setSize(50, 25);
        coreDropDown.setLocation(210, 5);
        p.add(coreDropDown);
        return p;
    }

    private void addEvents(final JFrame frame) {
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                CloseAction ca = new CloseAction();
                ActionEvent ae = new ActionEvent(frame,
                    ActionEvent.ACTION_PERFORMED,
                    "lolfag");
                ca.actionPerformed(ae);
            }
        });
    }

    private void reportMatch(String password, String tripcode) {
        System.out.println(password + " -> " + tripcode);
    }

    private void addTabs(Container frame) {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(Util.BG_COLOR);
        /**
         *  This chunk of code generates the "Execution" tab.
         */
        ImageIcon fire = new ImageIcon("./images/HouseFireIcon.png");
        JPanel execPanel = new JPanel();
        execPanel.setBackground(Util.BG_COLOR);
        execPanel.setLayout(new BorderLayout());
        tps.setText("<html><p>Your house is currently not on fire.</p></html>");
        execPanel.add(tps, BorderLayout.NORTH);
        model.addColumn("Password");
        model.addColumn("Tripcode");
        model.addRow(new Object[]{ "password", Hash.getTripCode("password") });
        table.setPreferredScrollableViewportSize(new Dimension(500, 100));
        table.setFillsViewportHeight(true);
        execPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel execIn = new JPanel();
        execIn.setBackground(Util.BG_COLOR);
        execIn.setLayout(new GridLayout(1, 4));
        JButton run = new JButton(runAction);
        run.setText("Run");
        run.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        JButton stop = new JButton(stopAction);
        stop.setText("Stop");
        stop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        stopAction.setEnabled(false);

        JButton clear = new JButton("Clear");
        clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel m = GUI.currentGUI.model;
                m.getDataVector().removeAllElements();
                m.fireTableDataChanged();
            }
        });

        JButton exit = new JButton(closeAction);
        exit.setText("Exit");
        execIn.add(run);
        execIn.add(stop);
        execIn.add(clear);
        execIn.add(exit);
        execPanel.add(execIn, BorderLayout.SOUTH);
        tabs.addTab("Execution", fire, execPanel, "Starts and [optionally] " +
                "extinguishes House Fires\u2122.");
        /**
         *  This chunk of code generates the "Settings" tab.
         */
        ImageIcon fool = new ImageIcon("./images/MoreCoresIcon.png");
        JPanel settPanel = new JPanel();
        settPanel.setBackground(Util.BG_COLOR);
        settPanel.setLayout(new BorderLayout());
        settPanel.add(createLabel("This is where settings go.",
                    new Color(0, 0, 0),
                    new Font(Font.SANS_SERIF, Font.PLAIN, 12),
                    JLabel.CENTER),
                BorderLayout.NORTH);
        settPanel.add(getRegExpArea(), BorderLayout.WEST);
        settPanel.add(getAdditionalSettingsArea(), BorderLayout.CENTER);
        tabs.addTab("Settings", fool, settPanel, "This is where you add more " +
                "cores\u2122 and choose various settings.");
        /**
         *  This marginally more clever code generates the "F.A.Q." tab.
         */
        ImageIcon why = new ImageIcon("./images/FAQIcon.png");
        JPanel faqPanel = new JPanel();
        faqPanel.setBackground(Util.BG_COLOR);
        faqPanel.setLayout(new BorderLayout());
        int faqV = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED;
        int faqH = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;
        File f = new File("./doc/faq.htm");
        try {
            JEditorPane edit = new JEditorPane(f.toURI().toURL());
            edit.setEditable(false);
            edit.addHyperlinkListener(new HyperlinkListener() {
                public void hyperlinkUpdate(HyperlinkEvent evt) {
                    if (evt.getEventType() ==
                        HyperlinkEvent.EventType.ACTIVATED) {
                        JEditorPane edit = (JEditorPane)evt.getSource();
                        try {
                            Desktop desk = Desktop.getDesktop();
                            java.net.URI uri = evt.getURL().toURI();
                            desk.browse(uri);
                        } catch (IOException ioe) {
                        } catch (java.net.URISyntaxException use) {
                        }
                    }
                }
            });
            faqPanel.add(edit, BorderLayout.CENTER);
            JScrollPane scroll = new JScrollPane(edit, faqV, faqH);
            tabs.addTab("F.A.Q.", why, scroll, "This is where more or less " +
                    "frequently asked questions are answered.");
        } catch (IOException e) {
            JLabel err = createLabel("<html><p>Something went wrong:</p><p>" +
                    e.toString() + "</p></html>",
                    new Color(255, 0, 0),
                    new Font(Font.SANS_SERIF, Font.BOLD, 14),
                    JLabel.CENTER);
            faqPanel.add(err, BorderLayout.CENTER);
            tabs.addTab("F.A.Q.", why, faqPanel, "Something went wrong in " +
                    "loading the F.A.Q. page.");
        }
        frame.add(tabs);
    }

    private JPanel getRegExpArea() {
        JPanel p = new JPanel();
        p.setBackground(Util.BG_COLOR);
        p.setLayout(new BorderLayout());
        p.add(createLabel("Regexps to search for:",
                    Color.black,
                    new Font(Font.SANS_SERIF, Font.PLAIN, 12),
                    JLabel.LEFT),
                BorderLayout.NORTH);
        StyleContext cont = new StyleContext();
        final StyledDocument doc = new DefaultStyledDocument(cont);
        // Default style.
        final Style style = cont.getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setAlignment(style, StyleConstants.ALIGN_LEFT);
        StyleConstants.setFontSize(style, 12);
        StyleConstants.setFontFamily(style, Font.MONOSPACED);
        StyleConstants.setSpaceAbove(style, 0);
        StyleConstants.setSpaceBelow(style, 0);
        try {
            Iterator<String> reader = Util.readLinesFromFile("./doc/regexps.txt");
            while (reader.hasNext()) {
                String str = reader.next();
                if (Util.isValidRegExp(str)) {
                    StyleConstants.setBackground(style, Color.white);
                } else {
                    StyleConstants.setBackground(style, Color.red);
                }
                doc.insertString(doc.getLength(), str+"\n", style);
            }
        } catch (BadLocationException e) {
            // OH NOES.
        } catch (NullPointerException e) {
            // SHiat.
        }
        regTextPane = new JTextPane(doc);
        regTextPane.setEditable(true);
        JScrollPane scroll = new JScrollPane(regTextPane);
        p.add(scroll, BorderLayout.CENTER);
        JButton val = new JButton("Validate");
        val.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String str = regTextPane.getText();
                Iterator<String> lines = Util.readLinesFromString(str);
                try {
                    doc.remove(0, doc.getLength());
                    StyleConstants.setFontFamily(style, Font.MONOSPACED);
                    while (lines.hasNext()) {
                        String tmp = lines.next();
                        if (Util.isValidRegExp(tmp)) {
                            StyleConstants.setBackground(style, Color.white);
                        } else {
                            StyleConstants.setBackground(style, Color.red);
                        }
                        doc.insertString(doc.getLength(), tmp+"\n", style);
                    }
                } catch (BadLocationException bad) {
                }
            }
        });
        p.add(val, BorderLayout.SOUTH);
        return p;
    }

    private JPanel getFAQ(String question, String answer) {
        JPanel p = new JPanel();
        p.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel q = createLabel("<html><h3>"+question+"</h3></html>",
                new Color(145, 9, 57),
                new Font(Font.SANS_SERIF, Font.BOLD, 14),
                JLabel.CENTER);
        q.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel a = createLabel("<html><p>"+answer+"</p></html>",
                Color.black,
                new Font(Font.SANS_SERIF, Font.PLAIN, 14),
                JLabel.CENTER);
        a.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(q);
        p.add(a);
        return p;
    }

    private Thread updater = null;
    private static GUI currentGUI;
    private final DefaultTableModel model = new DefaultTableModel();
    private final JTable table = new JTable(model);
    private final JLabel tps = createLabel("The program is current stopped.",
            Color.black,
            new Font(Font.SANS_SERIF, Font.PLAIN,
                12),
            JLabel.LEFT);
    private final CloseAction closeAction = new CloseAction();
    private final RunAction runAction = new RunAction();
    private final StopAction stopAction = new StopAction();
    private final JFrame frame = new JFrame("House Fire Explorer v.1");
    private JTextPane regTextPane;
    private JComboBox coreDropDown;
}
