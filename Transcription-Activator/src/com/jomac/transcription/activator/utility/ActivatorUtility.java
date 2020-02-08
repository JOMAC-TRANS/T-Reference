package com.jomac.transcription.activator.utility;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class ActivatorUtility {

    public static void openLink(String link) {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            for (int drv = (int) 'A'; drv <= (int) 'Z'; drv++) {
                String iexplore = String.valueOf((char) drv) + ":\\Program Files\\Internet Explorer\\iexplore.exe";

                if (new File(iexplore).exists()) {
                    try {
                        Runtime.getRuntime().exec(new String[]{iexplore, link});
                    } catch (IOException iex) {
                        browseLink(link);
                    }
                    break;
                }
            }
        } else {
            browseLink(link);
        }
    }

    private static void browseLink(String link) {
        try {
            browseLink(new URI(link));
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
    }

    private static void browseLink(URI link) {
        try {
            Desktop.getDesktop().browse(link);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
