package com.aurea.caonb.egizatullin.controllers.impls.inspection;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.springframework.http.ResponseEntity.status;

import com.aurea.caonb.egizatullin.controllers.commons.AbstractController;
import com.aurea.caonb.egizatullin.controllers.commons.AbstractResponse;
import com.aurea.caonb.egizatullin.data.InspectionDao;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/inspections", produces = "application/json")
public class InspectionController extends AbstractController {

    private final InspectionDao inspectionDao;

    @Autowired
    public InspectionController(InspectionDao inspectionDao) {
        this.inspectionDao = inspectionDao;
    }

    @ApiOperation(
        value = "List dead code occurrences by a given github repository id",
        tags = {"github"})
    @ApiResponses({
        @ApiResponse(code = HTTP_OK, message = "Nice!", response = SuccessfulListInspectionResponse.class),
        @ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = AbstractResponse.class),
        @ApiResponse(code = HTTP_INTERNAL_ERROR, message = HTTP_INTERNAL_ERROR_MESSAGE, response = AbstractResponse.class)})
    @RequestMapping(value = "/repository/{id}", method = RequestMethod.GET)
    public ResponseEntity<ListInspectionResponse> list(
        @ApiParam(value = "Identifier of processed repository ", required = true)
        @PathVariable("id") String repositoryId) {

        int id;
        try {
            id = Integer.parseInt(repositoryId);
        } catch (NumberFormatException ex) {
            return badAddRequest("Incorrect repository id");
        }

        byte[] inspectionsArchive = inspectionDao.getInspections(id);
        if(inspectionsArchive == null) {
            return badAddRequest("No such repository id");
        }

        return status(HTTP_OK)
            .body(new ListInspectionResponse(null, inspectionsArchive));
    }

    private ResponseEntity<ListInspectionResponse> badAddRequest(String msg) {
        return status(HTTP_BAD_REQUEST).body(new ListInspectionResponse(msg, null));
    }
}