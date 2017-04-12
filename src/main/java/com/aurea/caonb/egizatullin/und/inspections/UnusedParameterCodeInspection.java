package com.aurea.caonb.egizatullin.und.inspections;

import com.aurea.caonb.egizatullin.und.UndUtils;
import com.aurea.caonb.egizatullin.und.commons.ICodeInspectionCallback;
import com.scitools.understand.Database;
import com.scitools.understand.Entity;
import com.scitools.understand.Reference;
import java.util.HashSet;
import java.util.Set;

public class UnusedParameterCodeInspection implements ICodeInspection {

    private final Set<Integer> javaEnumIds;

    public UnusedParameterCodeInspection(Set<Integer> javaEnumIds) {
        this.javaEnumIds = javaEnumIds;
    }



    @Override
    public void inspect(Database udb, ICodeInspectionCallback callback) {
        Set<Integer> checkingMethods = getMethodsToCheck(udb);

        Entity[] ents = udb.ents("~Catch Parameter");
        for (Entity ent : ents) {
            Reference[] refs = ent.refs(null, null, false);
            if (refs.length > 0) {
                Reference declarationRef = refs[0];
                Entity parent = declarationRef.ent();
                if (checkingMethods.contains(parent.id())) {
                    boolean isUsed = false;
                    for (int i = 1; i < refs.length; i++) {
                        Reference ref = refs[i];
                        if (ref.ent().id() == parent.id()) {
                            // reference to parameter is
                            // into same method/constructor as parameter declaration
                            isUsed = true;
                            break;
                        }
                    }
                    if (!isUsed) {
                        callback.accept(
                            declarationRef.file().longname(false),
                            declarationRef.line(),
                            declarationRef.column(),
                            ent.name()
                        );
                    }
                }
            }
        }
    }

    /**
     * @param udb 'Understand' database
     * @return all existing methods except of <ul>
     *     <li>methods from interfaces</li>
     *     <li>{@link java.lang.Enum#valueOf(Class, String)}</li>
     *     <li>{@code T[] java.lang.Enum#values()}</li>
     * </ul>
     */
    private Set<Integer> getMethodsToCheck(Database udb) {
        Set<Integer> javaInterfaceIds = UndUtils.getEntityIds(udb, "Interface");
        Set<Integer> result = new HashSet<>();
        // 'Unknown' belongs to out-of-project code
        // 'Unresolved' belongs to 'native' methods
        Entity[] ents = udb.ents("~Abstract ~Unknown ~Unresolved Method");
        for (Entity ent : ents) {
            Reference[] refs = ent.refs(null, "Class", true);
            if (refs.length > 0) {
                Reference declarationRef = refs[0];
                Entity parent = declarationRef.ent();
                if (javaInterfaceIds.contains(parent.id())) {
                    continue;
                }
                if(javaEnumIds.contains(parent.id())) {
                    if(("valueOf".equals(ent.simplename()))) {
                        continue;
                    }
                    if(("values".equals(ent.simplename()))) {
                        continue;
                    }
                }
                result.add(ent.id());
            }
        }
        return result;
    }

}