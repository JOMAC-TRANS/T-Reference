package com.hccs.forms.components.custom.constants;

import javax.swing.ImageIcon;

public enum IconConstants {

    INFO("info.png"),
    WARNING("warning.png"),
    ERROR("error.png");
    private final ImageIcon icon;

    private IconConstants(String name) {
        this.icon = new ImageIcon(ValidationConstants.class.
                getResource("/com/hccs/resources/16x16/" + name));
    }

    public ImageIcon getIcon() {
        return icon;
    }
}