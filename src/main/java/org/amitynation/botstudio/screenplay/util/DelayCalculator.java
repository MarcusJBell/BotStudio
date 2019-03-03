package org.amitynation.botstudio.screenplay.util;

import org.amitynation.botstudio.screenplay.Screenplay;

public class DelayCalculator {

    public static long calculateTypingTime(Screenplay screenplay, String message) {
        long delay = screenplay.getMessageDelay();
        if (delay < 0) return -1L;
        // Using 60 WPM aka 1 word per second.
        long typeTime = countWords(message) * 1000L;
        // Takes longer than time until next message so start typing now.
        if (typeTime > delay) return -1L;

        return typeTime;
    }

    private static int countWords(String message) {
        String[] split = message.split("\\s+");
        return split.length;
    }

}
