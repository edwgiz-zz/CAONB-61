package com.aurea.caonb.egizatullin.und.inspections;

import com.aurea.caonb.egizatullin.und.commons.ICodeInspectionCallback;
import com.scitools.understand.Database;
import com.scitools.understand.Entity;
import com.scitools.understand.Reference;
import org.apache.commons.lang3.StringUtils;

public class UnusedEnumConstantCodeInspection implements ICodeInspection {

    @Override
    public void inspect(Database udb, ICodeInspectionCallback callback) {
        Entity[] ents = udb.ents("EnumConstant");
        for (Entity ent : ents) {
            String name = ent.name();
            Reference[] usebyRefs = ent.refs("Java Useby", null, false);
            if (usebyRefs.length == 0) {
                Reference[] refs = ent.refs("Definein", null, false);
                Reference ref = refs[0];
                if (StringUtils.contains(ref.ent().kind().name(), "Private")) {
                    Entity file = ref.file();
                    callback.accept(file.longname(false), ref.line(), ref.column(), name);
                }
            }
        }
    }

}