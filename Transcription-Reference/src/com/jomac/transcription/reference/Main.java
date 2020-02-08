/*
 * Main.java
 *
 * Created on June 19, 2012, 09:47:15 AM
 */
package com.jomac.transcription.reference;

import com.hccs.util.JavaChecker;
import com.hccs.util.Task;
import com.hccs.util.TaskThread;
import com.jomac.transcription.reference.controller.FormController;
import com.jomac.transcription.reference.controller.LocalController;
import com.jomac.transcription.reference.controller.TRPlugin;
import com.jomac.transcription.reference.engines.H2;
import com.jomac.transcription.reference.forms.AccountDialog;
import com.jomac.transcription.reference.forms.UpdateDialog;
import com.jomac.transcription.reference.utilities.PluginProcessor;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javax.persistence.PersistenceException;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class Main {

    public static final String CLIENT_DB = "ReferenceDB";
    private static boolean activeApp;
    private static int associatedKey = 0, registrationId = -1;
    private static Date expirationDate;
    private static FormController controller;
    private static String dBAccount, dBPath;
    private static ServerSocket serverSocket;
    private static final int APP_PORT = 29130;
    private static Preferences preferences;
    private static ResourceBundle resourceBundle;
    private static final String TRANSCRIPTION_STORAGE = "com.dssi.transcription.client.Storage";

    public static void main(String args[]) {
        try {
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
            UIManager.setLookAndFeel(
                    Preferences.userRoot().node(TRANSCRIPTION_STORAGE).get(
                            "mainframe.lookAndFeel",
                            UIManager.getSystemLookAndFeelClassName()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (args.length == 0) {
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

                LocalController local = new LocalController();
                dBAccount = CLIENT_DB;
                String path = getDBPath().concat(File.separator).concat(CLIENT_DB);
                local.setDbPath(path);
                local.setUsername(getResourceBundle().getString("db_user"));
                local.setPassword(getResourceBundle().getString("db_pass"));
                if (new File(path.concat(".h2.db")).exists()) {
                    local.validateDB();
                } else {
                    local.createDB();
                }
                local.initChecker();
                dBAccount = "";
                H2.closeInstance();

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        null,
                        "Transcription Reference is already running",
                        "TransRef",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } catch (PersistenceException px) {
                JOptionPane.showMessageDialog(
                        null,
                        "Unable to connect to Server. \n"
                        + "Please check your INTERNET and try again later",
                        "Connection Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } catch (Exception ex) {
                System.out.println("ERROR CREATING DB!");
                ex.printStackTrace();
                return;
            }
        }

        if (associatedKey > 0) {
            new PluginProcessor().start();
        } else {
            showNoPlugins(activeApp ? "No Plugins to Download" : "UnActivated Program");
        }

        String pluginName = getPreferences().get("pluginName", "");
        if (pluginName.isEmpty()) {
            pluginName = checkPluginAvailability();
        } else {
            boolean valid = false;
            for (TRPlugin p : TRPlugin.INSTANCE.getAllPlugins()) {
                if (pluginName.endsWith(p.name())
                        && new File(Main.getDBPath(), "reference_" + p.name() + ".h2.db").exists()) {
                    valid = true;
                    break;
                }
            }
            if (!valid) {
                pluginName = checkPluginAvailability();
            }
        }

        setDataBaseName(pluginName);
        getPreferences().put("pluginName", pluginName);
        try {
            getPreferences().flush();
        } catch (Exception e) {
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                controller = new FormController();
                controller.showForm();
            }
        });

        if (args.length == 0) {
            new PluginProcessor().StartReferenceFiles();
        }
    }

    public static boolean isActiveApp() {
        return activeApp;
    }

    public static void setActiveApp(boolean activeApp) {
        Main.activeApp = activeApp;
    }

    public static void setAssociatedPlugins(int associatedKey) {
        Main.associatedKey = associatedKey;
    }

    public static int getAssociatedKey() {
        return associatedKey;
    }

    public static Date getExpirationDate() {
        return expirationDate;
    }

    public static int getRegistrationId() {
        return registrationId;
    }

    public static void setRegistrationId(int registrationId) {
        Main.registrationId = registrationId;
    }

    public static void setExpirationDate(Date expirationDate) {
        Main.expirationDate = expirationDate;
    }

    private static String checkPluginAvailability() {
        String pluginName;
        AccountDialog ad = new AccountDialog(null, true);
        ad.initButtons();
        ad.setVisible(true);
        if ((pluginName = ad.getSelectedPlugin()).isEmpty()) {
            showNoPlugins("No Plugin Selected");
        }
        return pluginName;
    }

    private static void showNoPlugins(String message) {
        JOptionPane.showMessageDialog(null,
                message
                + "\n\nT.Reference will now close!",
                "T.Reference",
                JOptionPane.ERROR_MESSAGE);
        terminate();
    }

    public static FormController getController() {
        return controller;
    }

    public static ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = ResourceBundle.getBundle(
                    "com.jomac.transcription.reference.resources.messages_en_US");
        }

        return resourceBundle;
    }

    public static Preferences getPreferences() {
        if (preferences == null) {
            preferences = Preferences.userRoot().node(Main.class.getName());
        }
        return preferences;
    }

    public static void terminate() {
        try {
            serverSocket.close();
        } catch (Exception ex) {
        }
        System.exit(0);
    }

    public static String getDBAccount() {
        return dBAccount;
    }

    public static void setDataBaseName(String dataBaseName) {
        dBAccount = dataBaseName;
    }

    public static String getDBPath() {
        if (dBPath == null || dBPath.isEmpty()) {
            dBPath = System.getProperty("user.home")
                    + File.separator + ".transcription" + File.separator + "XXXdb" + File.separator
                    + "T.Reference";
        }
        return dBPath;
    }

    public static String getReferenceFilesPath() {
        return System.getProperty("user.home")
                + File.separator + ".transcription" + File.separator + "XXXReferenceFiles";
    }

    public static String getDictatorsPath() {
        return System.getProperty("user.home")
                + File.separator + ".transcription" + File.separator + "XXXDictators";
    }

    public static String getBatchPath() {
        return System.getProperty("user.home")
                + File.separator + ".transcription" + File.separator + "XXXBATCH";
    }

    public static void restartApplication() {
        H2.closeInstance();
        controller.getMainFrame().dispose();
        System.gc();
        main(new String[]{"skip"});
    }

    public static void restartDeletePlugins() {
        H2.closeInstance();
        new PluginProcessor().checkPlugins();
        restartApplication();
    }

    public static void restartUpdateApplication() {
        H2.closeInstance();
        controller.getMainFrame().dispose();
        System.gc();

        new TaskThread(new Task() {
            LocalController lc;
            UpdateDialog udiag;
            boolean valid;

            @Override
            public void initialize() {
                System.gc();
                lc = new LocalController();
                udiag = new UpdateDialog(null, false);
                udiag.setStatus("Connecting to Transcription Server..."
                        + "\n\nPlease wait.");
                udiag.setLocationRelativeTo(null);
                udiag.setVisible(true);
            }

            @Override
            public void doInBackground() {
                valid = lc.updateReferenceDB();
            }

            @Override
            public void finished() {
                udiag.dispose();
                JOptionPane.showMessageDialog(null,
                        valid ? "Successfully Updated TranRef"
                                : "Failed to update TransRef"
                                + "\n\nT.Reference will now close!",
                        "Transcription Update",
                        valid ? JOptionPane.INFORMATION_MESSAGE
                                : JOptionPane.ERROR_MESSAGE);
                Main.setDataBaseName("");
                if (valid) {
                    main(new String[]{"skip"});
                } else {
                    terminate();
                }
            }
        }).start();
    }
}
