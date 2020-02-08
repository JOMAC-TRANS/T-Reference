package com.jomac.transcription.reference.forms;

import com.jomac.transcription.reference.Main;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import javax.swing.TransferHandler;

public class SpecificDialog extends javax.swing.JDialog {

    public SpecificDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        try {
            Dimension d = Main.getController().getMainFrame().getSize();
            setSize((int) (d.getWidth() / 1.5), ((int) (d.getHeight() / 2)));
            setLocationRelativeTo(parent);
        } catch (Exception e) {
        }
    }

    public void setEditorText(String content) {
        epDisplay2.setText(content);
        epDisplay2.setCaretPosition(0);
        epDisplay2.setLineWrap(true);
        epDisplay2.setWrapStyleWord(true);

//        if (content.contains("\n") || content.contains("\\s")) {
//            epDisplay.setText(content.replaceAll("\n", "<br>").replaceAll("\\s", "&nbsp;"));
//        } else {
//            epDisplay.setText(content);
//        }
//        epDisplay.setCaretPosition(0);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popMenu = new javax.swing.JPopupMenu();
        miCopySelected = new javax.swing.JMenuItem();
        jScrollPane2 = new javax.swing.JScrollPane();
        epDisplay = new javax.swing.JEditorPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        epDisplay2 = new javax.swing.JTextArea();

        miCopySelected.setText("Copy to clipboard");
        miCopySelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miCopySelectedActionPerformed(evt);
            }
        });
        popMenu.add(miCopySelected);

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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(480, 280));
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
                formWindowLostFocus(evt);
            }
        });

        epDisplay2.setEditable(false);
        epDisplay2.setColumns(20);
        epDisplay2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        epDisplay2.setRows(5);
        epDisplay2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                epDisplay2MouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(epDisplay2);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void epDisplayMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_epDisplayMouseReleased
        if (evt.isPopupTrigger()) {
            popMenu.show(epDisplay, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_epDisplayMouseReleased

    private void miCopySelectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miCopySelectedActionPerformed
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        TransferHandler transferHandler = epDisplay.getTransferHandler();
        transferHandler.exportToClipboard(epDisplay, clipboard, TransferHandler.COPY);
    }//GEN-LAST:event_miCopySelectedActionPerformed

    private void formWindowLostFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowLostFocus
//        if (test) {
//            System.exit(0);
//        } else {
//            dispose();
//        }
    }//GEN-LAST:event_formWindowLostFocus

    private void epDisplay2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_epDisplay2MouseReleased
        if (evt.isPopupTrigger()) {
            popMenu.show(epDisplay2, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_epDisplay2MouseReleased

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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                SpecificDialog dialog = new SpecificDialog(new javax.swing.JFrame(), true);
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane epDisplay;
    private javax.swing.JTextArea epDisplay2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JMenuItem miCopySelected;
    private javax.swing.JPopupMenu popMenu;
    // End of variables declaration//GEN-END:variables

}