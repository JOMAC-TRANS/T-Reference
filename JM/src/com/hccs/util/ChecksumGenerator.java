/*
 * ChecksumGenerator.java
 *
 * Created on May 9, 2013  4:03:34 PM
 *
 *
 */
package com.hccs.util;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class ChecksumGenerator {

    public static final String SHA1 = "SHA1";
    public static final String MD5 = "MD5";

    public static String generateSha1(File file) throws Exception {
        return generateChecksum(file, SHA1);
    }

    public static String generateMd5(File file) throws Exception {
        return generateChecksum(file, MD5);
    }

    public static String generateMd5FromString(String data) throws Exception {
        return generateChecksumFromString(data, MD5);
    }

    public static String generateSha1FromString(String data) throws Exception {
        return generateChecksumFromString(data, SHA1);
    }

    private static String generateChecksum(File file, String type) throws Exception {
        StringBuilder buffer = new StringBuilder("");
        if (!(type.equals(SHA1) || type.equals(MD5))) {
            //return null;
            throw new Exception("type must be " + SHA1 + " or " + MD5 + ".");
        }

        try {
            MessageDigest md = MessageDigest.getInstance(type);
            FileInputStream fis = new FileInputStream(file);
            byte[] dataBytes = new byte[1024];
            int nread = 0;

            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }

            byte[] mdbytes = md.digest();

            //convert the byte to hex format
            for (int i = 0; i < mdbytes.length; i++) {
                buffer.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return buffer.toString();
    }

    private static String generateChecksumFromString(String data, String type) throws Exception {
        StringBuilder buffer = new StringBuilder("");
        if (!(type.equals(SHA1) || type.equals(MD5))) {
            //return null;
            throw new Exception("type must be " + SHA1 + " or " + MD5 + ".");
        }

        try {
            MessageDigest md = MessageDigest.getInstance(type);
            md.update(data.getBytes());

            byte[] mdbytes = md.digest();

            //convert the byte to hex format
            for (int i = 0; i < mdbytes.length; i++) {
                buffer.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return buffer.toString(); 
    }


    public static void main(String... args) throws Exception{
        String f1 = "D:\\20082851\\1.PDF";
        String f2 = "D:\\20082851\\1-newer.PDF";

        String md51 = generateChecksum(new File(f1), "MD5");
        String md52 = generateChecksum(new File(f2), "MD5");

        System.out.println(md51);
        System.out.println(md52);
    }
}
