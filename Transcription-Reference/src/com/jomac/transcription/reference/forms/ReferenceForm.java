/*
 * ReferenceForm.java
 *
 * Created on July 24, 2012, 11:42 AM
 */
package com.jomac.transcription.reference.forms;

import com.hccs.util.*;
import com.jomac.transcription.reference.Main;
import com.jomac.transcription.reference.ReferenceFileLink;
import com.jomac.transcription.reference.controller.TRPlugin;
import com.jomac.transcription.reference.forms.tablemodels.ReferenceAbstractTableModel;
import com.jomac.transcription.reference.forms.tablemodels.ReferenceTableModel;
import com.jomac.transcription.reference.jpa.models.DictatorBean;
import com.jomac.transcription.reference.jpa.models.SpecificBean;
import com.jomac.transcription.reference.queries.*;
import com.jomac.transcription.reference.utilities.ComponentUtil;
import com.jomac.transcription.reference.utilities.FileUtility;
import com.jomac.transcription.reference.utilities.Formatter;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
//import org.h2.util.Profiler;

public class ReferenceForm extends javax.swing.JFrame {

    private File dictionaryPath;
    private String pluginName;
    private int documentFSize, sytemFSize;
    private List<Integer> workTypes;
    private DictatorBean dictator;
    private SpecificDialog accntSDialog;
    private SpecificDialog WTSDialog;
    private SpecificBean wtSpecificBean;
    private TableRowSorter<ReferenceAbstractTableModel> sorter;

