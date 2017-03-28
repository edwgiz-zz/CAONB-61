package com.aurea.caonb.egizatullin.und.inspections;

import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.endsWith;

import com.aurea.caonb.egizatullin.und.commons.ICodeInspectionCallback;
import com.scitools.understand.Database;
import com.scitools.understand.Entity;
import com.scitools.understand.Reference;
import org.apache.commons.lang3.StringUtils;

public class UnusedClassMemberCodeInspection implements ICodeInspection {

    private final String kinds;

    public UnusedClassMemberCodeInspection(String kinds) {
        this.kinds = kinds;
    }

    @Override
    public void inspect(Database udb, ICodeInspectionCallback callback) {
        Entity[] ents = udb.ents(kinds);
        for (Entity ent : ents) {
            String name = ent.name();
            if (endsWith(name, ".serialVersionUID")) {
                continue;
            }
            boolean hasRefs = isNotEmpty(ent.refs(null, "~Class", true));
            if (!hasRefs) {
                Reference ref = ent.refs(null, "Class", true)[0];
                Entity file = ref.file();
                callback.accept(file.longname(false), ref.line(), ref.column(), name);
            }
        }
    }
}