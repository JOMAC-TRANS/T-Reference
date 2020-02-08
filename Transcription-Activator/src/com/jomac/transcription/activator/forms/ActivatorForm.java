package com.jomac.transcription.activator.forms;

import com.hccs.util.ComponentUtils;
import com.hccs.util.Task;
import com.hccs.util.TaskThread;
import com.jomac.transcription.activator.forms.tablemodels.ActivatorAbstractTableModel;
import com.jomac.transcription.activator.model.PluginBean;
import com.jomac.transcription.activator.utility.ActivatorUtility;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

public class ActivatorForm extends javax.swing.JFrame {

    private TableRowSorter<AbstractTableModel> sorter;
    private boolean toggledButton;
    private File selectedFile;
    private String timeAndDate;
    private final ListSelectorPanel associatedPlugins;

    public ActivatorForm() {
        initComponents();
        associatedPlugins = new ListSelectorPanel();
        associatedPlugins.revertTheMarkedValue(true);
//        tblResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pnlPlugins.add(associatedPlugins, BorderLayout.CENTER);
        tblResults.setHighlighters(HighlighterFactory.createAlternateStriping(new Color(235, 235, 235), Color.WHITE));
        tblResults.setGridColor(Color.LIGHT_GRAY);
        tblResults.setSelectionBackground(new Color(50, 100, 255));
        tblResults.setSelectionForeground(Color.WHITE);
    }

    public void initActivatorTable(ActivatorAbstractTableModel model, TableRowSorter tblSorter) {
        tblResults.setModel(model);
        tblResults.setRowSorter(sorter = tblSorter);
    }

    public void addTableSelectionListener(ListSelectionListener l) {
        tblResults.getSelectionModel().addListSelectionListener(l);
    }

    public void addCheckBoxActionListener(ActionListener a) {
        chkShowAllRequest.addActionListener(a);
    }

    public void addTextSearchBoxListener(KeyListener l) {
        txtSearchBox.addKeyListener(l);
    }

    public void addTextSearchFilterListener(KeyListener l) {
        txtSearchFilter.addKeyListener(l);
    }

    public void menuExitAddActionListener(ActionListener a) {
        miExit.addActionListener(a);
    }

    public void refreshAddActionListener(ActionListener a) {
        btnRequestTB.addActionListener(a);
        miRequest.addActionListener(a);
    }

    public void approveAccountActionListener(ActionListener a) {
        btnApprove.addActionListener(a);
        btnApproveTB.addActionListener(a);
        miApprove.addActionListener(a);
    }

    public void disregardAddActionListener(ActionListener a) {
        btnDeny.addActionListener(a);
        btnDenyTB.addActionListener(a);
        miDeny.addActionListener(a);
    }

    public void deleteActionListener(ActionListener a) {
        btnDeleteTB.addActionListener(a);
        miDelete.addActionListener(a);
    }

    public void accountCleanUpActionListener(ActionListener a) {
        btnAccountCleanup.addActionListener(a);
    }

    public void showNewRequestActionListener(ActionListener a) {
        btnNewRequest.addActionListener(a);
    }

    public void setNewRequestSelected() {
        btnNewRequest.setSelected(true);
    }

    public void showUpdateRequestActionListener(ActionListener a) {
        btnUpdateRequest.addActionListener(a);
    }

    public void showApprovedRequestActionListener(ActionListener a) {
        btnShowApproved.addActionListener(a);
    }

    public void showExpiringAccountActionListener(ActionListener a) {
        btnExpiring.addActionListener(a);
    }

    public void showDuplicateAccountActionListener(ActionListener a) {
        btnDuplicate.addActionListener(a);
    }

    public void miAboutAddActionListener(ActionListener a) {
        miAbout.addActionListener(a);
    }

    public int getDetailsSplitPaneLocation() {
        return detailsSplitPane.getDividerLocation();
    }

    public void setDetailsSplitPaneLocation(int val) {
        detailsSplitPane.setDividerLocation(val);
    }

    public int getRightSplitPaneLocation() {
        return rightSplitPane.getDividerLocation();
    }

    public void setRightSplitPaneLocation(int val) {
        rightSplitPane.setDividerLocation(val);
    }

    public void populateAvailablePlugins(List<PluginBean> list) {
        associatedPlugins.setAvailableValues(list.toArray(new PluginBean[list.size()]));
    }

