/*
 * ListSelectorPanel.java
 *
 * Created on March 01, 2014, 03:22 PM
 */
package com.jomac.transcription.reference.forms;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.*;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.UIManager;

public class ListSelectorPanel extends javax.swing.JPanel {

    private Object[] availableValue, selectedValue;
    private List<Object> markedValue;
    private boolean revertMarkedValue = false;

    public ListSelectorPanel() {
        initComponents();

        lstAvailable.setModel(new DefaultListModel());
        lstSelected.setModel(new DefaultListModel());
        lstSelected.setCellRenderer(new MarkedItemListRenderer());
    }

    public void toggleForm(final boolean enabled) {
        btnAddAllToSelected.setEnabled(enabled);
        btnAddToSelected.setEnabled(enabled);
        btnRemoveAllFromSelected.setEnabled(enabled);
        btnRemoveFromSelected.setEnabled(enabled);
        lstAvailable.setEnabled(enabled);
        lstSelected.setEnabled(enabled);
        txtAvailable.setEnabled(enabled);
        txtSelected.setEnabled(enabled);
    }

    public void clear() {
        txtAvailable.setText("");
        txtSelected.setText("");
        lblAvailable.setText("Available: 0");
        ((DefaultListModel) lstAvailable.getModel()).clear();
        lblSelected.setText("Selected: 0");
        ((DefaultListModel) lstSelected.getModel()).clear();
        if (markedValue != null) {
            markedValue.clear();
        }
    }

    public void moveValues(
            Object[] values,
            final JList lstSource,
            final JList lstDestination) {
        if (values == null) {
            return;
        }

        final DefaultListModel srcModel = new DefaultListModel(),
                destModel = new DefaultListModel();
        Object[] sourceV, destinationV;

        if (lstSource.equals(lstAvailable)) {
            sourceV = txtAvailable.getText().isEmpty() ? ((DefaultListModel) lstSource.getModel()).toArray() : availableValue;
            destinationV = txtSelected.getText().isEmpty() ? ((DefaultListModel) lstDestination.getModel()).toArray() : selectedValue;
        } else {
            sourceV = txtSelected.getText().isEmpty() ? ((DefaultListModel) lstSource.getModel()).toArray() : selectedValue;
            destinationV = txtAvailable.getText().isEmpty() ? ((DefaultListModel) lstDestination.getModel()).toArray() : availableValue;
        }

        // Clone models and process list in cloned models to prevent ArrayIndexOutOfBounds
        for (Object xx : sourceV) {
            srcModel.addElement(xx);
        }
        for (Object xx : destinationV) {
            destModel.addElement(xx);
        }

        SortedSet<Object> set = new TreeSet<Object>(new OrderComparator());

        for (Object xx : values) {
            set.add(xx);
            srcModel.removeElement(xx);
        }

        set.addAll(Arrays.asList(destModel.toArray()));

        destModel.clear();
        for (Object xx : set.toArray()) {
            destModel.addElement(xx);
        }

        lstSource.setModel(srcModel);
        lstDestination.setModel(destModel);

        lblAvailable.setText("Available: " + lstAvailable.getModel().getSize());
        lblSelected.setText("Selected: " + lstSelected.getModel().getSize());

        availableValue = ((DefaultListModel) lstAvailable.getModel()).toArray();
        selectedValue = ((DefaultListModel) lstSelected.getModel()).toArray();
        txtAvailable.setText("");
        txtSelected.setText("");
    }

    public void setMarkedValues(List<Object> values) {
        if (values == null) {
            return;
        }
        markedValue = values;
    }

    public void setAvailableValues(Object[] values) {
        if (values == null) {
            return;
        }

        final DefaultListModel model = new DefaultListModel();

        SortedSet<Object> set = new TreeSet<Object>(new OrderComparator());

        set.addAll(Arrays.asList(values));

        for (Object xx : set.toArray()) {
            model.addElement(xx);
        }

        availableValue = values;
        lstAvailable.setModel(model);
        lblAvailable.setText("Available: " + model.size());
        lstAvailable.revalidate();
        lstSelected.revalidate();
    }

    public void setSelectedValues(Object[] values) {
        if (values == null) {
            return;
        }

        ((DefaultListModel) lstSelected.getModel()).clear();

        selectedValue = values;
        moveValues(values, lstAvailable, lstSelected);
        repaint();
        revalidate();
    }

