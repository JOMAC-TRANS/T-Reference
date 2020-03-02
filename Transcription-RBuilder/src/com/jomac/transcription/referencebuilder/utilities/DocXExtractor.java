/*
 * DocXExtractor.java
 *
 * Created on Feb. 15, 2020, 12:30 PM
 */
package com.jomac.transcription.referencebuilder.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

public class DocXExtractor extends FileReader {

    private List<XWPFParagraph> paragraphList;
    private StringBuilder sb;
    private InputStream is;
    private boolean encripted;

    public DocXExtractor(File document) {
        super(document);
    }

    @Override
    public boolean openDocument() {
        boolean result = true;

        try {
            sb = new StringBuilder();
            is = new FileInputStream(document);
            XWPFDocument xwpDoc = new XWPFDocument(is);
            paragraphList = xwpDoc.getParagraphs();
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

        int[] levelCurrentValues = new int[]{0, 0, 0};
        for (XWPFParagraph paragraph : paragraphList) {
            String levelText = paragraph.getNumLevelText();
            BigInteger levelDepth = paragraph.getNumIlvl();

            if (levelText != null) {
                levelCurrentValues[levelDepth.intValue()] += 1;

                levelText = levelText.replace("%1", "" + levelCurrentValues[0]);
                levelText = levelText.replace("%2", "" + levelCurrentValues[1]);
                levelText = levelText.replace("%3", "" + levelCurrentValues[2]);
                sb.append(levelText).append(" ").append(paragraph.getText()).append("\n");
            } else {
                if (!paragraph.getText().trim().isEmpty() && ("Heading1".equalsIgnoreCase(paragraph.getStyle()))) {
                    sb.append("<b>");
                    sb.append(paragraph.getText());
                    sb.append("</b>").append("\n");
                } else {
                    sb.append(paragraph.getText()).append("\n");
                }
            }
        }

        System.out.println(sb.toString());

        return sb.toString();
    }
}
