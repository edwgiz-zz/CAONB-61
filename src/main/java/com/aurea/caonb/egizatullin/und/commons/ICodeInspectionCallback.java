package com.aurea.caonb.egizatullin.und.commons;

/**
 * Collector of code inspections
 */
public interface ICodeInspectionCallback {

    void accept(String filename, int line, int column, String entityName);
    CodeInspectionType getCodeInspectionType();
}
