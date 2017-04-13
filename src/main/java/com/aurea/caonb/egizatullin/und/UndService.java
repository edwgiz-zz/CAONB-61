package com.aurea.caonb.egizatullin.und;

import static com.aurea.caonb.egizatullin.und.UndUtils.getEntityIds;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.Files.walkFileTree;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import com.aurea.caonb.egizatullin.und.commons.ICodeInspectionCallback;
import com.aurea.caonb.egizatullin.und.inspections.ICodeInspection;
import com.aurea.caonb.egizatullin.und.inspections.UnusedEnumConstantCodeInspection;
import com.aurea.caonb.egizatullin.und.inspections.UnusedFieldCodeInspection;
import com.aurea.caonb.egizatullin.und.inspections.UnusedMethodCodeInspection;
import com.aurea.caonb.egizatullin.und.inspections.UnusedParameterCodeInspection;
import com.aurea.caonb.egizatullin.und.inspections.UnusedVariableCodeInspection;
import com.aurea.caonb.egizatullin.utils.process.ProcessUtils;
import com.scitools.understand.Database;
import com.scitools.understand.Understand;
import com.scitools.understand.UnderstandException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;
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
            Set<Integer> javaEnumIds = getEntityIds(udb, "Enum");
            try {
                for (ICodeInspectionCallback cic : collectors) {
                    List<ICodeInspection> cis = getCodeInspection(cic, javaEnumIds);
                    for (ICodeInspection ci : cis) {
                        ci.inspect(udb, cic);
                    }
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
     *
     * @param cic code inspection callback
     * @return code inspection instance
     */
    List<ICodeInspection> getCodeInspection(ICodeInspectionCallback cic,
        Set<Integer> javaEnumIds) {
        switch (cic.getCodeInspectionType()) {
            case UNUSED_METHOD:
                return singletonList(new UnusedMethodCodeInspection(javaEnumIds));
            case UNUSED_VARIABLE:
                return singletonList(new UnusedVariableCodeInspection());
            case UNUSED_FIELD:
                return asList(
                    new UnusedFieldCodeInspection(),
                    new UnusedEnumConstantCodeInspection());
            case UNUSED_PARAMETER:
                return singletonList(new UnusedParameterCodeInspection(javaEnumIds));
            default:
                throw new IllegalArgumentException(cic.toString()
                    + " has unsupported inspection type: " + cic.getCodeInspectionType());
        }
    }
}