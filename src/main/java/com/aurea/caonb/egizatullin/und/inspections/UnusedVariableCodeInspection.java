package com.aurea.caonb.egizatullin.und.inspections;

import com.aurea.caonb.egizatullin.und.commons.ICodeInspectionCallback;
import com.scitools.understand.Database;
import com.scitools.understand.Entity;
import com.scitools.understand.Reference;

public class UnusedVariableCodeInspection implements ICodeInspection {

    @Override
    public void inspect(Database udb, ICodeInspectionCallback callback) {
        Entity[] ents = udb.ents("~Private ~Public ~Protected ~Static ~Unknown Variable");//3984
        for (Entity ent : ents) {
            if(ent.name().contains("lfw1")) {
                Reference[] refs = ent.refs("Java Useby", null, false);
                if(refs.length == 0) {
                    Reference declaration = ent.refs("Definein", "Method", true)[0];
                    Entity file = declaration.file();
                    callback.accept(file.longname(false), declaration.line(), declaration.column(),
                        ent.name());
                }
            }
        }

    }
}