    public void addToSelectedValues(Object[] values) {
        if (values == null) {
            return;
        }
        moveValues(values, lstAvailable, lstSelected);
        repaint();
        revalidate();
    }

    public List<Object> getSelectedValues() {
        List<Object> selectedValues = new ArrayList<>();

        for (int i = 0; i < lstSelected.getModel().getSize(); i++) {
            selectedValues.add(lstSelected.getModel().getElementAt(i));
        }
        return selectedValues;
    }

    public void removeSelectedValues() {
        btnRemoveAllFromSelectedActionPerformed(null);
    }

    public void filterSelection(JList listSelector, Object[] selections, String query) {
        DefaultListModel model = new DefaultListModel();

        for (int i = 0; i < selections.length; i++) {
            if (selections[i] != null) {
                if (selections[i].toString().toLowerCase().matches("^" + query.toLowerCase() + ".*")) {
                    model.addElement(selections[i]);
                }
            }
        }
        listSelector.setModel(model);

        if (listSelector.getModel().getSize() == 1) {
            listSelector.setSelectedIndex(0);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        ListSelectorPanel lsp = new ListSelectorPanel();
        lsp.revertTheMarkedValue(true);
        frmTest.add(lsp, java.awt.BorderLayout.CENTER);
        frmTest.setSize(400, 300);
        frmTest.setVisible(true);
    }

    public void revertTheMarkedValue(boolean revertMarkedValue) {
        this.revertMarkedValue = revertMarkedValue;
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
        miAddToSelected = new javax.swing.JMenuItem();
        miRemoveFromSelected = new javax.swing.JMenuItem();
        miAddAllToSelected = new javax.swing.JMenuItem();
        miRemoveAllFromSelected = new javax.swing.JMenuItem();
        frmTest = new javax.swing.JFrame();
        jPanel1 = new javax.swing.JPanel();
        btnAddTestData = new javax.swing.JButton();
        btnEven = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        lblAvailable = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        btnAddToSelected = new javax.swing.JButton();
        btnAddAllToSelected = new javax.swing.JButton();
        txtAvailable = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstAvailable = new javax.swing.JList();
        jPanel8 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        lblSelected = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        btnRemoveFromSelected = new javax.swing.JButton();
        btnRemoveAllFromSelected = new javax.swing.JButton();
        txtSelected = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstSelected = new javax.swing.JList();

        miAddToSelected.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/reference/resources/16x16/1rightarrow.png"))); // NOI18N
        miAddToSelected.setText("Add to Selected");
        miAddToSelected.setEnabled(false);
        miAddToSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miAddToSelectedActionPerformed(evt);
            }
        });
        popMenu.add(miAddToSelected);

        miRemoveFromSelected.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/reference/resources/16x16/1leftarrow.png"))); // NOI18N
        miRemoveFromSelected.setText("Remove From Selected");
        miRemoveFromSelected.setEnabled(false);
        miRemoveFromSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miRemoveFromSelectedActionPerformed(evt);
            }
        });
        popMenu.add(miRemoveFromSelected);

        miAddAllToSelected.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/reference/resources/16x16/2rightarrow.png"))); // NOI18N
        miAddAllToSelected.setText("Add All to Selected");
        miAddAllToSelected.setEnabled(false);
        miAddAllToSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miAddAllToSelectedActionPerformed(evt);
            }
        });
        popMenu.add(miAddAllToSelected);

        miRemoveAllFromSelected.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/reference/resources/16x16/2leftarrow.png"))); // NOI18N
        miRemoveAllFromSelected.setText("Remove All From Selected");
        miRemoveAllFromSelected.setEnabled(false);
        miRemoveAllFromSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miRemoveAllFromSelectedActionPerformed(evt);
            }
        });
        popMenu.add(miRemoveAllFromSelected);

        frmTest.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        frmTest.setTitle("ListSelectorPanel Test");
        frmTest.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                frmTestWindowClosing(evt);
            }
        });

        btnAddTestData.setText("Add Test Data");
        btnAddTestData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddTestDataActionPerformed(evt);
            }
        });
        jPanel1.add(btnAddTestData);

        btnEven.setText("Mark Even Value");
        btnEven.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEvenActionPerformed(evt);
            }
        });
        jPanel1.add(btnEven);

        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });
        jPanel1.add(btnClear);

        frmTest.getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

        setLayout(new java.awt.GridLayout(1, 3, 4, 0));

        jPanel6.setLayout(new java.awt.BorderLayout(0, 4));

        jPanel2.setLayout(new java.awt.BorderLayout(0, 5));

        lblAvailable.setText("Available: 0");
        jPanel2.add(lblAvailable, java.awt.BorderLayout.CENTER);

        jPanel3.setLayout(new java.awt.GridLayout(1, 2, 2, 0));

        btnAddToSelected.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/reference/resources/16x16/1rightarrow.png"))); // NOI18N
        btnAddToSelected.setToolTipText("Add to Selected");
        btnAddToSelected.setEnabled(false);
        btnAddToSelected.setFocusable(false);
        btnAddToSelected.setPreferredSize(new java.awt.Dimension(25, 22));
        btnAddToSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddToSelectedActionPerformed(evt);
            }
        });
        jPanel3.add(btnAddToSelected);

        btnAddAllToSelected.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/reference/resources/16x16/2rightarrow.png"))); // NOI18N
        btnAddAllToSelected.setToolTipText("Add All to Selected");
        btnAddAllToSelected.setEnabled(false);
        btnAddAllToSelected.setFocusable(false);
        btnAddAllToSelected.setPreferredSize(new java.awt.Dimension(25, 22));
        btnAddAllToSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddAllToSelectedActionPerformed(evt);
            }
        });
        jPanel3.add(btnAddAllToSelected);

        jPanel2.add(jPanel3, java.awt.BorderLayout.EAST);

        txtAvailable.setEnabled(false);
        txtAvailable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAvailableKeyReleased(evt);
            }
        });
        jPanel2.add(txtAvailable, java.awt.BorderLayout.SOUTH);

        jPanel6.add(jPanel2, java.awt.BorderLayout.NORTH);

        lstAvailable.setEnabled(false);
        lstAvailable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lstAvailableMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstAvailableMouseClicked(evt);
            }
        });
        lstAvailable.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstAvailableValueChanged(evt);
            }
        });
        lstAvailable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                lstAvailableKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(lstAvailable);

        jPanel6.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        add(jPanel6);

        jPanel8.setLayout(new java.awt.BorderLayout(0, 4));

        jPanel4.setLayout(new java.awt.BorderLayout(0, 5));

        lblSelected.setText("Selected: 0");
        jPanel4.add(lblSelected, java.awt.BorderLayout.CENTER);

        jPanel5.setLayout(new java.awt.GridLayout(1, 2, 2, 0));

        btnRemoveFromSelected.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/reference/resources/16x16/1leftarrow.png"))); // NOI18N
        btnRemoveFromSelected.setToolTipText("Remove From Selected");
        btnRemoveFromSelected.setEnabled(false);
        btnRemoveFromSelected.setFocusable(false);
        btnRemoveFromSelected.setPreferredSize(new java.awt.Dimension(25, 22));
        btnRemoveFromSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveFromSelectedActionPerformed(evt);
            }
        });
        jPanel5.add(btnRemoveFromSelected);

        btnRemoveAllFromSelected.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jomac/transcription/reference/resources/16x16/2leftarrow.png"))); // NOI18N
        btnRemoveAllFromSelected.setToolTipText("Remove All From Selected");
        btnRemoveAllFromSelected.setEnabled(false);
        btnRemoveAllFromSelected.setFocusable(false);
        btnRemoveAllFromSelected.setPreferredSize(new java.awt.Dimension(25, 22));
        btnRemoveAllFromSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveAllFromSelectedActionPerformed(evt);
            }
        });
        jPanel5.add(btnRemoveAllFromSelected);

        jPanel4.add(jPanel5, java.awt.BorderLayout.EAST);

        txtSelected.setEnabled(false);
        txtSelected.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSelectedKeyReleased(evt);
            }
        });
        jPanel4.add(txtSelected, java.awt.BorderLayout.SOUTH);

        jPanel8.add(jPanel4, java.awt.BorderLayout.NORTH);

        lstSelected.setEnabled(false);
        lstSelected.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lstSelectedMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstSelectedMouseClicked(evt);
            }
        });
        lstSelected.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstSelectedValueChanged(evt);
            }
        });
        lstSelected.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                lstSelectedKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(lstSelected);

        jPanel8.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        add(jPanel8);
    }// </editor-fold>//GEN-END:initComponents

