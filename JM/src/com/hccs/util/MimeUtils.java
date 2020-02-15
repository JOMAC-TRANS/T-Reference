/*
 * MimeUtils.java
 *
 * Created on August 28, 2010, 9:04 AM
 *
 */
package com.hccs.util;

import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;
import java.io.File;
import java.net.URL;
import java.util.Collection;
import org.apache.log4j.LogManager;

public class MimeUtils {

    static {
        LogManager.shutdown();
        MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
    }

    public static Collection getMimeTypes(File file) {
        return MimeUtil.getMimeTypes(file);
    }

    public static Collection getMimeTypes(String path) {
        return MimeUtil.getMimeTypes(path);
    }

    public static Collection getMimeTypes(byte[] data) {
        return MimeUtil.getMimeTypes(data);
    }

    public static Collection getMimeTypes(URL url) {
        return MimeUtil.getMimeTypes(url);
    }

    public static MimeType getDefaultMimeType(File file) {
        return (MimeType) MimeUtil.getMimeTypes(file).iterator().next();
    }

    public static MimeType getDefaultMimeType(String path) {
        return (MimeType) MimeUtil.getMimeTypes(path).iterator().next();
    }

    public static MimeType getDefaultMimeType(URL url) {
        return (MimeType) MimeUtil.getMimeTypes(url).iterator().next();
    }

    /* TODO: public functions for registering MimeDetector
     private static void regMagicMime()
     {
     MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
     }

     private static void unregMagicMime()
     {
     MimeUtil.unregisterMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
     }*/
    public static void main(String... args) {
        /*
         MimeType mimeType = getDefaultMimeType(new File("C:\\JOMAC Transcription\\Inbox\\2010.0428.095601.396.search_the_web.weather.0.wav"));
         System.out.println("Mime type:" + mimeType);
         System.out.println("Media type:" + mimeType.getMediaType());
         System.out.println("Sub type:" + mimeType.getSubType());
         */

        File audio = new File("C:\\JOMAC Transcription\\Inbox\\sample1.ogg");
        Collection<MimeType> mimes = getMimeTypes(audio);
        for (MimeType mime : mimes) {
            System.out.println("Mime type:" + mime);
            System.out.println("Media type:" + mime.getMediaType());
            System.out.println("Sub type:" + mime.getSubType());
        }
    }
}
