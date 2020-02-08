package com.hccs.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;

public class SHA1Digester {

    private static MessageDigest msgDigest = null;
    private static String algorithm = "SHA-1";
    private static String encoding = "UTF-8";

    public static String digest(String plainText) {
        try {
            msgDigest = MessageDigest.getInstance(algorithm);
            msgDigest.update(plainText.getBytes(encoding));
            byte rawByte[] = msgDigest.digest();
            return Base64.encodeBase64String(rawByte);

        } catch (NoSuchAlgorithmException e) {
            System.out.println("No Such Algorithm Exists");
        } catch (UnsupportedEncodingException e) {
            System.out.println("The Encoding Is Not Supported");
        }
        return null;
    }
}