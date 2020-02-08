package com.jomac.transcription.reference.utilities.dropbox;

import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Reference:
 * http://www.java2s.com/Tutorials/Java/IO_How_to/FileChannel/Monitor_progress_of_FileChannels_transferFrom_method.htm
 *
 * @author DSalenga
 */
public class DownloadByteChannel implements ReadableByteChannel {

    private final ProgressListener listener;
    private final long size;
    private final ReadableByteChannel rbc;
    private long sizeRead;

    public DownloadByteChannel(String remoteSource, ProgressListener listener) throws Exception {
        URL url = new URL(remoteSource);
        this.size = url.openConnection().getContentLength();
        this.rbc = Channels.newChannel(url.openStream());
        this.listener = listener;
    }

    public DownloadByteChannel(URL url, ProgressListener listener) throws Exception {

        this.size = url.openConnection().getContentLength();
        this.rbc = Channels.newChannel(url.openStream());
        this.listener = listener;
    }

    public DownloadByteChannel(ReadableByteChannel rbc, long expectedSize, ProgressListener listener) {
        this.listener = listener;
        this.size = expectedSize;
        this.rbc = rbc;
    }

    public long getContentSize() {
        return size;
    }

    @Override
    public void close() throws IOException {
        rbc.close();
    }

    public long getReadSoFar() {
        return sizeRead;
    }

    @Override
    public boolean isOpen() {
        return rbc.isOpen();
    }

    @Override
    public int read(ByteBuffer bb) throws IOException {
        int n;
        double progress;
        if ((n = rbc.read(bb)) > 0) {
            sizeRead += n;
            progress = size > 0 ? (double) sizeRead / (double) size * 100.0 : -1.0;
            listener.printProgress(this, size, progress);
        }
        return n;
    }
}
