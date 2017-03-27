package com.aurea.caonb.egizatullin.utils.file;


import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isWritable;
import static java.nio.file.Paths.get;

import java.io.IOException;
import java.nio.file.Path;

public final class FileUtils {

    public static Path getDirectory(String baseDir, String subDir, final String message) {
        Path p = get(baseDir);
        if (!isDirectory(p)) {
            throw new RuntimeException(
                message + ": '" + p + "' is not a directory");
        }
        if (!isWritable(p)) {
            throw new RuntimeException(
                message + ": '" + p + "' is not a writable directory");
        }

        p = p.resolve(subDir);
        if (!isDirectory(p)) {
            try {
                createDirectories(p);
            } catch (IOException e) {
                throw new RuntimeException(
                    message + ": '" + p + "': can't create dir", e);
            }
        }
        return p;
    }


    private FileUtils() {
    }
}
