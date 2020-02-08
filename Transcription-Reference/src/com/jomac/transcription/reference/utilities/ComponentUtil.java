/*
 * ComponentUtil.java
 *
 * Created on 06 28, 2012, 10:25:52 AM
 */
package com.jomac.transcription.reference.utilities;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.text.JTextComponent;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

public class ComponentUtil {

    public static ComponentUtil getInstance() {
        return ComponentUtilHolder.INSTANCE;
    }

    private static class ComponentUtilHolder {

        private static final ComponentUtil INSTANCE = new ComponentUtil();
    }

    public void toggleComponents(Component[] components, boolean enable) {
        for (Component xx : components) {
            if (!(xx instanceof JLabel)) {
                xx.setEnabled(enable);
            }
        }
    }

    public void clearComponents(Component[] components) {
        for (Component xx : components) {
            if (!(xx instanceof JLabel)) {
                if (xx instanceof JTextComponent) {
                    ((JTextComponent) xx).setText("");
                } else if (xx instanceof JComboBox) {
                    ((JComboBox) xx).setSelectedItem(null);
                } else if (xx instanceof JXDatePicker) {
                    ((JXDatePicker) xx).setDate(null);
                } else if (xx instanceof JPasswordField) {
                    ((JPasswordField) xx).setText("");
                }
            }
        }
    }

    public static void createAlternateHighlighter(JXTable tbl) {
        tbl.setHighlighters(HighlighterFactory.createAlternateStriping(new Color(235, 235, 235), Color.WHITE));
        tbl.setGridColor(Color.LIGHT_GRAY);
        tbl.setSelectionBackground(new Color(50, 100, 255));
        tbl.setSelectionForeground(Color.WHITE);
    }
}
