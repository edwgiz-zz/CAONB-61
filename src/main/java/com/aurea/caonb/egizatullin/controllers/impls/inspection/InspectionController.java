package com.aurea.caonb.egizatullin.controllers.impls.inspection;

import static com.aurea.caonb.egizatullin.controllers.commons.pagination.PaginationUtils.PAGE_PARAM_NAME;
import static com.aurea.caonb.egizatullin.controllers.commons.pagination.PaginationUtils.PER_PAGE_PARAM_NAME;
import static com.aurea.caonb.egizatullin.controllers.commons.pagination.PaginationUtils.getOffset;
import static com.aurea.caonb.egizatullin.controllers.commons.pagination.PaginationUtils.paginationHeader;
import static com.aurea.caonb.egizatullin.controllers.commons.pagination.PaginationUtils.parse;
import static com.aurea.caonb.egizatullin.utils.http.HttpUtils.addParam;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.springframework.http.ResponseEntity.status;

import com.aurea.caonb.egizatullin.controllers.commons.AbstractController;
import com.aurea.caonb.egizatullin.controllers.commons.AbstractResponse;
import com.aurea.caonb.egizatullin.controllers.commons.pagination.Pagination;
import com.aurea.caonb.egizatullin.data.InspectionDao;
import com.aurea.caonb.egizatullin.processing.CodeInspectionItem;
import com.aurea.caonb.egizatullin.utils.collection.SubListResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/inspections", produces = "application/json")
public class InspectionController extends AbstractController {

    private static final String ID_PARAM_NAME = "id";
    private static final String FILE_PARAM_NAME = "file";

    private final InspectionDao inspectionDao;

    @Autowired
    public InspectionController(InspectionDao inspectionDao) {
        this.inspectionDao = inspectionDao;
    }

    @ApiOperation(
        value = "List dead code occurrences by a given github repository id. ",
        notes = "Supports pagination in according to rfc5988",
        tags = {"github"})
    @ApiResponses({
        @ApiResponse(code = HTTP_OK, message = "Nice!", response = ListInspectionResponse.class),
        @ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = AbstractResponse.class),
        @ApiResponse(code = HTTP_INTERNAL_ERROR, message = HTTP_INTERNAL_ERROR_MESSAGE, response = AbstractResponse.class)})
    @RequestMapping(value = "/repository/{id}", method = RequestMethod.GET)
    public ResponseEntity<ListInspectionResponse> list(
        @ApiParam(value = "Identifier of processed repository ", required = true)
        @PathVariable(ID_PARAM_NAME) String repositoryId,
        @ApiParam(value = "Relative path filtering substring",
            example = "src/main/java/com/aurea/caonb/egizatullin/utils/file/RecursiveDeleteFileVisitor.java")
        @RequestParam(name = FILE_PARAM_NAME, required = false) String file,
        @ApiParam(value = "Page number", defaultValue = "1")
        @RequestParam(name = PAGE_PARAM_NAME, required = false) String page,
        @ApiParam(value = "How many items you want each page to return")
        @RequestParam(name = PER_PAGE_PARAM_NAME, required = false) String perPage
    ) {

        int id;
        try {
            id = Integer.parseInt(repositoryId);
        } catch (NumberFormatException ex) {
            return badAddRequest("Incorrect repository id");
        }

        Pagination p;
        try {
            p = parse(page, perPage);
        } catch (IllegalArgumentException e) {
            return status(HTTP_BAD_REQUEST).body(new ListInspectionResponse(e.getMessage(), null));
        }

        SubListResult<CodeInspectionItem> slr = inspectionDao.getInspections(
            id,
            trimToNull(file),
            getOffset(p),
            p.perPage);
        if (slr == null) {
            return badAddRequest("No such repository id");
        }

        p.count = slr.totalSize;

        p.queryBase = new StringBuilder();
        addParam(p.queryBase, ID_PARAM_NAME, id);
        addParam(p.queryBase, FILE_PARAM_NAME, file);

        return
            paginationHeader(status(HTTP_OK), p)
            .body(new ListInspectionResponse(null, slr.items));
    }

    private ResponseEntity<ListInspectionResponse> badAddRequest(String msg) {
        return status(HTTP_BAD_REQUEST).body(new ListInspectionResponse(msg, null));
    }
}