    public void setMarkedPlugins(List<PluginBean> plugins) {
        List<Object> objPlugins = new ArrayList<>();
        objPlugins.addAll(plugins);
        associatedPlugins.setMarkedValues(objPlugins);
    }

    public void setAssociatedPlugins(List<PluginBean> plugins) {
        associatedPlugins.setSelectedValues(plugins.toArray(new PluginBean[plugins.size()]));
    }

    public void clearAssociatedPlugins() {
        associatedPlugins.removeSelectedValues();
    }

    public void clearPlugins() {
        associatedPlugins.clear();
    }

    public List<PluginBean> getAPlugins() {
        List<PluginBean> list = new ArrayList<>();
        for (Object obj : associatedPlugins.getSelectedValues()) {
            list.add((PluginBean) obj);
        }
        return list;
    }

    public int getAssociatedPlugins() {
        int total = 0;
        for (PluginBean pb : getAPlugins()) {
            total += pb.getPluginValue();
        }
        return total;
    }

    public void toggleActivatorForm(boolean enable, boolean buttons) {
        btnSave.setEnabled(enable && tblResults.getModel().getRowCount() != 0);
        btnRequestTB.setEnabled(enable);
        miRequest.setEnabled(enable);
        tblResults.setEnabled(enable);
        txtSearchBox.setEnabled(enable);
        txtSearchFilter.setEnabled(enable);
        btnNewRequest.setEnabled(enable);
        btnUpdateRequest.setEnabled(enable);
        btnShowApproved.setEnabled(enable);
        btnDuplicate.setEnabled(enable);
        btnExpiring.setEnabled(enable);
        btnAccountCleanup.setEnabled(enable);

        if (!enable) {
            toggleShowAllRequest(false);

        }
        if (buttons) {
            toggleButtons(enable);
        }
    }

    public boolean checkSearchPanel() {
        return (pnlSearchContainer.getComponentCount() != 0);
    }

    public void removeSearchPanel() {
        pnlSearchContainer.removeAll();
    }

    public void setSearchPanelFocus() {
        txtSearchBox.requestFocus();
    }

    public void addFilterPanel() {
        pnlSearchContainer.add(pnlSearchPanel, java.awt.BorderLayout.CENTER);
    }

    public void toggleShowAllRequest(boolean enable) {
        chkShowAllRequest.setEnabled(enable);
    }

    public void toggleButtons(boolean enable) {
        if (enable) {
            enable = sorter.getViewRowCount() > 0;
        }
        toggledButton = enable;
        btnDeny.setEnabled(enable);
        miDeny.setEnabled(enable);
        btnDenyTB.setEnabled(enable);

        btnApprove.setEnabled(enable);
        miApprove.setEnabled(enable);
        btnApproveTB.setEnabled(enable);
        btnDeleteTB.setEnabled(enable);
        miDelete.setEnabled(enable);
        associatedPlugins.toggleForm(enable);
    }

    public boolean toggledButton() {
        return toggledButton;
    }

    public boolean isSendEmailButtonSelected() {
        return chkEmailNoti.isSelected();
    }

    public void setSendEmailButtonSelected(boolean val) {
        chkEmailNoti.setSelected(val);
    }

    public void clearSearchBox() {
        txtSearchBox.setText("");
    }

    public void clearStringFilter() {
        txtSearchFilter.setText("");
    }

    public String getSearchString() {
        return txtSearchBox.getText();
    }

    public String getFilterString() {
        return txtSearchFilter.getText();
    }

    public void setRowSorter(RowFilter r) {
        sorter.setRowFilter(r);
    }

    public void removeSorter() {
        sorter.setRowFilter(null);
    }

    public void setRowCountLabel() {
        removeSorter();
        tblResults.revalidate();
        lblRowCount.setText("Row Count: " + sorter.getViewRowCount());
    }

    public ActivatorAbstractTableModel getTableModel() {
        return (ActivatorAbstractTableModel) tblResults.getModel();
    }

    public boolean showAllRequest() {
        return chkShowAllRequest.isSelected();
    }

    public void setApproveSaveButton(String val) {
        btnApprove.setText(val);
        btnApproveTB.setText(val);
    }

    public int getRowModel() {
        int row = tblResults.getSelectedRow();
        if (row != -1) {
            row = tblResults.convertRowIndexToModel(row);
        }
        return row;
    }

