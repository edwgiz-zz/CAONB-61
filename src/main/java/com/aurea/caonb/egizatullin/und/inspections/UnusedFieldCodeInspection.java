package com.aurea.caonb.egizatullin.und.inspections;

import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.endsWith;

import com.aurea.caonb.egizatullin.und.commons.ICodeInspectionCallback;
import com.scitools.understand.Database;
import com.scitools.understand.Entity;
import com.scitools.understand.Kind;
import com.scitools.understand.Reference;

public class UnusedFieldCodeInspection implements ICodeInspection {

    @Override
    public void inspect(Database udb, ICodeInspectionCallback callback) {
        Entity[] ents = udb.ents("Private Variable");
        SerialVersionUidMatcher f = new SerialVersionUidMatcher();
        for (Entity ent : ents) {
            String name = ent.name();
            if (f.matches(name, ent)) {
                continue;
            }

            boolean hasRefs = isNotEmpty(ent.refs(null, "~Class", true));
            if (!hasRefs) {
                Reference[] refs = ent.refs(null, "Class", false);
                Reference ref = refs[0];
                if (hasClassReference(refs, ref)) {
                    continue;
                }

                Entity file = ref.file();
                callback.accept(file.longname(false), ref.line(), ref.column(), name);
            }
        }
    }

    private boolean hasClassReference(Reference[] refs, Reference selfRef) {
        for (int i = 1; i < refs.length; i++) {
            Reference ref = refs[i];
            if (ref.line() == selfRef.line()
                && ref.column() <= selfRef.column()) {
                // skip ref to itself as well as ref to an field type
                continue;
            }
            return true;
        }
        return false;
    }

    /**
     * Detects
     */
    private class SerialVersionUidMatcher {

        private Kind privateStaticVariable;

        boolean matches(String name, Entity ent) {
            if (endsWith(name, ".serialVersionUID")) {
                if ("long".equals(ent.type())) {
                    Kind kind = ent.kind();
                    if (privateStaticVariable == null) {
                        if ("Private Static Variable".equals(kind.name())) {
                            privateStaticVariable = kind;
                            return true;
                        }
                    } else {
                        if (privateStaticVariable.equals(kind)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }
}