/*
 * @(#)String.java
 *
 * Created on January 9, 2007, 4:20 PM
 *
 * Copyright (c) DynamicSoftSolutions, Inc.
 */
package com.hccs.util;

public class StringUtilities {

    /**
     * Uppercase the first character of each word in an underscore separated
     * words.
     *
     * e.g. some_word -> Some Word
     */
    public static String ucwordsDash(String word) {
        if (word == null) {
            return "";
        }
        String[] orig = word.split("_");
        for (int i = 0; i < orig.length; i++) {
            orig[i] = capitalize(orig[i]);
        }
        return join(",", orig);
    }

    /**
     * Joins the string representation of data into one string.
     *
     * e.g. join(", ", ["apples", "bananas", "carrots"]) -> "apples, bananas,
     * carrots"
     *
     * @param sep string separator
     * @param data the data to be joined. This uses toString(), so data[] can be
     * any array.
     */
    public static <E> String join(String sep, E[] data) {
        StringBuffer buf = new StringBuffer();
        if (data.length > 0) {
            buf.append(data[0]);
            if (sep == null) {
                sep = "";
            }
            for (int i = 1; i < data.length; i++) {
                buf.append(sep + data[i]);
            }
        }
        return new String(buf);
    }

    /**
     * repeats a
     * <code>str</code>
     * <code>num</code> times, separated by
     * <code>sep</code>.
     *
     * e.g. repeat(",", "?", 5) -> "?,?,?,?,?"
     *
     * @param sep string separator
     * @param str string to be repeated
     * @param num number of times str is to be repeated
     * @returns repeated string
     */
    public static String repeat(String sep, String str, int num) {
        if (str == null) {
            return null;
        }
        String[] data = new String[num];
        for (int i = 0; i < num; i++) {
            data[i] = str;
        }
        return join(sep, data);
    }

