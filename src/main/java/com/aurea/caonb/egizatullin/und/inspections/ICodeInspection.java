package com.aurea.caonb.egizatullin.und.inspections;

import com.aurea.caonb.egizatullin.und.commons.ICodeInspectionCallback;
import com.scitools.understand.Database;

/**
 * Represents a certain type of code inspection
 */
public interface ICodeInspection {

    /**
     * Searches for code inspection items in given database
     * @param udb 'Understand' database
     * @param callback consumer of found code inspection items
     */
    void inspect(Database udb, ICodeInspectionCallback callback);
}
