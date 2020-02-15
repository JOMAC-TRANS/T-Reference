/*
 * PlainTextExtractor.java
 *
 * Created on Mar 03, 2012, 03:48 PM
 */
package com.jomac.transcription.referencebuilder.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;

public class PlainTextExtractor extends FileReader {

    private InputStream is;
    private Scanner scanner;

    public PlainTextExtractor(File document) {
        super(document);
    }

    @Override
    public boolean openDocument() {
        boolean result = true;

        try {
            is = new FileInputStream(document);
            scanner = new Scanner(is);
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }

        return result;
    }

    @Override
    public boolean closeDocument() {
        boolean result = true;

        try {
            is.close();
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }

        return result;
    }

    @Override
    public String extractText() {
        StringBuilder builder = new StringBuilder();

        try {
            while (scanner.hasNextLine()) {
                builder.append("\n");
                builder.append(scanner.nextLine());
            }
        } catch (Exception e) {
            return null;
        } finally {
            return builder.toString();
        }
    }

    @Override
    public boolean encryptedFile() {
        return false;
    }
}
