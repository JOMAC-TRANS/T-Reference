package com.hccs.forms.components.custom.util;

import com.hccs.forms.components.custom.constants.IconConstants;
import com.hccs.forms.components.custom.constants.ValidationConstants;
import java.awt.Color;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.border.Border;
import org.jdesktop.swingx.border.IconBorder;

public class XBorderFactory{
    
    public static Border getBorder() {
        return getBorder(ValidationConstants.NORMAL);
    }
    
    public static Border getBorder(ValidationConstants type) {
        return getBorder(type, new Insets(1, 1, 1, 1));
    }
    
    public static Border getBorder(ValidationConstants type, Insets inset){
        ImageIcon icon = null;
        /**
         * Color Constants
         * Color(181, 181, 181) is equal to -4868683
         * Color(255, 212, 42) is equal to -11222
         * Color(245, 77, 76) is equal to -701108
         */
        int rgb = -4868683;
        switch (type) {
            case WARNING:
                icon = IconConstants.WARNING.getIcon();
                rgb = -11222;
                break;
            case ERROR:
                icon = IconConstants.ERROR.getIcon();
                rgb = -701108;
                break;
            case INFO:
                icon = IconConstants.INFO.getIcon();
        }
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(rgb), 1, false),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(inset.top, inset.left, inset.bottom, inset.right),
                        new IconBorder(icon, 11, 1))
        );
    }
}
