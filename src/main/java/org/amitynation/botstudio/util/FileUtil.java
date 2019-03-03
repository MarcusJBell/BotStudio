package org.amitynation.botstudio.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class FileUtil {

    public static void createOrClearFile(File... files) {
        for (File file : files) {
            createOrClearFile(file);
        }
    }

    /**
     * If file doesn't exist it will be created. If file contains data it will be erased.
     */
    public static void createOrClearFile(File file) {
        if (!file.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                PrintWriter writer = new PrintWriter(file);
                writer.print("");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
