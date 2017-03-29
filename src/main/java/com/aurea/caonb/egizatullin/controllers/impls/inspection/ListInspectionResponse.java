package com.aurea.caonb.egizatullin.controllers.impls.inspection;

import com.aurea.caonb.egizatullin.controllers.commons.AbstractResponse;
import com.aurea.caonb.egizatullin.utils.jackson.UngzipRawSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ListInspectionResponse extends AbstractResponse {

    @JsonSerialize(using = UngzipRawSerializer.class)
    private final byte[] inspectionList;

    public ListInspectionResponse(String errorMessage, byte[] inspectionList) {
        super(errorMessage);
        this.inspectionList = inspectionList;
    }

}
