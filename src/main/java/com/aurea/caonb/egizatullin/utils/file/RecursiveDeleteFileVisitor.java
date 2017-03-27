package com.aurea.caonb.egizatullin.utils.file;

import static java.nio.file.Files.delete;
import static java.nio.file.Files.getFileAttributeView;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributeView;

public final class RecursiveDeleteFileVisitor implements FileVisitor<Path> {

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
        throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
        throws IOException {
        try {
            delete(file);
        } catch (AccessDeniedException e) {
            DosFileAttributeView dosAttrs = getFileAttributeView(file, DosFileAttributeView.class);
            if(dosAttrs != null) {
                dosAttrs.setReadOnly(false);
                delete(file);
            } else {
                throw e;
            }
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc)
        throws IOException {
        if (exc != null) {
            return FileVisitResult.TERMINATE;
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc)
        throws IOException {
        if (exc != null) {
            return FileVisitResult.TERMINATE;
        }
        delete(dir);
        return FileVisitResult.CONTINUE;
    }
}