private void btnAddToSelectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddToSelectedActionPerformed
    Object[] values = lstAvailable.getSelectedValues();
    moveValues(values, lstAvailable, lstSelected);
}//GEN-LAST:event_btnAddToSelectedActionPerformed

private void btnRemoveFromSelectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveFromSelectedActionPerformed
    Object[] values = lstSelected.getSelectedValues();
    moveValues(values, lstSelected, lstAvailable);
}//GEN-LAST:event_btnRemoveFromSelectedActionPerformed

private void btnAddAllToSelectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddAllToSelectedActionPerformed
    DefaultListModel model = (DefaultListModel) lstAvailable.getModel();
    moveValues(model.toArray(), lstAvailable, lstSelected);
}//GEN-LAST:event_btnAddAllToSelectedActionPerformed

private void btnRemoveAllFromSelectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveAllFromSelectedActionPerformed
    DefaultListModel model = (DefaultListModel) lstSelected.getModel();
    moveValues(model.toArray(), lstSelected, lstAvailable);
}//GEN-LAST:event_btnRemoveAllFromSelectedActionPerformed

private void miAddToSelectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miAddToSelectedActionPerformed
    btnAddToSelectedActionPerformed(null);
}//GEN-LAST:event_miAddToSelectedActionPerformed