    public List<Integer> getSelectedRowsModel() {
        List<Integer> rows = new ArrayList<>();
        int[] xy = tblResults.getSelectedRows();
        if (xy.length > 0) {
            for (int i = 0; i < xy.length; i++) {
                rows.add(tblResults.convertRowIndexToModel(xy[i]));
            }
        }
        return rows;
    }

    public void clearTableSelection() {
        tblResults.clearSelection();
        tblResults.repaint();
    }

    public int showCleanUpMessage(String info) {
        Object[] options = {"Yes To All", "Yes", "No", "Cancel"};
        String msg = "Do you want to delete this account?" + info;
        return JOptionPane.showOptionDialog(this, msg, "Account Clean-Up",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);
    }

    public void showAccountCleanUp(boolean valid, int total, int deleted) {
        String message = valid ? "Account Cleaned!\n\n"
                + "Total: " + total + "\nDeleted: " + deleted
                : "Failed to Clean account/s!";

        JOptionPane.showMessageDialog(
                this, message, "90-days Clean-up",
                valid ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }

    public void showIPCheckerWarning() {
        final Component comp = this;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(
                        comp, "IP is not in the philippines block", "IP Checker",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    public void showConnectionError() {
        JOptionPane.showMessageDialog(
                this, "Connection Error Occured", "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public void showQuotaExceeded() {
        JOptionPane.showMessageDialog(
                this, "Daily sending quota exceeded", "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public void showActivationMessage(boolean valid) {
        String message = valid ? "Account Approved!"
                : "Account Failed!";

        JOptionPane.showMessageDialog(
                this, message, "Activation",
                valid ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }

    public void showActivationMessage(boolean valid, boolean email) {
        String message = valid ? "Account Approved!"
                : "Account Failed!";

        message = message + (email ? "\nEmail Sent!" : "\nEmail Failed!");
        JOptionPane.showMessageDialog(
                this, message, "Activation",
                valid ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }

    public void showSavedMessage(boolean valid) {
        String message = valid ? "Account Saved!"
                : "Account Failed!";

        JOptionPane.showMessageDialog(
                this, message, "Activation",
                valid ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }

    public void showSavedMessage(boolean valid, boolean email) {
        String message = valid ? "Account Saved!"
                : "Account Failed!";
        message = message + (email ? "\nEmail Sent!" : "\nEmail Failed!");
        JOptionPane.showMessageDialog(
                this, message, "Activation",
                valid ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }

    public boolean exitConfirmation() {
        return (JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to Exit?",
                "Exit",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION);
    }

    public boolean discardConfirmation(boolean single) {
        return (JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to disregard "
                + (single ? "this" : "these") + " request?",
                "Disregarding",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION);
    }

    public boolean deleteConfirmation(boolean single) {
        return (JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to DELETE "
                + (single ? "this" : "these") + " request?"
                + "\nNote: This action can not be undone",
                "Deleting",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION);
    }

    public void setlabelStatus(String value) {
        lblStatus.setText(value.isEmpty() ? " " : value);
    }

    public boolean createPathFile() {
        SimpleDateFormat timeCreated = new SimpleDateFormat("MMMMM dd yyyy");
        timeAndDate = timeCreated.format(Calendar.getInstance().getTime());
        File defaultFile;
        int index = 1;

        do {
            String newFile = timeAndDate + ((index == 1) ? "" : "_" + String.valueOf(index)) + ".csv";

            defaultFile = new File(fcSave.getCurrentDirectory(), newFile);
            index++;
        } while (defaultFile.exists());

        fcSave.setSelectedFile(defaultFile);

        if (fcSave.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = fcSave.getSelectedFile();
            boolean cont = true;
            int yesno;

            if (selectedFile.exists()) {
                yesno = JOptionPane.showConfirmDialog(
                        null,
                        "File already exists? Overwrite file?",
                        "File exists",
                        JOptionPane.YES_NO_OPTION);

                cont = (yesno == JOptionPane.YES_OPTION);
            }

            if (cont) {
                if (!selectedFile.getName().endsWith(".csv")) {
                    selectedFile = new File(
                            selectedFile.getParent(),
                            selectedFile.getName() + ".csv");
                }
            }
            return true;
        }
        return false;
    }

    public void setFullName(String val) {
        txtFullName.setText(val);
    }

    public void setProductInfo(String val) {
        txtProductInfo.setText(val);
    }

    public void setPhoneInfo(String val) {
        txtPhone.setText(val);
    }

    public void setEmailAddInfo(String val) {
        txtEmailAdd.setText(val);
    }

    public void setIPAddInfo(String val) {
        txtIPAdd.setText(val);
        btnChecker1.setEnabled(!val.isEmpty());
        btnChecker2.setEnabled(!val.isEmpty());

    }

    public void setOSInfo(String val) {
        txtOS.setText(val);
    }

    public void setJavaInfo(String val) {
        txtJava.setText(val);
    }

    public void setComputerInfo(String val) {
        txtComputer.setText(val);
    }

    public void setProfileInfo(String val) {
        txtProfile.setText(val);
    }

    public void setHDInfo(String val) {
        txtHD.setText(val);
    }

    public void setMotherBoradInfo(String val) {
        txtMobo.setText(val);
    }

    public void setExpirationDate(String val) {
        txtExpiration.setText(val);
    }

    public void setLastLoginDate(String val) {
        txtLastLogin.setText(val);
    }

    public Date getExpirationDate() {
        return dpExpiration.getDate();
    }

    public void setExpirationDate(Date date) {
        dpExpiration.setDate(date);
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

        fcSave = new javax.swing.JFileChooser();
        btnFilterGroup = new javax.swing.ButtonGroup();
        pnlSearchPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtSearchBox = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        splitPane = new javax.swing.JSplitPane();
        jPanel6 = new javax.swing.JPanel();
        rightSplitPane = new javax.swing.JSplitPane();
        jPanel13 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        lblRowCount = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        chkShowAllRequest = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblResults = new org.jdesktop.swingx.JXTable();
        jPanel10 = new javax.swing.JPanel();
        pnlSearchContainer = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtSearchFilter = new javax.swing.JTextField();
        jPanel14 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        btnApprove = new javax.swing.JButton();
        btnDeny = new javax.swing.JButton();
        jPanel17 = new javax.swing.JPanel();
        detailsSplitPane = new javax.swing.JSplitPane();
        jPanel19 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtHD = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtMobo = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtFullName = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtPhone = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtEmailAdd = new javax.swing.JTextField();
        txtJava = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtOS = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtIPAdd = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtProfile = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtComputer = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jPanel21 = new javax.swing.JPanel();
        btnChecker1 = new javax.swing.JButton();
        btnChecker2 = new javax.swing.JButton();
        pnlPlugins = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel20 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        txtProductInfo = new javax.swing.JTextField();
        jPanel24 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        txtLastLogin = new javax.swing.JTextField();
        pnlExpiration = new javax.swing.JPanel();
        jPanel22 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        txtExpiration = new javax.swing.JTextField();
        jPanel23 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        dpExpiration = new com.hccs.forms.components.ZDatePicker("MM-dd-yyyy","##-##-####",'_');
        jPanel2 = new javax.swing.JPanel();
        chkEmailNoti = new javax.swing.JCheckBox();
        jScrollPane4 = new javax.swing.JScrollPane();
        jToolBar2 = new javax.swing.JToolBar();
        btnNewRequest = new javax.swing.JToggleButton();
        btnUpdateRequest = new javax.swing.JToggleButton();
        btnShowApproved = new javax.swing.JToggleButton();
        btnExpiring = new javax.swing.JToggleButton();
        btnDuplicate = new javax.swing.JToggleButton();
        btnAccountCleanup = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        btnRequestTB = new javax.swing.JButton();
        btnDenyTB = new javax.swing.JButton();
        btnDeleteTB = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnApproveTB = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuFile = new javax.swing.JMenu();
        miRequest = new javax.swing.JMenuItem();
        miApprove = new javax.swing.JMenuItem();
        miDeny = new javax.swing.JMenuItem();
        miDelete = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        miExit = new javax.swing.JMenuItem();
        mnuHelp = new javax.swing.JMenu();
        miAbout = new javax.swing.JMenuItem();

        fcSave.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        fcSave.setDialogTitle("Save File");

        pnlSearchPanel.setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Search:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        jPanel3.add(jLabel1, gridBagConstraints);

        txtSearchBox.setPreferredSize(new java.awt.Dimension(6, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        jPanel3.add(txtSearchBox, gridBagConstraints);

        pnlSearchPanel.add(jPanel3, java.awt.BorderLayout.CENTER);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel1.setLayout(new java.awt.BorderLayout());

        splitPane.setDividerSize(2);
        splitPane.setEnabled(false);

        jPanel6.setLayout(new java.awt.BorderLayout());

        rightSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanel13.setLayout(new java.awt.BorderLayout());

        jPanel11.setLayout(new java.awt.GridLayout(1, 0, 5, 2));

        lblRowCount.setText("Row Count:");
        jPanel11.add(lblRowCount);

        jPanel9.setLayout(new java.awt.BorderLayout());

        chkShowAllRequest.setText("Show All Requests");
        chkShowAllRequest.setEnabled(false);
        jPanel9.add(chkShowAllRequest, java.awt.BorderLayout.EAST);

        jPanel11.add(jPanel9);

        jPanel13.add(jPanel11, java.awt.BorderLayout.SOUTH);

        jScrollPane1.setViewportView(tblResults);

        jPanel13.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel10.setLayout(new java.awt.GridLayout(1, 0));

        pnlSearchContainer.setLayout(new java.awt.BorderLayout());
        jPanel10.add(pnlSearchContainer);

        jPanel16.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jLabel2.setText("Filter:");
        jPanel16.add(jLabel2);

        txtSearchFilter.setEnabled(false);
        txtSearchFilter.setPreferredSize(new java.awt.Dimension(200, 25));
        jPanel16.add(txtSearchFilter);

        jPanel10.add(jPanel16);

        jPanel13.add(jPanel10, java.awt.BorderLayout.PAGE_START);

        rightSplitPane.setLeftComponent(jPanel13);

        jPanel14.setLayout(new java.awt.BorderLayout());

        btnApprove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/activator/resources/16x16/apply.png"))); // NOI18N
        btnApprove.setText("Approve ");
        btnApprove.setEnabled(false);
        btnApprove.setPreferredSize(new java.awt.Dimension(135, 25));
        jPanel8.add(btnApprove);

        btnDeny.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/activator/resources/16x16/cancel.png"))); // NOI18N
        btnDeny.setText("Deny");
        btnDeny.setEnabled(false);
        btnDeny.setPreferredSize(new java.awt.Dimension(110, 25));
        jPanel8.add(btnDeny);

        jPanel14.add(jPanel8, java.awt.BorderLayout.NORTH);

        jPanel17.setLayout(new java.awt.BorderLayout());

        jPanel19.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel19.setLayout(new java.awt.BorderLayout());

        jPanel12.setLayout(new java.awt.GridBagLayout());

        jLabel5.setText("HardDisk SN:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(jLabel5, gridBagConstraints);

        txtHD.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(txtHD, gridBagConstraints);

        jLabel6.setText("MotherBoard SN:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(jLabel6, gridBagConstraints);

        txtMobo.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(txtMobo, gridBagConstraints);

        jLabel7.setText("Full Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(jLabel7, gridBagConstraints);

        txtFullName.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(txtFullName, gridBagConstraints);

        jLabel8.setText("Phone Number:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(jLabel8, gridBagConstraints);

        txtPhone.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(txtPhone, gridBagConstraints);

        jLabel9.setText("Email Address:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(jLabel9, gridBagConstraints);

        txtEmailAdd.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(txtEmailAdd, gridBagConstraints);

        txtJava.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(txtJava, gridBagConstraints);

        jLabel10.setText("Java Version:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(jLabel10, gridBagConstraints);

        txtOS.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(txtOS, gridBagConstraints);

        jLabel11.setText("Operating System:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(jLabel11, gridBagConstraints);

        txtIPAdd.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(txtIPAdd, gridBagConstraints);

        jLabel12.setText("Public IP:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(jLabel12, gridBagConstraints);

        txtProfile.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(txtProfile, gridBagConstraints);

        jLabel13.setText("Profile Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(jLabel13, gridBagConstraints);

        txtComputer.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(txtComputer, gridBagConstraints);

        jLabel14.setText("Computer Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(jLabel14, gridBagConstraints);

        jPanel21.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 0));

        btnChecker1.setText("IP Checker 1");
        btnChecker1.setEnabled(false);
        btnChecker1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChecker1ActionPerformed(evt);
            }
        });
        jPanel21.add(btnChecker1);

        btnChecker2.setText("IP Checker 2");
        btnChecker2.setEnabled(false);
        btnChecker2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChecker2ActionPerformed(evt);
            }
        });
        jPanel21.add(btnChecker2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel12.add(jPanel21, gridBagConstraints);

        jPanel19.add(jPanel12, java.awt.BorderLayout.NORTH);

        detailsSplitPane.setLeftComponent(jPanel19);

        pnlPlugins.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pnlPlugins.setLayout(new java.awt.BorderLayout(5, 10));

        jPanel18.setLayout(new java.awt.BorderLayout(5, 5));

        jPanel5.setLayout(new java.awt.GridLayout(1, 0, 5, 5));

        jPanel20.setLayout(new java.awt.GridLayout(0, 1));

        jLabel16.setText("Product Info");
        jPanel20.add(jLabel16);

        txtProductInfo.setEditable(false);
        jPanel20.add(txtProductInfo);

        jPanel5.add(jPanel20);

        jPanel24.setLayout(new java.awt.GridLayout(0, 1));

        jLabel18.setText("Last Login");
        jPanel24.add(jLabel18);

        txtLastLogin.setEditable(false);
        jPanel24.add(txtLastLogin);

        jPanel5.add(jPanel24);

        jPanel18.add(jPanel5, java.awt.BorderLayout.NORTH);

        pnlExpiration.setLayout(new java.awt.GridLayout(1, 0, 5, 5));

        jPanel22.setLayout(new java.awt.GridLayout(0, 1));

        jLabel17.setText("Expiration Date");
        jPanel22.add(jLabel17);

        txtExpiration.setEditable(false);
        jPanel22.add(txtExpiration);

        pnlExpiration.add(jPanel22);

        jPanel23.setLayout(new java.awt.GridLayout(0, 1));

        jLabel19.setText("New Expiration Date");
        jPanel23.add(jLabel19);
        jPanel23.add(dpExpiration);

        pnlExpiration.add(jPanel23);

        jPanel18.add(pnlExpiration, java.awt.BorderLayout.CENTER);

        pnlPlugins.add(jPanel18, java.awt.BorderLayout.NORTH);

        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 0));

        chkEmailNoti.setText("Send Email Notification");
        jPanel2.add(chkEmailNoti);

        pnlPlugins.add(jPanel2, java.awt.BorderLayout.SOUTH);

        detailsSplitPane.setRightComponent(pnlPlugins);

        jPanel17.add(detailsSplitPane, java.awt.BorderLayout.CENTER);

        jPanel14.add(jPanel17, java.awt.BorderLayout.CENTER);

        rightSplitPane.setRightComponent(jPanel14);

        jPanel6.add(rightSplitPane, java.awt.BorderLayout.CENTER);

        splitPane.setRightComponent(jPanel6);

        jScrollPane4.setMinimumSize(new java.awt.Dimension(104, 250));
        jScrollPane4.setPreferredSize(new java.awt.Dimension(105, 364));

        jToolBar2.setFloatable(false);
        jToolBar2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar2.setRollover(true);

        btnFilterGroup.add(btnNewRequest);
        btnNewRequest.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/activator/resources/32x32/bookmark_add.png"))); // NOI18N
        btnNewRequest.setText("New Request");
        btnNewRequest.setFocusable(false);
        btnNewRequest.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNewRequest.setMaximumSize(new java.awt.Dimension(100, 60));
        btnNewRequest.setMinimumSize(new java.awt.Dimension(100, 60));
        btnNewRequest.setPreferredSize(new java.awt.Dimension(100, 60));
        btnNewRequest.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(btnNewRequest);

        btnFilterGroup.add(btnUpdateRequest);
        btnUpdateRequest.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/activator/resources/32x32/bookmark_toolbar.png"))); // NOI18N
        btnUpdateRequest.setText("Update Account");
        btnUpdateRequest.setFocusable(false);
        btnUpdateRequest.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnUpdateRequest.setMaximumSize(new java.awt.Dimension(100, 60));
        btnUpdateRequest.setMinimumSize(new java.awt.Dimension(100, 60));
        btnUpdateRequest.setPreferredSize(new java.awt.Dimension(100, 60));
        btnUpdateRequest.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(btnUpdateRequest);

        btnFilterGroup.add(btnShowApproved);
        btnShowApproved.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/activator/resources/32x32/bookmark.png"))); // NOI18N
        btnShowApproved.setText("Approved Accounts");
        btnShowApproved.setFocusable(false);
        btnShowApproved.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnShowApproved.setMaximumSize(new java.awt.Dimension(100, 60));
        btnShowApproved.setMinimumSize(new java.awt.Dimension(100, 60));
        btnShowApproved.setPreferredSize(new java.awt.Dimension(100, 60));
        btnShowApproved.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(btnShowApproved);

        btnFilterGroup.add(btnExpiring);
        btnExpiring.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/activator/resources/32x32/bookmarks_list_add.png"))); // NOI18N
        btnExpiring.setText("Expiring Account");
        btnExpiring.setFocusable(false);
        btnExpiring.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExpiring.setMaximumSize(new java.awt.Dimension(100, 60));
        btnExpiring.setMinimumSize(new java.awt.Dimension(100, 60));
        btnExpiring.setPreferredSize(new java.awt.Dimension(100, 60));
        btnExpiring.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(btnExpiring);

        btnFilterGroup.add(btnDuplicate);
        btnDuplicate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/activator/resources/32x32/kpager.png"))); // NOI18N
        btnDuplicate.setText("Show Duplicate");
        btnDuplicate.setFocusable(false);
        btnDuplicate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDuplicate.setMaximumSize(new java.awt.Dimension(100, 60));
        btnDuplicate.setMinimumSize(new java.awt.Dimension(100, 60));
        btnDuplicate.setPreferredSize(new java.awt.Dimension(100, 60));
        btnDuplicate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(btnDuplicate);

        btnAccountCleanup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/activator/resources/32x32/kalarm.png"))); // NOI18N
        btnAccountCleanup.setText("Clean-Up");
        btnAccountCleanup.setToolTipText("90-days Clean-up");
        btnAccountCleanup.setFocusable(false);
        btnAccountCleanup.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAccountCleanup.setMaximumSize(new java.awt.Dimension(100, 60));
        btnAccountCleanup.setMinimumSize(new java.awt.Dimension(100, 60));
        btnAccountCleanup.setPreferredSize(new java.awt.Dimension(100, 60));
        btnAccountCleanup.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(btnAccountCleanup);

        jScrollPane4.setViewportView(jToolBar2);

        splitPane.setLeftComponent(jScrollPane4);

        jPanel1.add(splitPane, java.awt.BorderLayout.CENTER);

        jPanel7.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lblStatus.setText(" ");
        jPanel7.add(lblStatus);

        jPanel1.add(jPanel7, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        jToolBar1.setRollover(true);

        btnRequestTB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/activator/resources/32x32/bottom.png"))); // NOI18N
        btnRequestTB.setText("Get Requests");
        btnRequestTB.setEnabled(false);
        btnRequestTB.setFocusable(false);
        btnRequestTB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRequestTB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnRequestTB);

        btnDenyTB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/activator/resources/32x32/cancel.png"))); // NOI18N
        btnDenyTB.setText("Deny");
        btnDenyTB.setEnabled(false);
        btnDenyTB.setFocusable(false);
        btnDenyTB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDenyTB.setMaximumSize(new java.awt.Dimension(71, 57));
        btnDenyTB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnDenyTB);

        btnDeleteTB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/activator/resources/32x32/edittrash.png"))); // NOI18N
        btnDeleteTB.setText("Delete");
        btnDeleteTB.setEnabled(false);
        btnDeleteTB.setFocusable(false);
        btnDeleteTB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDeleteTB.setMaximumSize(new java.awt.Dimension(71, 57));
        btnDeleteTB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnDeleteTB);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/activator/resources/32x32/save.png"))); // NOI18N
        btnSave.setText("Save to CSV");
        btnSave.setToolTipText("Save");
        btnSave.setEnabled(false);
        btnSave.setFocusable(false);
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSave.setMaximumSize(new java.awt.Dimension(71, 57));
        btnSave.setMinimumSize(new java.awt.Dimension(67, 57));
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave);

        btnApproveTB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/activator/resources/32x32/apply.png"))); // NOI18N
        btnApproveTB.setText("Approve ");
        btnApproveTB.setEnabled(false);
        btnApproveTB.setFocusable(false);
        btnApproveTB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnApproveTB.setMaximumSize(new java.awt.Dimension(71, 57));
        btnApproveTB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnApproveTB);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.PAGE_START);

        mnuFile.setText("File");

        miRequest.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/activator/resources/16x16/bottom.png"))); // NOI18N
        miRequest.setText("Get Requests");
        miRequest.setEnabled(false);
        mnuFile.add(miRequest);

        miApprove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/activator/resources/16x16/apply.png"))); // NOI18N
        miApprove.setText("Approve");
        miApprove.setEnabled(false);
        mnuFile.add(miApprove);

        miDeny.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/activator/resources/16x16/cancel.png"))); // NOI18N
        miDeny.setText("Deny");
        miDeny.setEnabled(false);
        mnuFile.add(miDeny);

        miDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/activator/resources/16x16/edittrash.png"))); // NOI18N
        miDelete.setText("Delete");
        miDelete.setEnabled(false);
        mnuFile.add(miDelete);
        mnuFile.add(jSeparator1);

        miExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/activator/resources/16x16/exit.png"))); // NOI18N
        miExit.setMnemonic('X');
        miExit.setText("Exit");
        mnuFile.add(miExit);

        jMenuBar1.add(mnuFile);

        mnuHelp.setText("Help");

        miAbout.setText("About");
        mnuHelp.add(miAbout);

        jMenuBar1.add(mnuHelp);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if (createPathFile()) {
            new TaskThread(new Task() {
                @Override
                public void start() {
                    lblStatus.setText("Saving file...");
                    toggleButtons(false);
                }

                @Override
                public void doInBackground() {
                    try {
                        ComponentUtils.exportHTBLToCsv(
                                tblResults,
                                selectedFile,
                                "Generated on: " + new java.util.Date());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void finished() {
                    lblStatus.setText(" ");
                    toggleButtons(true);
                }
            }).start();
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnChecker1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChecker1ActionPerformed
        ActivatorUtility.openLink("http://geoiplookup.net/ip/" + txtIPAdd.getText());
    }//GEN-LAST:event_btnChecker1ActionPerformed

    private void btnChecker2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChecker2ActionPerformed
        ActivatorUtility.openLink("http://www.ip-tracker.org/locator/ip-lookup.php?ip=" + txtIPAdd.getText());
    }//GEN-LAST:event_btnChecker2ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAccountCleanup;
    private javax.swing.JButton btnApprove;
    private javax.swing.JButton btnApproveTB;
    private javax.swing.JButton btnChecker1;
    private javax.swing.JButton btnChecker2;
    private javax.swing.JButton btnDeleteTB;
    private javax.swing.JButton btnDeny;
    private javax.swing.JButton btnDenyTB;
    private javax.swing.JToggleButton btnDuplicate;
    private javax.swing.JToggleButton btnExpiring;
    private javax.swing.ButtonGroup btnFilterGroup;
    private javax.swing.JToggleButton btnNewRequest;
    private javax.swing.JButton btnRequestTB;
    private javax.swing.JButton btnSave;
    private javax.swing.JToggleButton btnShowApproved;
    private javax.swing.JToggleButton btnUpdateRequest;
    private javax.swing.JCheckBox chkEmailNoti;
    private javax.swing.JCheckBox chkShowAllRequest;
    private javax.swing.JSplitPane detailsSplitPane;
    private com.hccs.forms.components.ZDatePicker dpExpiration;
    private javax.swing.JFileChooser fcSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JLabel lblRowCount;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JMenuItem miAbout;
    private javax.swing.JMenuItem miApprove;
    private javax.swing.JMenuItem miDelete;
    private javax.swing.JMenuItem miDeny;
    private javax.swing.JMenuItem miExit;
    private javax.swing.JMenuItem miRequest;
    private javax.swing.JMenu mnuFile;
    private javax.swing.JMenu mnuHelp;
    private javax.swing.JPanel pnlExpiration;
    private javax.swing.JPanel pnlPlugins;
    private javax.swing.JPanel pnlSearchContainer;
    private javax.swing.JPanel pnlSearchPanel;
    private javax.swing.JSplitPane rightSplitPane;
    private javax.swing.JSplitPane splitPane;
    private org.jdesktop.swingx.JXTable tblResults;
    private javax.swing.JTextField txtComputer;
    private javax.swing.JTextField txtEmailAdd;
    private javax.swing.JTextField txtExpiration;
    private javax.swing.JTextField txtFullName;
    private javax.swing.JTextField txtHD;
    private javax.swing.JTextField txtIPAdd;
    private javax.swing.JTextField txtJava;
    private javax.swing.JTextField txtLastLogin;
    private javax.swing.JTextField txtMobo;
    private javax.swing.JTextField txtOS;
    private javax.swing.JTextField txtPhone;
    private javax.swing.JTextField txtProductInfo;
    private javax.swing.JTextField txtProfile;
    private javax.swing.JTextField txtSearchBox;
    private javax.swing.JTextField txtSearchFilter;
    // End of variables declaration//GEN-END:variables
}
