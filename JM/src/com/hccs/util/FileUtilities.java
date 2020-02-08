/*
 * FileUtilities.java
 *
 * Created on January 17, 2007, 10:27 AM
 *
 */
package com.hccs.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author jon
 */
public class FileUtilities {

    private static final int MOVE_RETRY_COUNT = 100;
    private static final int ONE_MB = 1024 * 1024;
    private static final int TEN_MB = 10 * ONE_MB;
    private static final int FIFTY_MB = 5 * TEN_MB;
    private static final int KEY = 345;

    /**
     * A decorator to filter an Iterator<File> based on a java.io.FileFilter.
     * Used by walkDirectory(dir, filter)
     */
    private static class FileFilterIterator implements Iterator<File> {

        private final Iterator<File> basis;
        private final FileFilter filter;
        private File tmp;

        public FileFilterIterator(Iterator<File> basis, FileFilter filter) {
            this.basis = basis;
            this.filter = filter;
            tmp = null;
        }

        @Override
        public boolean hasNext() {
            if (tmp == null) {
                while (basis.hasNext()) {
                    tmp = basis.next();
                    if (filter.accept(tmp)) {
                        return true;
                    }
                }
                tmp = null;
            }

            return (tmp != null);
        }

        @Override
        public File next() {
            File ret;
            if (tmp != null || hasNext()) {
                ret = tmp;
                tmp = null;
            } else {
                throw new NoSuchElementException();
            }
            return ret;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Basic Encryption using XOR algorithm.
     *
     * @param f the file to encrypt.
     * @throws java.io.FileNotFoundException
     */
    public static void xor(File f) throws FileNotFoundException, IOException {
        xor(f, new byte[1024]);
    }

    /**
     * Basic Encryption using XOR algorithm.
     *
     * @param f the file to encrypt.
     * @param data the size to encrypt.
     * @throws java.io.FileNotFoundException
     */
    public static void xor(File f, byte[] data) throws FileNotFoundException, IOException {
        RandomAccessFile rac = new RandomAccessFile(f, "rw");
        int len = rac.read(data);

        byte[] newData = new byte[len];

        for (int i = 0; i < data.length; i++) {
            newData[i] = (byte) (0xFF & ((int) data[i] ^ KEY));
        }

        rac.seek(0);
        rac.write(newData);

        rac.close();
    }

    /**
     * Recursively delete a directory (or a file). "Force" becaue this is
     * similar to rm -rf
     *
     * @param f a file or a directory
     * @return
     */
    public static boolean forceDelete(File f) {
        if (!f.exists()) {
            return true;
        } else if (f.isFile()) {
            return f.delete();
        } else if (f.isDirectory()) {
            boolean ret = true;
            for (String s : f.list()) {
                if (!forceDelete(new File(f, s))) {
                    ret = false;
                }
            }
            return f.delete() && ret;
        }
        return false;
    }

    /**
     * Forcely moves a file to another location. Deletes dest if exist. In
     * addition, we loop several times if in case it failed. Looping is
     * neccessary since renameTo() is not much reliable specially in Windows
     * platform.
     *
     * See this thread:
     * http://stackoverflow.com/questions/1000183/reliable-file-renameto-alternative-on-windows
     *
     * @param src the source java.io.File
     * @param dest the destination java.io.File
     * @return true if succeeded; false otherwise
     */
    public static boolean forceMove(File src, File dest) {
        boolean success = false;
        int retryCount = 0;

        if (dest.exists()) {
            dest.delete();
        }

        do {
            success = src.renameTo(dest);

            if (!success) {
                try {
                    Thread.sleep(100);
                } catch (Exception ex) {
                }

                retryCount++;
            }
        } while (!success && retryCount <= MOVE_RETRY_COUNT);

        return success;
    }

    /**
     * Lazy directory walker. Only returns files. Breadth-first search.
     */
    private static class DirectoryIterator implements Iterator<File> {

        private final File[] files;
        private final List<File> dirs;
        private Iterator<File> subdirIter;
        private int curfile, curdir;
        private File lookAhead;
        private boolean finished;

        public DirectoryIterator(File dir) {
            files = dir.listFiles();
            dirs = new ArrayList<>();
            curfile = curdir = 0;
            subdirIter = null;
            lookAhead = null;
            finished = false;
        }

        @Override
        public boolean hasNext() {
            if (lookAhead != null) {
                return true;
            }
            if (finished) {
                return false;
            }
            try {
                lookAhead = next();
                return lookAhead != null;
            } catch (NoSuchElementException e) {
                finished = true;
            }
            return false;
        }

        // TODO: inefficient, we are keeping reference to a File[] even if we
        // have finished the current dir
        @Override
        public File next() {
            File ret;

            // see if we have a lookAhead value, caused by hasNext
            if (lookAhead != null) {
                ret = lookAhead;
                lookAhead = null;
                return ret;
            } else if (finished) {
                throw new NoSuchElementException();
            }

            // try to give a file inside the current dir
            while (curfile < files.length) {
                ret = files[curfile];
                curfile++;

                // TODO: why did I not assign this to lookAhead?
                // because I expect next() to be called?
                if (ret.isFile()) {
                    return ret;
                }

                // save directories to be searched later
                dirs.add(ret);
            }

            // recurse to dirs inside current dir
            if (subdirIter == null || !subdirIter.hasNext()) {
                do {
                    if (curdir < dirs.size()) {
                        subdirIter = new DirectoryIterator(dirs.get(curdir));
                        curdir++;
                    } else {
                        throw new NoSuchElementException();
                    }
                } while (curdir < dirs.size() && !subdirIter.hasNext());
            }

            return subdirIter.next();
        }

        @Override
        public void remove() {
        }
    }

    /**
     * Returns an iterator of File that will walk through all the files
     * contained in the argument directory. Not efficient on first run, but
     * faster on succeeding runs.
     *
     * @param dir the directory to walk
     * @return
     */
    public static Iterator<File> walkDirectory(File dir) {
        if (!dir.isDirectory()) {
            List<File> ret = new ArrayList<>();
            ret.add(dir);
            return ret.iterator();
        }
        return new DirectoryIterator(dir);
    }

    public static Iterator<File> walkDirectory(File dir, FileFilter filter) {
        return new FileFilterIterator(walkDirectory(dir), filter);
    }

    /**
     * Pipes the stream from an InputStream to an OutputStream. This version of
     * the method is used for piping many pairs of streams.
     *
     * @param is the input stream
     * @param os the output stream
     * @param buf the buffer to use. useful if we have many streams to pipe, so
     * that we allocate the buffer only once.
     * @throws java.io.IOException
     */
    public static void pipeStream(InputStream is, OutputStream os, byte[] buf) throws IOException {
        int read = 0;
        read = is.read(buf);
        while (read > -1) {
            os.write(buf, 0, read);
            read = is.read(buf);
        }
    }

    /**
     * Pipes the stream from an InputStream to an OutputStream. This version of
     * the method is used for piping a single pair of streams.
     *
     * @param is the input stream
     * @param os the output stream
     * @param bufsz the size of the buffer to use.
     */
    public static void pipeStream(InputStream is, OutputStream os, int bufsz) throws IOException {
        byte[] buf = new byte[bufsz];
        pipeStream(is, os, buf);
    }

    /**
     * Recursively zip the contents of a directory. The paths stored in the zip
     * file is actual path of the files relative to the dir specfified as arg.
     *
     * @param dir the directory to zip.
     * @param outputZip the output zip file
     * @param ziplevel argument to ZipOutputStream.setLevel
     * @throws java.io.FileNotFoundException
     */
    public static void zipDirectory(File dir, File outputZip, int ziplevel) throws FileNotFoundException, IOException {
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputZip));
        ZipEntry entry;
        FileInputStream fis;
        byte[] buf = new byte[10240];