private void miRemoveFromSelectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miRemoveFromSelectedActionPerformed
    btnRemoveFromSelectedActionPerformed(null);
}//GEN-LAST:event_miRemoveFromSelectedActionPerformed

private void miAddAllToSelectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miAddAllToSelectedActionPerformed
    btnAddAllToSelectedActionPerformed(null);
}//GEN-LAST:event_miAddAllToSelectedActionPerformed

private void miRemoveAllFromSelectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miRemoveAllFromSelectedActionPerformed
    btnRemoveAllFromSelectedActionPerformed(null);
}//GEN-LAST:event_miRemoveAllFromSelectedActionPerformed

private void lstAvailableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstAvailableMouseReleased
    if (evt.isPopupTrigger()) {
        popMenu.show(lstAvailable, evt.getX(), evt.getY());
    }
}//GEN-LAST:event_lstAvailableMouseReleased

private void lstSelectedMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstSelectedMouseReleased
    if (evt.isPopupTrigger()) {
        popMenu.show(lstSelected, evt.getX(), evt.getY());
    }
}//GEN-LAST:event_lstSelectedMouseReleased

private void lstAvailableValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstAvailableValueChanged
    if (evt.getValueIsAdjusting() || lstAvailable.getSelectedValue() == null) {
        btnAddToSelected.setEnabled(false);
        miAddToSelected.setEnabled(false);
    } else {
        btnAddToSelected.setEnabled(true);
        miAddToSelected.setEnabled(true);
    }
}//GEN-LAST:event_lstAvailableValueChanged

private void lstSelectedValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstSelectedValueChanged
    if (evt.getValueIsAdjusting() || lstSelected.getSelectedValue() == null) {
        btnRemoveFromSelected.setEnabled(false);
        miRemoveFromSelected.setEnabled(false);
    } else {
        btnRemoveFromSelected.setEnabled(true);
        miRemoveFromSelected.setEnabled(true);
    }
}//GEN-LAST:event_lstSelectedValueChanged

private void btnAddTestDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddTestDataActionPerformed
    int nCount = 16;
    Integer[] data = new Integer[nCount];
    Integer[] data2 = new Integer[nCount / 2];

    clear();
    toggleForm(true);

    for (int i = 0; i < nCount; i++) {
        data[i] = new Integer(i);
    }
    setAvailableValues(data);
    System.arraycopy(data, 0, data2, 0, data2.length);
    setSelectedValues(data2);
}//GEN-LAST:event_btnAddTestDataActionPerformed

private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
    clear();
}//GEN-LAST:event_btnClearActionPerformed

private void lstAvailableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstAvailableMouseClicked
    if (evt.getClickCount() >= 2) {
        btnAddToSelectedActionPerformed(null);
    }
}//GEN-LAST:event_lstAvailableMouseClicked

private void lstSelectedMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstSelectedMouseClicked
    if (evt.getClickCount() >= 2) {
        btnRemoveFromSelectedActionPerformed(null);
    }
}//GEN-LAST:event_lstSelectedMouseClicked

