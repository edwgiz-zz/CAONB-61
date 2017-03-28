package com.aurea.caonb.egizatullin.processing;


import com.aurea.caonb.egizatullin.und.commons.CodeInspectionType;

public class CodeInspectionItem {

    public final CodeInspectionType type;
    public final String file;
    public final String entityName;
    public final int line;
    public final int column;

    public CodeInspectionItem(CodeInspectionType type, String file, String entityName, int line,
        int column) {
        this.type = type;
        this.file = file;
        this.entityName = entityName;
        this.line = line;
        this.column = column;
    }
}