    public ReferenceForm() {

        initComponents();
        initComboBoxValue();
        AutoCompleteDecorator.decorate(cmbDictator);

        txtQuery.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                boolean valid = true;

                String query = txtQuery.getText().trim();
                if ((query.contains("+") && query.length() == 1)
                        || query.contains("++")) {
                    valid = false;
                } else {
                    Pattern p = Pattern.compile("[*]+[1-5][^\\d]");
                    for (String x : query.split("[+]")) {
                        if (x.trim().startsWith("\"") && x.trim().endsWith("\"")
                                && (x.replaceAll("[^*]", "").length() == 1)
                                && (x.contains(" * ") || (((Matcher) p.matcher(x)).find()))) {
                            continue;
                        } else if (x.trim().isEmpty()
                                || (x.contains("*") && x.trim().length() == 1)
                                || x.contains("*") && x.trim().contains(" ")
                                || x.replaceAll("[^*]", "").length() > 2
                                || x.contains("**") || x.contains("\"")) {
                            valid = false;
                            break;
                        }
                    }
                }
                btnGo.setEnabled(valid);
                txtQuery.setForeground(valid ? Color.black : Color.red);
            }
        });

        final JTextField editor2 = (JTextField) cmbDictator.getEditor().getEditorComponent();
        editor2.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                editor2.setForeground(cmbDictator.getSelectedIndex() > 0 ? Color.black : Color.red);
                if (e.getKeyCode() == 10) {
                    setDictatorWorkTypes();
                } else if ((e.getKeyCode() == 8 || e.getKeyCode() == 127) && txtQuery.getText().isEmpty()) {
                    initWorkType();
                }
            }
        });

        editor2.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (editor2.getText().isEmpty()) {
                    initWorkType();
                } else if (cmbDictator.getSelectedItem() instanceof DictatorBean) {
                    setDictatorWorkTypes();
                }
            }
        });
        dictionaryPath = new File("C:\\Program Files\\JOMAC Drugs\\Transcription-Dictionary.exe");
        if (!dictionaryPath.exists()) {
            File tmpFile = new File("C:\\Program Files (x86)\\JOMAC Drugs\\Transcription-Dictionary.exe");
            if (tmpFile.exists()) {
                dictionaryPath = tmpFile;
            }
        }
        btnJDrugs.setVisible(dictionaryPath.exists());
        jMenuBar1.add(pnlSample);
    }

    public void initReferenceTable(ReferenceTableModel tblModel) {
        sorter = new TableRowSorter<ReferenceAbstractTableModel>(tblModel);
        tblResults.setModel(tblModel);
        tblResults.setRowSorter(sorter);
        tblResults.getColumnModel().getColumn(1).setMaxWidth(50);
        tblResults.getColumnModel().getColumn(1).setPreferredWidth(40);
        tblResults.getColumnModel().getColumn(2).setMaxWidth(60);
        tblResults.getColumnModel().getColumn(2).setPreferredWidth(53);
        tblResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ComponentUtil.createAlternateHighlighter(tblResults);
    }

    public boolean validateQuery() {
        String value = txtQuery.getText();
        return !(value.trim().isEmpty() || value.trim().length() == 0
                || !btnGo.isEnabled() || value.replaceAll("[+]", "").isEmpty());
    }

    public String getSearchQuery() {
        return txtQuery.getText().trim();
    }

    public String getPluginName() {
        return pluginName;
    }

    public int getDocumentFSize() {
        return documentFSize;
    }

    public void setDocumentFSize(int documentFSize) {
        this.documentFSize = documentFSize;
    }

    public int getSytemFSize() {
        return sytemFSize;
    }

    public void setSytemFSize(int sytemFSize) {
        this.sytemFSize = sytemFSize;
    }

    public void setDocumentDisplay(String text) {
        epDisplay.setText(text);
    }

    public void setDocumentScroll(int val) {
        jScrollPane2.getVerticalScrollBar().setValue(val);
    }

    public int getDocumentFontSize() {
        return documentFSize;
    }

    public Object getSelectedDictator() {
        return cmbDictator.getSelectedItem();
    }

    public Object getSearchLimitItem() {
        return cmbLimit.getSelectedItem();
    }

    public Object getSelectedWorkType() {
        return cmbWorkType.getSelectedItem();
    }

    public int getSearchLimitIndex() {
        return cmbLimit.getSelectedIndex();
    }

    public void setSearchLimit(int val) {
        cmbLimit.setSelectedIndex(val);
    }

    public void setStatusMsg(final String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                lblMessage.setText(msg);
            }
        });
    }

    public void setResultsMsg(String msg) {
        lblResults.setText(msg);
    }

    public int getSelectedRow() {
        int row = tblResults.getSelectedRow();
        if (row != -1) {
            row = tblResults.convertRowIndexToModel(row);
        }
        return row;
    }

    public void refreshTable() {
        tblResults.repaint();
        tblResults.revalidate();
        sorter.setRowFilter(null);
        txtQuery.requestFocus();
    }

    public int getSplitPane1Location() {
        return jSplitPane1.getDividerLocation();
    }

    public int getSplitPane2Location() {
        return jSplitPane2.getDividerLocation();
    }

    public void setSplitPane1Location(int location) {
        jSplitPane1.setDividerLocation(location);
    }

    public void setSplitPane2Location(int location) {
        jSplitPane2.setDividerLocation(location);
    }

    public void setReferenceAccount(String pluginName) {
        switch (pluginName) {
            case "reference_B":
                btnBay.setSelected(true);
                break;
            case "reference_C":
                btnCentral.setSelected(true);
                break;
            case "reference_F":
                btnFlint.setSelected(true);
                break;
            case "reference_G":
                btnGSamaritan.setSelected(true);
                break;
            case "reference_H":
                btnHackettstown.setSelected(true);
                break;
            case "reference_L":
                btnLapeer.setSelected(true);
                break;
            case "reference_M":
                btnMacomb.setSelected(true);
                break;
            case "reference_O":
                btnOakland.setSelected(true);
                break;
            case "reference_S":
                btnSeton.setSelected(true);
                break;
            case "reference_SPEC3":
                btnSpec3.setSelected(true);
                break;
            case "reference_HS":
                btnHospitalSpecific.setSelected(true);
                break;
        }
    }

    public void showUnActivatedProgram() {
        String content = "Deactivated Program\n";

        JOptionPane.showMessageDialog(
                this,
                content,
                "Deactivated TransRef",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void showErrorDialog(String errorLog) {
        JOptionPane.showMessageDialog(
                this,
                errorLog,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public void showAccountDialog(String content) {
        if (JOptionPane.showConfirmDialog(
                this,
                content + "\n\nDo you want to select other plugin/s?\n",
                (content.contains("expired") ? "T.Reference Plug-in Expiration" : "Error"),
                JOptionPane.ERROR_MESSAGE) == JOptionPane.YES_OPTION) {
            //selecting other 
            AccountDialog ad = new AccountDialog(this, true);
            ad.initButtons();
            ad.setSelectedPlugin(Main.getDBAccount());
            ad.setVisible(true);
            setReferenceAccount(ad.getSelectedPlugin());
        } else {
            Main.getController().terminateApp();
        }
    }

    public void toggleRequestButton(boolean enable) {
        miReDownload.setEnabled(enable);
        miRequest.setEnabled(enable);
        miUpdateAccnt.setEnabled(enable);
    }

    public void toggleNavs(boolean enabled) {
        btnNext.setEnabled(enabled);
        btnNextDoc.setEnabled(enabled);
        btnPrev.setEnabled(enabled);
        btnPrevDoc.setEnabled(enabled);
    }

    public void toggleComponents(boolean enabled) {
        btnJDrugs.setEnabled(enabled);
        btnDictators.setEnabled(enabled);
        btnPsychDrugs.setEnabled(enabled);
        btnGuidelines.setEnabled(enabled);
        btnNormalLabs.setEnabled(enabled);
        tblResults.setEnabled(enabled);
        cmbDictator.setEnabled(enabled);
        txtQuery.setEnabled(enabled);
        btnGo.setEnabled(enabled);
        cmbWorkType.setEnabled(enabled);
        cmbLimit.setEnabled(enabled);

    }

    public void toggleToolbar(boolean enabled) {
        mnuAccount.setEnabled(enabled);
        if (enabled) {
            initButtons();
        } else {
            togglePluginsButton(btnBay, mirBay, false);
            togglePluginsButton(btnCentral, mirCentral, false);
            togglePluginsButton(btnFlint, mirFlit, false);
            togglePluginsButton(btnGSamaritan, mirGSamaritan, false);
            togglePluginsButton(btnHackettstown, mirHackettstown, false);
            togglePluginsButton(btnLapeer, mirLapeer, false);
            togglePluginsButton(btnMacomb, mirMacomb, false);
            togglePluginsButton(btnOakland, mirOakland, false);
            togglePluginsButton(btnSeton, mirSeton, false);
            togglePluginsButton(btnSpec3, mirSpec3, false);
            togglePluginsButton(btnHospitalSpecific, mirHospitalSpecific, false);
        }
    }

    public void initButtons() {
        int associated = Main.getAssociatedKey();
        if (associated == 0) {
            return;
        }

        String selectedPlugin = Main.getDBAccount();
        JToggleButton selectedButton = null, tmpToggleButton;
        JRadioButtonMenuItem tmpBMenuItem;
        JToolBar.Separator tmpSeparator;

        for (TRPlugin x : TRPlugin.INSTANCE.getAllPlugins()) {
            if (!((x.getValue() & associated) == x.getValue())) {
                continue;
            }

            tmpToggleButton = null;
            tmpBMenuItem = null;
            tmpSeparator = null;

            switch (x) {
                case B:
                    tmpToggleButton = btnBay;
                    tmpBMenuItem = mirBay;
                    tmpSeparator = spBay;
                    break;
                case C:
                    tmpToggleButton = btnCentral;
                    tmpBMenuItem = mirCentral;
                    tmpSeparator = spCentral;
                    break;
                case F:
                    tmpToggleButton = btnFlint;
                    tmpBMenuItem = mirFlit;
                    tmpSeparator = spFlint;
                    break;
//                case G:
//                    tmpToggleButton = btnGSamaritan;
//                    tmpBMenuItem = mirGSamaritan;
//                    tmpSeparator = spGS;
//                    break;
//                case H:
//                    tmpToggleButton = btnHackettstown;
//                    tmpBMenuItem = mirHackettstown;
//                    tmpSeparator = spHacketts;
//                    break;
                case L:
                    tmpToggleButton = btnLapeer;
                    tmpBMenuItem = mirLapeer;
                    tmpSeparator = spLapeer;
                    break;
//                case M:
//                    tmpToggleButton = btnMacomb;
//                    tmpBMenuItem = mirMacomb;
//                    tmpSeparator = spMacomb;
//                    break;
                case O:
                    tmpToggleButton = btnOakland;
                    tmpBMenuItem = mirOakland;
                    tmpSeparator = spOakland;
                    break;
                case S:
                    tmpToggleButton = btnSeton;
                    tmpBMenuItem = mirSeton;
                    tmpSeparator = spSeton;
                    break;
//                case SPEC3:
//                    tmpToggleButton = btnSpec3;
//                    tmpBMenuItem = mirSpec3;
//                    tmpSeparator = spSpec3;
//                    break;
//                case HS:
//                    tmpToggleButton = btnHospitalSpecific;
//                    tmpBMenuItem = mirHospitalSpecific;
//                    tmpSeparator = null;
//                    break;
            }

            if (tmpToggleButton != null) {
                if (tmpBMenuItem != null) {
                    setPluginButtonVisible(tmpToggleButton, tmpBMenuItem, tmpSeparator, true);
                    togglePluginsButton(tmpToggleButton, tmpBMenuItem,
                            (x.getValue() & associated) == x.getValue());
                }
                if (selectedPlugin.equals("reference_" + x.name())) {
                    selectedButton = tmpToggleButton;
                }
            }
        }
        if (selectedButton != null && !selectedButton.isEnabled()) {
            System.out.println("toggle!");
            toggleComponents(false);
        }
    }

    private void togglePluginsButton(JToggleButton jtog, JRadioButtonMenuItem jmnu, boolean value) {
        jtog.setEnabled(value);
        jmnu.setEnabled(value);
    }

    private void setPluginButtonVisible(JToggleButton jtog,
            JRadioButtonMenuItem jmnu,
            JToolBar.Separator spt, boolean value) {
        jtog.setVisible(value);
        jmnu.setVisible(value);
        if (spt != null) {
            spt.setVisible(value);
        }
    }

    private void initComboBoxValue() {
        for (int xx = 50; xx <= 1000; xx = xx + 50) {
            cmbLimit.addItem(xx);
        }
        cmbLimit.addItem("NO LIMIT");
    }

    private void setDictatorWorkTypes() {
        Object selected = cmbDictator.getSelectedItem();
        if (selected != null && selected instanceof DictatorBean) {
            if (dictator == null || !dictator.equals(selected)) {
                dictator = (DictatorBean) selected;
            } else {
                return;
            }
            cmbWorkType.removeAllItems();
            cmbWorkType.addItem("ALL");
            for (int xx : new DWTypeQueries().findWorkTypeByDictator(dictator)) {
                cmbWorkType.addItem(xx);
            }
        } else {
            initWorkType();
        }
    }

    public void initWorkType() {
        //<editor-fold defaultstate="collapsed" desc="WorkType Initialization">
        new TaskThread(new Task() {
            @Override
            public void initialize() {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        cmbWorkType.removeAllItems();
                        cmbWorkType.addItem("loading....");
                    }
                });
            }

            @Override
            public void doInBackground() {
                if (workTypes == null || workTypes.isEmpty()) {
                    workTypes = new WorkTypeQueries().getWorkTypeList();
                }
            }

            @Override
            public void finished() {
                cmbWorkType.removeAllItems();
                cmbWorkType.addItem("ALL");
                for (int xx : workTypes) {
                    cmbWorkType.addItem(xx);
                }
            }
        }).start();
        //</editor-fold>
    }

    public void initDictator() {
        //<editor-fold defaultstate="collapsed" desc="Dictator Initialization">
        new TaskThread(new Task() {
            List<DictatorBean> dictators;

            @Override
            public void initialize() {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        cmbDictator.addItem("loading....");
                    }
                });
            }

            @Override
            public void doInBackground() {
                dictators = new DictatorQueries().getAllDictator();
            }

            @Override
            public void finished() {
                cmbDictator.removeAllItems();
                cmbDictator.addItem("");
                for (DictatorBean xx : dictators) {
                    cmbDictator.addItem(xx);
                }
                dictators.clear();
            }
        }).start();
        //</editor-fold>
    }

    public void initSpecific() {
        //<editor-fold defaultstate="collapsed" desc="Specific Initialization">
        new TaskThread(new Task() {
            SpecificBean sBean;

            @Override
            public void doInBackground() {
                sBean = new SpecificQueries().getSpecific(null);
            }

            @Override
            public void finished() {
                if (sBean != null) {
                    if (accntSDialog == null) {
                        accntSDialog = new SpecificDialog(Main.getController().getMainFrame(), false);
                    }
                    accntSDialog.setEditorText(sBean.getDocument());
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            if (btnBay.isSelected()) {
                                btnBay.setForeground(Color.red);
                            }
                            if (btnCentral.isSelected()) {
                                btnCentral.setForeground(Color.red);
                            }
                            if (btnFlint.isSelected()) {
                                btnFlint.setForeground(Color.red);
                            }
                            if (btnGSamaritan.isSelected()) {
                                btnGSamaritan.setForeground(Color.red);
                            }
                            if (btnHackettstown.isSelected()) {
                                btnHackettstown.setForeground(Color.red);
                            }
                            if (btnLapeer.isSelected()) {
                                btnLapeer.setForeground(Color.red);
                            }
                            if (btnMacomb.isSelected()) {
                                btnMacomb.setForeground(Color.red);
                            }
                            if (btnOakland.isSelected()) {
                                btnOakland.setForeground(Color.red);
                            }
                            if (btnSeton.isSelected()) {
                                btnSeton.setForeground(Color.red);
                            }
                            if (btnSpec3.isSelected()) {
                                btnSpec3.setForeground(Color.red);
                            }
                            if (btnHospitalSpecific.isSelected()) {
                                btnHospitalSpecific.setForeground(Color.red);
                            }
                        }
                    });
                }
            }
        }).start();
        //</editor-fold>
    }

    public boolean showWarning() {
        return (cmbLimit.getSelectedItem() instanceof String
                && cmbWorkType.getSelectedIndex() == 0
                && cmbDictator.getSelectedIndex() <= 0);
    }

    public void setPluginName(String plugin) {
        pluginName = plugin;
    }

    public void setSystemFontSize(int val) {
        sytemFSize = val;
    }

    public void setDocumentFontSize(int val) {
        documentFSize = val;
    }

    public void setFormFontSize() {
        setFormFontSize(null);
    }

    public void setFormFontSize(String content) {
        Font font = new Font("Tahoma", Font.PLAIN, sytemFSize);

        if (!epDisplay.getText().isEmpty() && content != null) {
            epDisplay.setText(Formatter.getInstance().beanToHtml(content, documentFSize));
        }

        lblDictator.setFont(font);
        cmbDictator.setFont(font);
        lblSearch.setFont(font);
        txtQuery.setFont(font);
        lblWorkType.setFont(font);
        cmbWorkType.setFont(font);
        lblScope.setFont(font);
        cmbLimit.setFont(font);

        lblResults.setFont(font);
        tblResults.setFont(font);
        tblResults.getTableHeader().setFont(font);
    }

    public void addListSelectionListener(ListSelectionListener l) {
        tblResults.getSelectionModel().addListSelectionListener(l);
    }

    public void addSearchActionListener(ActionListener a) {
        btnGo.addActionListener(a);
        txtQuery.addActionListener(a);
    }

    public void addCloseActionListener(ActionListener a) {
        miClose.addActionListener(a);
    }

    public void addAboutActionListener(ActionListener a) {
        miAbout.addActionListener(a);
    }

    public void addNextActionListener(ActionListener a) {
        btnNext.addActionListener(a);
    }

    public void addPrevActionListener(ActionListener a) {
        btnPrev.addActionListener(a);
    }

    public void addPreferenceActionListener(ActionListener a) {
        miPreference.addActionListener(a);
    }

    public void addRequestListener(ActionListener a) {
        miRequest.addActionListener(a);
    }

    public void addReDownloadListener(ActionListener a) {
        miReDownload.addActionListener(a);
    }

    public void addUpdateAccountListener(ActionListener a) {
        miUpdateAccnt.addActionListener(a);
    }

    public void addLimitItemStateListener(ItemListener i) {
        cmbLimit.addItemListener(i);
    }

    public void addWorkTypeStateListener(ItemListener i) {
        cmbWorkType.addItemListener(i);
    }

    public void addDocumentDisplayKeyListener(KeyAdapter keyAdapter) {
        epDisplay.addKeyListener(keyAdapter);
    }

    public void setDisplayCaret(int startLoc) {
        setDisplayCaret(startLoc, -1);
    }

    public void setDisplayCaret(int startLoc, int endLoc) {
        epDisplay.requestFocus();
        epDisplay.setCaretPosition(startLoc);
        if (endLoc > -1) {
            epDisplay.moveCaretPosition(startLoc + endLoc);
        }
    }

    public int getDisplaySelectionStart() {
        return epDisplay.getSelectionStart();
    }

    public void setWorkTypeSpecific(SpecificBean sBean) {
        wtSpecificBean = sBean;
        lblWorkType.setForeground(sBean == null ? Color.BLACK : Color.RED);
    }

    public String getDisplayWord() {
        try {
            return epDisplay.getDocument().getText(0, epDisplay.getDocument().getLength()).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setAccountChanged(String plugin) {
        if (pluginName == null) {
            return;
        }

        pluginName = plugin;
        Main.getController().savePreference();
        Main.restartApplication();
    }

    private void openReferenceFile(String fileName) {
        File eFileDir = new File(Main.getReferenceFilesPath());

        if (eFileDir.exists() && eFileDir.list().length > 0) {
            boolean found = false;
            for (File x : eFileDir.listFiles()) {
                if (x.getName().contains(fileName)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                try {
                    FileUtility.pipeStream(eFileDir.getPath(), fileName);
                } catch (Exception ex) {
                }
            }
        } else {
            eFileDir.mkdirs();
            try {
                FileUtility.pipeStream(eFileDir.getPath(), ReferenceFileLink.TR_DICTATORS.toString());
                FileUtility.pipeStream(eFileDir.getPath(), ReferenceFileLink.DRUGS_PHRASES.toString());
                FileUtility.pipeStream(eFileDir.getPath(), ReferenceFileLink.NORMALLABS.toString());
                FileUtility.pipeStream(eFileDir.getPath(), ReferenceFileLink.GUIDELINES.toString());
            } catch (Exception e) {
            }
        }
        openFile(eFileDir.getPath() + File.separator + fileName);
    }

    private void openFile(String filePath) {
        final File batch = new File(Main.getBatchPath());
        final List<String> params = new ArrayList<>();

        if (!batch.exists() || batch.list().length != 1) {
            if (batch.exists()) {
                for (File x : batch.listFiles()) {
                    x.delete();
                }
            } else {
                batch.mkdirs();
            }
            try {
                FileUtility.pipeStream(batch.getPath(), "fileAccess.cmd");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        params.add("cmd");
        params.add("/c");
        params.add("fileAccess");
        params.add(batch.getPath());
        params.add(filePath);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessBuilder pb = new ProcessBuilder(params);
                    pb.redirectErrorStream(true);
                    pb.directory(batch);
                    Process pr = pb.start();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                        if (line.startsWith("The process cannot access the file")) {
                            JOptionPane.showMessageDialog(Main.getController().getMainFrame(),
                                    "The process cannot access the file because it is being used by another process.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void nextDocument() {
        btnNextDocActionPerformed(null);
    }

    public void prevDocument() {
        btnPrevDocActionPerformed(null);
    }

    public void centerLineInScrollPane() {
        Container container = SwingUtilities.getAncestorOfClass(JViewport.class, epDisplay);

        if (container == null) {
            return;
        }

        try {
            Rectangle r = epDisplay.modelToView(epDisplay.getCaretPosition());
            JViewport viewport = (JViewport) container;

            int extentWidth = viewport.getExtentSize().width;
            int viewWidth = viewport.getViewSize().width;

            int x = Math.max(0, r.x - (extentWidth / 2));
            x = Math.min(x, viewWidth - extentWidth);

            int extentHeight = viewport.getExtentSize().height;
            int viewHeight = viewport.getViewSize().height;

            int y = Math.max(0, r.y - (extentHeight / 2));
            y = Math.min(y, viewHeight - extentHeight);

            viewport.setViewPosition(new java.awt.Point(x, y));
        } catch (Exception ble) {
            ble.printStackTrace();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        popMenu = new javax.swing.JPopupMenu();
        miCopySelected = new javax.swing.JMenuItem();
        groupAccount = new javax.swing.ButtonGroup();
        pnlSample = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        btnJDrugs = new javax.swing.JButton();
        btnGuidelines = new javax.swing.JButton();
        btnDictators = new javax.swing.JButton();
        btnPsychDrugs = new javax.swing.JButton();
        btnNormalLabs = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        btnSamples = new javax.swing.JButton();
        tbAccounts = new javax.swing.JToolBar();
        btnBay = new javax.swing.JToggleButton();
        spBay = new javax.swing.JToolBar.Separator();
        btnCentral = new javax.swing.JToggleButton();
        spCentral = new javax.swing.JToolBar.Separator();
        btnFlint = new javax.swing.JToggleButton();
        spFlint = new javax.swing.JToolBar.Separator();
        btnGSamaritan = new javax.swing.JToggleButton();
        spGS = new javax.swing.JToolBar.Separator();
        btnHackettstown = new javax.swing.JToggleButton();
        spHacketts = new javax.swing.JToolBar.Separator();
        btnLapeer = new javax.swing.JToggleButton();
        spLapeer = new javax.swing.JToolBar.Separator();
        btnMacomb = new javax.swing.JToggleButton();
        spMacomb = new javax.swing.JToolBar.Separator();
        btnOakland = new javax.swing.JToggleButton();
        spOakland = new javax.swing.JToolBar.Separator();
        btnSeton = new javax.swing.JToggleButton();
        spSeton = new javax.swing.JToolBar.Separator();
        btnSpec3 = new javax.swing.JToggleButton();
        spSpec3 = new javax.swing.JToolBar.Separator();
        btnHospitalSpecific = new javax.swing.JToggleButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        pnlSearch = new javax.swing.JPanel();
        btnGo = new javax.swing.JButton();
        lblSearch = new javax.swing.JLabel();
        txtQuery = new javax.swing.JTextField();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel5 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        lblResults = new javax.swing.JLabel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel8 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        lblScope = new javax.swing.JLabel();
        cmbLimit = new javax.swing.JComboBox();
        lblWorkType = new javax.swing.JLabel();
        cmbWorkType = new javax.swing.JComboBox();
        lblDictator = new javax.swing.JLabel();
        cmbDictator = new javax.swing.JComboBox();
        jPanel12 = new javax.swing.JPanel();
        scrollPanel = new javax.swing.JScrollPane();
        tblResults = new org.jdesktop.swingx.JXTable();
        jPanel13 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        epDisplay = new javax.swing.JEditorPane();
        jPanel15 = new javax.swing.JPanel();
        btnPrevDoc = new javax.swing.JButton();
        btnPrev = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        btnNextDoc = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        lblMessage = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuFile = new javax.swing.JMenu();
        miClose = new javax.swing.JMenuItem();
        mnuEdit = new javax.swing.JMenu();
        miPreference = new javax.swing.JMenuItem();
        mnuAccount = new javax.swing.JMenu();
        mirBay = new javax.swing.JRadioButtonMenuItem();
        mirCentral = new javax.swing.JRadioButtonMenuItem();
        mirFlit = new javax.swing.JRadioButtonMenuItem();
        mirGSamaritan = new javax.swing.JRadioButtonMenuItem();
        mirHackettstown = new javax.swing.JRadioButtonMenuItem();
        mirLapeer = new javax.swing.JRadioButtonMenuItem();
        mirMacomb = new javax.swing.JRadioButtonMenuItem();
        mirOakland = new javax.swing.JRadioButtonMenuItem();
        mirSeton = new javax.swing.JRadioButtonMenuItem();
        mirSpec3 = new javax.swing.JRadioButtonMenuItem();
        mirHospitalSpecific = new javax.swing.JRadioButtonMenuItem();
        jSeparator11 = new javax.swing.JPopupMenu.Separator();
        miUpdateAccnt = new javax.swing.JMenuItem();
        miRequest = new javax.swing.JMenuItem();
        miReDownload = new javax.swing.JMenuItem();
        jSeparator12 = new javax.swing.JPopupMenu.Separator();
        mnuHelp1 = new javax.swing.JMenu();
        miAbout = new javax.swing.JMenuItem();

        miCopySelected.setText("Copy to clipboard");
        miCopySelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miCopySelectedActionPerformed(evt);
            }
        });
        popMenu.add(miCopySelected);

        pnlSample.setLayout(new javax.swing.BoxLayout(pnlSample, javax.swing.BoxLayout.LINE_AXIS));

        jPanel6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 15, 0));

        btnJDrugs.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnJDrugs.setForeground(new java.awt.Color(0, 0, 255));
        btnJDrugs.setText("JDrugs");
        btnJDrugs.setToolTipText("Launch JOMAC Dictionary");
        btnJDrugs.setEnabled(false);
        btnJDrugs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnJDrugsActionPerformed(evt);
            }
        });
        jPanel6.add(btnJDrugs);

        btnGuidelines.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnGuidelines.setForeground(new java.awt.Color(0, 0, 255));
        btnGuidelines.setText("Guidelines");
        btnGuidelines.setToolTipText("Guidelines and Instructions");
        btnGuidelines.setEnabled(false);
        btnGuidelines.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuidelinesActionPerformed(evt);
            }
        });
        jPanel6.add(btnGuidelines);

        btnDictators.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnDictators.setForeground(new java.awt.Color(0, 0, 255));
        btnDictators.setText("Dictators");
        btnDictators.setToolTipText("Hospital's Dictators");
        btnDictators.setEnabled(false);
        btnDictators.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDictatorsActionPerformed(evt);
            }
        });
        jPanel6.add(btnDictators);

        btnPsychDrugs.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnPsychDrugs.setForeground(new java.awt.Color(0, 0, 255));
        btnPsychDrugs.setText("Psych Drugs/Phrases");
        btnPsychDrugs.setToolTipText("Psych Drugs and Phrases");
        btnPsychDrugs.setEnabled(false);
        btnPsychDrugs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPsychDrugsActionPerformed(evt);
            }
        });
        jPanel6.add(btnPsychDrugs);

        btnNormalLabs.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnNormalLabs.setForeground(new java.awt.Color(0, 0, 255));
        btnNormalLabs.setText("Normal Labs");
        btnNormalLabs.setToolTipText("Normal Lab Values");
        btnNormalLabs.setEnabled(false);
        btnNormalLabs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNormalLabsActionPerformed(evt);
            }
        });
        jPanel6.add(btnNormalLabs);

        pnlSample.add(jPanel6);

        jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));

        btnSamples.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/reference/resources/16x16/messagebox_info.png"))); // NOI18N
        btnSamples.setToolTipText("ExampleQuery");
        btnSamples.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSamplesActionPerformed(evt);
            }
        });
        jPanel4.add(btnSamples);

        pnlSample.add(jPanel4);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        tbAccounts.setRollover(true);

        groupAccount.add(btnBay);
        btnBay.setText("B");
        btnBay.setEnabled(false);
        btnBay.setFocusable(false);
        btnBay.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnBay.setMaximumSize(new java.awt.Dimension(75, 25));
        btnBay.setPreferredSize(new java.awt.Dimension(40, 25));
        btnBay.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnBay.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                btnBayStateChanged(evt);
            }
        });
        btnBay.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnBayMouseReleased(evt);
            }
        });
        tbAccounts.add(btnBay);
        btnBay.setVisible(false);
        tbAccounts.add(spBay);
        spBay.setVisible(false);

        groupAccount.add(btnCentral);
        btnCentral.setText("C");
        btnCentral.setEnabled(false);
        btnCentral.setFocusable(false);
        btnCentral.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCentral.setMaximumSize(new java.awt.Dimension(75, 25));
        btnCentral.setPreferredSize(new java.awt.Dimension(40, 25));
        btnCentral.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCentral.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                btnCentralStateChanged(evt);
            }
        });
        btnCentral.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnCentralMouseReleased(evt);
            }
        });
        tbAccounts.add(btnCentral);
        btnCentral.setVisible(false);
        tbAccounts.add(spCentral);
        spCentral.setVisible(false);

        groupAccount.add(btnFlint);
        btnFlint.setText("F");
        btnFlint.setEnabled(false);
        btnFlint.setFocusable(false);
        btnFlint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFlint.setMaximumSize(new java.awt.Dimension(75, 25));
        btnFlint.setPreferredSize(new java.awt.Dimension(40, 25));
        btnFlint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnFlint.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                btnFlintStateChanged(evt);
            }
        });
        btnFlint.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnFlintMouseReleased(evt);
            }
        });
        tbAccounts.add(btnFlint);
        btnFlint.setVisible(false);
        tbAccounts.add(spFlint);
        spFlint.setVisible(false);

        groupAccount.add(btnGSamaritan);
        btnGSamaritan.setText("G");
        btnGSamaritan.setEnabled(false);
        btnGSamaritan.setFocusable(false);
        btnGSamaritan.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGSamaritan.setMaximumSize(new java.awt.Dimension(75, 25));
        btnGSamaritan.setPreferredSize(new java.awt.Dimension(40, 25));
        btnGSamaritan.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGSamaritan.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                btnGSamaritanStateChanged(evt);
            }
        });
        btnGSamaritan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnGSamaritanMouseReleased(evt);
            }
        });
        tbAccounts.add(btnGSamaritan);
        btnGSamaritan.setVisible(false);
        tbAccounts.add(spGS);
        spGS.setVisible(false);

        groupAccount.add(btnHackettstown);
        btnHackettstown.setText("H");
        btnHackettstown.setEnabled(false);
        btnHackettstown.setFocusable(false);
        btnHackettstown.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnHackettstown.setMaximumSize(new java.awt.Dimension(75, 25));
        btnHackettstown.setPreferredSize(new java.awt.Dimension(40, 25));
        btnHackettstown.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnHackettstown.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                btnHackettstownStateChanged(evt);
            }
        });
        btnHackettstown.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnHackettstownMouseReleased(evt);
            }
        });
        tbAccounts.add(btnHackettstown);
        btnHackettstown.setVisible(false);
        tbAccounts.add(spHacketts);
        spHacketts.setVisible(false);

        groupAccount.add(btnLapeer);
        btnLapeer.setText("L");
        btnLapeer.setEnabled(false);
        btnLapeer.setFocusable(false);
        btnLapeer.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnLapeer.setMaximumSize(new java.awt.Dimension(75, 25));
        btnLapeer.setPreferredSize(new java.awt.Dimension(40, 25));
        btnLapeer.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnLapeer.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                btnLapeerStateChanged(evt);
            }
        });
        btnLapeer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnLapeerMouseReleased(evt);
            }
        });
        tbAccounts.add(btnLapeer);
        btnLapeer.setVisible(false);
        tbAccounts.add(spLapeer);
        spLapeer.setVisible(false);

        groupAccount.add(btnMacomb);
        btnMacomb.setText("M");
        btnMacomb.setEnabled(false);
        btnMacomb.setFocusable(false);
        btnMacomb.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMacomb.setMaximumSize(new java.awt.Dimension(75, 25));
        btnMacomb.setPreferredSize(new java.awt.Dimension(40, 25));
        btnMacomb.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMacomb.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                btnMacombStateChanged(evt);
            }
        });
        btnMacomb.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnMacombMouseReleased(evt);
            }
        });
        tbAccounts.add(btnMacomb);
        btnMacomb.setVisible(false);
        tbAccounts.add(spMacomb);
        spMacomb.setVisible(false);

        groupAccount.add(btnOakland);
        btnOakland.setText("O");
        btnOakland.setEnabled(false);
        btnOakland.setFocusable(false);
        btnOakland.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnOakland.setMaximumSize(new java.awt.Dimension(75, 25));
        btnOakland.setPreferredSize(new java.awt.Dimension(40, 25));
        btnOakland.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOakland.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                btnOaklandStateChanged(evt);
            }
        });
        btnOakland.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnOaklandMouseReleased(evt);
            }
        });
        tbAccounts.add(btnOakland);
        btnOakland.setVisible(false);
        tbAccounts.add(spOakland);
        spOakland.setVisible(false);

        groupAccount.add(btnSeton);
        btnSeton.setText("S");
        btnSeton.setEnabled(false);
        btnSeton.setFocusable(false);
        btnSeton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSeton.setMaximumSize(new java.awt.Dimension(75, 25));
        btnSeton.setPreferredSize(new java.awt.Dimension(40, 25));
        btnSeton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSeton.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                btnSetonStateChanged(evt);
            }
        });
        btnSeton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnSetonMouseReleased(evt);
            }
        });
        tbAccounts.add(btnSeton);
        btnSeton.setVisible(false);
        tbAccounts.add(spSeton);
        spSeton.setVisible(false);

        groupAccount.add(btnSpec3);
        btnSpec3.setText("SPEC3");
        btnSpec3.setEnabled(false);
        btnSpec3.setFocusable(false);
        btnSpec3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSpec3.setMaximumSize(new java.awt.Dimension(75, 25));
        btnSpec3.setPreferredSize(new java.awt.Dimension(40, 25));
        btnSpec3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSpec3.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                btnSpec3StateChanged(evt);
            }
        });
        btnSpec3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnSpec3MouseReleased(evt);
            }
        });
        tbAccounts.add(btnSpec3);
        btnSpec3.setVisible(false);
        tbAccounts.add(spSpec3);
        spSpec3.setVisible(false);

        groupAccount.add(btnHospitalSpecific);
        btnHospitalSpecific.setText("HS");
        btnHospitalSpecific.setEnabled(false);
        btnHospitalSpecific.setFocusable(false);
        btnHospitalSpecific.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnHospitalSpecific.setMaximumSize(new java.awt.Dimension(75, 25));
        btnHospitalSpecific.setPreferredSize(new java.awt.Dimension(40, 25));
        btnHospitalSpecific.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnHospitalSpecific.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                btnHospitalSpecificStateChanged(evt);
            }
        });
        btnHospitalSpecific.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnHospitalSpecificMouseReleased(evt);
            }
        });
        tbAccounts.add(btnHospitalSpecific);
        btnHospitalSpecific.setVisible(false);

        getContentPane().add(tbAccounts, java.awt.BorderLayout.PAGE_START);

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel2.setLayout(new java.awt.BorderLayout(0, 4));

        jPanel3.setLayout(new java.awt.BorderLayout(0, 4));

        pnlSearch.setLayout(new java.awt.BorderLayout(8, 0));

        btnGo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/reference/resources/16x16/lens.png"))); // NOI18N
        btnGo.setMnemonic('G');
        btnGo.setEnabled(false);
        btnGo.setFocusable(false);
        btnGo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGo.setMaximumSize(new java.awt.Dimension(20, 20));
        btnGo.setMinimumSize(new java.awt.Dimension(20, 20));
        btnGo.setPreferredSize(new java.awt.Dimension(25, 25));
        btnGo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        pnlSearch.add(btnGo, java.awt.BorderLayout.EAST);

        lblSearch.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblSearch.setText("Search:");
        pnlSearch.add(lblSearch, java.awt.BorderLayout.WEST);

        txtQuery.setEnabled(false);
        pnlSearch.add(txtQuery, java.awt.BorderLayout.CENTER);

        jPanel3.add(pnlSearch, java.awt.BorderLayout.NORTH);

        jSplitPane1.setBorder(null);
        jSplitPane1.setDividerSize(3);
        jSplitPane1.setResizeWeight(0.2);
        jSplitPane1.setOneTouchExpandable(true);

        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel9.setPreferredSize(new java.awt.Dimension(4, 44));
        jPanel9.setLayout(new java.awt.BorderLayout());

        lblResults.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblResults.setText(" ");
        jPanel9.add(lblResults, java.awt.BorderLayout.WEST);

        jPanel5.add(jPanel9, java.awt.BorderLayout.SOUTH);

        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanel8.setLayout(new java.awt.BorderLayout());

        jPanel11.setLayout(new java.awt.GridBagLayout());

        lblScope.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblScope.setText("Limit:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 2;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 4);
        jPanel11.add(lblScope, gridBagConstraints);

        cmbLimit.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cmbLimit.setEnabled(false);
        cmbLimit.setMinimumSize(new java.awt.Dimension(70, 20));
        cmbLimit.setPreferredSize(new java.awt.Dimension(150, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 2;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel11.add(cmbLimit, gridBagConstraints);

        lblWorkType.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblWorkType.setText("Work Type:");
        lblWorkType.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblWorkTypeMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 2;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 4);
        jPanel11.add(lblWorkType, gridBagConstraints);

        cmbWorkType.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cmbWorkType.setEnabled(false);
        cmbWorkType.setMinimumSize(new java.awt.Dimension(70, 20));
        cmbWorkType.setPreferredSize(new java.awt.Dimension(150, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 2;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel11.add(cmbWorkType, gridBagConstraints);

        lblDictator.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblDictator.setText("Dictator:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 2;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 4);
        jPanel11.add(lblDictator, gridBagConstraints);

        cmbDictator.setEditable(true);
        cmbDictator.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cmbDictator.setEnabled(false);
        cmbDictator.setPreferredSize(new java.awt.Dimension(200, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 2;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel11.add(cmbDictator, gridBagConstraints);

        jPanel8.add(jPanel11, java.awt.BorderLayout.CENTER);

        jSplitPane2.setLeftComponent(jPanel8);

        jPanel12.setLayout(new java.awt.BorderLayout());

        tblResults.setEnabled(false);
        tblResults.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        scrollPanel.setViewportView(tblResults);

        jPanel12.add(scrollPanel, java.awt.BorderLayout.CENTER);

        jSplitPane2.setRightComponent(jPanel12);

        jPanel5.add(jSplitPane2, java.awt.BorderLayout.CENTER);

        jSplitPane1.setLeftComponent(jPanel5);

        jPanel13.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jScrollPane2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        epDisplay.setEditable(false);
        epDisplay.setBorder(null);
        epDisplay.setContentType("text/html"); // NOI18N
        epDisplay.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                epDisplayMouseReleased(evt);
            }
        });
        jScrollPane2.setViewportView(epDisplay);

        jPanel13.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanel15.setPreferredSize(new java.awt.Dimension(221, 40));
        jPanel15.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 1));

        btnPrevDoc.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnPrevDoc.setMnemonic('p');
        btnPrevDoc.setText("Prev. Doc");
        btnPrevDoc.setEnabled(false);
        btnPrevDoc.setPreferredSize(new java.awt.Dimension(90, 40));
        btnPrevDoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrevDocActionPerformed(evt);
            }
        });
        jPanel15.add(btnPrevDoc);

        btnPrev.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnPrev.setEnabled(false);
        btnPrev.setLabel("<");
        btnPrev.setPreferredSize(new java.awt.Dimension(49, 40));
        jPanel15.add(btnPrev);

        btnNext.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnNext.setEnabled(false);
        btnNext.setLabel(">");
        btnNext.setPreferredSize(new java.awt.Dimension(49, 40));
        jPanel15.add(btnNext);

        btnNextDoc.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnNextDoc.setMnemonic('n');
        btnNextDoc.setText("Next Doc");
        btnNextDoc.setEnabled(false);
        btnNextDoc.setPreferredSize(new java.awt.Dimension(90, 40));
        btnNextDoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextDocActionPerformed(evt);
            }
        });
        jPanel15.add(btnNextDoc);

        jPanel13.add(jPanel15, java.awt.BorderLayout.SOUTH);

        jSplitPane1.setRightComponent(jPanel13);

        jPanel3.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        jPanel2.add(jPanel3, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lblMessage.setText(" ");
        jPanel1.add(lblMessage);

        jPanel2.add(jPanel1, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        mnuFile.setMnemonic('F');
        mnuFile.setText("File");

        miClose.setMnemonic('C');
        miClose.setText("Close");
        mnuFile.add(miClose);

        jMenuBar1.add(mnuFile);

        mnuEdit.setText("Edit");

        miPreference.setText("Preference");
        mnuEdit.add(miPreference);

        jMenuBar1.add(mnuEdit);

        mnuAccount.setText("Account");
        mnuAccount.setEnabled(false);

        mirBay.setText("B");
        mirBay.setEnabled(false);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, btnBay, org.jdesktop.beansbinding.ELProperty.create("${selected}"), mirBay, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mnuAccount.add(mirBay);
        mirBay.setVisible(false);

        mirCentral.setText("C");
        mirCentral.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, btnCentral, org.jdesktop.beansbinding.ELProperty.create("${selected}"), mirCentral, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mnuAccount.add(mirCentral);
        mirCentral.setVisible(false);

        mirFlit.setText("F");
        mirFlit.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, btnFlint, org.jdesktop.beansbinding.ELProperty.create("${selected}"), mirFlit, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mnuAccount.add(mirFlit);
        mirFlit.setVisible(false);

        mirGSamaritan.setText("G");
        mirGSamaritan.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, btnGSamaritan, org.jdesktop.beansbinding.ELProperty.create("${selected}"), mirGSamaritan, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mnuAccount.add(mirGSamaritan);
        mirGSamaritan.setVisible(false);

        mirHackettstown.setText("H");
        mirHackettstown.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, btnHackettstown, org.jdesktop.beansbinding.ELProperty.create("${selected}"), mirHackettstown, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mnuAccount.add(mirHackettstown);
        mirHackettstown.setVisible(false);

        mirLapeer.setText("L");
        mirLapeer.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, btnLapeer, org.jdesktop.beansbinding.ELProperty.create("${selected}"), mirLapeer, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mnuAccount.add(mirLapeer);
        mirLapeer.setVisible(false);

        mirMacomb.setText("M");
        mirMacomb.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, btnMacomb, org.jdesktop.beansbinding.ELProperty.create("${selected}"), mirMacomb, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mnuAccount.add(mirMacomb);
        mirMacomb.setVisible(false);

        mirOakland.setText("O");
        mirOakland.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, btnOakland, org.jdesktop.beansbinding.ELProperty.create("${selected}"), mirOakland, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mnuAccount.add(mirOakland);
        mirOakland.setVisible(false);

        mirSeton.setText("S");
        mirSeton.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, btnSeton, org.jdesktop.beansbinding.ELProperty.create("${selected}"), mirSeton, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mnuAccount.add(mirSeton);
        mirSeton.setVisible(false);

        mirSpec3.setText("SPEC3");
        mirSpec3.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, btnSpec3, org.jdesktop.beansbinding.ELProperty.create("${selected}"), mirSpec3, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mnuAccount.add(mirSpec3);
        mirSpec3.setVisible(false);

        mirHospitalSpecific.setText("HS");
        mirHospitalSpecific.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, btnHospitalSpecific, org.jdesktop.beansbinding.ELProperty.create("${selected}"), mirHospitalSpecific, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mnuAccount.add(mirHospitalSpecific);
        mirHospitalSpecific.setVisible(false);
        mnuAccount.add(jSeparator11);

        miUpdateAccnt.setText("Update Account");
        miUpdateAccnt.setEnabled(false);
        mnuAccount.add(miUpdateAccnt);

        miRequest.setText("Request Plugin");
        miRequest.setEnabled(false);
        mnuAccount.add(miRequest);

        miReDownload.setText("Re-Download Plugin/s");
        miReDownload.setEnabled(false);
        mnuAccount.add(miReDownload);
        mnuAccount.add(jSeparator12);

        jMenuBar1.add(mnuAccount);

        mnuHelp1.setText("Help");

        miAbout.setText("About");
        mnuHelp1.add(miAbout);

        jMenuBar1.add(mnuHelp1);

        setJMenuBar(jMenuBar1);

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void miCopySelectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miCopySelectedActionPerformed
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        TransferHandler transferHandler = epDisplay.getTransferHandler();
        transferHandler.exportToClipboard(epDisplay, clipboard, TransferHandler.COPY);
    }//GEN-LAST:event_miCopySelectedActionPerformed

    private void epDisplayMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_epDisplayMouseReleased
        if (evt.isPopupTrigger()) {
            popMenu.show(epDisplay, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_epDisplayMouseReleased

    private void btnNextDocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextDocActionPerformed
        int row = tblResults.getSelectedRow();
        if (row < tblResults.getRowCount() - 1) {
            tblResults.setRowSelectionInterval(0, row + 1);
            Rectangle rect = tblResults.getCellRect(row + 1, 0, true);
            tblResults.scrollRectToVisible(rect);
        }
    }//GEN-LAST:event_btnNextDocActionPerformed

    private void btnPrevDocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrevDocActionPerformed
        int row = tblResults.getSelectedRow();
        if (row > 0) {
            tblResults.setRowSelectionInterval(0, row - 1);
            Rectangle rect = tblResults.getCellRect(row - 1, 0, true);
            tblResults.scrollRectToVisible(rect);
        }
    }//GEN-LAST:event_btnPrevDocActionPerformed

    private void btnSamplesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSamplesActionPerformed
        SampleQueryDialog sd = new SampleQueryDialog(this, false);
        sd.setVisible(true);
    }//GEN-LAST:event_btnSamplesActionPerformed

    private void btnBayStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_btnBayStateChanged
        if (btnBay.isSelected() && !"reference_B".equals(pluginName)) {
            setAccountChanged("reference_B");
        }
    }//GEN-LAST:event_btnBayStateChanged

    private void btnFlintStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_btnFlintStateChanged
        if (btnFlint.isSelected() && !"reference_F".equals(pluginName)) {
            setAccountChanged("reference_F");
        }
    }//GEN-LAST:event_btnFlintStateChanged

    private void btnLapeerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_btnLapeerStateChanged
        if (btnLapeer.isSelected() && !"reference_L".equals(pluginName)) {
            setAccountChanged("reference_L");
        }
    }//GEN-LAST:event_btnLapeerStateChanged

    private void btnMacombStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_btnMacombStateChanged
        if (btnMacomb.isSelected() && !"reference_M".equals(pluginName)) {
            setAccountChanged("reference_M");
        }
    }//GEN-LAST:event_btnMacombStateChanged

    private void btnSpec3StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_btnSpec3StateChanged
        if (btnSpec3.isSelected() && !"reference_SPEC3".equals(pluginName)) {
            setAccountChanged("reference_SPEC3");
        }
    }//GEN-LAST:event_btnSpec3StateChanged

    private void btnGSamaritanStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_btnGSamaritanStateChanged
        if (btnGSamaritan.isSelected() && !"reference_G".equals(pluginName)) {
            setAccountChanged("reference_G");
        }
    }//GEN-LAST:event_btnGSamaritanStateChanged

    private void btnCentralStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_btnCentralStateChanged
        if (btnCentral.isSelected() && !"reference_C".equals(pluginName)) {
            setAccountChanged("reference_C");
        }
    }//GEN-LAST:event_btnCentralStateChanged

    private void btnOaklandStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_btnOaklandStateChanged
        if (btnOakland.isSelected() && !"reference_O".equals(pluginName)) {
            setAccountChanged("reference_O");
        }
    }//GEN-LAST:event_btnOaklandStateChanged

    private void btnSetonStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_btnSetonStateChanged
        if (btnSeton.isSelected() && !"reference_S".equals(pluginName)) {
            setAccountChanged("reference_S");
        }
    }//GEN-LAST:event_btnSetonStateChanged

    private void btnHackettstownStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_btnHackettstownStateChanged
        if (btnHackettstown.isSelected() && !"reference_H".equals(pluginName)) {
            setAccountChanged("reference_H");
        }
    }//GEN-LAST:event_btnHackettstownStateChanged

    private void btnHospitalSpecificStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_btnHospitalSpecificStateChanged
        if (btnHospitalSpecific.isSelected() && !"reference_HS".equals(pluginName)) {
            setAccountChanged("reference_HS");
        }
    }//GEN-LAST:event_btnHospitalSpecificStateChanged

    private void btnBayMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBayMouseReleased
        if (btnBay.isSelected()
                && btnBay.getForeground().equals(Color.red) && evt.isPopupTrigger()) {
            accntSDialog.setTitle("BAY Specific");
            accntSDialog.setVisible(true);
        }
    }//GEN-LAST:event_btnBayMouseReleased

    private void lblWorkTypeMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblWorkTypeMouseReleased
        if (lblWorkType.getForeground().equals(Color.RED) && wtSpecificBean != null) {
            if (WTSDialog == null) {
                WTSDialog = new SpecificDialog(Main.getController().getMainFrame(), false);
                WTSDialog.setTitle("Work Type Specific");
            }
            WTSDialog.setEditorText(wtSpecificBean.getDocument());
            WTSDialog.setVisible(true);
        }
    }//GEN-LAST:event_lblWorkTypeMouseReleased

    private void btnCentralMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCentralMouseReleased
        if (btnCentral.isSelected()
                && btnCentral.getForeground().equals(Color.red) && evt.isPopupTrigger()) {
            accntSDialog.setTitle("Central Specific");
            accntSDialog.setVisible(true);
        }
    }//GEN-LAST:event_btnCentralMouseReleased

    private void btnFlintMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnFlintMouseReleased
        if (btnFlint.isSelected()
                && btnFlint.getForeground().equals(Color.red) && evt.isPopupTrigger()) {
            accntSDialog.setTitle("Flint Specific");
            accntSDialog.setVisible(true);
        }
    }//GEN-LAST:event_btnFlintMouseReleased

    private void btnGSamaritanMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnGSamaritanMouseReleased
        if (btnGSamaritan.isSelected()
                && btnGSamaritan.getForeground().equals(Color.red) && evt.isPopupTrigger()) {
            accntSDialog.setTitle("Good Samaritan Specific");
            accntSDialog.setVisible(true);
        }
    }//GEN-LAST:event_btnGSamaritanMouseReleased

    private void btnHackettstownMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHackettstownMouseReleased
        if (btnHackettstown.isSelected()
                && btnHackettstown.getForeground().equals(Color.red) && evt.isPopupTrigger()) {
            accntSDialog.setTitle("Hackettstown Specific");
            accntSDialog.setVisible(true);
        }
    }//GEN-LAST:event_btnHackettstownMouseReleased

    private void btnLapeerMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnLapeerMouseReleased
        if (btnLapeer.isSelected()
                && btnLapeer.getForeground().equals(Color.red) && evt.isPopupTrigger()) {
            accntSDialog.setTitle("Lapeer Specific");
            accntSDialog.setVisible(true);
        }
    }//GEN-LAST:event_btnLapeerMouseReleased

    private void btnMacombMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMacombMouseReleased
        if (btnMacomb.isSelected()
                && btnMacomb.getForeground().equals(Color.red) && evt.isPopupTrigger()) {
            accntSDialog.setTitle("Macomb Specific");
            accntSDialog.setVisible(true);
        }
    }//GEN-LAST:event_btnMacombMouseReleased

    private void btnOaklandMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnOaklandMouseReleased
        if (btnOakland.isSelected()
                && btnOakland.getForeground().equals(Color.red) && evt.isPopupTrigger()) {
            accntSDialog.setTitle("Oakland Specific");
            accntSDialog.setVisible(true);
        }
    }//GEN-LAST:event_btnOaklandMouseReleased

    private void btnSetonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSetonMouseReleased
        if (btnSeton.isSelected()
                && btnSeton.getForeground().equals(Color.red) && evt.isPopupTrigger()) {
            accntSDialog.setTitle("Seton Specific");
            accntSDialog.setVisible(true);
        }
    }//GEN-LAST:event_btnSetonMouseReleased

    private void btnSpec3MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSpec3MouseReleased
        if (btnSpec3.isSelected()
                && btnSpec3.getForeground().equals(Color.red) && evt.isPopupTrigger()) {
            accntSDialog.setTitle("Spec3 Specific");
            accntSDialog.setVisible(true);
        }
    }//GEN-LAST:event_btnSpec3MouseReleased

    private void btnHospitalSpecificMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHospitalSpecificMouseReleased
        if (btnHospitalSpecific.isSelected()
                && btnHospitalSpecific.getForeground().equals(Color.red) && evt.isPopupTrigger()) {
            accntSDialog.setTitle("HS Specific");
            accntSDialog.setVisible(true);
        }
    }//GEN-LAST:event_btnHospitalSpecificMouseReleased

    private void btnDictatorsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDictatorsActionPerformed
        openReferenceFile(ReferenceFileLink.TR_DICTATORS.toString());
    }//GEN-LAST:event_btnDictatorsActionPerformed

    private void btnPsychDrugsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPsychDrugsActionPerformed
        openReferenceFile(ReferenceFileLink.DRUGS_PHRASES.toString());
    }//GEN-LAST:event_btnPsychDrugsActionPerformed

    private void btnNormalLabsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNormalLabsActionPerformed
        openReferenceFile(ReferenceFileLink.NORMALLABS.toString());
    }//GEN-LAST:event_btnNormalLabsActionPerformed

    private void btnGuidelinesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuidelinesActionPerformed
        openReferenceFile(ReferenceFileLink.GUIDELINES.toString());
    }//GEN-LAST:event_btnGuidelinesActionPerformed

    private void btnJDrugsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnJDrugsActionPerformed
        try {
            Runtime.getRuntime().exec(
                    "cmd /c start Transcription-Dictionary.exe",
                    null,
                    dictionaryPath.getParentFile());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }//GEN-LAST:event_btnJDrugsActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnBay;
    private javax.swing.JToggleButton btnCentral;
    private javax.swing.JButton btnDictators;
    private javax.swing.JToggleButton btnFlint;
    private javax.swing.JToggleButton btnGSamaritan;
    private javax.swing.JButton btnGo;
    private javax.swing.JButton btnGuidelines;
    private javax.swing.JToggleButton btnHackettstown;
    private javax.swing.JToggleButton btnHospitalSpecific;
    private javax.swing.JButton btnJDrugs;
    private javax.swing.JToggleButton btnLapeer;
    private javax.swing.JToggleButton btnMacomb;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnNextDoc;
    private javax.swing.JButton btnNormalLabs;
    private javax.swing.JToggleButton btnOakland;
    private javax.swing.JButton btnPrev;
    private javax.swing.JButton btnPrevDoc;
    private javax.swing.JButton btnPsychDrugs;
    private javax.swing.JButton btnSamples;
    private javax.swing.JToggleButton btnSeton;
    private javax.swing.JToggleButton btnSpec3;
    private javax.swing.JComboBox cmbDictator;
    private javax.swing.JComboBox cmbLimit;
    private javax.swing.JComboBox cmbWorkType;
    private javax.swing.JEditorPane epDisplay;
    private javax.swing.ButtonGroup groupAccount;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator11;
    private javax.swing.JPopupMenu.Separator jSeparator12;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JLabel lblDictator;
    private javax.swing.JLabel lblMessage;
    private javax.swing.JLabel lblResults;
    private javax.swing.JLabel lblScope;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JLabel lblWorkType;
    private javax.swing.JMenuItem miAbout;
    private javax.swing.JMenuItem miClose;
    private javax.swing.JMenuItem miCopySelected;
    private javax.swing.JMenuItem miPreference;
    private javax.swing.JMenuItem miReDownload;
    private javax.swing.JMenuItem miRequest;
    private javax.swing.JMenuItem miUpdateAccnt;
    private javax.swing.JRadioButtonMenuItem mirBay;
    private javax.swing.JRadioButtonMenuItem mirCentral;
    private javax.swing.JRadioButtonMenuItem mirFlit;
    private javax.swing.JRadioButtonMenuItem mirGSamaritan;
    private javax.swing.JRadioButtonMenuItem mirHackettstown;
    private javax.swing.JRadioButtonMenuItem mirHospitalSpecific;
    private javax.swing.JRadioButtonMenuItem mirLapeer;
    private javax.swing.JRadioButtonMenuItem mirMacomb;
    private javax.swing.JRadioButtonMenuItem mirOakland;
    private javax.swing.JRadioButtonMenuItem mirSeton;
    private javax.swing.JRadioButtonMenuItem mirSpec3;
    private javax.swing.JMenu mnuAccount;
    private javax.swing.JMenu mnuEdit;
    private javax.swing.JMenu mnuFile;
    private javax.swing.JMenu mnuHelp1;
    private javax.swing.JPanel pnlSample;
    private javax.swing.JPanel pnlSearch;
    private javax.swing.JPopupMenu popMenu;
    private javax.swing.JScrollPane scrollPanel;
    private javax.swing.JToolBar.Separator spBay;
    private javax.swing.JToolBar.Separator spCentral;
    private javax.swing.JToolBar.Separator spFlint;
    private javax.swing.JToolBar.Separator spGS;
    private javax.swing.JToolBar.Separator spHacketts;
    private javax.swing.JToolBar.Separator spLapeer;
    private javax.swing.JToolBar.Separator spMacomb;
    private javax.swing.JToolBar.Separator spOakland;
    private javax.swing.JToolBar.Separator spSeton;
    private javax.swing.JToolBar.Separator spSpec3;
    private javax.swing.JToolBar tbAccounts;
    private org.jdesktop.swingx.JXTable tblResults;
    private javax.swing.JTextField txtQuery;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
