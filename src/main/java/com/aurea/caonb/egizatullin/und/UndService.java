package com.aurea.caonb.egizatullin.und;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.Files.walkFileTree;
import static java.nio.file.StandardOpenOption.CREATE_NEW;

import com.aurea.caonb.egizatullin.und.commons.ICodeInspectionCallback;
import com.aurea.caonb.egizatullin.und.inspections.ICodeInspection;
import com.aurea.caonb.egizatullin.und.inspections.UnusedClassMemberCodeInspection;
import com.aurea.caonb.egizatullin.und.inspections.UnusedParameterCodeInspection;
import com.aurea.caonb.egizatullin.utils.process.ProcessUtils;
import com.scitools.understand.Database;
import com.scitools.understand.Understand;
import com.scitools.understand.UnderstandException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class UndService {


    private static final String UDB_FILE_NAME = "und.udb";
    private static final String UND_SRC_LIST_FILE_NAME = "und_src_list.txt";

    private final String undExe;


    public UndService(
        @Value("${scitools.und.exe.path}") String undExe) {
        this.undExe = undExe;
    }

    public void buildDatabase(Path projectDir) {
        try {
            try (BufferedWriter filesToAnalyze =
                newBufferedWriter(projectDir.resolve(UND_SRC_LIST_FILE_NAME), UTF_8, CREATE_NEW)) {
                walkFileTree(projectDir,
                    new SourceRootDetectionFileVisitor(projectDir.getNameCount(), filesToAnalyze));
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't find src files in " + projectDir, e);
        }

        ProcessBuilder pb = new ProcessBuilder(undExe, "-quiet",
            "create", "-db", UDB_FILE_NAME, "-languages", "java",
            "add", "@" + UND_SRC_LIST_FILE_NAME,
            "analyze");
        pb.directory(projectDir.toFile());
        ProcessUtils.run(pb, code -> code == 0, p -> {
        });

        Path udbPath = projectDir.resolve(UDB_FILE_NAME);
        if (!exists(udbPath)) {
            throw new RuntimeException("Can't create " + udbPath + " file");
        }
    }

    public void inspectCode(Path p, Collection<ICodeInspectionCallback> collectors) {
        Path udbPath = p.resolve(UDB_FILE_NAME);
        try {
            Database udb = Understand.open(udbPath.toString());
            try {
                for (ICodeInspectionCallback cic : collectors) {
                    getCodeInspection(cic).inspect(udb, cic);
                }
            } finally {
                udb.close();
            }
        } catch (UnderstandException e) {
            throw new RuntimeException("Can't read " + udbPath);
        }
    }

    /**
     * Factory method
     * @param cic code inspection callback
     * @return code inspection instance
     */
    ICodeInspection getCodeInspection(ICodeInspectionCallback cic) {
        switch (cic.getCodeInspectionType()) {
            case UNUSED_METHOD:
                return new UnusedClassMemberCodeInspection("Private Method");
            case UNUSED_FIELD:
                return new UnusedClassMemberCodeInspection("Private Variable");
            case UNUSED_PARAMETER:
                return new UnusedParameterCodeInspection();
            default:
                throw new IllegalArgumentException(cic.toString()
                    + " has unsupported inspection type: " + cic.getCodeInspectionType());
        }
    }
}