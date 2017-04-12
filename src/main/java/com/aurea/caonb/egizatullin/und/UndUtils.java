package com.aurea.caonb.egizatullin.und;


import com.scitools.understand.Database;
import com.scitools.understand.Entity;
import java.util.HashSet;
import java.util.Set;

public final class UndUtils {

    public static Set<Integer> getEntityIds(Database udb, String kindName) {
        Set<Integer> ids = new HashSet<>();
        Entity[] ents = udb.ents(kindName);
        for (Entity ent : ents) {
            ids.add(ent.id());
        }
        return ids;
    }


    private UndUtils() {
    }
}
