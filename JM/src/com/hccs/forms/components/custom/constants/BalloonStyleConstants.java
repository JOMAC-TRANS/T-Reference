package com.hccs.forms.components.custom.constants;

import java.awt.Color;
import net.java.balloontip.styles.BalloonTipStyle;
import net.java.balloontip.styles.EdgedBalloonStyle;
import net.java.balloontip.styles.RoundedBalloonStyle;

public enum BalloonStyleConstants {

    ROUND(new RoundedBalloonStyle(
            10,
            10,
            new Color(255, 255, 225),
            Color.black)),
    EDGE(new EdgedBalloonStyle(Color.WHITE, Color.DARK_GRAY));

    private final BalloonTipStyle style;

    private BalloonStyleConstants(BalloonTipStyle style) {
        this.style = style;
    }

    public BalloonTipStyle getStyle() {
        return style;
    }
}
