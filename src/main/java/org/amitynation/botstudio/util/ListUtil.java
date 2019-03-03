package org.amitynation.botstudio.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class ListUtil {

    /**
     * Gets first or null object from given list. My version of Kotlin's built in function that does the same.
     *
     * @param list      List to iterate
     * @param predicate Lambda to determine if the object [T] is what you're looking for.
     * @return Returns the first object of type [T] or null if none is found that matches the predicate.
     */
    @Nullable
    public static <T> T firstOrNull(@NotNull List<T> list, @NotNull Function<T, Boolean> predicate) {
        for (T t : list) {
            if (predicate.apply(t)) {
                return t;
            }
        }
        return null;
    }

    @Nullable
    public static <T> T firstOrNull(@NotNull List<T> list) {
        if (list.size() == 0) return null;
        return list.get(0);
    }

}
