package com.aurea.caonb.egizatullin.und;

import static com.aurea.caonb.egizatullin.und.commons.CodeInspectionType.UNUSED_PARAMETER;

import com.aurea.caonb.egizatullin.processing.CodeInspectionCollector;
import com.aurea.caonb.egizatullin.processing.CodeInspectionItem;
import com.aurea.caonb.egizatullin.und.commons.ICodeInspectionCallback;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;

public class UndServiceTest {

    @Test
    @Ignore
    public void testCreate() throws Exception {
        Path p = Paths.get("e:/workdir/github/RxJava-2.x");
        UndService s = new UndService("c:/Program Files/SciTools/bin/pc-win64/und.exe");
        s.buildDatabase(p);
    }

    @Test
    @Ignore
    public void testCodeChesk() throws Exception {
        UndService s = new UndService("c:/Program Files/SciTools/bin/pc-win64/und.exe");
        Path p = Paths.get("e:/workdir/github/RxJava-2.x");

        ArrayList<CodeInspectionItem> inspectionItems = new ArrayList<>();
        {
            List<ICodeInspectionCallback> cics = new ArrayList<>();
//            cics.add(new CodeInspectionCollector(p.getNameCount(), UNUSED_METHOD, inspectionItems));
//            cics.add(new CodeInspectionCollector(p.getNameCount(), UNUSED_FIELD, inspectionItems));
            cics.add(
                new CodeInspectionCollector(p.getNameCount(), UNUSED_PARAMETER, inspectionItems));
            s.inspectCode(p, cics);
        }
    }
}