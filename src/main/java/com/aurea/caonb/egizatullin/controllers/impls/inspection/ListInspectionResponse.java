package com.aurea.caonb.egizatullin.controllers.impls.inspection;

import com.aurea.caonb.egizatullin.controllers.commons.AbstractResponse;
import com.aurea.caonb.egizatullin.processing.CodeInspectionItem;
import java.util.List;

public class ListInspectionResponse extends AbstractResponse {

    public final List<CodeInspectionItem> inspectionList;

    ListInspectionResponse(String errorMessage, List<CodeInspectionItem> inspectionList) {
        super(errorMessage);
        this.inspectionList = inspectionList;
    }
}
