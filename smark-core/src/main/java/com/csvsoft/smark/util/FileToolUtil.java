package com.csvsoft.smark.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class FileToolUtil {
    public FileToolUtil() {
    }

    public static void writeToFile(String fileContent, String fileName) {
        File file = new File(fileName);
        File dir = file.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            System.out.println("Writing to file:" + fileName);
            FileUtils.write(file, fileContent, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException("Unable to write to file:" + fileName, e);
        }

    }
}
