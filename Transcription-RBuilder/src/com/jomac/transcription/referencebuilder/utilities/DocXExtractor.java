/*
 * DocXExtractor.java
 *
 * Created on Feb. 15, 2020, 12:30 PM
 */
package com.jomac.transcription.referencebuilder.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class DocXExtractor extends FileReader {

    XWPFWordExtractor wordExtractor;
    private InputStream is;
    private boolean encripted;

    public DocXExtractor(File document) {
        super(document);
    }

    @Override
    public boolean openDocument() {
        boolean result = true;

        try {
            is = new FileInputStream(document);
            XWPFDocument xwpDoc = new XWPFDocument(is);
            wordExtractor = new XWPFWordExtractor(xwpDoc);
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
