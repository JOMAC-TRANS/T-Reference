/*
 * FileReader.java
 *
 * Created on Mar 03, 2012, 03:48 PM
 */
package com.jomac.transcription.referencebuilder.utilities;

import java.io.File;

public abstract class FileReader {

    protected File document;

    public static FileReader createFileReader(File document) {

        if (document.getName().toLowerCase().endsWith(".html")) {
            return new PlainTextExtractor(document);
        } else if (document.getName().toLowerCase().endsWith(".doc")) {
            return new WordTextExtractor(document);
        } else if (document.getName().toLowerCase().endsWith(".docx")){
            //TODO: 
            System.out.println("extrating docx!");
            return new WordTextExtractor(document);
        }
        return null;
    }

    protected FileReader(File document) {
        this.document = document;
    }

    public abstract boolean openDocument();

    public abstract boolean closeDocument();

    public abstract boolean encryptedFile();

    public abstract String extractText();
}