        zos.setLevel(ziplevel);

        if (dir.isDirectory()) {
            Iterator<File> files = walkDirectory(dir);
            String abspath = dir.getCanonicalPath();
            File curfile;

            while (files.hasNext()) {
                curfile = files.next();

                // get substring of canonical path minus the canonical path of
                // the containing dir (the dir arg) and the starting '/'  
                // also, dir separator of zip entries '/' (e.g. when zipping
                // an odt, it takes '\' separator as an error)
                entry = new ZipEntry(curfile.getCanonicalPath().substring(abspath.length() + 1).replaceAll("\\\\", "/"));

                fis = new FileInputStream(curfile);
                zos.putNextEntry(entry);
                pipeStream(fis, zos, buf);
                fis.close();
                zos.closeEntry();
            }
        } else {  // arg is just a file
            entry = new ZipEntry(dir.getName());
            fis = new FileInputStream(dir);
            zos.putNextEntry(entry);
            pipeStream(fis, zos, buf);
            fis.close();
            zos.closeEntry();
        }

        zos.close();
    }

    /**
     * Unzips a zip file to a destination directory.
     *
     * @param file is the zip file
     * @param destdir is the destination directory
     * @throws ZipException
     * @throws IOException
     */
    public static void unzip(File file, File destdir) throws ZipException, IOException {
        ZipFile zip = new ZipFile(file);
        Enumeration entries = zip.entries();
        ZipEntry entry;
        byte[] buf = new byte[10240];   // 10k
        File outfile;
        FileOutputStream fos;

        while (entries.hasMoreElements()) {
            entry = (ZipEntry) entries.nextElement();
            outfile = new File(destdir, entry.getName());
            if (entry.isDirectory()) {
                outfile.mkdirs();
            } else {
                outfile.getCanonicalFile().getParentFile().mkdirs();
                fos = new FileOutputStream(outfile);
                FileUtilities.pipeStream(zip.getInputStream(entry), fos, buf);
                fos.close();
            }
        }
        zip.close();
    }