    /**
     * capitalizes the 1st letter of the string and converts to lower case the
     * rest of the string.
     *
     * e.g. capitalize("hello World") -> "Hello world"
     *
     * @param s the string to be capitalized
     * @returns capitalized string
     */
    public static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        } else if (s.length() == 1) {
            return s.toUpperCase();
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    /**
     * adapted from http://www.rgagnon.com/javadetails/java-0306.html *didn't do
     * anything about the space char
     */
    public static String escapeHTML(String s) {
        StringBuffer buf = new StringBuffer();
        if (s != null) {
            int len = s.length();
            char c;
            int ci;
            for (int i = 0; i < len; i++) {
                c = s.charAt(i);
                switch (c) {
                    case '<':
                        buf.append("&lt;");
                        break;
                    case '>':
                        buf.append("&gt;");
                        break;
                    case '&':
                        buf.append("&amp;");
                        break;
                    case '"':
                        buf.append("&quot;");
                        break;
                    default:
                        ci = 0xffff & c;
                        if (ci < 160) // 7 bit
                        {
                            buf.append(c);
                        } else { // unicode
                            buf.append("&#");
                            buf.append(Integer.toString(ci));
                            buf.append(';');
                        }

                        break;
                }
            }
        }

        return buf.toString();
    }

    public static String byteArrayToHexString(byte[] buf) {
        StringBuffer strbuf = new StringBuffer();
        String tmp;
        int len;
        for (byte b : buf) {
            tmp = Integer.toHexString(b);
            //System.out.println(tmp);
            len = tmp.length();
            if (len < 2) {
                strbuf.append("0" + tmp);
            } else if (len > 2) {
                strbuf.append(tmp.substring(len - 2, len));
            } else {
                strbuf.append(tmp);
            }
        }
        return strbuf.toString();
    }

    /**
     * Remove all spaces in the input string.
     *
     * @param str the String to be <em>de-spaced</em>
     * @return a String with spaces inside removed
     */
    public static String trimSpaces(String str) {
        return str.replaceAll("\\s", "");
    }

    /**
     * Convert bytes into human readble String format.
     *
     * @param bytes the long to be converted
     * @return a String of human readble format (e.g. KB for kilobytes, etc.)
     */
    public static String bytes2HumanReadable(long bytes) {
        if (bytes < 1024) {
            return bytes + " bytes";
        }

        float kb = bytes / 1024f;
        float mb = 0;
        float gb = 0;

        if (kb > 1000) {
            mb = kb / 1024f;

            if (mb > 1000) {
                gb = mb / 1024f;
                return String.valueOf(gb).substring(0, String.valueOf(gb).indexOf('.') + 2) + " GB";
            } else {
                return String.valueOf(mb).substring(0, String.valueOf(mb).indexOf('.') + 2) + " MB";
            }
        } else {
            return String.valueOf(kb).substring(0, String.valueOf(kb).indexOf('.') + 2) + " KB";
        }
    }

    /**
     * Convert to base-32 string (i.e. 0-9A-T). This is used in encoding
     * contactid into authorid so to fit 2^64 into 13 characters (5*13=65)
     */
    public static String encodeBase32(long num) {
        final char[] CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V'};
        StringBuilder ret = new StringBuilder();
        int tmp;

        while (num > 0) {
            tmp = (int) (num % 32);
            num = num / 32;
            ret.append(CHARS[tmp]);
        }
        ret.append("0000000000000");

        return ret.reverse().substring(ret.length() - 13);
    }

    /**
     * Convert a Base32 encoded string (e.g. via encodeBase32()) into a long
     * value.
     *
     * @param str the Base32 encoded string
     * @return the long equivalent of the encoded string
     */
    public static long decodeBase32(String str) {
        final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUV";
        StringBuffer buf = new StringBuffer(str.toUpperCase());
        int strlen = str.length(), pos;
        long value = 0, scale = 1;
        char ch;

        if (str.length() > 13 || str.length() < 1) {
            throw new IllegalArgumentException(str + " is not a valid Base32 string.");
        }

        for (int i = strlen - 1; i >= 0; i--) {
            ch = buf.charAt(i);
            pos = CHARS.indexOf((int) ch);
            if (pos == -1) {
                throw new IllegalArgumentException(str + " is not a valid Base32 string.");
            }
            value += pos * scale;
            scale *= 32;
        }

        return value;
    }

    public static String stringToAscii(String string) {
        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);

            buffer.append(
                    Character.isLetterOrDigit(c)
                    ? String.valueOf(c)
                    : ("_" + (int) c + "_"));
        }
        return buffer.toString();
    }

    public static String asciiToString(String string) {
        StringBuffer buffer = new StringBuffer();
        int chunk = 0, sentinel = 0;

        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);

            if (c == '_') {
                if (i == string.length() - 1) {
                    break;
                }
                if (string.charAt(i + 1) == '_') {
                    continue;
                }

                chunk = i;
                sentinel = string.indexOf('_', chunk + 1);
                String ascii = string.substring(
                        chunk + 1,
                        sentinel);
                i = sentinel;

                buffer.append((char) Integer.parseInt(ascii));
            } else {
                buffer.append(c);
            }
        }
        return buffer.toString();
    }

    /**
     * Convert nanoseconds into human readble String format.
     *
     * @param nano the nanoseconds to be converted
     * @return a String of human readble format
     */
    public static String nanoTime2HumanReadable(long nano) {
        long millis = nano / 1000000L;
        long secs = 0;
        long mins = 0,
                hours = 0,
                days = 0;
        if (millis > 1000) {
            secs = millis / 1000;
            millis = millis % 1000;
        }

        if (secs > 60) {
            mins = secs / 60;
            secs = secs % 60;
        }

        if (mins > 60) {
            hours = mins / 60;
            mins = mins % 60;
        }

        if (hours > 24) {
            days = hours / 24;
            hours = hours % 24;
        }

        return (days >= 1 ? (days + "day ") : "")
                + (hours >= 1 ? (hours + "hr ") : "")
                + (mins >= 1 ? (mins + "min ") : "")
                + (secs >= 1 ? (secs + "sec ") : "")
                + (millis >= 1 ? (millis + "ms ") : "");
    }
}
