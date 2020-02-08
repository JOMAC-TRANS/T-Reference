package com.jomac.transcription.reference.controller;

import com.hccs.util.ComputerInfo;
import com.jomac.transcription.reference.Main;
import com.jomac.transcription.reference.engines.DBCreator;
import com.jomac.transcription.reference.engines.H2;
import com.jomac.transcription.reference.engines.Postgre;
import com.jomac.transcription.reference.forms.UpdateDialog;
import com.jomac.transcription.reference.jpa.models.ActivatorBean;
import com.jomac.transcription.reference.jpa.models.RegistrationBean;
import com.jomac.transcription.reference.queries.ActivatorQueries;
import com.jomac.transcription.reference.queries.DBQueries;
import com.jomac.transcription.reference.queries.RegistrationQueries;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class LocalController {

    private ActivatorBean bean;
    private long remainingDays;
    private String dbPath, username, password;

    public void setDbPath(String dbPath) {
        this.dbPath = dbPath;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void createDB() throws Exception {
        new DBCreator(dbPath, username, password).create();
    }

    public void validateDB() throws Exception {
        String dbValue = new DBQueries().getSchemaVersion();
        if (dbValue == null || dbValue.isEmpty()) {
            renewDB();
        }

        int localSchema = Integer.parseInt(Main.getResourceBundle().getString("local_schema"));
        int dbSchema = Integer.parseInt(dbValue);

        if (dbSchema < localSchema) {
            renewDB();
        } else if (dbSchema > localSchema) {
            throw new Exception("Client is not updated!");
        }
    }

    public void initChecker() throws Exception {
        ActivatorQueries localQueries = new ActivatorQueries();
        bean = localQueries.getActivatorBean();
        if (bean == null) {
            if (Main.getResourceBundle().getString("product_activation").contains("false")) {
                Main.setActiveApp(true);
                Main.setAssociatedPlugins(7);
                System.out.println("one");
            } else {
                System.out.println("two: " + Main.getResourceBundle().getString("product_activation"));
                RegistrationController rc = new RegistrationController(new JFrame());
                rc.showForm(true);
            }
        } else {
            if (!bean.getActivated() || bean.getPlugins() <= 0 || (bean.getActivated() && !checkPresentDate(bean.getLastlogin()))) {
                RegistrationQueries remoteQueries = new RegistrationQueries();
                RegistrationBean rBean;

                UpdateDialog udiag = new UpdateDialog(null, false);
                udiag.setStatus("Connecting to Transcription Server..."
                        + "\n\nPlease wait..");
                udiag.setLocationRelativeTo(null);
                udiag.setVisible(true);
                int counter = 0;
                do {
                    rBean = remoteQueries.getRegistrationById(bean.getRegistrationid());
                    counter++;
                } while (rBean == null && counter < 3);
                udiag.dispose();

                if (rBean != null) {
                    Main.setRegistrationId(rBean.getRegistrationid());
                    bean.setExpirationdate(rBean.getExpirationdate());
                    bean.setActivated(rBean.getActive() ? !isExpired() : false);
                    String request = rBean.getProductid().getProductvalue();
                    int val;
                    try {
                        val = request.contains("0x")
                                ? Integer.valueOf(request.substring(2, request.length()), 16)
                                : Integer.parseInt(request);

                    } catch (Exception e) {
                        val = 0;
                    }
                    bean.setPlugins(val);
                    bean.setLastlogin(new Date());
                    rBean.getProductid().setVersion(
                            Main.getResourceBundle().getString("product_version"));
                    rBean.setLastlogin(new Date());
                    remoteQueries.save(rBean);
                    localQueries.save(bean);
                    Postgre.closeInstance();
                } else {
                    if (registerProgram() && localQueries.removeActivatorBean(bean)) {
                        RegistrationController rc = new RegistrationController(new JFrame());
                        rc.showForm(true);
                    }
                    Main.terminate();
                }

                if (isExpired()) {
                    String content = "This account is already expired. You may no longer use this account.\n"
                            + "Please contact the program vendor.";
                    JOptionPane.showMessageDialog(
                            null,
                            content,
                            "Program Expiration",
                            JOptionPane.ERROR_MESSAGE);

                    Main.terminate();
                } else {
                    if (inValidMachine(rBean) ? inValidMachine(rBean) : false) {
                        JOptionPane.showMessageDialog(
                                null,
                                "Registration FAILED!\n"
                                + "Invalid Computer Details",
                                "Program Verification",
                                JOptionPane.ERROR_MESSAGE);

                        if (registerProgram() && localQueries.removeActivatorBean(bean)) {
                            RegistrationController rc = new RegistrationController(new JFrame());
                            rc.showForm(true);
                        } else {
                            Main.terminate();
                        }
                    }
                    if (remainingDays <= 3) {
                        JOptionPane.showMessageDialog(
                                null,
                                "<html><b>Warning!<br/><br/>" + (remainingDays + 1)
                                + "</b> day/s remaining<br/>"
                                + "Please contact the program vendor.<br/></html>",
                                "Program Expiration",
                                JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
            Main.setActiveApp(bean.getActivated());
            Main.setExpirationDate(bean.getExpirationdate());
            Main.setRegistrationId(bean.getRegistrationid());
            Main.setAssociatedPlugins(bean.getActivated() ? bean.getPlugins() : 0);
        }
    }

    public boolean updateReferenceDB() {
        boolean valid;
        Main.setDataBaseName(Main.CLIENT_DB);
        ActivatorQueries localQueries = new ActivatorQueries();
        int counter = 0;
        do {
            bean = localQueries.getActivatorBean();
            counter++;
        } while (bean == null && counter < 3);

        if (bean == null) {
            return false;
        } else {

            RegistrationQueries remoteQueries;
            RegistrationBean rBean;
            try {
                remoteQueries = new RegistrationQueries();
                rBean = remoteQueries.getRegistrationById(bean.getRegistrationid());
            } catch (Exception e) {
                return false;
            }

            if (rBean != null) {
                Main.setRegistrationId(rBean.getRegistrationid());
                bean.setExpirationdate(rBean.getExpirationdate());
                bean.setActivated(rBean.getActive() ? !isExpired() : false);
                String request = rBean.getProductid().getProductvalue();
                int val;
                try {
                    val = request.contains("0x")
                            ? Integer.valueOf(request.substring(2, request.length()), 16)
                            : Integer.parseInt(request);

                } catch (Exception e) {
                    val = 0;
                }
                bean.setPlugins(val);
                bean.setLastlogin(new Date());
                rBean.setLastlogin(new Date());
                try {
                    remoteQueries.save(rBean);
                    localQueries.save(bean);
                    valid = true;
                } catch (Exception e) {
                    valid = false;
                } finally {
                    Postgre.closeInstance();
                    H2.closeInstance();
                }
            } else {
                valid = false;
            }

            if (isExpired()) {
                String content = "This account is already expired. You may no longer use this account.\n"
                        + "Please contact the program vendor.";
                JOptionPane.showMessageDialog(
                        null,
                        content,
                        "Program Expiration",
                        JOptionPane.ERROR_MESSAGE);
                Main.terminate();
            } else {
                Main.setActiveApp(bean.getActivated());
                Main.setExpirationDate(bean.getExpirationdate());
                Main.setAssociatedPlugins(bean.getActivated() ? bean.getPlugins() : 0);
            }
        }

        return valid;
    }

    public boolean checkPresentDate(Date oldDate) {
        if (oldDate == null) {
            return false;
        }
        Calendar oldCal = Calendar.getInstance();
        Calendar newCal = Calendar.getInstance();

        oldCal.setTime(oldDate);
        newCal.setTime(new Date());

        // Check for future value
        if (oldCal.after(newCal)) {
            return false;
        }

        return countDiffDay(oldCal, newCal) <= 7;
    }

    public int countDiffDay(Calendar c1, Calendar c2) {
        int counter = 0;
        while (!c1.after(c2)) {
            c1.add(Calendar.DAY_OF_MONTH, 1);
            counter++;
        }

        if (counter > 0) {
            counter = counter - 1;
        }

        return (counter);
    }

    private boolean registerProgram() {
        int selectedOption = JOptionPane.showConfirmDialog(null,
                "SOFTWARE REGISTRATION FAILED!\n\n"
                + "Do you want to register again?",
                "TransRef",
                JOptionPane.YES_NO_OPTION);
        return (selectedOption == JOptionPane.YES_OPTION);
    }

    private void renewDB() throws Exception {
        String newPath = dbPath.concat(".h2.db");
        H2.closeInstance();
        new File(newPath).delete();
        if (new File(newPath).exists()) {
            throw new Exception("Unable to Delete DB");
        } else {
            createDB();
        }
    }

    private boolean inValidMachine(RegistrationBean rBean) {
        ComputerInfo id = new ComputerInfo();

        return (!rBean.getMachineid().getComputername().equalsIgnoreCase(id.getComputerName())
                || !rBean.getMachineid().getProfilename().equalsIgnoreCase(id.getUserName())
                || !rBean.getMachineid().getMotherboard().equalsIgnoreCase(id.getMotherBoardSN())
                || !rBean.getMachineid().getHarddisk().equalsIgnoreCase(id.getHardDiskSN())
                || !rBean.getMachineid().getOsversion().equalsIgnoreCase(id.getOSInfo()));
    }

    private boolean isExpired() {
        if (bean == null) {
            return true;
        }

        Date todayDate = new Date(),
                expirationDate = bean.getExpirationdate();

        if (todayDate.after(expirationDate)) {
            remainingDays = 0;
            return true;
        }
        remainingDays = TimeUnit.DAYS.convert(
                (expirationDate.getTime() - todayDate.getTime()), TimeUnit.MILLISECONDS);

        return false;
    }
}