    public static void unzipGZ(File file, File destdir) {
        try {
            GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(file));
            OutputStream out = new FileOutputStream(
                    new File(
                            destdir,
                            file.getName().replaceAll(".gz", "")));

            byte[] buf = new byte[1024];

            int len;
            while ((len = gzis.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            gzis.close();
            out.close();
        } catch (IOException e) {
            System.out.println("Exception has been thrown" + e);
        }
    }

    public static byte[] digest(String algorithm, InputStream is, byte[] buf)
            throws IOException, NoSuchAlgorithmException {
        MessageDigest dg = MessageDigest.getInstance(algorithm);
        BufferedInputStream bis = new BufferedInputStream(is);
        int read = 0;

        //System.out.println("Digeset Length: " + dg.getDigestLength());
        read = bis.read(buf);
        while (read > -1) {
            dg.update(buf, 0, read);
            read = bis.read(buf);
        }

        return dg.digest();
    }

    public static byte[] digest(String algorithm, InputStream is)
            throws IOException, NoSuchAlgorithmException {
        byte[] buf = new byte[10240];  // 10K buffer

        return digest(algorithm, is, buf);
    }

    /**
     * Computes the digest of a file using a specified algorithm (e.g. SHA1,
     * MD5). Reuses the byte array passed, useful when performing many digest
     * computations sequentially.
     *
     * @param algorithm name of the algorithm (e.g. SHA1, MD5)
     * @param file the file whose hash will be computed
     * @param buf the buffer to use when reading from file
     * @return
     * @throws java.io.FileNotFoundException
     * @throws java.security.NoSuchAlgorithmException
     * @see #pipeStream
     * @see StringUtilities.byteArrayToHexString
     */
    public static byte[] digest(String algorithm, File file, byte[] buf)
            throws FileNotFoundException, NoSuchAlgorithmException, IOException {
        return digest(algorithm, new FileInputStream(file), buf);
    }

    /**
     * Computes the digest of a file using a specified algorithm (e.g. SHA1,
     * MD5). Use this version of the method if only a single file is to be
     * computed.
     *
     * @param algorithm name of the algorithm (e.g. SHA1, MD5)
     * @param file the file whose hash will be computed
     * @return
     * @throws java.io.FileNotFoundException
     * @throws java.security.NoSuchAlgorithmException
     */
    public static byte[] digest(String algorithm, File file)
            throws FileNotFoundException, NoSuchAlgorithmException, IOException {
        byte[] buf = new byte[10240];  // 10K buffer

        return digest(algorithm, file, buf);
    }

    /**
     * File copy.
     *
     * @param src source file
     * @param dst destination file
     * @param overwrite if true, overwrite destination if the latter exists
     * @return
     * @throws java.io.FileNotFoundException
     */
    public static boolean copy(File src, File dst, boolean overwrite) throws FileNotFoundException, IOException {
        return copy(src, dst, overwrite, new byte[10240]);
    }

    public static boolean copy(File src, File dst, boolean overwrite, byte[] buf) throws FileNotFoundException, IOException {
        if (dst.exists()) {
            if (overwrite) {
                return false;
            }
            if (!dst.delete()) {
                return false;
            }
        }
        FileInputStream in = new FileInputStream(src);
        FileOutputStream out = new FileOutputStream(dst);
        pipeStream(in, out, buf);
        in.close();
        out.close();
        return true;
    }

    public static void nioCopy(File src, File dst) throws FileNotFoundException, IOException {
        FileInputStream inFile = new FileInputStream(src);
        FileOutputStream outFile = new FileOutputStream(dst);
        FileChannel inChannel = inFile.getChannel();
        FileChannel outChannel = outFile.getChannel();

        if (inChannel.size() <= FIFTY_MB) {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } else {
            for (ByteBuffer buffer = ByteBuffer.allocate(TEN_MB);
                    inChannel.read(buffer) != -1;
                    buffer.clear()) {
                buffer.flip();
                while (buffer.hasRemaining()) {
                    outChannel.write(buffer);
                }
            }
        }

        inChannel.close();
        outChannel.close();
    }

    /**
     * Extract resource from the given ClassLoader. The resource parameter
     * should be compatible with the function System.getResource. The resource
     * will be saved in a file in the destination path with filename equal to
     * the resource's base name.
     *
     * @param cl ClassLoader to get the resource from
     * @param rsrc
     * @throws java.io.FileNotFoundException
     * @pram rsrc the name of the resource, e.g. com/foo/bar.properties
     * @param destPath Path of the directory where to save the file containing
     * the resource
     * @return File representing the resource
     */
    public static File extractResource(ClassLoader cl, String rsrc, String destPath)
            throws FileNotFoundException, IOException {
        File f = new File(rsrc);
        String basename = f.getName();
        f = new File(destPath, basename);
        InputStream is = cl.getResourceAsStream(rsrc);
        FileOutputStream os = new FileOutputStream(f);
        FileUtilities.pipeStream(is, os, 1024);
        os.close();
        is.close();
        return f;
    }

    public static File extractResource(String rsrc, String destPath)
            throws FileNotFoundException, IOException {
        return FileUtilities.extractResource(FileUtilities.class.getClassLoader(), rsrc, destPath);
    }

    /**
     * Returns a 2-element String array whose first element is the basename and
     * the second element is the extension.
     *
     * E.g. foo.txt returns { "foo", "txt" }
     *
     * @param f
     * @return
     */
    public static String[] getBaseAndExtension(File f) {
        String[] ret = new String[2];
        String name = f.getName();
        String base = name.contains(".")
                ? name.substring(0, name.lastIndexOf(".")) : name;
        String ext = name.contains(".")
                ? name.substring(name.lastIndexOf(".") + 1) : null;

        ret[0] = base;
        ret[1] = ext;
        return ret;
    }

    public static String[] getBaseAndExtension(String f) {
        return getBaseAndExtension(new File(f));
    }

    /**
     * Deletes this file and all its subdirectories and subfiles recursively.
     *
     * @param dir the file to delete
     */
    public static void deleteFile(File dir) {
        if (dir.exists()) {
            if (dir.isFile()) {
                dir.delete();
            } else {
                if (dir.list() != null && dir.list().length == 0) {
                    dir.delete();
                } else {
                    for (File xx : dir.listFiles()) {
                        deleteFile(xx);
                    }
                }
                dir.delete();
            }
        }
    }

    public static long getCRC32Checksum(File file) {
        long checksum = -1;
        try (InputStream is = new FileInputStream(file)){
            checksum = getCRC32Checksum(is);
        } catch (FileNotFoundException e) {
            System.err.println("File not found.");
        } catch(Exception e){
            e.printStackTrace();
        }
        return checksum;
    }

    public static long getCRC32Checksum(InputStream is) {
        long checksum = -1;
        try {
            // Computer CRC32 checksum
            CheckedInputStream cis = new CheckedInputStream(is, new CRC32());
            byte[] buf = new byte[128];

            while (cis.read(buf) >= 0) {
            }

            checksum = cis.getChecksum().getValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return checksum;
    }

    public static File getSortedFileName(File baseDir) {
        return getSortedFileName(baseDir, null);
    }

    public static File getSortedFileName(File baseDir, String ext) {
        File[] xx = baseDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File xx) {
                return !xx.getName().equalsIgnoreCase("tie");
            }
        });
        sortFiles(xx);
        String format = "0000";

