/*
 * AboutBGPainter.java
 */
package com.hccs.forms;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import javax.swing.ImageIcon;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.Painter;

public class AboutBGPainter implements Painter<JXPanel> {

    private static final String RESOURCES16 = "/com/hccs/resources/16x16/";
    private static final Image COMPUTER_ICON = new ImageIcon(AboutBGPainter.class.getResource(RESOURCES16 + "server.png")).getImage();
    private static final Image USER_ICON = new ImageIcon(AboutBGPainter.class.getResource(RESOURCES16 + "user.png")).getImage();
    private static final Image NETWORK_ICON = new ImageIcon(AboutBGPainter.class.getResource(RESOURCES16 + "net_comp.png")).getImage();
    private static final Image COFFEE_ICON = new ImageIcon(AboutBGPainter.class.getResource(RESOURCES16 + "coffee.png")).getImage();
    private Image logo;
    private int logoWidth;
    private int logoHeight;
    private String productname;
    private String productversion;
    private String productvendor;
    private String productAndVendor;
    private String vendorurl;
    private String username;
    private String host;
    private String port;
    private String hostAndPort;
    private String osInfo;
    private String java;
    private Font fontTitle;
    private Font fontDetails;
    private Font fontDetails2;
    private Color bgColor1;
    private Color bgColor2;
    private Color borderColor1;
    private Color borderColor2;

    public AboutBGPainter(
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
        this.logo = logo;
        this.logoWidth = logo.getWidth(null);
        this.logoHeight = logo.getHeight(null);
        this.productname = productname;
        this.productversion = productversion;
        this.productvendor = productvendor;
        this.vendorurl = vendorurl;
        this.productAndVendor = productvendor + " (" + vendorurl + ")";
        this.username = username;
        this.host = host;
        this.port = port;
        this.hostAndPort = host + ":" + port;
        this.osInfo = System.getProperty("os.name") + " "
                + System.getProperty("os.version") + " ("
                + System.getProperty("os.arch") + ")";

        this.java = "Java " + System.getProperty("java.version") + " " + System.getProperty("java.vendor");
        this.bgColor1 = bgColor1;
        this.bgColor2 = bgColor2;
        this.borderColor1 = bgColor1;
        this.borderColor2 = new Color(Math.max((int) (bgColor2.getRed() * 0.9d), 0),
                Math.max((int) (bgColor2.getGreen() * 0.9d), 0),
                Math.max((int) (bgColor2.getBlue() * 0.9d), 0));
    }

    public AboutBGPainter(
            Image logo,
            String productname,
            String productversion,
            String productvendor,
            String vendorurl,
            String username,
            String host,
            String port) {
        this(logo, productname, productversion, productvendor, vendorurl,
                username, host, port, new Color(1f, 1f, 1f), new Color(0.7f, 0.7f, 0.7f));
    }

    @Override
    public void paint(Graphics2D g, JXPanel t, int width, int height) {
        initVars(g);

        g.clearRect(0, 0, width, height);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Gradient background with 3d border
        // background
        Point pt1, pt2;

        pt1 = new Point(0, 0);
        pt2 = new Point(width, height);

        GradientPaint gpaint = new GradientPaint(pt1, bgColor1, pt2, bgColor2);
        g.setPaint(gpaint);

        g.fillRect(0, 0, width, height);

        pt1 = new Point(0, 0);
        pt2 = new Point(15 + logoWidth, height);

        gpaint = new GradientPaint(pt1, bgColor1, pt2, bgColor2);
        g.setPaint(gpaint);

        g.fillRect(0, 0, 15 + logoWidth, height);

        // border
        g.setColor(borderColor2);
        g.drawLine(15 + logoWidth, 0, 15 + logoWidth, height);

        g.setColor(borderColor1);
        g.drawLine(16 + logoWidth, 0, 16 + logoWidth, height);

        g.setColor(borderColor2);
        g.drawRect(0, 0, width - 1, height - 1);

        g.setColor(borderColor1);
        g.drawRect(1, 1, width - 3, height - 3);

        // Logo
        g.drawImage(logo, 10, 20, null);

        // Title with white shadow
        int fontHeight = g.getFontMetrics().getHeight();
        int startY = fontHeight + 40;
        int offsetX, offsetY;

        offsetX = logoWidth + 30;
        g.setColor(borderColor1);
        g.setFont(fontTitle);
        g.drawString(this.productname, offsetX + 1, startY + 1);
        g.setColor(Color.BLACK);
        g.setFont(fontTitle);
        g.drawString(this.productname, offsetX, startY);

        // Version
        g.setFont(fontDetails);
        fontHeight = g.getFontMetrics().getHeight();
        offsetY = fontHeight;
        g.drawString("Version " + this.productversion, offsetX, startY + offsetY);

        // Vendor
        g.setFont(fontDetails2);
        fontHeight = g.getFontMetrics().getHeight();

        offsetY += fontHeight + 20;
        g.drawString(this.productvendor, offsetX, startY + offsetY);
        offsetY += fontHeight;
        g.drawString(this.vendorurl, offsetX, startY + offsetY);
        offsetY += fontHeight;

        // User Info, System Info, Connection Info
        // user
        if (this.username != null) {
            offsetY += fontHeight + 40;
            g.drawImage(USER_ICON, offsetX, startY + offsetY, null);
            g.drawString(this.username, offsetX + 25, startY + offsetY + 11);
        }

        // host port
        if (this.host != null && this.port != null) {
            offsetY += fontHeight + 14;
            g.drawImage(NETWORK_ICON, offsetX, startY + offsetY, null);
            g.drawString(this.hostAndPort, offsetX + 25, startY + offsetY + 11);
        }

        // system info
        if (this.osInfo != null) {
            offsetY += fontHeight + 14;
            g.drawImage(COMPUTER_ICON, offsetX, startY + offsetY, null);
            g.drawString(this.osInfo, offsetX + 25, startY + offsetY + 11);
        }

        // java
        if (this.java != null) {
            offsetY += fontHeight + 10;
            g.drawImage(COFFEE_ICON, offsetX, startY + offsetY, null);
            g.drawString(this.java, offsetX + 25, startY + offsetY + 15);
        }

    }

    private void initVars(Graphics2D g) {
        if (fontTitle == null) {
            fontTitle = g.getFont().deriveFont(20f).deriveFont(Font.BOLD);
        }

        if (fontDetails == null) {
            fontDetails = g.getFont().deriveFont(12f);
        }

        if (fontDetails2 == null) {
            fontDetails2 = g.getFont().deriveFont(10f);
        }
    }
}
