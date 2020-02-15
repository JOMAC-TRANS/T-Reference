/*
 * MainFrame.java
 *
 * Created on June 28, 2012, 03:25:38 PM
 */
package com.jomac.transcription.referencebuilder.forms;

import com.hccs.forms.AboutFrame;
import com.hccs.forms.components.FileDragDropCallback;
import com.hccs.forms.components.FileDragDropHandler;
import com.hccs.util.ComponentUtils;
import com.hccs.util.FileUtilities;
import com.hccs.util.MimeUtils;
import com.hccs.util.Task;
import com.hccs.util.TaskThread;
import com.jomac.transcription.referencebuilder.Main;
import com.jomac.transcription.referencebuilder.Reference;
import com.jomac.transcription.referencebuilder.jpa.models.DictatorBean;
import com.jomac.transcription.referencebuilder.jpa.models.DictatorWorkTypeBean;
import com.jomac.transcription.referencebuilder.jpa.models.DocumentBean;
import com.jomac.transcription.referencebuilder.jpa.models.SpecificBean;
import com.jomac.transcription.referencebuilder.jpa.models.WorkTypeBean;
import com.jomac.transcription.referencebuilder.queries.DictatorQueries;
import com.jomac.transcription.referencebuilder.queries.DictatorWorkTypeQueries;
import com.jomac.transcription.referencebuilder.queries.DocumentQueries;
import com.jomac.transcription.referencebuilder.queries.SpecificQueries;
import com.jomac.transcription.referencebuilder.queries.WorkTypeQueries;
import com.jomac.transcription.referencebuilder.utilities.FileReader;
import java.awt.*;
import java.io.File;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class MainFrame extends javax.swing.JFrame {

    private boolean converting;
    private Preferences preferences;
    private DefaultTableModel model;
    private Reference reference;

    private enum Status {

        READY, DONE, EMPTY, ERROR
    }
    private FileNameExtensionFilter fileExtension = new FileNameExtensionFilter(
            "Document files files (*.doc)", "doc");

    public MainFrame() {
        initComponents();
        loadPreferences();

        setTitle(Main.getResourceBundle().getString("product_name")
                + "-" + Main.getResourceBundle().getString("product_account"));
        setIconImage(new ImageIcon(MainFrame.class.getResource(
                "/com/jomac/transcription/referencebuilder/resources/16x16/reference.png")).getImage());
        fcOpen.setMultiSelectionEnabled(true);
        model = (DefaultTableModel) tblList.getModel();
        FileDragDropHandler handler = new FileDragDropHandler();

        handler.setTargetComponent(tblList);
        handler.addCallback(new FileDragDropCallback() {
            public boolean validate(File[] files) {
                boolean valid = false;

                try {
                    for (File f : files) {
                        if (!f.exists()) {
                            continue;
                        }

                        if (f.isDirectory() || isValidFile(f)) {
                            valid = true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return valid;
            }

            public void processFiles(final File[] files) {
                new TaskThread(new Task() {
                    @Override
                    public void start() {
                        toggleForm(false);
                        lblStatus1.setText("Loading files to convert...");
                    }

                    @Override
                    public void doInBackground() throws InterruptedException {
                        populateList(files);
                    }

                    @Override
                    public void finished() {
                        toggleForm(!converting);
                        lblStatus1.setText("Result: " + model.getRowCount());
                    }
                }).start();
            }
        });

        fcOpen.removeChoosableFileFilter(fileExtension);
        fcOpen.setFileFilter(fileExtension);
        reference = new Reference();
    }

    private void loadPreferences() {
        preferences = Preferences.userRoot().node(MainFrame.class.getName());

        setBounds(
                preferences.getInt("x", 100),
                preferences.getInt("y", 100),
                preferences.getInt("width", 651),
                preferences.getInt("height", 355));
    }

    private void terminateApp() {
        this.setVisible(false);
        try {
            TimeUnit.MILLISECONDS.sleep(150);
        } catch (Exception ex) {
        }

        Main.closeServerSocket();
        Rectangle bounds = getBounds();

        preferences.putInt("x", bounds.x);
        preferences.putInt("y", bounds.y);
        preferences.putInt("width", bounds.width);
        preferences.putInt("height", bounds.height);

        try {
            preferences.flush();
//            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
//                public void run() {
//                    WordProcessorManager.getDefaultFileConverter().close();
//                }
//            }));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);

        }
    }

    private boolean isValidFile(File xx) {
        boolean valid = false;

        if (xx.isHidden()) {
            if (xx.getName().contains("~")) {
                xx.delete();
                System.out.println("deleting... " + xx);
            } else {
                System.out.println(xx);
            }
            return false;
        }
        Collection _mimeTypes = MimeUtils.getMimeTypes(xx);
        String[] name = FileUtilities.getBaseAndExtension(xx);
        if (name[1] != null) {
            if (name[1].equals("docx")) {
                System.out.println("DOCX: " + xx);
//                return false;
                return true;
            }

            if (!xx.isHidden()) {
                valid = (name[1].equals("doc") && _mimeTypes.contains("application/msword"));
            }
        }

        return valid;
    }

    private void populateList(File[] files) {
        for (final File xx : files) {
            if (xx.isFile() && isValidFile(xx)) {
                ComponentUtils.invokeInEDT(new Runnable() {
                    @Override
                    public void run() {
                        model.addRow(new Object[]{xx, Status.READY});
                    }
                });
            } else if ((xx.isDirectory()) && (xx.listFiles() != null)) {
                File[] subFiles = xx.listFiles();
                populateList(subFiles);
            }
        }
    }

    private void toggleForm(final boolean enabled) {
        ComponentUtils.invokeInEDT(new Runnable() {
            @Override
            public void run() {
                miConvert.setEnabled(enabled);
                miRemoveAll.setEnabled(enabled);
                miRemove.setEnabled(enabled);

                btnConvert.setEnabled(enabled);
                btnRemoveAll.setEnabled(enabled);
                btnRemove.setEnabled(enabled);
            }
        });
    }

    private String readFile(final File sFile) {
        String content = "";
        FileReader fr = FileReader.createFileReader(sFile);

        if (fr != null) {
            if (fr.openDocument()) {
                fr.extractText();
                content = fr.extractText();
                fr.closeDocument();
            } else if (fr.encryptedFile()) {
                content = "";
            }
        }

        return content;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fcOpen = new javax.swing.JFileChooser();
        pnlStatus = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        pnlTop = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnAdd = new javax.swing.JButton();
        btnConvert = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        btnRemoveAll = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblList = new org.jdesktop.swingx.JXTable();
        lblStatus1 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuFile = new javax.swing.JMenu();
        miAdd = new javax.swing.JMenuItem();
        miConvert = new javax.swing.JMenuItem();
        miRemove = new javax.swing.JMenuItem();
        miRemoveAll = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        miExit = new javax.swing.JMenuItem();
        mnuHelp = new javax.swing.JMenu();
        miDetails = new javax.swing.JMenuItem();

        fcOpen.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        pnlStatus.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 5, 2, 2));
        pnlStatus.setLayout(new java.awt.BorderLayout());

        lblStatus.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        pnlStatus.add(lblStatus, java.awt.BorderLayout.CENTER);

        getContentPane().add(pnlStatus, java.awt.BorderLayout.SOUTH);

        pnlTop.setPreferredSize(new java.awt.Dimension(397, 100));
        pnlTop.setLayout(new java.awt.BorderLayout());

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/referencebuilder/resources/32x32/filenew.png"))); // NOI18N
        btnAdd.setText("Add");
        btnAdd.setFocusable(false);
        btnAdd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAdd.setIconTextGap(2);
        btnAdd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAdd);

        btnConvert.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/referencebuilder/resources/32x32/recycled.png"))); // NOI18N
        btnConvert.setText("Convert");
        btnConvert.setFocusable(false);
        btnConvert.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnConvert.setIconTextGap(2);
        btnConvert.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnConvert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConvertActionPerformed(evt);
            }
        });
        jToolBar1.add(btnConvert);

        btnRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/referencebuilder/resources/32x32/removeselected.png"))); // NOI18N
        btnRemove.setText("Remove");
        btnRemove.setFocusable(false);
        btnRemove.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRemove.setIconTextGap(2);
        btnRemove.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRemove);

        btnRemoveAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/referencebuilder/resources/32x32/removeselected.png"))); // NOI18N
        btnRemoveAll.setText("Remove All");
        btnRemoveAll.setFocusable(false);
        btnRemoveAll.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRemoveAll.setIconTextGap(2);
        btnRemoveAll.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRemoveAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveAllActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRemoveAll);

        pnlTop.add(jToolBar1, java.awt.BorderLayout.CENTER);

        getContentPane().add(pnlTop, java.awt.BorderLayout.NORTH);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        tblList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "File", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblList.setEditable(false);
        jScrollPane3.setViewportView(tblList);
        tblList.getColumnModel().getColumn(1).setPreferredWidth(60);
        tblList.getColumnModel().getColumn(1).setMaxWidth(120);

        jPanel2.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        lblStatus1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblStatus1.setText("Result: 0");
        jPanel2.add(lblStatus1, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        mnuFile.setText("File");

        miAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/referencebuilder/resources/16x16/filenew.png"))); // NOI18N
        miAdd.setText("Add");
        miAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miAddActionPerformed(evt);
            }
        });
        mnuFile.add(miAdd);

        miConvert.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/referencebuilder/resources/16x16/recycled.png"))); // NOI18N
        miConvert.setText("Convert");
        miConvert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miConvertActionPerformed(evt);
            }
        });
        mnuFile.add(miConvert);

        miRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/referencebuilder/resources/16x16/removeselected.png"))); // NOI18N
        miRemove.setText("Remove");
        miRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miRemoveActionPerformed(evt);
            }
        });
        mnuFile.add(miRemove);

        miRemoveAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/referencebuilder/resources/16x16/removeall.png"))); // NOI18N
        miRemoveAll.setText("Remove All");
        miRemoveAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miRemoveAllActionPerformed(evt);
            }
        });
        mnuFile.add(miRemoveAll);
        mnuFile.add(jSeparator1);

        miExit.setText("Exit");
        miExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miExitActionPerformed(evt);
            }
        });
        mnuFile.add(miExit);

        jMenuBar1.add(mnuFile);

        mnuHelp.setText("Help");

        miDetails.setText("About");
        miDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miDetailsActionPerformed(evt);
            }
        });
        mnuHelp.add(miDetails);

        jMenuBar1.add(mnuHelp);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void miConvertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miConvertActionPerformed
        btnConvertActionPerformed(evt);
    }//GEN-LAST:event_miConvertActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        converting = false;
        terminateApp();
    }//GEN-LAST:event_formWindowClosing

    private void miExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miExitActionPerformed
        terminateApp();
        System.exit(0);
    }//GEN-LAST:event_miExitActionPerformed

    private void miDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miDetailsActionPerformed
        new AboutFrame(
                this,
                new ImageIcon(Main.class.getResource("/com/jomac/transcription/referencebuilder/resources/128x128/reference.png")).getImage(),
                Main.getResourceBundle().getString("product_name"),
                Main.getResourceBundle().getString("product_version"),
                Main.getResourceBundle().getString("product_vendor"),
                Main.getResourceBundle().getString("vendor_url"),
                null,
                null,
                null,
                new Color(255, 158, 90),
                new Color(133, 59, 19)).setVisible(true);
    }//GEN-LAST:event_miDetailsActionPerformed

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        new TaskThread(new Task() {
            @Override
            public void start() {
                toggleForm(false);
                lblStatus1.setText("Removing from list...");
            }

            @Override
            public void doInBackground() throws InterruptedException {
                ComponentUtils.invokeInEDT(new Runnable() {
                    @Override
                    public void run() {
                        int[] row = tblList.getSelectedRows();
                        for (int i = row.length - 1; i >= 0; i--) {
                            model.removeRow(row[i]);
                        }
                    }
                });
            }

            @Override
            public void finished() {
                toggleForm(true);
                lblStatus1.setText("Result: " + model.getRowCount());
            }
        }).start();
    }//GEN-LAST:event_btnRemoveActionPerformed

    private void btnRemoveAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveAllActionPerformed
        new TaskThread(new Task() {
            @Override
            public void start() {
                toggleForm(false);
                lblStatus1.setText("Removing from list...");
            }

            @Override
            public void doInBackground() throws InterruptedException {
                ComponentUtils.invokeInEDT(new Runnable() {
                    public void run() {
                        model.setRowCount(0);
                    }
                });
            }

            @Override
            public void finished() {
                toggleForm(true);
                lblStatus1.setText("Result: " + model.getRowCount());
            }
        }).start();
    }//GEN-LAST:event_btnRemoveAllActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        int result = fcOpen.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            new TaskThread(new Task() {
                @Override
                public void start() {
                    toggleForm(false);
                    lblStatus1.setText("Loading files to convert...");
                }

                @Override
                public void doInBackground() throws InterruptedException {
                    populateList(fcOpen.getSelectedFiles());
                }

                @Override
                public void finished() {
                    toggleForm(!converting);
                    lblStatus1.setText("Result: " + model.getRowCount());
                }
            }).start();
        }
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnConvertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConvertActionPerformed

        if (reference.deleteDB()) {
            try {
                reference.createDB();
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to delete Reference DB in \n"
                    + System.getProperty("user.dir").concat(File.separator).concat("db"),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        new TaskThread(new Task() {
            DictatorQueries dictator;
            WorkTypeQueries workType;
            DocumentQueries document;
            SpecificQueries specific;
            DictatorWorkTypeQueries dictatorWType;
            DictatorBean dBean = null;
            WorkTypeBean wBean = null;
            DictatorWorkTypeBean dwBean = null;

            @Override
            public void initialize() {
                dictator = new DictatorQueries();
                workType = new WorkTypeQueries();
                document = new DocumentQueries();
                specific = new SpecificQueries();
                dictatorWType = new DictatorWorkTypeQueries();
                converting = true;
            }

            @Override
            public void start() {
                toggleForm(false);
                lblStatus1.setText("Converting files...");

            }

            @Override
            public void doInBackground() throws InterruptedException {
                boolean skipFile = false;
                for (int i = 0; i < model.getRowCount(); i++) {
                    if (!converting) {
                        break;
                    }

                    File xx = new File(model.getValueAt(i, 0).toString());
                    if (!xx.exists()) {
                        continue;
                    }
                    String[] path = xx.getAbsolutePath().split(Pattern.quote(File.separator));
                    String dictator_;
                    boolean getPath = false;
                    for (int x = 0; x < path.length; x++) {
                        if (!getPath) {
                            String accnt = path[x].toUpperCase();
                            getPath = accnt.contains("BAY")
                                    || accnt.contains("CENTRAL")
                                    || accnt.contains("SAMARITAN")
                                    || accnt.contains("HACKETTSTOWN")
                                    || accnt.contains("HRMC")
                                    || accnt.contains("FLINT")
                                    || accnt.contains("LAPEER")
                                    || accnt.contains("MACOMB")
                                    || accnt.contains("OAKLAND")
                                    || accnt.contains("SETON")
                                    || accnt.contains("SPEC3")
                                    || accnt.contains("HOSPITAL");
                        }
                        if (getPath) {
                            if (xx.getName().equalsIgnoreCase(("999999.doc")) && (x + 2 >= path.length)) {
                                System.out.println("99: " + xx);
                                SpecificBean spBean = new SpecificBean();
                                spBean.setDictatorworktypeid(null);
                                spBean.setDocument(readFile(xx));
                                specific.save(spBean);
                                model.setValueAt(Status.DONE, i, 1);
                                break;
                            }

                            String account_ = path[x++];
                            dictator_ = path[x++];
                            String workType_ = path[x++];

                            if (!workType_.toLowerCase().startsWith("work type")) {
                                System.out.println("WT Error: " + xx.getAbsolutePath());
                                skipFile = true;
                                break;
                            }

                            String exclude = "Work Type ";
                            String wType = workType_.substring(exclude.length());
                            if (wType.contains(" ")) {
                                String[] wx = wType.replaceAll("  ", " ").split(" ");
                                wType = wx[0];
                            }
                            try {
                                Integer.parseInt(wType);
                            } catch (Exception e) {
                                System.out.println(xx.getAbsolutePath());
                                skipFile = true;
                                break;
                            }

                            if ((dBean == null || !dBean.toString().equals(dictator_))) {
                                String[] dict = dictator_.split(" ");
                                String[] dict_ = {dictator_};

                                boolean splitName = false;
                                //!Main.getResourceBundle().getString("product_account").equalsIgnoreCase("spec3");
                                if (splitName) {
                                    dBean = dictator.findDictator(dict);
                                    if (dBean == null || dBean.getDictatorid() == null) {
                                        dBean = new DictatorBean();
                                        dBean.setLastname(dict[0]);
                                        dBean.setFirstname(dict[1]);
                                        if (dict.length > 2) {
                                            dBean.setMiddlename(dict[2]);
                                        }
                                        dictator.save(dBean);
                                    }
                                } else {
                                    dBean = dictator.findDictator(dict_);
                                    if (dBean == null || dBean.getDictatorid() == null) {
                                        dBean = new DictatorBean();
                                        dBean.setLastname(dictator_);
                                        dictator.save(dBean);
                                    }
                                }
                            }

                            if (wBean == null || !wBean.toString().equals(wType)) {
                                wBean = workType.findWorkType(wType);
                                if (wBean == null || wBean.getWorkTypeid() == null) {
                                    wBean = new WorkTypeBean();
                                    wBean.setWorktype(wType);

                                    workType.save(wBean);
                                }
                            }

                            dwBean = dictatorWType.findDWByDictatorAndWorkType(dBean, wBean);
                            if (dwBean == null || dwBean.getDictatorworktypeid() == null) {
                                dwBean = new DictatorWorkTypeBean();
                                dwBean.setDictatorid(dBean);
                                dwBean.setWorktypeid(wBean);
                                dictatorWType.save(dwBean);
                            }
                            break;
                        }
                    }

                    try {
                        if (skipFile) {
                            skipFile = false;
                            model.setValueAt(Status.ERROR, i, 1);
                        } else if (dwBean != null) {
                            if (xx.getName().equalsIgnoreCase("999999.doc")) {
                                System.out.println("999: " + xx);
                                SpecificBean spBean = new SpecificBean();
                                spBean.setDictatorworktypeid(dwBean);
                                spBean.setDocument(readFile(xx));
                                specific.save(spBean);
                                model.setValueAt(Status.DONE, i, 1);
                            } else {
                                DocumentBean bean = new DocumentBean();
                                String content = readFile(xx);
                                if (content.trim().isEmpty()) {
                                    System.out.println("Empty: " + xx.getAbsolutePath());
                                    model.setValueAt(Status.EMPTY, i, 1);
                                    continue;
                                }
                                if (content.contains("<")) {
                                    if (content.contains("\r\r</")) {
                                        content = content.replaceAll("\r\r</", "\r</");
                                    }
                                    if (content.contains("\r</")) {
                                        content = content.replaceAll("\r</", "\n\n</");
                                        System.out.println("Replacing \\r  to \\n  on " + xx.getPath());
                                    }
//                                content = content.replaceAll("<", "&lt;");
                                    content = content.replaceAll("</", "<b>");
                                }
                                if (content.contains(">")) {
//                                content = content.replaceAll(">", "&gt;");
                                    content = content.replaceAll("/>", "</b>");
                                }

                                bean.setDocument(content);
                                bean.setDictatorworktypeid(dwBean);
                                document.save(bean);
                                model.setValueAt(Status.DONE, i, 1);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        model.setValueAt(Status.ERROR, i, 1);
                    }
                }
                reference.cleanUp();
                reference.zipDB();
            }

            @Override
            public void finished() {
                lblStatus1.setText("Result: " + model.getRowCount());
                System.out.println("Done!");
            }
        }).start();
    }//GEN-LAST:event_btnConvertActionPerformed

    private void miAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miAddActionPerformed
        btnAddActionPerformed(evt);
    }//GEN-LAST:event_miAddActionPerformed

    private void miRemoveAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miRemoveAllActionPerformed
        btnRemoveAllActionPerformed(evt);
    }//GEN-LAST:event_miRemoveAllActionPerformed

    private void miRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miRemoveActionPerformed
        btnRemoveActionPerformed(evt);
    }//GEN-LAST:event_miRemoveActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnConvert;
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnRemoveAll;
    private javax.swing.JFileChooser fcOpen;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblStatus1;
    private javax.swing.JMenuItem miAdd;
    private javax.swing.JMenuItem miConvert;
    private javax.swing.JMenuItem miDetails;
    private javax.swing.JMenuItem miExit;
    private javax.swing.JMenuItem miRemove;
    private javax.swing.JMenuItem miRemoveAll;
    private javax.swing.JMenu mnuFile;
    private javax.swing.JMenu mnuHelp;
    private javax.swing.JPanel pnlStatus;
    private javax.swing.JPanel pnlTop;
    private org.jdesktop.swingx.JXTable tblList;
    // End of variables declaration//GEN-END:variables
}
