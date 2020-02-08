package com.jomac.transcription.reference.utilities;

import com.hccs.util.StringUtilities;
import com.hccs.util.TaskLock;
import com.hccs.util.TaskLocker;
import com.hccs.util.TaskProgress;
import com.jomac.transcription.reference.PluginFileLink;
import com.jomac.transcription.reference.Main;
import com.jomac.transcription.reference.ReferenceFileLink;
import com.jomac.transcription.reference.controller.TRPlugin;
import com.jomac.transcription.reference.forms.ProgressDialog;
import com.jomac.transcription.reference.utilities.dropbox.DownloadByteChannel;
import com.jomac.transcription.reference.utilities.dropbox.ProgressListener;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

/**
 *
 * @author DSalenga
 */
public class PluginProcessor {

    private TaskLocker taskLocker;
//    private final int associatedKey = 511;
    private final int associatedKey = Main.getAssociatedKey();
    private final String tRefLocation = Main.getDBPath();
    private final File refFilesLocation = new File(Main.getReferenceFilesPath());

    private final File zipLocation = new File(tRefLocation, "RAW");
    private ProgressDialog pBar;

    public static void main(String[] args) {
        try {
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

//        new PluginProcessor().start();
        new PluginProcessor().StartReferenceFiles();
    }

    public void StartReferenceFiles() {
        if (!refFilesLocation.exists()) {
            refFilesLocation.mkdirs();
        }

        checkReferenceFiles(ReferenceFileLink.GUIDELINES);
        checkReferenceFiles(ReferenceFileLink.DRUGS_PHRASES);
        checkReferenceFiles(ReferenceFileLink.TR_DICTATORS);
        checkReferenceFiles(ReferenceFileLink.NORMALLABS);
    }

    private void checkReferenceFiles(ReferenceFileLink fl) {
        File docFile = new File(refFilesLocation, fl.toString());
        URL url;
        try {
            url = new URL(fl.getLink());
        } catch (Exception e) {
            url = null;
            e.printStackTrace();
        }
        if (docFile.exists() && url != null) {
            long fileSize;
            try {
                fileSize = url.openConnection().getContentLength();
            } catch (Exception e) {
                fileSize = 0L;
            }

            if (fileSize != docFile.length()) {
                docFile.delete();
            } else {
                System.out.println("Reference File [" + fl.toString() + "] is still Updated..");
                return;
            }
        }

        if (url == null) {
            if (docFile.exists()) {
                return;
            }
            /*
                Extract Document from the Application Source
             */
            try {
                FileUtility.pipeStream(refFilesLocation.getPath(), fl.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                ReadableByteChannel channel = Channels.newChannel(url.openStream());
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(docFile.getAbsolutePath());
                    fos.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
                } finally {
                    if (fos != null) {
                        fos.close();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("File available: " + docFile.exists());
        }
        if (docFile.exists()) {
            docFile.setReadOnly();
        }
    }

    public void start() {
        if (zipLocation.exists()) {
            long diff = (new Date()).getTime()
                    - Main.getPreferences().getLong("Pluginlastupdate", new Date().getTime());

//            System.out.println("Diff: " + TimeUnit.MILLISECONDS.toDays(diff));
            if (TimeUnit.MILLISECONDS.toDays(diff) >= 30) {
                checkPlugins();
            }
        } else {
            zipLocation.mkdirs();
        }

        taskLocker = new TaskLocker();

        validateDownloadTask(TRPlugin.B, PluginFileLink.B);
        validateDownloadTask(TRPlugin.C, PluginFileLink.C);
        validateDownloadTask(TRPlugin.F, PluginFileLink.F);
//        validateDownloadTask(TRPlugin.G, PluginFileLink.G);
        validateDownloadTask(TRPlugin.L, PluginFileLink.L);
        validateDownloadTask(TRPlugin.O, PluginFileLink.O);
        validateDownloadTask(TRPlugin.S, PluginFileLink.S);

        if (taskLocker.getThreadCount() > 0) {
            new progressWorker().execute();
            getDialog().setVisible(true);
        } else {
            FileUtility.extractRawFiles(zipLocation);
        }
    }

    private void validateDownloadTask(TRPlugin plugin, PluginFileLink fLinkPlugin) {
        File h2File = new File(tRefLocation, fLinkPlugin.toString().replace("zip", "h2.db"));
        File zipFile = new File(zipLocation, fLinkPlugin.toString());

        if ((plugin.getValue() & associatedKey) == plugin.getValue()) {

            if (zipFile.exists()) {
                return;
            }

            try {
                getDialog().addProgressTask_(taskLocker.getThreadCount(),
                        new TaskProgress(0, 100, plugin.toString(), plugin.toString(), ""));
                taskLocker.addThread(createDownloadTask(fLinkPlugin.getLink(), plugin.toString(), zipFile));
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            if (zipFile.exists()) {
                zipFile.delete();
            }
            if (h2File.exists()) {
                h2File.delete();
            }
        }
    }

    private TaskLock createDownloadTask(final String fileLink, final String plugin, final File fileLocation) {
        System.out.println("Downloading at " + fileLocation);

        return new TaskLock() {

            @Override
            public void run() {
                try {
                    ProgressListener listener = new ProgressListener() {
                        @Override
                        public void printProgress(DownloadByteChannel rbc, long size, double progress) {
                            getDialog().updateProgressBar_(plugin, plugin
                                    + " ( " + StringUtilities.bytes2HumanReadable(rbc.getReadSoFar()) + " / "
                                    + StringUtilities.bytes2HumanReadable(size) + " ) ", (int) progress);
                        }
                    };

                    ReadableByteChannel channel = new DownloadByteChannel(fileLink, listener);
                    FileOutputStream fos = null;
                    try {
                        System.out.println("1");
                        fos = new FileOutputStream(fileLocation.getAbsolutePath());
                        System.out.println("2" + (channel.isOpen()));
                        fos.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
                        System.out.println("3");
                    }catch (Exception ex){
                        if (ex.getMessage().contains("")){
                            System.out.println("Error here!");
                        }
                    } finally {
                        if (fos != null) {
                            fos.close();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    // Delete old plugin
    public void checkPlugins() {
        TaskLocker cpLocker = new TaskLocker();

        for (File zipFile : new File(Main.getDBPath(), "RAW").listFiles()) {
            PluginFileLink fLinkPlugin = null;
            for (PluginFileLink fL : PluginFileLink.values()) {
                if (fL.toString().equalsIgnoreCase(zipFile.getName())) {
                    fLinkPlugin = fL;
                    break;
                }
            }

            if (zipFile.exists() && fLinkPlugin != null) {
                final PluginFileLink fLinkPlugin_ = fLinkPlugin;
                cpLocker.addThread(new TaskLock() {
                    @Override
                    public void run() {
                        long fileSize;
                        try {
                            fileSize = new URL(fLinkPlugin_.getLink())
                                    .openConnection().getContentLength();
                        } catch (Exception e) {
                            fileSize = 0L;
                            e.printStackTrace();
                        }

                        if (fileSize > 1) {
                            if (fileSize != zipFile.length()) {
                                zipFile.delete();
                                File h2File = new File(Main.getDBPath(),
                                        fLinkPlugin_.toString().replace("zip", "h2.db"));
                                if (h2File.exists()) {
                                    System.out.println("deleting h2File: " + h2File);
                                    h2File.delete();
                                }
                            } else {
                                System.out.println(fLinkPlugin_.name() + " Zip is still updated ");
                            }
                        } else {
                            System.out.println("Error occured while connecting");
                        }
                    }
                });
            }
        }
        cpLocker.runAndWaitLocker();
        Main.getPreferences().putLong("Pluginlastupdate", new Date().getTime());
    }

    public class progressWorker extends SwingWorker<Object, Object> {

        @Override
        protected Object doInBackground() throws Exception {
            taskLocker.runAndWaitLocker();
            if (getDialog().isVisible()) {
                getDialog().dispose();
            }
            return null;
        }

        @Override
        protected void done() {
            FileUtility.extractRawFiles(zipLocation);
        }

    }

    protected ProgressDialog getDialog() {
        if (pBar == null) {
            pBar = new ProgressDialog(null, true);
            pBar.setLocationRelativeTo(null);
        }
        return pBar;
    }

}
