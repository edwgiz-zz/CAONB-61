package com.aurea.caonb.egizatullin.und.inspections;

import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;

import com.aurea.caonb.egizatullin.und.commons.ICodeInspectionCallback;
import com.scitools.understand.Database;
import com.scitools.understand.Entity;
import com.scitools.understand.Reference;


public class UnusedMethodCodeInspection implements ICodeInspection {

    @Override
    public void inspect(Database udb, ICodeInspectionCallback callback) {
        Entity[] ents = udb.ents("Private Method");
        for (Entity ent : ents) {
            boolean hasRefs = isNotEmpty(ent.refs(null, "~Class", true));
            if (!hasRefs) {
                Reference ref = ent.refs(null, "Class", true)[0];
                Entity file = ref.file();
                callback.accept(file.longname(false), ref.line(), ref.column(), ent.name());
            }
        }
    }
}