package com.aurea.caonb.egizatullin.und.inspections;

import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;

import com.aurea.caonb.egizatullin.und.commons.ICodeInspectionCallback;
import com.scitools.understand.Database;
import com.scitools.understand.Entity;
import com.scitools.understand.Kind;
import com.scitools.understand.Reference;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;


public class UnusedMethodCodeInspection implements ICodeInspection {

    private final Set<Integer> javaEnumIds;
    private final Map<Kind, String> constructors;

    public UnusedMethodCodeInspection(Set<Integer> javaEnumIds) {
        this.javaEnumIds = javaEnumIds;
        constructors = new HashMap<>();
    }

    @Override
    public void inspect(Database udb, ICodeInspectionCallback callback) {
        Entity[] ents = udb.ents("Private Method");

        for (Entity ent : ents) {
            boolean hasRefs = isNotEmpty(ent.refs("Callby", null, true));
            if (!hasRefs) {
                Reference refToParent = ent.refs("Definein", null, true)[0];
                Entity parent = refToParent.ent();
                if (isIgnored(ent, parent)) {
                    continue;
                }

                Entity file = refToParent.file();
                callback.accept(file.longname(false), refToParent.line(), refToParent.column(),
                    ent.name());
            }
        }
    }

    private boolean isIgnored(Entity ent, Entity parent) {
        if (javaEnumIds.contains(parent.id())) {
            // is enum constructor
            if (constructorKind(ent) != null) {
                return true;
            }
        } else {
            // is 'Object readResolve()' in non-enum
            if (ent.simplename().equals("readResolve")) {
                Reference[] returns = ent.refs("Java Typed", null, false);
                if (returns.length > 0) {
                    String methodReturnType = returns[0].ent().longname(false);
                    if ("java.lang.Object".equals(methodReturnType)) {
                        Reference[] params = ent.refs("Java Set", null, false);
                        if (params.length == 0) {
                            return true;
                        }
                    }
                }
            }
        }

        if (StringUtils.contains(constructorKind(ent), "Private")) {
            // is Utils constructor
            String parentName = parent.simplename();
            if (parentName.endsWith("Util") || parentName.endsWith("Utils")) {
                return true;
            }
        }

        return false;
    }

    private String constructorKind(Entity ent) {
        Kind kind = ent.kind();
        return constructors.computeIfAbsent(kind,
            k -> k.name().contains("Constructor") ? k.name() : null);
    }
}