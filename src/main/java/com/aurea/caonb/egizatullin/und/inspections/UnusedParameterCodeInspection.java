package com.aurea.caonb.egizatullin.und.inspections;

import static com.aurea.caonb.egizatullin.und.commons.CodeInspectionType.UNUSED_PARAMETER;

import com.aurea.caonb.egizatullin.processing.CodeInspectionItem;
import com.aurea.caonb.egizatullin.und.commons.ICodeInspectionCallback;
import com.scitools.understand.Database;
import com.scitools.understand.Entity;
import com.scitools.understand.Reference;
import java.util.HashSet;
import java.util.Set;

public class UnusedParameterCodeInspection implements ICodeInspection {

    @Override
    public void inspect(Database udb, ICodeInspectionCallback callback) {
        Set<Integer> implementedMethods = new HashSet<>();
        {
            Set<Integer> interfaceIds = new HashSet<>();
            {
                Entity[] ents = udb.ents("Interface");
                for (Entity ent : ents) {
                    interfaceIds.add(ent.id());
                }
            }
            // 'Unknown' belongs to out-of-project code
            // 'Unresolved' belongs to 'native' methods
            Entity[] ents = udb.ents("~Abstract ~Unknown ~Unresolved Method");
            for (Entity ent : ents) {
                Reference[] refs = ent.refs(null, "Class", true);
                if (refs.length > 0) {
                    Reference declarationRef = refs[0];
                    Entity parent = declarationRef.ent();
                    if (!interfaceIds.contains(parent.id())) {
                        implementedMethods.add(ent.id());
                    }
                }
            }
        }

        Entity[] ents = udb.ents("~Catch Parameter");
        for (Entity ent : ents) {
            Reference[] refs = ent.refs(null, null, false);
            if (refs.length > 0) {
                Reference declarationRef = refs[0];
                Entity parent = declarationRef.ent();
                if (implementedMethods.contains(parent.id())) {
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
}