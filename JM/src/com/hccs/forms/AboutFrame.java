/*
 * AboutFrame.java
 *
 * Created on 10 23, 10, 3:36:52 PM
 */
package com.hccs.forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import javax.swing.JFrame;
import org.jdesktop.swingx.JXPanel;

public class AboutFrame extends javax.swing.JFrame {

    private JXPanel container;

    /**
     * Creates new form AboutFrame
     */
    public AboutFrame(
            Component parent,
            Image logo,
            String productname,
            String productversion,
            String productvendor,
            String vendorurl,
            String username,
            String host,
            String port,
            Color bgColor1,
            Color bgColor2) {
        initComponents();
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        container = new JXPanel();
        container.setBackgroundPainter(new AboutBGPainter(logo, productname, productversion, productvendor, vendorurl, username, host, port, bgColor1, bgColor2));

        add(BorderLayout.CENTER, container);
    }

    public AboutFrame(
            Component parent,
            Image logo,
            String productname,
            String productversion,
            String productvendor,
            String vendorurl,
            String username,
            String host,
            String port) {
        initComponents();
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        container = new JXPanel();
        container.setBackgroundPainter(new AboutBGPainter(logo, productname, productversion, productvendor, vendorurl, username, host, port));

        add(BorderLayout.CENTER, container);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
                formWindowLostFocus(evt);
            }
        });

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        dispose();
    }//GEN-LAST:event_formMouseClicked

    private void formWindowLostFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowLostFocus
        dispose();
    }//GEN-LAST:event_formWindowLostFocus

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AboutFrame(
                        null,
                        new javax.swing.ImageIcon(AboutFrame.class
                        .getResource("/com/hccs/resources/128x128/testlogo.png")).getImage(),
                        "JOMAC Line Counter",
                        "2.22.09",
                        "JOMAC Corporation",
                        "http://jomac.healthcarecoding.net",
                        "jomac",
                        "192.168.0.150",
                        "1099").setVisible(true);

                /** Uncomment to test AboutFrame with Color parameters.
                new AboutFrame
                (
                    null,
                    new javax.swing.ImageIcon(AboutFrame.class
                             .getResource("/com/hccs/resources/128x128/testlogo2.png")).getImage(),
                    "JOMAC Transcription",
                    "2.22.09",
                    "JOMAC Corporation",
                    "http://jomac.healthcarecoding.net",
                    "jomac",
                    "192.168.0.150",
                    "1099",
                    new Color(0, 198, 255),
                    new Color(0, 84, 255)
                ).setVisible(true);*/
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
