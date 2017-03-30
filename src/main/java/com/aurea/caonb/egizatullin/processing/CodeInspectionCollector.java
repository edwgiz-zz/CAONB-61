package com.aurea.caonb.egizatullin.processing;

import com.aurea.caonb.egizatullin.und.commons.CodeInspectionType;
import com.aurea.caonb.egizatullin.und.commons.ICodeInspectionCallback;
import java.nio.file.Paths;
import java.util.Collection;


public class CodeInspectionCollector implements ICodeInspectionCallback {

    private final int projectPathDepth;
    private final CodeInspectionType type;
    private final Collection<CodeInspectionItem> inspectionItems;

    /**
     * @param projectPathDepth will be used to convert absolute paths from 'Understand' database to
     * their relative representation
     * @param type type of code inspection
     * @param inspectionItems will be used to collect inspection items from 'Understand' service
     */
    public CodeInspectionCollector(
        int projectPathDepth,
        CodeInspectionType type,
        Collection<CodeInspectionItem> inspectionItems) {
        this.projectPathDepth = projectPathDepth;
        this.type = type;
        this.inspectionItems = inspectionItems;
    }

    @Override
    public void accept(String filename, int line, int column, String entityName) {
        filename = Paths.get(filename)
            .subpath(projectPathDepth,
                Paths.get(filename).getNameCount())
            .toString()
            .replace('\\', '/');// to unix relative path

        inspectionItems.add(new CodeInspectionItem(
            getCodeInspectionType(),
            filename,
            entityName,
            line,
            column));
    }

    @Override
    public CodeInspectionType getCodeInspectionType() {
        return type;
    }

    int getProjectPathDepth() {
        return projectPathDepth;
    }

    Collection<CodeInspectionItem> getInspectionItems() {
        return inspectionItems ;
    }
}
