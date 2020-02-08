package com.jomac.transcription.activator.controller;

import com.hccs.forms.AboutFrame;
import com.hccs.util.DateUtilities;
import com.hccs.util.EmailUtilities;
import com.hccs.util.StringUtilities;
import com.hccs.util.Task;
import com.hccs.util.TaskThread;
import com.jomac.transcription.activator.Main;
import com.jomac.transcription.activator.forms.ActivatorForm;
import com.jomac.transcription.activator.forms.tablemodels.RegistrationTableModel;
import com.jomac.transcription.activator.jpa.models.MachineBean;
import com.jomac.transcription.activator.jpa.models.PersonBean;
import com.jomac.transcription.activator.jpa.models.ProductBean;
import com.jomac.transcription.activator.jpa.models.RegistrationBean;
import com.jomac.transcription.activator.model.EmailBean;
import com.jomac.transcription.activator.model.PluginBean;
import com.jomac.transcription.activator.queries.RegistrationQuries;
import com.jomac.transcription.activator.utility.IpAddressChecker;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;
import javax.swing.ImageIcon;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;

public class ActivatorController {

    private ATYPE aType;
    private PROGRAM program;
    private ActivatorForm form;
    private boolean showAllRequest;
    private RegistrationQuries regQuries;
    private Boolean msgExceeded = null;
    private Preferences preferences;
    private ResourceBundle resourceBundle;
    private Map<PROGRAM, List<PluginBean>> plugins;
    private EmailUtilities sentEmailUtil;
    private RegistrationTableModel regTModel;
    private IpAddressChecker ipChecker;
    private final String RESOURCE_PATH = "/com/jomac/transcription/activator/resources/";

    private enum ATYPE {

        REQUEST,
        UPDATE,
        ACTIVE,
        DUPLICATE,
        EXPIRE
    }

    private enum PROGRAM {

        REFERENCE,
        CLIENT,
        DRUGS
    }

    private enum STATUS {
        //<editor-fold defaultstate="collapsed" desc="Activator Status...">

        FSERVER {
            @Override
            public String toString() {
                return "Fetching from server...";
            }
        },
        MSG_LOADING {
            @Override
            public String toString() {
                return "Loading Messages...";
            }
        },
        SENDING {
            @Override
            public String toString() {
                return "Sending... ";
            }
        },
        MSG_SENT {
            @Override
            public String toString() {
                return "Message sent!";
            }
        },
        MSG_FAILED {
            @Override
            public String toString() {
                return "Message Failed!";
            }
        },
        EMPTY_TRASH {
            @Override
            public String toString() {
                return "Deleting Requests...";
            }
        }
        //</editor-fold>
    }

