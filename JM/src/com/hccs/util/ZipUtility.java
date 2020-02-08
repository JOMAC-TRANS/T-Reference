/*
 * ZipUtility.java
 *
 * Created on September 18, 2010, 11:34 AM
 *
 */
package com.hccs.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.ISevenZipInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;

public class ZipUtility {

    public static final int ERROR_INITIALIZATION = 1;
    public static final int ERROR_DATA = 2;
    public static final int ERROR_INVALID_PASSWORD = 3;
    private boolean nativeLibraryInitilized;
    private File nativeLibraryPath;
    private ZipUtilityCallback callBack;
    private int lastError;

    public void addCallback(ZipUtilityCallback callback) {
        this.callBack = callback;
    }

    /**
     * Unzip a zip file to a destination directory.
     *
     * @param sourceFile - is the zip file
     * @param destinationDir - is the destination directory
     */
    public void unzip(final File sourceFile, final File destinationDir) {
        clearErrorFlags();
        unzipWithPassword(sourceFile, destinationDir, "", false);
    }

    /**
     * Unzip a zip file to a destination directory.
     *
     * @param sourceFile - is the zip file
     * @param destinationDir - is the destination directory
     * @param password - password of the zip file
     */
    public void unzipWithPassword(
            final File sourceFile, final File destinationDir, final String password) {
        clearErrorFlags();
        unzipWithPassword(sourceFile, destinationDir, password, false);
    }

    private void unzipWithPassword(
            final File sourceFile, final File destinationDir,
            String password, final boolean passwordZip) {
        initializeNativeLibrary();
        RandomAccessFile randomAccessFile = null;
        ISevenZipInArchive inArchive = null;

        try {
            randomAccessFile = new RandomAccessFile(sourceFile, "r");
            inArchive = SevenZip.openInArchive(null, // autodetect archive type
                    new RandomAccessFileInStream(randomAccessFile));

            ISimpleInArchive simpleInArchive = inArchive.getSimpleInterface();

            for (final ISimpleInArchiveItem item : simpleInArchive.getArchiveItems()) {
                if (!item.isFolder()) {
                    ExtractOperationResult result;

                    if (item.getPath().indexOf(File.separator) > 0) {
                        String path = destinationDir.getAbsolutePath() + File.separator
                                + item.getPath().substring(0, item.getPath().lastIndexOf(File.separator));
                        File folderExisting = new File(path);

                        if (!folderExisting.exists()) {
                            new File(path).mkdirs();
                        }
                    }

                    FileOutputStream out = new FileOutputStream(
                            destinationDir.getAbsoluteFile() + File.separator + item.getPath());

                    final FileChannel gma7 = out.getChannel();

                    result = item.extractSlow(new ISequentialOutStream() {
                        public int write(byte[] data) throws SevenZipException {
                            try {
                                ByteBuffer byteBuf = ByteBuffer.wrap(data);
                                gma7.write(byteBuf);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            return data.length; // Return amount of proceed data
                        }
                    }, password); /// password.

                    gma7.close();
                    out.close();

                    if (result == ExtractOperationResult.DATAERROR && !passwordZip) {

                        if (callBack != null) {
                            password = callBack.getZipPassword(sourceFile.getName());

                            if (password == null || password.isEmpty()) {
                                lastError = ERROR_DATA;
                                callBack.showErrorMessage(sourceFile.getName());
                            } else {
                                unzipWithPassword(sourceFile, destinationDir, password, true);
                            }
                        }

                        break;
                    } else if (result == ExtractOperationResult.DATAERROR && (passwordZip || password.length() != 0)) {
                        if (callBack != null) {
                            lastError = ERROR_INVALID_PASSWORD;
                            callBack.showInvalidPassword(sourceFile.getName());
                        }

                        break;
                    } else if (result != ExtractOperationResult.OK) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inArchive != null) {
                try {
                    inArchive.close();
                } catch (SevenZipException e) {
                    System.err.println("Error closing archive: " + e);
                    e.printStackTrace();
                }
            }
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    System.err.println("Error closing file: " + e);
                    e.printStackTrace();
                }
            }
        }
    }

    private void initializeNativeLibrary() {
        if (nativeLibraryInitilized) {
            return;
        }

        try {
            clearErrorFlags();

            File temp = File.createTempFile("temp", null);
            nativeLibraryPath = new File(
                    temp.getParent()
                    + File.separator
                    + "SevenZipJBinding");
            temp.delete();

            if (!nativeLibraryPath.exists()) {
                nativeLibraryPath.mkdir();
            }

            SevenZip.initSevenZipFromPlatformJAR(nativeLibraryPath);
            nativeLibraryInitilized = true;

            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                public void run() {
                    FileUtilities.forceDelete(nativeLibraryPath);
                }
            }));
        } catch (Exception ex) {
            lastError = ERROR_INITIALIZATION;
            ex.printStackTrace();
        }
    }

    public int getLastError() {
        return lastError;
    }

    public boolean hasError() {
        return lastError != 0;
    }

    private void clearErrorFlags() {
        lastError = 0;
    }
}
