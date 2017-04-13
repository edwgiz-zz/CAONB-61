package com.aurea.caonb.egizatullin.und;

import static com.aurea.caonb.egizatullin.und.commons.CodeInspectionType.UNUSED_VARIABLE;

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
        Path p = Paths.get("C:/Projects/commons-io");
        UndService s = new UndService("c:/Program Files/SciTools/bin/pc-win64/und.exe");
        s.buildDatabase(p);
    }

    @Test
    @Ignore
    public void testCodeChesk() throws Exception {
        UndService s = new UndService("c:/Program Files/SciTools/bin/pc-win64/und.exe");
        Path p = Paths.get("C:/Projects/commons-io");

        ArrayList<CodeInspectionItem> inspectionItems = new ArrayList<>();
        {
            List<ICodeInspectionCallback> cics = new ArrayList<>();
            cics.add(new CodeInspectionCollector(p.getNameCount(), UNUSED_VARIABLE, inspectionItems));
            s.inspectCode(p, cics);
        }
    }
}