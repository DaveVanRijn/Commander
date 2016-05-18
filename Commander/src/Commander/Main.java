/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Commander;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.apache.commons.validator.routines.UrlValidator;

/**
 *
 * @author Dave van Rijn, Student 500714558, Klas IS202
 */
public class Main extends javax.swing.JDialog {

    private static String errorText;
    private static final String MINIMIZED_OPTION = "/m", JAVA_OPTION = "/j";
    private static final String[] defaultLocations = new String[4];

    /**
     * Creates new form Main
     */
    public Main(java.awt.Frame parent, boolean modal) {
        super(parent, modal);

        //Set tranparant
        Color transparant = new Color(0, 0, 0, 0);
        setUndecorated(true);
        getRootPane().setOpaque(false);
        getContentPane().setBackground(transparant);
        setBackground(transparant);

        initComponents();

        //Set components transparant
        transparant = new Color(0, 0, 0, 1);
        txtCommand.setBackground(transparant);
        txtCommand.setForeground(Color.GREEN);
        txtCommand.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 150)));
        jScrollPane1.getViewport().setBackground(transparant);
        jScrollPane1.getViewport().setOpaque(false);
        txtError.setBackground(transparant);
        txtError.setForeground(Color.RED);
        txtError.setEditable(false);
        txtError.setFocusable(false);
        jScrollPane1.setVisible(false);
        setAlwaysOnTop(true);

        pack();
        Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int width = getPreferredSize().width;
        int height = getPreferredSize().height;
        setLocation(screen.width - width - 20, screen.height - height - 20);

        txtCommand.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                if (jScrollPane1.isVisible()) {
                    jScrollPane1.setVisible(false);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (jScrollPane1.isVisible()) {
                    jScrollPane1.setVisible(false);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                //Not supported
            }

        });

        String home = System.getProperty("user.home");
        defaultLocations[0] = home + "/Documents";
        defaultLocations[1] = home + "/Pictures";
        defaultLocations[2] = home + "/Downloads";
        defaultLocations[3] = home + "/Music";

//        run("Casino.jar", new ArrayList<>());
    }

    private static void error(String error) {
        String text = "Could not execute command, command /help for details.";
        txtError.setText(text);
        txtError.setVisible(true);

        Date date = new Date();
        SimpleDateFormat dateForm = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        errorText = dateForm.format(date) + " " + error;

        System.out.println(error);

    }

    private static void prepareCommand(String string) {
        String extension = null;
        List<String> options;
        String command;
        String[] commands = string.split(",");
        //Split to extension, options and command

        for (String s : commands) {
            options = getOptions(string);
            string = options.remove(options.size() - 1); //Get command without options
            String website = string;
            if (!website.contains("http") && !website.contains("https")) {
                if (!website.contains("www.")) {
                    website = "http://www." + website;
                } else {
                    website = "http://" + website;
                }
            }
            if (new UrlValidator().isValid(website)) {
                //Website
                System.out.println("website");
                runWebsite(website);
            } else {
                //Not a website
                if (string.lastIndexOf(".") < string.length() - 1 && string.lastIndexOf(".") > 0) {
                    extension = string.substring(string.lastIndexOf("."), string.length() - 1);
                    command = string.replace("." + extension, "");
                } else {
                    command = string;
                }
            }
        }
        System.out.println(string);
    }

    private static void runWebsite(String website) {
        try {
            Desktop.getDesktop().browse(URI.create(website));
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static List<String> getOptions(String string) {
        List<String> options = new ArrayList<>();
        if (string.startsWith(" ")) {
            string = string.replaceFirst(" ", "");
        }
        while (string.startsWith("/")) {
            int nextIndex = string.indexOf(" ");
            String option = string.substring(0, nextIndex);
            options.add(option);
            string = string.substring(nextIndex, string.length());
            if (string.startsWith(" ")) {
                string = string.replaceFirst(" ", "");
            }
        }
        //Add rest of the string to the list
        options.add(string);
        return options;
    }

    private static void doCommand() {
        String input = txtCommand.getText();
        String[] commands = input.split(",");

        //Execute one command at a time
        for (String s : commands) {
            int readingIndex = 0;
            String name;
            File target;
            List<String> options = new ArrayList<>();
            //Remove whitespaces
            if (s.startsWith(" ")) {
                s = s.replaceFirst(" ", "");
            }

            //Options
            while (s.startsWith("/")) {
                int nextIndex = s.indexOf(" ");
                String option = s.substring(readingIndex, nextIndex);
                options.add(option);
                s = s.substring(nextIndex, s.length()).replaceFirst(" ", "");
            }
        }
    }

    /**
     * Search the file with given name, starts searching in the given directory.
     * The given name will be searched in current directory first, if not
     * present all directories from given directory will be searched.
     *
     * @param dir Directory to start looking
     * @param name Name of the file
     * @return The file with the given name, if found. If no file is found the
     * method returns null.
     */
    private static File searchFile(File dir, String name) {

        //Check if file or associated directory is present in this directory
        for (File f : dir.listFiles()) {
            if (f.getName().equalsIgnoreCase(name)) {
                if (f.isDirectory()) {
                    return searchFile(f, name);
                } else {
                    return f;
                }
            }
        }

        //If not present in this directory, start searching all directories
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                searchFile(f, name);
            } else {
                if (f.getName().equalsIgnoreCase(name)) {
                    return f;
                }
            }
        }

        return null;

    }

    private static String intToString(int i) {
        if (i < 10) {
            return "0" + i;
        } else {
            return Integer.toString(i);
        }
    }

    //Actions
    private static void run(String name, List<String> options) {
        File fileToRun = null;
        File dir;

        //Get the file
        for (String s : defaultLocations) {
            dir = new File(s);
            fileToRun = searchFile(dir, name);
            if (fileToRun != null) {
                //File found
                break;
            }
        }
        if (fileToRun == null) {
            //File not found
            error("Couldn't find file '" + name + "' to run.");
        } else {
            try {
                String extension = name.substring(name.lastIndexOf("."));
                if (extension.equals("jar")) {
                    Process p = Runtime.getRuntime().exec(new String[]{"cmd.exe",
                        "/c", "java -jar " + fileToRun.getAbsolutePath()});
                } else {
                    Desktop.getDesktop().open(fileToRun);
                }
                txtCommand.setText(null);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static String getOption(String s) {
        switch (s) {
            case MINIMIZED_OPTION:
                return "/min ";
            case JAVA_OPTION:
                return "java -jar ";
            default:
                return null;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtCommand = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtError = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        txtCommand.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        txtCommand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCommandActionPerformed(evt);
            }
        });

        txtError.setColumns(20);
        txtError.setLineWrap(true);
        txtError.setRows(5);
        txtError.setWrapStyleWord(true);
        jScrollPane1.setViewportView(txtError);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(txtCommand, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCommand, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtCommandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCommandActionPerformed
        String command;
        if (!(command = txtCommand.getText()).isEmpty()) {
            prepareCommand(command);
        }
    }//GEN-LAST:event_txtCommandActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Main dialog = new Main(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private static javax.swing.JTextField txtCommand;
    private static javax.swing.JTextArea txtError;
    // End of variables declaration//GEN-END:variables
}
