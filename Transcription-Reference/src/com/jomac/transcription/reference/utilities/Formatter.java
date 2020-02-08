/*
 * Formatter.java
 *
 * Created on Jul 27, 2012, 3:07:23 PM
 */
package com.jomac.transcription.reference.utilities;

import com.jomac.transcription.reference.jpa.models.DocumentBean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Formatter {

    public static Formatter getInstance() {
        return FormatterHolder.INSTANCE;
    }

    private static class FormatterHolder {

        private static final Formatter INSTANCE = new Formatter();
    }

    public String supportWildChar(String query) {
        if (query != null && query.trim().length() > 0) {
            if (query.contains("*")) {
                if (Pattern.compile("[*][1-5]").matcher(query).find()) {
                    query = query.replaceAll(" [*][1-5] ", "%");
                    query = query.replaceAll("[*][1-5]", "%");
                } else {
                    query = query.replaceAll(" [*] ", "%");
                    query = query.replaceAll("[*]", "%");
                }
            }
        }
        return query.trim();
    }

    public String queryToExp(String query) {
        String regex = "(?<=[^\\\\])%";

        if (Pattern.compile(regex).matcher(query).find()) {
            query = "^" + query.replaceAll(regex, "|") + "$";
        }

        if (query.contains("\\")) {
            query = query.replace("\\", "");
        }

        return query;
    }

    public String beanToHtml(DocumentBean bean, int fontSize) {
        String content = bean.getDocumentHTML();

        if (content == null || content.isEmpty()) {
            return "";
        }

        StringBuffer replacement = new StringBuffer(content);

        replacement = replaceNewLine(replacement);
        replacement = replaceTab(replacement);

        //add to buffer
        return appendHTMLTags(replacement.toString(), fontSize).toString();

    }

    public String beanToHtml(String htmlString, int fontSize) {
        if (htmlString == null || htmlString.isEmpty()) {
            return "";
        }

        StringBuffer replacement = new StringBuffer(htmlString);

        replacement = replaceNewLine(replacement);
        replacement = replaceTab(replacement);

        //add to buffer
        return appendHTMLTags(replacement.toString(), fontSize).toString();

    }

    private StringBuilder appendHTMLTags(String content, int fontSize) {
        StringBuilder builder = new StringBuilder();

        builder.append("<html>");
        builder.append("<style TYPE=\"text/css\">");
        builder.append("<!--");
        builder.append("body { font: arial; ");

        if (fontSize != 0) {
            builder.append("font-size: ");
            builder.append(fontSize);
            builder.append("pt ");
        }

        builder.append("}-->");
        builder.append("</style>");
        builder.append("<body>");

        //Content
        builder.append(content);

        builder.append("<tr><td>&nbsp;</td></tr>");
        builder.append("</table>");
        builder.append("</body>");
        builder.append("</html>");
        return builder;
    }

    private StringBuffer replaceTab(StringBuffer replacement) {
        //replace the tab with html code
        Pattern pattern = Pattern.compile("\t");
        Matcher matcher = pattern.matcher(replacement.toString());
        replacement = new StringBuffer();

        while (matcher.find()) {
            matcher.appendReplacement(replacement, "&nbsp;&nbsp;"
                    + "&nbsp;&nbsp;"
                    + "&nbsp;&nbsp;"
                    + "&nbsp;&nbsp;");
        }
        matcher.appendTail(replacement);
        return replacement;
    }

    private StringBuffer replaceNewLine(StringBuffer replacement) {
        //replace the new line with html code
        Pattern pattern = Pattern.compile("\n");
        Matcher matcher = pattern.matcher(replacement.toString());
        replacement = new StringBuffer();

        while (matcher.find()) {
            matcher.appendReplacement(replacement, "<br/>");
        }
        matcher.appendTail(replacement);
        return replacement;
    }

    public String getStringBetween(String str, String open, String close) {
        if (str == null || open == null || close == null) {
            return "";
        }
        int start = str.indexOf(open);
        if (start != -1) {
            int end = str.indexOf(close, start + open.length());
            if (end != -1) {
                return str.substring(start + open.length(), end);
            }
        }
        return "";
    }
}
