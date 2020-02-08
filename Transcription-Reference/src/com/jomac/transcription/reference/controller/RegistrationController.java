package com.jomac.transcription.reference.controller;

import com.hccs.util.ComputerInfo;
import com.hccs.util.Task;
import com.hccs.util.TaskThread;
import com.jomac.transcription.reference.Main;
import com.jomac.transcription.reference.forms.RegistrationForm;
import com.jomac.transcription.reference.jpa.models.ActivatorBean;
import com.jomac.transcription.reference.jpa.models.MachineBean;
import com.jomac.transcription.reference.jpa.models.PersonBean;
import com.jomac.transcription.reference.jpa.models.ProductBean;
import com.jomac.transcription.reference.jpa.models.RegistrationBean;
import com.jomac.transcription.reference.queries.ActivatorQueries;
import com.jomac.transcription.reference.queries.RegistrationQueries;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;

public class RegistrationController {

    private RegistrationForm form;
    private String computerName, userName, schema_version,
            productName, productVersion, motherBoardSN,
            hdSN, osInfo, javaInfo, externalIP;
    private String fullName, phoneNum, emailAddress;
    private static final ResourceBundle bundle = Main.getResourceBundle();

    public RegistrationController(final Frame parent) {

        form = new RegistrationForm(parent, true);
        form.addRequestCodeAddActionListerner(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (form.validateRegistrationInfo()) {
                    new TaskThread(new Task() {
                        boolean valid;
                        RegistrationQueries remoteQuries;
                        ActivatorQueries localQueries;

                        @Override
                        public void initialize() {
                            form.toggleRegistrationForm(false);
                            initHardwareInfo();

                            try {
                                localQueries = new ActivatorQueries();
                                remoteQuries = new RegistrationQueries();
                            } catch (Exception e) {
                                valid = false;
                            }
                        }

                        @Override
                        public void doInBackground() {
                            if (localQueries == null || remoteQuries == null) {
                                valid = false;
                                return;
                            }

                            MachineBean mBean = new MachineBean();
                            mBean.setComputername(computerName);
                            mBean.setHarddisk(hdSN);
                            mBean.setJavaversion(javaInfo);
                            mBean.setMotherboard(motherBoardSN);
                            mBean.setOsversion(osInfo);
                            mBean.setProfilename(userName);

                            PersonBean pBean = new PersonBean();
                            pBean.setName(fullName);
                            pBean.setPhonenumber(phoneNum);
                            pBean.setEmail(emailAddress);
                            pBean.setLocation(externalIP);

                            ProductBean prBean = new ProductBean();
                            prBean.setName(productName);
                            prBean.setProductschema(schema_version);
                            prBean.setVersion(productVersion);
                            prBean.setProductrequest("0x" + Integer.toHexString(form.getSelectedPlugins()));
                            prBean.setProductvalue("0");

                            Calendar c = Calendar.getInstance();
                            c.setTime(new Date());
                            c.add(Calendar.DATE, 30);

                            RegistrationBean rBean = new RegistrationBean();
                            rBean.setMachineid(mBean);
                            rBean.setPersonid(pBean);
                            rBean.setProductid(prBean);
                            rBean.setExpirationdate(c.getTime());
                            rBean.setRegistrationdate(new Date());

                            if (remoteQuries.save(rBean)) {
                                System.out.println("Registration Success!");
                                ActivatorBean bean = new ActivatorBean();
                                bean.setExpirationdate(c.getTime());
                                bean.setRegistrationid(rBean.getRegistrationid());
                                localQueries.save(bean);
                                valid = true;
                            } else {
                                valid = false;
                            }
                        }

                        @Override
                        public void finished() {
                            String content;
                            if (valid) {
                                if (form.isVisible()) {
                                    form.clearRegistrationFields();
                                    saveUserPreference();
                                }
                                content = "Registration Success!\n"
                                        + "Please wait for your confirmation";
                            } else {
                                content = "Please Contact your System Administrator \n"
                                        + "for problems regarding Registration";
                            }

                            JOptionPane.showMessageDialog(
                                    form,
                                    content,
                                    "Program Registration", valid
                                            ? JOptionPane.INFORMATION_MESSAGE
                                            : JOptionPane.ERROR_MESSAGE
                            );
                            if (valid) {
                                form.dispose();
                            } else {
                                form.toggleRegistrationForm(true);
                            }
                        }
                    }).start();
                }
            }
        });

        form.toggleRegistrationForm(true);
    }

    private void saveUserPreference() {
        Preferences preferences = Main.getPreferences();
        preferences.put("fname", fullName);
        preferences.put("cnum", phoneNum);
        preferences.put("eadd", emailAddress);
        try {
            preferences.flush();
        } catch (Exception e) {
        }
    }

    public void showForm(boolean standalone) {
        //<editor-fold defaultstate="collapsed" desc="for standalone testing">
        if (standalone) {
            form.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
        }
        form.setLocationRelativeTo(form.getParent());
        form.setVisible(true);

        //</editor-fold>
    }

    private void initHardwareInfo() {
        ComputerInfo id = new ComputerInfo();
        computerName = id.getComputerName();
        userName = id.getUserName();
        motherBoardSN = id.getMotherBoardSN();
        hdSN = id.getHardDiskSN();
        osInfo = id.getOSInfo();
        javaInfo = id.getJavaInfo();
        externalIP = id.getExternalIP();

        fullName = form.getFullName();
        phoneNum = form.getPhoneNumber();
        emailAddress = form.getEmailAddress();

        productName = bundle.getString("product_name");
        productVersion = bundle.getString("product_version");
        schema_version = bundle.getString("schema_version");
    }
}
