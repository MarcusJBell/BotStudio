package org.amitynation.botstudio.util;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatUtil {

    // Used for '&' color conversion. Matches all colors. 0-9, A-F, K-O, and R.
    private static final Pattern REPLACE_COLOR_PATTERN = Pattern.compile("(?i)&(?=[0-9a-fk-or])");

    // Used for 'u00a7' color conversion. Matches all colors. 0-9, A-F, K-O, and R.
    private static final Pattern UNREPLACE_COLOR_PATTERN = Pattern.compile("(?i)\u00a7(?=[0-9a-fk-or])");

    // Used to get the color after the color symbol (\u00A7). Matches all colors. 0-9, A-F, K-O, and R.
    private static final Pattern GET_COLOR_CODE_PATTERN = Pattern.compile("(?i)(?<=\u00A7)[0-9a-fk-or]");

    // Used to get the color and symbol (\u00A7). Matches all colors. 0-9, A-F, K-O, and R.
    private static final Pattern GET_COLOR_CODE_WITH_SYMBOL_PATTERN = Pattern.compile("(?i)\u00A7[0-9a-fk-or]");

    /**
     * Replaces '&' color codes with minecraft's color codes. (replaces '&' with unicode symbol)
     *
     * @param input String to apply color to.
     * @return Returns string with color applied.
     */
    @NotNull
    public static String replaceColor(@NotNull String input) {
        int colorCode = '\u00a7';
        return REPLACE_COLOR_PATTERN.matcher(input).replaceAll("\u00A7");
    }

    @NotNull
    public static String unreplaceColor(@NotNull String input) {
        return UNREPLACE_COLOR_PATTERN.matcher(input).replaceAll("&");
    }

    @NotNull
    public static String replaceColorWithReadable(@NotNull String input) {
        String result = input;
        Matcher matcher = GET_COLOR_CODE_PATTERN.matcher(input);
        while (matcher.find()) {
            String found = matcher.group();
            result = GET_COLOR_CODE_WITH_SYMBOL_PATTERN.matcher(result).replaceFirst(getReadableFromCode(found.charAt(0)));
        }
        return result;
    }

    public static String getReadableFromCode(char colorCode) {
        return "<" + ChatColor.getByChar(colorCode).name() + ">";
    }
}
