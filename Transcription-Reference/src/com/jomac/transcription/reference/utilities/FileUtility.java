package com.jomac.transcription.reference.utilities;

import com.hccs.util.FileUtilities;
import com.hccs.util.TaskLock;
import com.hccs.util.TaskLocker;
import com.hccs.util.ZipUtility;
import com.jomac.transcription.reference.Main;
import com.jomac.transcription.reference.forms.UpdateDialog;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtility {

    private final static File H2_LOCATION = new File(Main.getDBPath());

    public static void pipeStream(String path, String fName) throws Exception {
        InputStream is = null;
        OutputStream os = null;
        File oFile = new File(path + File.separator + fName);
        try {
            is = FileUtility.class.getResourceAsStream("/com/jomac/transcription/reference/resources/" + fName);
            os = new FileOutputStream(oFile);
            FileUtilities.pipeStream(is, os, 1024 * 4);
        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
            if (oFile.exists()) {
                oFile.setReadOnly();
            }
        }
    }

    public static void extractRawFiles(File fpath) {
        if (fpath.exists()) {
            TaskLocker taskLocker = new TaskLocker();
            for (File x : fpath.listFiles()) {
                if (x.isFile() && !x.isHidden() && x.getAbsolutePath().endsWith("zip")) {
                    File h2File = new File(H2_LOCATION, x.getName().replace("zip", "h2.db"));

                    if (!h2File.exists()) {
                        final File xx = x;
                        taskLocker.addThread(new TaskLock() {

                            @Override
                            public void run() {
                                extractZipFile(xx);
                            }
                        });
                    }
                }
            }
            if (taskLocker.getThreadCount() == 0) {
                System.out.println("No need to Extract");
            } else {
                UpdateDialog udiag = new UpdateDialog(null, false);
                udiag.setStatus("Extracting Reference Files..."
                        + "\n\nPlease wait.");
                udiag.setLocationRelativeTo(null);
                udiag.setVisible(true);

                taskLocker.runAndWaitLocker();

                udiag.dispose();
            }
        }
    }

    public static void extractZipFile(File zipFile) {
        System.out.println("extracting " + zipFile.getAbsolutePath());
        try {
            ZipUtility sevenZip = new ZipUtility();
            sevenZip.unzip(zipFile, H2_LOCATION);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
