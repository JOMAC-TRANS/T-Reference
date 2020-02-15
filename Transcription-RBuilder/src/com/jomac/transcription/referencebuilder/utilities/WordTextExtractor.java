/*
 * WordTextExtractor.java
 *
 * Created on Aug. 01, 2012, 01:04 PM
 */
package com.jomac.transcription.referencebuilder.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hwpf.extractor.WordExtractor;

public class WordTextExtractor extends FileReader {

    private WordExtractor wordExtractor;
    private InputStream is;
    private boolean encripted;

    public WordTextExtractor(File document) {
        super(document);
    }

    @Override
    public boolean openDocument() {
        boolean result = true;

        try {
            is = new FileInputStream(document);
            wordExtractor = new WordExtractor(is);
            encripted = false;
        } catch (EncryptedDocumentException ex) {
            encripted = true;
            result = false;
        } catch (Exception e) {
            result = false;
        }

        return result;
    }

    @Override
    public boolean closeDocument() {
        boolean result = true;

        try {
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }

        return result;
    }

    @Override
    public boolean encryptedFile() {
        return encripted;
    }

    @Override
    public String extractText() {
        return wordExtractor.getText();
    }
}
