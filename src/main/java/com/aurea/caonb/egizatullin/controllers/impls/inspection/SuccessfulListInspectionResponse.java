package com.aurea.caonb.egizatullin.controllers.impls.inspection;

import com.aurea.caonb.egizatullin.processing.CodeInspectionItem;
import java.util.List;

public class SuccessfulListInspectionResponse extends ListInspectionResponse {

    SuccessfulListInspectionResponse(byte[] inspectionList) {
        super(null, inspectionList);
    }

    public List<CodeInspectionItem> getInspectionList() {
        throw new UnsupportedOperationException();
    }
}
