package com.jomac.transcription.reference.utilities.dropbox;

/**
 *
 * @author DSalenga
 */
public interface ProgressListener {

    public void printProgress(DownloadByteChannel rbc, long size, double progress);
}
