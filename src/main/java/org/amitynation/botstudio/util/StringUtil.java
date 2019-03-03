package org.amitynation.botstudio.util;

public class StringUtil {

    public static String getFinalArg(String[] args, int start) {
        final StringBuilder builder = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            if (i != start) {
                builder.append(" ");
            }
            builder.append(args[i]);
        }
        return builder.toString();
    }

    /**
     * Substrings a string before the last occurrence of the delimiter. My version of Kotlin's built in function that does the same.
     *
     * @param string    String to substring
     * @param delimiter Where the string should be substringed.
     * @return Returns the string up to the delimiter.
     */
    public static String substringBeforeLast(String string, String delimiter) {
        int lastIndex = string.lastIndexOf(delimiter);
        if (lastIndex == -1) {
            return string;
        }
        return string.substring(0, lastIndex);
    }

    /**
     * Substrings a string after the last occurrence of the delimiter. My version of Kotlin's built in function that does the same.
     *
     * @param string    String to substring
     * @param delimiter Where the string should be substringed.
     * @return Returns the string after the delimiter.
     */
    public static String substringAfterLast(String string, String delimiter) {
        int lastIndex = string.lastIndexOf(delimiter);
        if (lastIndex == -1) {
            return string;
        }
        return string.substring(lastIndex + 1);
    }
}