        String name;

        if (xx.length == 0) {
            name = format;
        } else {
            String filename;
            int x = 0;
            do {
                x++;
                filename = xx[xx.length - x].getName();
            } while (!filename.contains("."));
            filename = filename.substring(0, filename.indexOf("."));
            try {
                name = format + (Integer.parseInt(filename) + 1);
                name = name.substring(name.length() - format.length(), name.length());
            } catch (NumberFormatException e) {
                name = format;
            }
        }
        return new File(baseDir, ext != null ? (name + "." + ext) : name);
    }

    /**
     * Gives you a random file inside baseDir
     *
     * @param baseDir this is where the random file will reside
     * @return
     */
    public static File getRandomFile(File baseDir) {
        return getRandomFile(baseDir, null);
    }

    public static File getRandomFile(String baseDir) {
        return getRandomFile(new File(baseDir), null);
    }

    public static File getRandomFile(File baseDir, String ext) {
        File file = null;

        do {
            double random = Math.random();
            String name = String.valueOf(random);
            name = name.substring(name.indexOf(".") + 4);
            file = new File(baseDir, ext != null ? (name + "." + ext) : name);
        } while (file.exists());

        return file;
    }

    /**
     * Sorts files in alphabetical order.
     *
     * @param files the files to be sorted.
     */
    public static void sortFiles(File[] files) {
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                String name1 = o1.getName().contains(".")
                        ? o1.getName().substring(0, o1.getName().indexOf("."))
                        : o1.getName();
                String name2 = o2.getName().contains(".")
                        ? o2.getName().substring(0, o2.getName().indexOf("."))
                        : o2.getName();
                boolean numeric = true;
                int n1 = 0, n2 = 0;

                try {
                    n1 = Integer.parseInt(name1);
                } catch (NumberFormatException nex) {
                    numeric = false;
                }

                try {
                    n2 = Integer.parseInt(name2);
                } catch (NumberFormatException nex) {
                    numeric = false;
                }

               
    
                 int result = numeric ? n1 - n2 : name1.compareTo(name2);
                return result == 0 ? 0 : result > 0 ? 1 : -1;
            }
        });
    }

    /**
     * Sorts files in alphabetical order when a string is included in the file names.
     * Sorts by ascending order when all files have numerical file names.
     * 
     * @param files the files to be sorted.
     */
    public static void sortStringFiles(File[] files) {
        boolean b = false;
        for (File file : files) {
            String fileName = getBaseAndExtension(file)[0];
            try {
                Integer.parseInt(fileName);
            } catch (NumberFormatException e) {
                b = true;
                break;
            }
        }
        final boolean containsString = b;

        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                String name1 = getBaseAndExtension(o1)[0];
                String name2 = getBaseAndExtension(o2)[0];

                int res = containsString
                        ? name1.compareTo(name2)
                        : Integer.parseInt(name1) - Integer.parseInt(name2);
                return res == 0 ? 0 : res > 0 ? 1 : -1;
            }
        });
    }

    /**
     * Finds a File in the specified <code>dir</code>, with a filename of
     * <code>name</code>, and ends with one of the extensions <code>exts</code>.
     *
     * @param dir
     * @param name
     * @param exts
     * @return
     */
    public static File findFile(File dir, String name, String[] exts) {
        File[] files = dir.listFiles();

        if (files != null && files.length > 0) {
            for (File xx : files) {
                File ret = null;

                if (xx.isDirectory()) {
                    ret = findFile(xx, name, exts);
                } else {
                    String[] names = FileUtilities.getBaseAndExtension(xx);

                    if (names[0].equals(name)) {
                        for (String yy : exts) {
                            if (names[1] != null
                                    && yy.toLowerCase().equals(names[1].toLowerCase())) {
                                ret = xx;
                                break;
                            }
                        }
                    }
                }

                if (ret != null) {
                    return ret;
                }
            }
        }

        return null;
    }

    /**
     * Finds a File in the specified <code>dir</code>, with a filename of
     * <code>name</code>, and extension matches <code>extRegEx</code>.
     *
     * @param dir
     * @param name
     * @param extRegEx extension (supports regular expression)
     * @param bypassFiles [optional] files to bypass and will not be included in
     * finding process.
     * @return
     */
    public static File findFile(File dir, String name, String extRegEx, File... bypassFiles) {
        File[] files = dir.listFiles();

        if (files != null && files.length > 0) {
            for (File xx : files) {
                File ret = null;

                if (xx.isDirectory()) {
                    ret = findFile(xx, name, extRegEx, bypassFiles);
                } else {
                    boolean bypass = false;
                    for (File f : bypassFiles) {
                        if (xx.getAbsolutePath().equals(f.getAbsolutePath())) {
                            bypass = true;
                            break;
                        }
                    }

                    if (bypass) {
                        continue;
                    }

                    String[] names = FileUtilities.getBaseAndExtension(xx);

                    if (names[0].equals(name)) {
                        if (names[1] != null && names[1].matches(extRegEx)) {
                            ret = xx;
                        }
                    }
                }

                if (ret != null) {
                    return ret;
                }
            }
        }

        return null;
    }

    /**
     * utilities tester
     */
    public static void main(String[] args) throws Exception {
        /*unzipGZ(new File("C:\\Documents and Settings" +
         "\\Ezekiel\\My Documents\\YAP TEST\\" +
         "Yap 20090806\\batch_sh_01-1.wav.gz"),
         new File("C:\\Documents and Settings" +
         "\\Ezekiel\\My Documents\\YAP TEST\\" +
         "Yap 20090806\\"));*/
        /*
         try {
         copy(new File("C:\\test5.avi"), new File("C:\\test5\\test6.bak"), true);
         } catch (FileNotFoundException ex) {
         ex.printStackTrace();
         } catch (IOException ex) {
         ex.printStackTrace();
         }*
         //try {
         /* Iterator test:
         Iterator<File>iter = walkDirectory(new File("C:\\Documents and Settings\\jon\\.itranscribe\\91\\92"));
         int files = 0;
         while (iter.hasNext()) { System.out.println(iter.next()); files++; }
         System.out.println("nfiles: " + files);
         */

        /* zipDirectory(new File("c:\\work\\tmp"), new File("c:\\work\\tmp.zip"), 9); */
        /* System.out.println(StringUtilities.byteArrayToHexString(digest("SHA1", new File("c:\\diff.txt")))); */
        /*} catch (FileNotFoundException ex) {
         ex.printStackTrace();
         } catch (NoSuchAlgorithmException ex) {
         ex.printStackTrace();
         } catch (IOException ex) {
         ex.printStackTrace();
         }*/
//        File src = new File("C:\\batch_ye_02-2.wav\\batch_ye_02-2.wav");
//        File dst = new File("C:\\aa\\batch_ye_02-2.wav");
//        nioCopy(src, dst);
        File test = new File("C:\\Users\\Ezekiel\\Documents\\ChartStream-1.59.00-Beta_Setup.exe");
//        long crc = getCRC32Checksum(test);
//        System.out.println(crc);

        String md5 = ChecksumGenerator.generateMd5(test);
        System.out.println(md5);

        String testMd5 = ChecksumGenerator.generateMd5FromString(md5 + md5 + md5 + md5);
        System.out.println(testMd5);
    }
}
