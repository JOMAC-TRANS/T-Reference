/*
 * Main.java
 *
 * Created on June 28, 2012, 03:45:15 PM
 */
package com.jomac.transcription.referencebuilder;

import com.jomac.transcription.referencebuilder.forms.MainFrame;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    private static ResourceBundle resourceBundle;
    private static ServerSocket serverSocket;
    private static final int APP_PORT = 29130;
    private static final String TRANSCRIPTION_STORAGE = "com.dssi.transcription.client.Storage";
    private static MainFrame mainframe;

    public static void main(final String args[]) {
        try {
            serverSocket = new ServerSocket(APP_PORT);
        } catch (IOException ex) {
            return;
        }

        try {
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
            UIManager.setLookAndFeel(
                    Preferences.userRoot().node(TRANSCRIPTION_STORAGE).get(
                    "mainframe.lookAndFeel",
                    UIManager.getSystemLookAndFeelClassName()));
        } catch (Exception ex) {
        }

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                mainframe = new MainFrame();
                mainframe.setVisible(true);
            }
        });
    }

    public static ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = ResourceBundle.getBundle(
                    "com.jomac.transcription.referencebuilder.resources.messages_en_US");
        }

        return resourceBundle;
    }

    public static void closeServerSocket() {
        try {
            serverSocket.close();
        } catch (IOException ex) {
        }
    }
}
