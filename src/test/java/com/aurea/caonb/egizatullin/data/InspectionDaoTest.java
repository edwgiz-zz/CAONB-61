package com.aurea.caonb.egizatullin.data;

import static com.aurea.caonb.egizatullin.und.commons.CodeInspectionType.UNUSED_PARAMETER;
import static org.junit.Assert.assertEquals;

import com.aurea.caonb.egizatullin.processing.CodeInspectionItem;
import java.util.ArrayList;
import org.junit.Test;


public class InspectionDaoTest {

    @Test
    public void toGzJson() throws Exception {
        InspectionDao d = new InspectionDao();
        ArrayList<CodeInspectionItem> ii = new ArrayList<>();
        ii.add(new CodeInspectionItem(UNUSED_PARAMETER,
            "src/main/java/com/aurea/caonb/egizatullin/utils/file/RecursiveDeleteFileVisitor.java",
            "attrs",
            17, 75));
        ii.add(new CodeInspectionItem(UNUSED_PARAMETER,
            "src/main/java/com/aurea/caonb/egizatullin/utils/file/RecursiveDeleteFileVisitor.java",
            "attrs",
            23, 68));
        ii.add(new CodeInspectionItem(UNUSED_PARAMETER,
            "src\\main\\java\\com\\aurea\\caonb\\egizatullin\\und\\SourceRootDetectionFileVisitor.java",
            "attrs",
            25, 68));
        byte[] content = d.toGzJson(ii);
        assertEquals(222, content.length);
    }

}