private void frmTestWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_frmTestWindowClosing
{//GEN-HEADEREND:event_frmTestWindowClosing
    frmTest.dispose();
}//GEN-LAST:event_frmTestWindowClosing

    private void txtAvailableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAvailableKeyReleased
        filterSelection(lstAvailable, availableValue, txtAvailable.getText().trim());
        if (KeyEvent.VK_ENTER == evt.getKeyCode() && lstAvailable.getSelectedValue() != null) {
            lstAvailable.requestFocus();
            btnAddToSelectedActionPerformed(null);
            txtAvailable.requestFocus();
        } else if (KeyEvent.VK_DOWN == evt.getKeyCode()) {
            lstAvailable.requestFocus();
        }
    }//GEN-LAST:event_txtAvailableKeyReleased

    private void txtSelectedKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSelectedKeyReleased
        filterSelection(lstSelected, selectedValue, txtSelected.getText().trim());
        if (KeyEvent.VK_ENTER == evt.getKeyCode() && lstSelected.getSelectedValue() != null) {
            lstSelected.requestFocus();
            btnRemoveFromSelectedActionPerformed(null);
            txtSelected.requestFocus();
        } else if (KeyEvent.VK_DOWN == evt.getKeyCode()) {
            lstSelected.requestFocus();
        }
    }//GEN-LAST:event_txtSelectedKeyReleased

    private void lstAvailableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lstAvailableKeyReleased
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER && lstAvailable.getSelectedValue() != null) {
            btnAddToSelectedActionPerformed(null);
        }
    }//GEN-LAST:event_lstAvailableKeyReleased

    private void lstSelectedKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lstSelectedKeyReleased
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER && lstSelected.getSelectedValue() != null) {
            btnRemoveFromSelectedActionPerformed(null);
        }
    }//GEN-LAST:event_lstSelectedKeyReleased

    private void btnEvenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEvenActionPerformed
        if (selectedValue == null) {
            return;
        }
        List<Object> marked = new ArrayList<>();

        for (Object x : selectedValue) {
            if (((int) x) % 2 == 0) {
                marked.add(x);
            }
        }
        setMarkedValues(marked);
        lstSelected.repaint();
    }//GEN-LAST:event_btnEvenActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddAllToSelected;
    private javax.swing.JButton btnAddTestData;
    private javax.swing.JButton btnAddToSelected;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnEven;
    private javax.swing.JButton btnRemoveAllFromSelected;
    private javax.swing.JButton btnRemoveFromSelected;
    private static javax.swing.JFrame frmTest;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblAvailable;
    private javax.swing.JLabel lblSelected;
    private javax.swing.JList lstAvailable;
    private javax.swing.JList lstSelected;
    private javax.swing.JMenuItem miAddAllToSelected;
    private javax.swing.JMenuItem miAddToSelected;
    private javax.swing.JMenuItem miRemoveAllFromSelected;
    private javax.swing.JMenuItem miRemoveFromSelected;
    private javax.swing.JPopupMenu popMenu;
    private javax.swing.JTextField txtAvailable;
    private javax.swing.JTextField txtSelected;
    // End of variables declaration//GEN-END:variables

    private class OrderComparator implements Comparator, Serializable {

        @Override
        public int compare(Object o1, Object o2) {
            return o1.toString().compareTo(o2.toString());
        }

        @Override
        public boolean equals(Object obj) {
            return this.toString().equals(obj.toString());
        }
    }

    private class MarkedItemListRenderer extends DefaultListCellRenderer {

        private static final long serialVersionUID = 1L;
        private final Font font_ = lstAvailable.getFont();
        private final Color fGround = lstAvailable.getSelectionForeground();
        private final Color bGround = lstAvailable.getSelectionBackground();

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component comp = super.getListCellRendererComponent(list, value, index, false, false);

            if (isSelected & cellHasFocus) {
                comp.setForeground(fGround);
                comp.setBackground(bGround);
            } else if (markedValue != null && !markedValue.isEmpty()) {
                if (revertMarkedValue) {
                    if (markedValue.contains(value)) {
                        comp.setFont(font_);
                        comp.setForeground(Color.BLACK);
                    } else {
                        comp.setForeground(Color.RED);
                        comp.setFont(font_.deriveFont(Font.BOLD));
                    }
                } else {
                    for (Object xx : markedValue) {
                        if (xx.equals(value)) {
                            comp.setForeground(Color.RED);
                            comp.setFont(font_.deriveFont(Font.BOLD));
                            break;
                        }
                    }
                }
            }
            return comp;
        }
    }
}