    public ActivatorController() {
        initEmailUtilities();
        plugins = new HashMap<>();
        ipChecker = new IpAddressChecker();
        regQuries = new RegistrationQuries();
        regTModel = new RegistrationTableModel();
        preferences = Preferences.userRoot().node(ActivatorController.class.getName());

        form = new ActivatorForm();
        TableRowSorter tblSorter = new TableRowSorter(regTModel);
        tblSorter.setComparator(0, new java.util.Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return dateComparator(o1, o2);
            }
        });
        tblSorter.setComparator(5, new java.util.Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return dateComparator(o1, o2);
            }
        });
        form.initActivatorTable(regTModel, tblSorter);
        form.menuExitAddActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeApplication();
            }
        });
        form.refreshAddActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processActivatorAccounts();
            }
        });
        form.approveAccountActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final List<Integer> rows = form.getSelectedRowsModel();
                if (!rows.isEmpty()) {
                    new TaskThread(new Task() {
                        boolean valid;
                        RegistrationBean regBean;
                        Boolean email = null;

                        @Override
                        public void start() {
                            form.setlabelStatus(STATUS.SENDING.toString());
                            form.toggleButtons(false);
                        }

                        @Override
                        public void doInBackground() {
                            for (int row : rows) {
                                regBean = (RegistrationBean) regTModel.getWrapperObject(row);
                                if (regBean != null) {
                                    regBean.setActive(Boolean.TRUE);
                                    ProductBean pBean = regBean.getProductid();
                                    PersonBean pB = regBean.getPersonid();
                                    String val = rows.size() > 1
                                            ? pBean.getProductrequest() : "0x" + Integer.toHexString(form.getAssociatedPlugins());
                                    pBean.setProductrequest(val);
                                    pBean.setProductvalue(val);
                                    Date exDate = form.getExpirationDate();
                                    if (exDate != null) {
                                        regBean.setExpirationdate(exDate);
                                    }
                                    if (valid = regQuries.save(regBean)) {
                                        if (form.isSendEmailButtonSelected()) {
                                            try {
                                                EmailBean eB = new EmailBean();
                                                eB.setFullName(pB.getName());
                                                eB.setEmail(pB.getEmail());
                                                eB.setProductName(pBean.getName());
                                                eB.setProductVersion(pBean.getVersion());
                                                email = sendMessage(eB);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        if (ATYPE.ACTIVE != aType) {
                                            regTModel.removeRow(row);
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void finished() {
                            if (email != null) {
                                form.setlabelStatus(email ? STATUS.MSG_SENT.toString() : STATUS.MSG_FAILED.toString());
                                if (ATYPE.REQUEST == aType) {
                                    form.showActivationMessage(valid, email);
                                } else {
                                    form.showSavedMessage(valid, email);
                                }
                            } else {
                                if (ATYPE.REQUEST == aType) {
                                    form.showActivationMessage(valid);
                                } else {
                                    form.showSavedMessage(valid);
                                }
                            }
                            form.clearTableSelection();
                            form.toggleButtons(false);
                            form.toggleShowAllRequest(true);
                            form.setRowCountLabel();

                            try {
                                TimeUnit.SECONDS.sleep(1);
                            } catch (Exception ex) {
                            }
                            form.setlabelStatus("");
                        }
                    }).start();
                }
            }
        });
        form.disregardAddActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final List<Integer> rows = form.getSelectedRowsModel();
                if (!rows.isEmpty() && form.discardConfirmation(rows.size() == 1)) {
                    new TaskThread(new Task() {
                        RegistrationBean regBean;

                        @Override
                        public void start() {
                            form.toggleButtons(false);
                        }

                        @Override
                        public void doInBackground() {
                            for (int row : rows) {
                                regBean = (RegistrationBean) regTModel.getWrapperObject(row);
                                if (regBean != null) {
                                    regBean.setActive(false);
                                    if (regQuries.save(regBean)) {
                                        regTModel.removeRow(row);
                                    }
                                }
                            }
                        }

                        @Override
                        public void finished() {
                            form.clearTableSelection();
                            form.toggleButtons(true);
                            form.setRowCountLabel();
                        }
                    }).start();
                }
            }
        });
        form.addTableSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                form.toggleButtons(false);
                setRegistrationDetails(null);
                RegistrationBean regBean;

                if (!lsm.isSelectionEmpty()) {
                    List<Integer> rows = form.getSelectedRowsModel();
                    if (rows.size() == 1) {
                        regBean = (RegistrationBean) regTModel.getWrapperObject(rows.get(0));
                        if (regBean != null) {
                            String name = regBean.getProductid().getName();
                            if (name.contains("Reference")) {
                                program = PROGRAM.REFERENCE;
                            } else if (name.contains("Drugs") || name.contains("Dictionary")) {
                                program = PROGRAM.DRUGS;
                            } else if (name.contains("JOMAC Transcription")) {
                                program = PROGRAM.CLIENT;
                            }
                            setRegistrationDetails(regBean);
                        }
                    }
                    form.toggleButtons(!rows.isEmpty());
                }
            }
        });
        form.showNewRequestActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ATYPE.REQUEST != aType) {
                    aType = ATYPE.REQUEST;
                    processActivatorAccounts();
                }
            }
        });
        form.showUpdateRequestActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ATYPE.UPDATE != aType) {
                    aType = ATYPE.UPDATE;
                    processActivatorAccounts();
                }
            }
        });
        form.showApprovedRequestActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ATYPE.ACTIVE != aType) {
                    aType = ATYPE.ACTIVE;
                    processActivatorAccounts();
                }
            }
        });
        form.showExpiringAccountActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ATYPE.EXPIRE != aType) {
                    aType = ATYPE.EXPIRE;
                    processActivatorAccounts();
                }
            }
        });
        form.showDuplicateAccountActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ATYPE.DUPLICATE != aType) {
                    aType = ATYPE.DUPLICATE;
                    processActivatorAccounts();
                }
            }
        });
        form.accountCleanUpActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new TaskThread(new Task() {
                    List<RegistrationBean> regList;
                    boolean valid;
                    int deleted = 0;

                    @Override
                    public void initialize() {
                        form.toggleActivatorForm(false, true);
                    }

                    @Override
                    public void doInBackground() {
                        try {
                            regList = regQuries.findAllAbandonAccounts();
                            boolean showDiag = true;
                            int result;
                            for (RegistrationBean xx : regList) {
                                try {
                                    result = showDiag ? form.showCleanUpMessage("\nName: " + xx.getPersonid().getName()
                                            + "\nProduct: " + xx.getProductid().getName() + " " + xx.getProductid().getVersion()
                                            + "\nLast log-in: " + (xx.getLastlogin() != null
                                            ? (new SimpleDateFormat(DateUtilities.FORMAT_TYPE.MM_DD_YYYY_DASH.toString())).format(xx.getLastlogin())
                                            : "<No Data>")) : 1;

                                    if (result == 0) {
                                        result = 1;
                                        showDiag = false;
                                    }

                                    if ((result == 1) && regQuries.removeActivatorBean(xx)) {
                                        deleted++;
                                    } else if (result == 3) {
                                        break;
                                    }
                                } catch (Exception e) {
                                    continue;
                                }
                            }
                            valid = true;
                        } catch (Exception ex) {
                            valid = false;
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void finished() {
                        form.showAccountCleanUp(valid, regList.size(), deleted);
                        form.toggleActivatorForm(true, false);
                        if (valid) {
                            processActivatorAccounts();

                        }
                    }
                }).start();

            }
        });
        form.miAboutAddActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AboutFrame(
                        form,
                        new ImageIcon(Main.class.getResource(RESOURCE_PATH + "128x128/activator128.png")).getImage(),
                        resourceBundle.getString("product_name"),
                        resourceBundle.getString("product_version"),
                        resourceBundle.getString("product_vendor"),
                        resourceBundle.getString("vendor_url"),
                        null,
                        null,
                        null,
                        new Color(251, 251, 251),
                        new Color(85, 85, 85)).setVisible(true);
            }
        });
        form.deleteActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final List<Integer> rows = form.getSelectedRowsModel();
                if (!rows.isEmpty() && form.deleteConfirmation(rows.size() == 1)) {

                    new TaskThread(new Task() {
                        RegistrationBean regBean;

                        @Override
                        public void start() {
                            form.toggleActivatorForm(false, true);
                            form.setlabelStatus(STATUS.EMPTY_TRASH.toString());
                        }

                        @Override
                        public void doInBackground() {
                            List<Integer> tobedelete = new ArrayList<>();
                            for (int row : rows) {
                                try {
                                    regBean = (RegistrationBean) regTModel.getWrapperObject(row);
                                    if (regBean != null) {
                                        if (regQuries.removeActivatorBean(regBean)) {
                                            System.out.println("Reg Deleted! " + regBean.getPersonid());
                                            tobedelete.add(row);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            if (!tobedelete.isEmpty()) {
                                Collections.sort(tobedelete);
                                for (int i = tobedelete.size() - 1; i >= 0; i--) {
                                    regTModel.removeRow(tobedelete.get(i));
                                }
                            }
                        }

                        @Override
                        public void finished() {
                            form.clearTableSelection();
                            form.toggleActivatorForm(true, false);
                            form.setRowCountLabel();
                            form.setlabelStatus("");
                        }
                    }).start();
                }
            }
        });
        form.addCheckBoxActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ATYPE.REQUEST == aType) {
                    showAllRequest = form.showAllRequest();
                    processActivatorAccounts();
                }
            }
        });
        form.addTextSearchBoxListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case 27: // Esc
                        form.clearSearchBox();
                        break;
                    case 10: // Enter
                        processActivatorAccounts();
                        break;
                }
            }
        });
        form.addTextSearchFilterListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                form.setRowSorter(RowFilter.regexFilter(form.getFilterString().length() > 1
                        ? ("(?i).*" + form.getFilterString() + ".*")
                        : ("(?i)^" + form.getFilterString())));
            }
        });
    }

    private int dateComparator(String o1, String o2) {
        if (o1.isEmpty() || o2.isEmpty()) {
            return o1.compareTo(o2);
        }

        try {
            Date date1 = DateUtilities.parse(DateUtilities.FORMAT_TYPE.MM_DD_YYYY_DASH, o1);
            Date date2 = DateUtilities.parse(DateUtilities.FORMAT_TYPE.MM_DD_YYYY_DASH, o2);
            return date1.compareTo(date2);
        } catch (Exception ex) {
            ex.printStackTrace();
            return o1.compareTo(o2);
        }
    }

    private void loadPreferences() {
        form.setLocation(preferences.getInt("x", 25),
                preferences.getInt("y", 10));
        form.setSize(preferences.getInt("width", 950),
                preferences.getInt("height", 600));
        form.setRightSplitPaneLocation(preferences.getInt("rightSplitLocation", 200));
        form.setDetailsSplitPaneLocation(preferences.getInt("detailsSplitLocation", -1));
        form.setSendEmailButtonSelected(preferences.getBoolean("SendEmail", false));
        long lDate = preferences.getLong("expirationDate", 0);
        form.setExpirationDate(lDate > 0 ? new Date(lDate) : new Date());
    }

    private void closeApplication() {
        if (form.exitConfirmation()) {
            try {
                TimeUnit.MILLISECONDS.sleep(150);
            } catch (Exception ex) {
            }

            Main.closeServerSocket();
            Rectangle bounds = form.getBounds();

            preferences.putInt("x", bounds.x);
            preferences.putInt("y", bounds.y);
            preferences.putInt("width", bounds.width);
            preferences.putInt("height", bounds.height);
            preferences.putInt("rightSplitLocation", form.getRightSplitPaneLocation());
            preferences.putInt("detailsSplitLocation", form.getDetailsSplitPaneLocation());
            preferences.putBoolean("SendEmail", form.isSendEmailButtonSelected());
            Date date = form.getExpirationDate();
            preferences.putLong("expirationDate", date == null ? 0 : date.getTime());
            System.exit(0);
        }
    }

    public void showForm() {
        form.setTitle(resourceBundle.getString("product_name"));
        form.setIconImage(new ImageIcon(getClass().getResource(RESOURCE_PATH + "16x16/activator.png")).getImage());
        form.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                closeApplication();
            }
        });
        loadPreferences();
        form.setVisible(true);
        form.setNewRequestSelected();
        aType = ATYPE.REQUEST;
        processActivatorAccounts();

    }

    private void initEmailUtilities() {
        resourceBundle = Main.getResourceBundle();

        String smtpServer = resourceBundle.getString("smtp_server"),
                sendEmail = resourceBundle.getString("send_activator"),
                emailPassword = resourceBundle.getString("email_password"),
                emailPort = resourceBundle.getString("email_port");

        sentEmailUtil = new EmailUtilities(smtpServer, sendEmail, emailPassword, emailPort);
    }

    private void processActivatorAccounts() {
        new TaskThread(new Task() {
            List<RegistrationBean> regList;
            Long startTime;
            boolean error, removeFilter = true;

            @Override
            public void start() {
                regTModel.removeAll();
                form.setRowCountLabel();
                form.setlabelStatus(STATUS.FSERVER.toString());
                form.toggleActivatorForm(false, true);
            }

            @Override
            public void initialize() {
                startTime = System.nanoTime();
            }

            @Override
            public void doInBackground() {
                try {
                    form.setlabelStatus(STATUS.MSG_LOADING.toString());
                    form.setApproveSaveButton("Save");

                    switch (aType) {
                        case REQUEST:
                            form.setApproveSaveButton("Approve");
                            regList = regQuries.getAllRequestBean(showAllRequest);
                            break;
                        case UPDATE:
                            regList = regQuries.findNeed2UpdateAccount();
                            break;
                        case ACTIVE:
                            if (form.checkSearchPanel()) {
                                String searchVal = form.getSearchString();
                                if (searchVal.isEmpty()) {
                                    regList = regQuries.findAllApprovedAccount();
                                } else {
                                    regList = regQuries.findAllActiveByQuery(searchVal);
                                }
                            } else {
                                regList = new ArrayList<>();
                            }
                            form.addFilterPanel();
                            removeFilter = false;
                            break;
                        case EXPIRE:
                            regList = regQuries.findExpiringAccounts();
                            break;
                        case DUPLICATE:
                            regList = regQuries.findDuplicateAccounts();
                            break;
                    }
                    if (removeFilter) {
                        form.clearSearchBox();
                        form.removeSearchPanel();
                    } else {
                        form.setSearchPanelFocus();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    regList = new ArrayList<>();
                    error = true;
                }

                int msgSize;
                if ((msgSize = regList.size()) > 0) {
                    regTModel.addBeanlsToModelOrder(regList);
                }
                System.out.println("Account count: " + msgSize);
            }

            @Override
            public void finished() {
                if (error) {
                    form.showConnectionError();
                    form.setlabelStatus("Error...");
                } else {
                    form.toggleActivatorForm(true, false);
                    if (ATYPE.REQUEST == aType) {
                        form.toggleShowAllRequest(true);
                    }
                    form.setlabelStatus("");
                    form.setRowCountLabel();
                    form.repaint();
                }
                System.out.println("Account Time: "
                        + StringUtilities.nanoTime2HumanReadable(System.nanoTime() - startTime));
            }
        }).start();
    }

    private boolean sendMessage(EmailBean bean) {
        StringBuilder builder = new StringBuilder();
        builder.append("<html><body>");
        builder.append("<center><font style='font-size:18pt;color:red'><b>**PLEASE DO NOT REPLY TO THIS EMAIL**</b></font></center><br/>");
        builder.append("<br/>You may now use your Transcription Reference.");
        builder.append("<br/><br/><br/><br/>");

        builder.append("<table border=\"2\" width=\"70%\">");
        builder.append("<tr><td width=\"30%\"><b>Product:</b></td>");
        builder.append("<td width=\"70%\"><font style='color:red'>");
        builder.append(bean.getProductName()).append(" ");
        builder.append(bean.getProductVersion());
        builder.append("</font></td></tr>");
        builder.append("<tr><td><b>Registered to:</b></td>");
        builder.append("<td><font style='color:red'>");
        builder.append(bean.getFullName());
        builder.append("</font></td></tr>");
        builder.append("<tr><td><b>Activated Plugin/s:</b></td>");
        builder.append("<td><font style='color:red'>");
        for (PluginBean pb : form.getAPlugins()) {
            builder.append(pb.getPluginName());
            builder.append("<br/>");
        }
        builder.append("</font></td></tr>");
        builder.append("</table><br/><br/> ");

        builder.append("<br/><br/><br/>");
        builder.append("<center><font style='font-size:18pt;color:red'><b>**PLEASE DO NOT REPLY TO THIS EMAIL**</b></font></center>");
        builder.append("</body></html>");

        try {
            return sentEmailUtil.sendGMail(bean.getEmail(),
                    "Activation for " + bean.getProductName() + " " + bean.getProductVersion(), builder.toString(), true);
        } catch (Exception e) {
            if (e.toString().contains("Daily sending quota exceeded")) {
                if (msgExceeded == null) {
                    setSecondMail();
                    try {
                        return sendMessageMethod(bean, builder.toString());
                    } catch (Exception e1) {
                        if (e1.toString().contains("Daily sending quota exceeded")) {
                            setThirdMail();
                            try {
                                return sendMessageMethod(bean, builder.toString());
                            } catch (Exception e2) {
                                if (e2.toString().contains("Daily sending quota exceeded")) {
                                    form.showQuotaExceeded();
                                }
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }
                } else {
                    if (msgExceeded.booleanValue()) {
                        form.showQuotaExceeded();
                        return false;
                    } else {
                        setThirdMail();
                        try {
                            return sendMessageMethod(bean, builder.toString());
                        } catch (Exception e2) {
                            if (e2.toString().contains("Daily sending quota exceeded")) {
                                form.showQuotaExceeded();
                            }
                            return false;
                        }
                    }
                }
            } else {
                e.printStackTrace();
                return false;
            }
        }
    }

    private void setSecondMail() {
        System.out.println("setting for 2nd mail...");
        msgExceeded = Boolean.FALSE;
        sentEmailUtil.setEmailAddress(resourceBundle.getString("send_activator_backup"));
        sentEmailUtil.setEmailPassword(resourceBundle.getString("email_password2"));
    }

    private void setThirdMail() {
        System.out.println("setting for 3rd mail...");
        msgExceeded = Boolean.TRUE;
        sentEmailUtil.setEmailAddress(resourceBundle.getString("send_activator_beta"));
        sentEmailUtil.setEmailPassword(resourceBundle.getString("email_password"));
    }

    private boolean sendMessageMethod(EmailBean bean, String message) throws Exception {
        return sentEmailUtil.sendGMail(bean.getEmail(),
                "Activation for " + bean.getProductName() + " " + bean.getProductVersion(), message, true);

    }

    private boolean setRegistrationDetails(RegistrationBean bean) {
        if (bean == null) {
            form.setProductInfo("");
            form.setExpirationDate("");
            form.setLastLoginDate("");
            form.setFullName("");
            form.setPhoneInfo("");
            form.setEmailAddInfo("");
            form.setIPAddInfo("");
            form.setOSInfo("");
            form.setJavaInfo("");
            form.setComputerInfo("");
            form.setProfileInfo("");
            form.setHDInfo("");
            form.setMotherBoradInfo("");
            form.clearPlugins();
            return false;
        }

        boolean notifyIp = false;
        PersonBean pBean = bean.getPersonid();
        MachineBean mBean = bean.getMachineid();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");

        form.populateAvailablePlugins(initAllPlugins());
        form.setProductInfo(bean.getProductid().toString());
        form.setExpirationDate(sdf.format(bean.getExpirationdate()));
        form.setLastLoginDate(bean.getLastlogin() != null ? sdf.format(bean.getLastlogin()) : "");
        form.setFullName(pBean.getName());
        form.setPhoneInfo(pBean.getPhonenumber());
        form.setEmailAddInfo(pBean.getEmail());
        String ipAdd = pBean.getLocation();

        if (!ipAdd.isEmpty()) {
            ipChecker.setIpAddress(ipAdd);
            if (ipChecker.isValidIPFormat() && !ipChecker.isInRange()) {
                System.out.println("IP is not in the philippines block");
                form.showIPCheckerWarning();
            }
        }

        form.setIPAddInfo(ipAdd);

        form.setOSInfo(mBean.getOsversion());
        form.setJavaInfo(mBean.getJavaversion());
        form.setComputerInfo(mBean.getComputername());
        form.setProfileInfo(mBean.getProfilename());
        form.setHDInfo(mBean.getHarddisk());
        form.setMotherBoradInfo(mBean.getMotherboard());
        int ass, app;

        try {
            String request = bean.getProductid().getProductrequest();
            ass = request.contains("0x")
                    ? Integer.valueOf(request.substring(2, request.length()), 16)
                    : Integer.parseInt(request);

        } catch (Exception e) {
            ass = 0;
        }

        form.setAssociatedPlugins(initAssoPlugins(ass));

        if (!ATYPE.REQUEST.equals(aType)) {
            try {
                String approved = bean.getProductid().getProductvalue();
                app = approved.contains("0x")
                        ? Integer.valueOf(approved.substring(2, approved.length()), 16)
                        : Integer.parseInt(approved);
            } catch (Exception e) {
                app = 0;
            }

            form.setMarkedPlugins(initAssoPlugins(app));
        }

        return notifyIp;
    }

    private List<PluginBean> initAllPlugins() {
        switch (program) {
            case REFERENCE:
                if (!plugins.containsKey(PROGRAM.REFERENCE)) {
                    plugins.put(PROGRAM.REFERENCE, PluginController.INSTANCE.getAllPlugins());
                }
                break;
            case DRUGS:
                if (!plugins.containsKey(PROGRAM.DRUGS)) {
                    plugins.put(PROGRAM.DRUGS, DictionaryController.INSTANCE.getAllDictionary());
                }
                break;
            case CLIENT:
                if (!plugins.containsKey(PROGRAM.CLIENT)) {
                    plugins.put(PROGRAM.CLIENT, ClientController.INSTANCE.getAllPrograms());
                }
                break;
            default:
                plugins.put(null, new ArrayList<PluginBean>());
        }
        return plugins.get(program);
    }

//    New request plugin will be marked
    private List<PluginBean> initMarkedPlugins(int approved, int request) {
        List<PluginBean> marked = new ArrayList<>();
        for (PluginBean pb : initAllPlugins()) {
            if ((pb.getPluginValue() & request) == pb.getPluginValue()) {
                if (((pb.getPluginValue() & approved) == pb.getPluginValue())) {
                    System.out.println("ApprovedPlugin: " + pb);
                } else {
                    marked.add(pb);
                }
            }
        }
        return marked;
    }

    private List<PluginBean> initAssoPlugins(int associated) {
        List<PluginBean> pl = new ArrayList<>();
        for (PluginBean pb : initAllPlugins()) {
            if ((pb.getPluginValue() & associated) == pb.getPluginValue()) {
                pl.add(pb);
            }
        }
        return pl;
    }
}
