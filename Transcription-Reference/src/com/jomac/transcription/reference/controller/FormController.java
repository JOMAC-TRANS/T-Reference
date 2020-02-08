package com.jomac.transcription.reference.controller;

import com.hccs.forms.AboutFrame;
import com.hccs.util.ComponentUtils;
import com.hccs.util.StringUtilities;
import com.hccs.util.Task;
import com.hccs.util.TaskThread;
import com.jomac.transcription.reference.Main;
import com.jomac.transcription.reference.forms.PreferenceDialog;
import com.jomac.transcription.reference.forms.ReferenceForm;
import com.jomac.transcription.reference.forms.RegistrationForm;
import com.jomac.transcription.reference.forms.tablemodels.ReferenceTableModel;
import com.jomac.transcription.reference.jpa.models.DictatorBean;
import com.jomac.transcription.reference.jpa.models.DocumentBean;
import com.jomac.transcription.reference.jpa.models.RegistrationBean;
import com.jomac.transcription.reference.jpa.models.SpecificBean;
import com.jomac.transcription.reference.queries.DBQueries;
import com.jomac.transcription.reference.queries.DocumentQueries;
import com.jomac.transcription.reference.queries.RegistrationQueries;
import com.jomac.transcription.reference.queries.SpecificQueries;
import com.jomac.transcription.reference.utilities.Formatter;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class FormController {

    private DocumentBean bean;
    private String query, pluginName;
    private ReferenceForm mainFrame;
    private ReferenceTableModel tblModel;
    private Preferences preferences;
    private ResourceBundle resourceBundle;
    private final String RESOURCE_PATH = "/com/jomac/transcription/reference/resources/";
    private NavigableMap<Integer, String> mappings;

    public JFrame getMainFrame() {
        return mainFrame;
    }

    public FormController() {
        preferences = Main.getPreferences();
        tblModel = new ReferenceTableModel();
        mappings = new TreeMap<>();

        resourceBundle = Main.getResourceBundle();
        mainFrame = new ReferenceForm();
        mainFrame.initReferenceTable(tblModel);

        mainFrame.addCloseActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                terminateApp();
            }
        });
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                terminateApp();
            }

            @Override
            public void windowGainedFocus(WindowEvent e) {
                System.gc();
            }
        });
        mainFrame.addAboutActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AboutFrame(
                        mainFrame,
                        new ImageIcon(Main.class.getResource(RESOURCE_PATH + "128x128/reference128.png")).getImage(),
                        "T.Reference",
                        resourceBundle.getString("product_version"),
                        "",
                        "",
                        null,
                        null,
                        null,
                        new Color(251, 247, 254),
                        new Color(133, 52, 227)).setVisible(true);
            }
        });
        mainFrame.addPreferenceActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PreferenceDialog pr = new PreferenceDialog(mainFrame, true);
                pr.setVisible(true);
                if (pr.isModified()) {
                    mainFrame.setSystemFontSize(preferences.getInt("systemF", 14));
                    mainFrame.setDocumentFontSize(preferences.getInt("documentF", 16));
                    mainFrame.setFormFontSize(bean != null ? bean.getDocumentHTML() : null);
                    mainFrame.setDisplayCaret(0);
                }
            }
        });
        mainFrame.addReDownloadListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.toggleComponents(false);
                mainFrame.toggleToolbar(false);

                if (JOptionPane.showConfirmDialog(mainFrame,
                        "TransRef need to be closed before Re-Downloading of Plugin/s!\n\n"
                        + "Do you want to continue?",
                        "Download Plugins",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    Main.restartDeletePlugins();
                } else {
                    boolean activeApp = Main.isActiveApp();
                    mainFrame.toggleComponents(activeApp);
                    mainFrame.toggleToolbar(activeApp);
                }
            }
        });
        mainFrame.addRequestListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final RegistrationQueries remoteQuries = new RegistrationQueries();

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        RegistrationBean tmpBean;

                        int counter = 0;
                        do {
                            tmpBean = remoteQuries.getRegistrationById(Main.getRegistrationId());
                            counter++;
                        } while (tmpBean == null && counter < 3);

                        if (tmpBean == null) {
                            JOptionPane.showMessageDialog(mainFrame,
                                    "Unable to connect to Server.\n"
                                    + "Please try again later",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        final RegistrationForm form = new RegistrationForm(mainFrame, true);
                        final RegistrationBean rBean = tmpBean;
                        form.hideRegistrationInfo();
                        String currentPlugin = rBean.getProductid().getProductvalue();
                        String request = rBean.getProductid().getProductrequest();
                        int val, reqVal;
                        try {
                            val = currentPlugin.contains("0x")
                                    ? Integer.valueOf(currentPlugin.substring(2, currentPlugin.length()), 16)
                                    : Integer.parseInt(currentPlugin);

                        } catch (Exception ex) {
                            val = 0;
                        }
                        try {
                            reqVal = request.contains("0x")
                                    ? Integer.valueOf(request.substring(2, request.length()), 16)
                                    : Integer.parseInt(request);

                        } catch (Exception ex) {
                            reqVal = 0;
                        }

                        form.initRegisteredPlugin(val, reqVal);

                        form.addRequestCodeAddActionListerner(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if (form.validatePluginInfo()) {
                                    new TaskThread(new Task() {
                                        boolean valid;

                                        @Override
                                        public void initialize() {
                                            form.toggleRegistrationForm(false);
                                        }

                                        @Override
                                        public void doInBackground() throws InterruptedException {
                                            if (Main.getRegistrationId() > -1) {
                                                rBean.getProductid().setProductrequest("0x" + Integer.toHexString(form.getSelectedPlugins()));
                                                valid = remoteQuries.save(rBean);
                                            } else {
                                                valid = false;
                                            }
                                        }

                                        @Override
                                        public void finished() {
                                            if (form.isVisible()) {
                                                String content;
                                                if (valid) {
                                                    form.clearRegistrationFields();
                                                    content = "Registration Success!\n"
                                                            + "Please wait for your confirmation";
                                                    form.dispose();
                                                } else {
                                                    content = "Please Contact your System Administrator \n"
                                                            + "for problems regarding Request";
                                                    form.toggleRegistrationForm(true);
                                                }
                                                JOptionPane.showMessageDialog(
                                                        form,
                                                        content,
                                                        "Program Request", valid
                                                                ? JOptionPane.INFORMATION_MESSAGE
                                                                : JOptionPane.ERROR_MESSAGE);
                                            }
                                        }
                                    }).start();
                                }
                            }
                        });
                        form.setLocationRelativeTo(mainFrame);
                        form.toggleRegistrationForm(true);
                        form.setVisible(true);
                    }
                });
            }
        });
        mainFrame.addUpdateAccountListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog(mainFrame,
                        "TransRef need to be closed before account update!\n\n"
                        + "Do you want to continue?",
                        "Account Update",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    Main.restartUpdateApplication();
                }
            }
        });

        mainFrame.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }

                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                if (!lsm.isSelectionEmpty()) {
                    int row = mainFrame.getSelectedRow();
                    try {
                        if (row != -1) {
                            System.gc();
                            bean = ((DocumentBean) tblModel.getWrapperObject(row));
                            String htmlContent = Formatter.getInstance().beanToHtml(bean, mainFrame.getDocumentFontSize());
                            mainFrame.setDocumentDisplay(htmlContent);

                            try {
                                initMappings();
                                mainFrame.setDocumentScroll(0);
                                int loc = mappings.firstKey();
                                mainFrame.setDisplayCaret(loc, mappings.get(loc).length());
                                mainFrame.centerLineInScrollPane();
                            } catch (Exception ex) {
                                mainFrame.setDisplayCaret(0);
                            }
                            mainFrame.toggleNavs(true);
                        }
                    } catch (ArrayIndexOutOfBoundsException ae) {
                    }

                } else {
                    bean = null;
                    mainFrame.setDocumentDisplay("");
                    mainFrame.toggleNavs(false);
                }
            }
        });
        mainFrame.addNextActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextSelectAction();
            }
        });
        mainFrame.addPrevActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                previousSelectAction();
            }
        });
        mainFrame.addLimitItemStateListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (mainFrame.validateQuery() && (e.getStateChange() == ItemEvent.SELECTED)) {
                    doProcess(mainFrame.showWarning());
                }
            }
        });
        mainFrame.addWorkTypeStateListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (mainFrame.validateQuery()) {
                        doProcess(false);
                    }

                    searchWTSpecific();
                }
            }
        });
        mainFrame.addSearchActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mainFrame.validateQuery()) {
                    doProcess(mainFrame.showWarning());
                }
            }
        });
        mainFrame.addDocumentDisplayKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                int key = evt.getKeyCode();
                if (key == 38) {//up arrow key
                    previousSelectAction();
                } else if (key == 40) {//down arrow key
                    nextSelectAction();
                } else if (key == 37) {//previous arrow key
                    mainFrame.prevDocument();
                } else if (key == 39) {//next arrow key
                    mainFrame.nextDocument();
                }
            }
        });
        initDBReference();
    }

    private void nextSelectAction() {
        int selectStart = mainFrame.getDisplaySelectionStart();
        Integer loc = mappings.higherKey(selectStart) != null
                ? mappings.higherKey(selectStart)
                : mappings.firstKey().intValue();
        mainFrame.setDisplayCaret(loc, mappings.get(loc).length());
        mainFrame.centerLineInScrollPane();
    }

    private void previousSelectAction() {
        int selectStart = mainFrame.getDisplaySelectionStart();
        Integer loc = mappings.lowerKey(selectStart) != null
                ? mappings.lowerKey(selectStart)
                : mappings.lastKey().intValue();
        mainFrame.setDisplayCaret(loc, mappings.get(loc).length());
        mainFrame.centerLineInScrollPane();
    }

    public void showForm() {
        if (mainFrame == null) {
            return;
        }

        String accnt = Main.getDBAccount();
        mainFrame.setTitle(accnt.isEmpty() ? resourceBundle.getString("product_name")
                : resourceBundle.getString("product_name") + accnt.substring(9));
        mainFrame.setIconImage(new ImageIcon(getClass().getResource(RESOURCE_PATH + "16x16/reference.png")).getImage());
        loadPreferences();
        mainFrame.setVisible(true);
    }

    private void loadPreferences() {
        preferences = Main.getPreferences();

        mainFrame.setReferenceAccount(pluginName = preferences.get("pluginName", ""));
        mainFrame.setPluginName(pluginName);
        mainFrame.setLocation(preferences.getInt("x", 10),
                preferences.getInt("y", 10));
        mainFrame.setSize(preferences.getInt("width", 950),
                preferences.getInt("height", 700));
        mainFrame.setSplitPane1Location(preferences.getInt("splitLocation1", 255));
        mainFrame.setSplitPane2Location(preferences.getInt("splitLocation2", -1));

        mainFrame.setSearchLimit(preferences.getInt("searchLimit", 10));
        mainFrame.setSystemFontSize(preferences.getInt("systemF", 14));
        mainFrame.setDocumentFontSize(preferences.getInt("documentF", 16));
        mainFrame.setFormFontSize();
    }

    public void savePreference() {
        Rectangle bounds = mainFrame.getBounds();

        preferences.put("pluginName", mainFrame.getPluginName());
        preferences.putInt("x", bounds.x);
        preferences.putInt("y", bounds.y);
        preferences.putInt("width", bounds.width);
        preferences.putInt("height", bounds.height);
        preferences.putInt("splitLocation1", mainFrame.getSplitPane1Location());
        preferences.putInt("splitLocation2", mainFrame.getSplitPane2Location());

        preferences.putInt("searchLimit", mainFrame.getSearchLimitIndex());
        preferences.putInt("fontSize", mainFrame.getSytemFSize());
        preferences.putInt("docfontSize", mainFrame.getDocumentFSize());

        try {
            preferences.flush();
        } catch (Exception e) {
        }
    }

    public void terminateApp() {
        mainFrame.setVisible(false);
        try {
            TimeUnit.MILLISECONDS.sleep(150);
        } catch (Exception ex) {
        }

        savePreference();
        Main.terminate();
    }

    private void initMappings() {
        mappings.clear();
        int pos = 0;
        int carretPos = 0;
        while (carretPos >= 0) {
            String begin = "<font style=\"background-color: yellow\">";
            String end = "</font>";
            pos = bean.getDocumentHTML().toLowerCase().indexOf(begin, pos);
            String word = bean.getDocumentHTML().toLowerCase().substring(pos + begin.length(), bean.getDocumentHTML().toLowerCase().indexOf(end, pos + begin.length()));
            pos = pos + begin.length();
            try {
                String docs = mainFrame.getDisplayWord();
                carretPos = docs.indexOf(word.toLowerCase(), carretPos);

                if (carretPos < 0) {
                    carretPos = docs.indexOf(word.replace("  ", " ").toLowerCase(), carretPos);
                    if (carretPos < 0) {
                        return;
                    }
                }

                mappings.put(carretPos, word);

                carretPos = carretPos + word.length();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initDBReference() {
        //<editor-fold defaultstate="collapsed" desc="Database Initialization">
        new TaskThread(new Task() {
            int pluginSize;
            String errorLog;
            boolean validPlugins, activeApp;

            @Override
            public void initialize() {
                mainFrame.setStatusMsg("Initializing database...");

//                Profiler prof = new Profiler();
//                prof.startCollecting();
                errorLog = new DBQueries().validateSchemaVersion(
                        Main.getDBAccount(),
                        resourceBundle.getString("schema_version"));
                activeApp = Main.isActiveApp();
                pluginSize = TRPlugin.INSTANCE.getAllPlugins().size();
//                prof.stopCollecting();
//                System.out.println(prof.getTop(3));
            }

            @Override
            public void doInBackground() {
                if (activeApp) {
                    ComponentUtils.invokeInEDT(new Runnable() {
                        @Override
                        public void run() {
                            if (!errorLog.isEmpty()) {
                                if (pluginSize == 0) {
                                    mainFrame.showErrorDialog(errorLog);
                                    terminateApp();
                                } else {
                                    mainFrame.showAccountDialog(errorLog);
                                }
                            } else {
                                validPlugins = Main.getAssociatedKey() > 0 && pluginSize > 0;
                            }
                        }
                    }, true);
                }
            }

            @Override
            public void finished() {
                if (!resourceBundle.getString("product_activation").contains("true")) {
                    Main.setAssociatedPlugins(1791);
                    validPlugins = true;
                    activeApp = true;
                }

                mainFrame.setStatusMsg("");
                if (errorLog.isEmpty() && validPlugins) {
                    mainFrame.initWorkType();
                    mainFrame.initDictator();
                    mainFrame.initSpecific();
                    mainFrame.toggleComponents(true);
                }

                if (!activeApp) {
                    mainFrame.showUnActivatedProgram();
                }
                mainFrame.toggleToolbar(true);
                mainFrame.toggleRequestButton(activeApp);
            }
        }).start();
        //</editor-fold>
    }

    private void searchWTSpecific() {
        if (mainFrame.getSelectedDictator() == null
                || !(mainFrame.getSelectedDictator() instanceof DictatorBean)
                || !(mainFrame.getSelectedWorkType() instanceof Integer)) {
            mainFrame.setWorkTypeSpecific(null);
            return;
        }

        new TaskThread(new Task() {
            SpecificBean sBean;
            Map<String, Object> filterMap = new WeakHashMap<>();

            @Override
            public void doInBackground() {
                try {
                    filterMap.put("dictatorid", ((DictatorBean) mainFrame.getSelectedDictator()).getDictatorid());
                    filterMap.put("worktype", mainFrame.getSelectedWorkType().toString());
                    sBean = new SpecificQueries().getSpecific(filterMap);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void finished() {
                mainFrame.setWorkTypeSpecific(sBean);
            }
        }).start();

    }

    private void doProcess(boolean showWarning) {
        System.gc();
        if (showWarning) {
            Object[] options = {"Continue", "Cancel"};
            int reply = JOptionPane.showOptionDialog(
                    null,
                    "<html><b>Reference Filters are empty!</b><br/><br/> "
                    + "To have more <b>accurate</b> results <br/>"
                    + "It is recommended to use filters when searching. <br/>"
                    + "</html>",
                    "WARNING!",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    options,
                    options[1]);

            if (reply != 0) {
                return;
            }
        }

        new TaskThread(new Task() {
            boolean accountChecking;
            String value;
            Long startTime;
            List<DocumentBean> results;
            Map<String, Object> filterMap;

            @Override
            public void start() {
                mainFrame.setStatusMsg("Loading...");

                mainFrame.setResultsMsg("Job Count:");
                mainFrame.toggleComponents(false);
                mainFrame.toggleToolbar(false);
                tblModel.removeAll();
            }

            @Override
            public void initialize() {
                startTime = System.nanoTime();
                accountChecking = false;
                filterMap = new WeakHashMap<>();
                query = mainFrame.getSearchQuery();

                if (mainFrame.getSearchLimitItem() instanceof Integer) {
                    filterMap.put("searchLimit", Integer.parseInt(mainFrame.getSearchLimitItem().toString()) * 3);
                }

                if (mainFrame.getSelectedWorkType() instanceof Integer) {
                    filterMap.put("worktype", mainFrame.getSelectedWorkType().toString());
                }

                try {
                    filterMap.put("dictatorid", ((DictatorBean) mainFrame.getSelectedDictator()).getDictatorid());
                } catch (Exception e) {
                }

                filterMap.put("document", Formatter.getInstance().supportWildChar(query));
                filterMap.put("oQuery", query);
            }

            @Override
            public void doInBackground() {
                results = new DocumentQueries().getQueryResults(filterMap);

                ComponentUtils.invokeInEDT(new Runnable() {
                    @Override
                    public void run() {
                        tblModel.addAll(results);
                    }
                });
            }

            @Override
            public void finished() {
                mainFrame.refreshTable();
                mainFrame.setResultsMsg("Job Count: " + results.size());
                mainFrame.setStatusMsg("");
                mainFrame.toggleComponents(true);
                mainFrame.toggleToolbar(true);
                System.out.println("Search Time: " + StringUtilities.nanoTime2HumanReadable(System.nanoTime() - startTime));
                results.clear();
                filterMap.clear();
            }
        }).start();
    }
}
