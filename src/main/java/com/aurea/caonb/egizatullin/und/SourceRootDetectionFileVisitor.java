package com.aurea.caonb.egizatullin.und;

import static java.nio.file.FileVisitResult.CONTINUE;
import static org.apache.commons.lang3.StringUtils.endsWithIgnoreCase;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;


final class SourceRootDetectionFileVisitor extends SimpleFileVisitor<Path> {

    private final int rootNameCount;
    private final Writer out;

    SourceRootDetectionFileVisitor(int rootNameCount, Writer out) {
        this.rootNameCount = rootNameCount;
        this.out = out;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (endsWithIgnoreCase(file.getFileName().toString(), ".java")) {
            out.write(file.subpath(rootNameCount, file.getNameCount()).toString());
            out.write('\n');
        }
        return CONTINUE;
    }
}
