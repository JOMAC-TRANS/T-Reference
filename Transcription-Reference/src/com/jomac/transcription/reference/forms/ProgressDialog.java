package com.jomac.transcription.reference.forms;

import com.hccs.util.ComponentUtils;
import com.hccs.util.TaskProgress;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 *
 * @author DSalenga
 */
public class ProgressDialog extends javax.swing.JDialog {

    private final List<TaskProgress> tasks;
    private final List<TaskProgress> tempTasks;
    private int totalProgress = 0;

    public ProgressDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        tasks = new ArrayList<>();
        tempTasks = new ArrayList<>();
    }

    public void addProgressTask_(final int taskCtr, final TaskProgress task) {
        ComponentUtils.invokeInEDT(new Runnable() {
            @Override
            public void run() {
                tempTasks.add(task);
                final JLabel lbl = new JLabel();
                lbl.setName(task.getName());
                lbl.setText(task.getRemainingTime());
                addComponent(1, taskCtr, task);
                addComponent(0, taskCtr, lbl);
            }
        });
        updateProgressBar_(task.getName(), task.getRemainingTime(), task.getValue());
    }

    public void removeProgress_(final String progressName) {
        ComponentUtils.invokeInEDT(new Runnable() {
            @Override
            public void run() {
                final Iterator<TaskProgress> elements = tasks.iterator();
                while (elements.hasNext()) {
                    final TaskProgress xx = elements.next();
                    if (xx.getName().equalsIgnoreCase(progressName)) {
                        removeComponent(xx.getName());
                        elements.remove();
                    }
                }
            }
        });
    }

    public void updateProgressBar_(final String progressName, final String remainingTime, final int val) {
        ComponentUtils.invokeInEDT(new Runnable() {
            @Override
            public void run() {
                tasks.addAll(tempTasks);
                final Iterator<TaskProgress> elements = tasks.iterator();   //Conversion to avoid concurrent modification for finished task.
                while (elements.hasNext()) {
                    final TaskProgress xx = elements.next();
                    if (xx.getName().equalsIgnoreCase(progressName)) {
                        updateProgressLabel(xx.getName(), remainingTime);
                        if (val == xx.getMaximum()) {
                            removeComponent(xx.getName());
                            elements.remove();
                        }
                        xx.setValue(val);
                    }
                }
                int val = 0;
                for (TaskProgress xx : tasks) {
                    val += xx.getValue();
                }
                totalProgress = tasks.isEmpty() ? 0 : val / tasks.size();
                tempTasks.clear();
            }
        });
    }

    public void updateProgressLabel(String name, final String val) {
        for (final Component xx : pnlInformationalDocs.getComponents()) {
            if (xx instanceof JLabel && xx.getName().equalsIgnoreCase(name)) {
                ComponentUtils.invokeInEDT(new Runnable() {
                    @Override
                    public void run() {
                        ((JLabel) xx).setText(val);
                    }
                });
                break;
            }
        }
    }

    public void removeComponent(String name) {
        for (final Component xx : pnlInformationalDocs.getComponents()) {
            if ((xx instanceof JLabel || xx instanceof TaskProgress) && xx.getName().equalsIgnoreCase(name)) {
                ComponentUtils.invokeInEDT(new Runnable() {
                    @Override
                    public void run() {
                        for (int x = 0; x < 2; x++) {
                            pnlInformationalDocs.remove(xx);
                        }
                    }
                });
            }
        }
    }

    private void addComponent(final int x, final int y, final JComponent c) {
        ComponentUtils.invokeInEDT(new Runnable() {
            @Override
            public void run() {
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.anchor = GridBagConstraints.NORTHWEST;
                gbc.weightx = 1.0;
                gbc.weighty = 0.0;
                gbc.gridx = x;
                gbc.gridy = y;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.anchor = GridBagConstraints.WEST;
                gbc.insets = new java.awt.Insets(0, 5, 2, 5);
                pnlInformationalDocs.add(c, gbc);
            }
        });
    }

    public int getTotalProgress() {
        return totalProgress;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pluginsScroll = new javax.swing.JScrollPane();
        pnlPlugins = new javax.swing.JPanel();
        pnlInformationalDocs = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Plugin Downloader");
        setIconImage(new ImageIcon(getClass().getResource("/com/jomac/transcription/reference/resources/16x16/reference.png")).getImage());
        setMinimumSize(new java.awt.Dimension(350, 250));
        setPreferredSize(new java.awt.Dimension(480, 350));
        getContentPane().setLayout(new java.awt.BorderLayout(20, 20));

        pluginsScroll.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pluginsScroll.setMinimumSize(new java.awt.Dimension(200, 23));

        pnlPlugins.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 10, 10, 10));
        pnlPlugins.setLayout(new java.awt.BorderLayout());

        pnlInformationalDocs.setMinimumSize(new java.awt.Dimension(0, 300));
        pnlInformationalDocs.setLayout(new java.awt.GridBagLayout());
        pnlPlugins.add(pnlInformationalDocs, java.awt.BorderLayout.NORTH);

        pluginsScroll.setViewportView(pnlPlugins);

        getContentPane().add(pluginsScroll, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ProgressDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ProgressDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ProgressDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ProgressDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ProgressDialog dialog = new ProgressDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane pluginsScroll;
    private javax.swing.JPanel pnlInformationalDocs;
    private javax.swing.JPanel pnlPlugins;
    // End of variables declaration//GEN-END:variables
}
