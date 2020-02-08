package com.jomac.transcription.reference.forms;

import com.jomac.transcription.reference.Main;
import com.jomac.transcription.reference.controller.TRPlugin;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class RegistrationForm extends javax.swing.JDialog {

    private int pluginsCount = 0, tmpPluginCount = 0;
    private final ListSelectorPanel associatedPlugins;

    public RegistrationForm(Frame parent, boolean modal) {
        super(parent == null ? new JFrame() : parent, modal);
        initComponents();
        associatedPlugins = new ListSelectorPanel();
        pnlPlugins.add(associatedPlugins, BorderLayout.CENTER);

        associatedPlugins.setAvailableValues(TRPlugin.INSTANCE.getAllPlugins().toArray());

        if (parent != null) {
            setTitle(parent.getTitle() + " Registration");
            loadUserPreferences();
        }

        lblError.setVisible(false);
    }

    public final void initSelectedPlugins(int plugins) {
        List<TRPlugin> pl = new ArrayList<>();
        pluginsCount = 0;
        for (TRPlugin pb : TRPlugin.INSTANCE.getAllPlugins()) {
            if ((pb.getValue() & plugins) == pb.getValue()) {
                pluginsCount++;
                pl.add(pb);
            }
        }

        associatedPlugins.setSelectedValues(pl.toArray());
    }

    public void initRegisteredPlugin(int pluginsApproved, int pluginsRequest) {
        List<Object> pl = new ArrayList<>();
        pluginsCount = 0;

        if (pluginsApproved == pluginsRequest || pluginsRequest < pluginsApproved) {
            for (TRPlugin pb : TRPlugin.INSTANCE.getAllPlugins()) {
                if ((pb.getValue() & pluginsApproved) == pb.getValue()) {
                    pluginsCount++;
                    pl.add(pb);
                }
            }
            associatedPlugins.setSelectedValues(pl.toArray());
            associatedPlugins.setMarkedValues(pl);
        } else {
            List<Object> qpl = new ArrayList<>();
            for (TRPlugin pb : TRPlugin.INSTANCE.getAllPlugins()) {
                if ((pb.getValue() & pluginsRequest) == pb.getValue()) {
                    qpl.add(pb);
                    if ((pb.getValue() & pluginsApproved) == pb.getValue()) {
                        pluginsCount++;
                        pl.add(pb);
                    }
                }
            }
            associatedPlugins.setSelectedValues(qpl.toArray());
            associatedPlugins.setMarkedValues(pl);
        }

        associatedPlugins.revertTheMarkedValue(true);
    }

    public String getFullName() {
        return txtFullName.getText().trim();
    }

    public String getEmailAddress() {
        return txtEmailAdd.getText().trim();
    }

    public String getPhoneNumber() {
        return txtPhone.getText().trim();
    }

    public int getSelectedPlugins() {
        int total = 0;
        tmpPluginCount = 0;
        for (Object pb : associatedPlugins.getSelectedValues()) {
            total += ((TRPlugin) pb).getValue();
            tmpPluginCount++;
        }
        return total;
    }

    public void hideRegistrationInfo() {
        pnlPersonInfo.setVisible(false);
        lblUnregistered.setVisible(true);
    }

    public void addRequestCodeAddActionListerner(ActionListener a) {
        btnRequestCode.addActionListener(a);
    }

    public void showActivationMessage(boolean valid) {
        String message = valid ? "Activation Success!"
                : "Activation Failed!";

        JOptionPane.showMessageDialog(
                this, message, "Activation",
                valid ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }

    public void showRegistrationMessage(boolean valid) {
        String message = "<html><b>Request " + (valid ? " Sent!" : " Failed!")
                + "</b>" + (valid ? "</html>" : "<br><br><font size=-2><b>Note:</b><br/>"
                        + "You may need to disable your antivirus temporarily or"
                        + "<br/>Contact your technical support group.</font><br/></html>");

        JOptionPane.showMessageDialog(
                this, message, "Registration",
                valid ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }

    public void showDuplicateEntry() {
        JOptionPane.showMessageDialog(
                this, "Duplicate entry detected", "Registration",
                JOptionPane.ERROR_MESSAGE);
    }

    public void showOnceRegistration() {
        JOptionPane.showMessageDialog(
                this, "Registration is valid once a day", "Registration",
                JOptionPane.WARNING_MESSAGE);
    }

    public boolean validatePluginInfo() {

        if (getSelectedPlugins() == 0) {
            JOptionPane.showMessageDialog(this,
                    "<html><b>Required Field/s:<br>"
                    + "Select Plugin/s to register"
                    + "</b></html>", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (tmpPluginCount < pluginsCount) {
            return JOptionPane.showConfirmDialog(
                    this, "Warning! Some of your plugins may deactivated!\n"
                    + "Do you want to continue?",
                    "Request Warning!",
                    JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION;
        } else {
            return JOptionPane.showConfirmDialog(
                    this, "Click on OK to confirm your associated Plugins",
                    "Request",
                    JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION;
        }
    }

    public boolean validateRegistrationInfo() {
        String regEx = "^[a-z0-9_\\+-]+(\\.[a-z0-9_\\+-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*\\.([a-z]{2,4})$";
        StringBuilder builder = new StringBuilder();

        if (getPhoneNumber().isEmpty()) {
            builder.append("<br>Phone Number");
        }

        if (getFullName().isEmpty()) {
            builder.append("<br>Full Name");
        }

        if (getEmailAddress().isEmpty()) {
            builder.append("<br>Email Address");
        } else if (!getEmailAddress().matches(regEx)) {
            builder.append("<br>Invalid Email Address");
        } else if (txtEmailAdd2.getText().isEmpty()) {
            builder.append("<br>Re-Type Email Address");
        } else if (!txtEmailAdd2.getText().equals(getEmailAddress())) {
            builder.append("<br>Email Address Not matched");
        }

        if (getSelectedPlugins() == 0) {
            builder.append("<br>Select Plugin/s to register");
        }

        if (builder.toString().isEmpty()) {
            return JOptionPane.showConfirmDialog(
                    this, "Click on OK to confirm that this is your correct registration information",
                    "Activation",
                    JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION;

        } else {
            JOptionPane.showMessageDialog(this,
                    "<html><b>Required Field/s:<br>"
                    + builder.toString()
                    + "</b></html>", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public void toggleRegistrationForm(final boolean enable) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                txtPhone.setEnabled(enable);
                txtFullName.setEnabled(enable);
                txtEmailAdd.setEnabled(enable);
                txtEmailAdd2.setEnabled(enable);
                btnRequestCode.setEnabled(enable);
                associatedPlugins.toggleForm(enable);
            }
        });
    }

    public void clearRegistrationFields() {
        txtPhone.setText("");
        txtFullName.setText("");
        txtEmailAdd.setText("");
        txtEmailAdd2.setText("");
    }

    public String getClipboardContents() {
        String result = "";
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        boolean hasTransferableText
                = (contents != null)
                && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        if (hasTransferableText) {
            try {
                result = (String) contents.getTransferData(DataFlavor.stringFlavor);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    private void loadUserPreferences() {
        Preferences preferences = Main.getPreferences();
        String fName = preferences.get("fname", "");
        txtFullName.setText(fName);
        txtPhone.setText(preferences.get("cnum", ""));
        txtEmailAdd.setText(preferences.get("eadd", ""));
        if (!fName.trim().isEmpty()) {
            txtEmailAdd2.requestFocus();
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
        java.awt.GridBagConstraints gridBagConstraints;

        pnlMain = new javax.swing.JPanel();
        pnlRegistration = new javax.swing.JPanel();
        pnlPersonInfo = new javax.swing.JPanel();
        txtEmailAdd2 = new javax.swing.JTextField();
        txtPhone = new javax.swing.JTextField();
        txtFullName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtEmailAdd = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        lblError = new javax.swing.JLabel();
        pnlPlugins = new javax.swing.JPanel();
        lblUnregistered = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        btnRequestCode = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Registration");
        setPreferredSize(new java.awt.Dimension(450, 425));
        setResizable(false);

        pnlMain.setLayout(new java.awt.BorderLayout());

        pnlRegistration.setLayout(new java.awt.BorderLayout(5, 5));

        pnlPersonInfo.setLayout(new java.awt.GridBagLayout());

        txtEmailAdd2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtEmailAdd2KeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 54;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 5, 2);
        pnlPersonInfo.add(txtEmailAdd2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 54;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 5, 2);
        pnlPersonInfo.add(txtPhone, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 54;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 5, 2);
        pnlPersonInfo.add(txtFullName, gridBagConstraints);

        jLabel1.setText("Re-enter Email:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlPersonInfo.add(jLabel1, gridBagConstraints);

        jLabel2.setText("Phone Number:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlPersonInfo.add(jLabel2, gridBagConstraints);

        jLabel3.setText("Full Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlPersonInfo.add(jLabel3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 54;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 5, 2);
        pnlPersonInfo.add(txtEmailAdd, gridBagConstraints);

        jLabel4.setText("Email Address:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlPersonInfo.add(jLabel4, gridBagConstraints);

        lblError.setForeground(new java.awt.Color(255, 51, 51));
        lblError.setText("Error: Email Address did not match!");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 2;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlPersonInfo.add(lblError, gridBagConstraints);

        pnlRegistration.add(pnlPersonInfo, java.awt.BorderLayout.NORTH);

        pnlPlugins.setLayout(new java.awt.BorderLayout(5, 5));

        lblUnregistered.setForeground(new java.awt.Color(255, 51, 51));
        lblUnregistered.setText("Unregistered Plugins are marked");
        lblUnregistered.setVisible(false);
        pnlPlugins.add(lblUnregistered, java.awt.BorderLayout.SOUTH);

        pnlRegistration.add(pnlPlugins, java.awt.BorderLayout.CENTER);

        pnlMain.add(pnlRegistration, java.awt.BorderLayout.CENTER);

        btnRequestCode.setText("Request Plugin Activation");
        jPanel8.add(btnRequestCode);

        pnlMain.add(jPanel8, java.awt.BorderLayout.SOUTH);

        getContentPane().add(pnlMain, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtEmailAdd2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEmailAdd2KeyReleased
        lblError.setVisible(!txtEmailAdd.getText().equals(txtEmailAdd2.getText()));
    }//GEN-LAST:event_txtEmailAdd2KeyReleased
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRequestCode;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JLabel lblError;
    private javax.swing.JLabel lblUnregistered;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlPersonInfo;
    private javax.swing.JPanel pnlPlugins;
    private javax.swing.JPanel pnlRegistration;
    private javax.swing.JTextField txtEmailAdd;
    private javax.swing.JTextField txtEmailAdd2;
    private javax.swing.JTextField txtFullName;
    private javax.swing.JTextField txtPhone;
    // End of variables declaration//GEN-END:variables
}
