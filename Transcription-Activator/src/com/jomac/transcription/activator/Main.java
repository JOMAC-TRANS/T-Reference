package com.jomac.transcription.activator;

import com.hccs.util.JavaChecker;
import com.jomac.transcription.activator.controller.ActivatorController;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class Main {

    private static ResourceBundle resourceBundle;
    private static ServerSocket serverSocket;
    private static final int APP_PORT = 29131;

    public static ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = ResourceBundle.getBundle(
                    "com.jomac.transcription.activator.resources.messages_en_US");
        }

        return resourceBundle;
    }

    public static void main(String args[]) {
        try {
            serverSocket = new ServerSocket(APP_PORT);
            if (!new JavaChecker().validateJavaVersion()) {
                JOptionPane.showMessageDialog(null,
                        "Your Java Version is not compatible with your computer\n\n"
                        + "Please download 64-bit version of Java or\n"
                        + "contact your System Administrator",
                        "Java Version Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (IOException ex) {
            return;
        }

        try {
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
            UIManager.setLookAndFeel(
                    Preferences.userRoot().node("com.dssi.transcription.client.Storage").get(
                            "mainframe.lookAndFeel",
                            UIManager.getSystemLookAndFeelClassName()));
        } catch (Exception e) {
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ActivatorController ac = new ActivatorController();
                ac.showForm();
            }
        });

    }

    public static void closeServerSocket() {
        try {
            serverSocket.close();
        } catch (Exception ex) {
        }
    }
}
