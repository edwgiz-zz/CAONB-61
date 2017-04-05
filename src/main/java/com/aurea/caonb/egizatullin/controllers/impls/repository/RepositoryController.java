package com.aurea.caonb.egizatullin.controllers.impls.repository;

import static com.aurea.caonb.egizatullin.controllers.commons.pagination.PaginationUtils.PAGE_PARAM_NAME;
import static com.aurea.caonb.egizatullin.controllers.commons.pagination.PaginationUtils.PER_PAGE_PARAM_NAME;
import static com.aurea.caonb.egizatullin.controllers.commons.pagination.PaginationUtils.getOffset;
import static com.aurea.caonb.egizatullin.controllers.commons.pagination.PaginationUtils.paginationHeader;
import static com.aurea.caonb.egizatullin.controllers.commons.pagination.PaginationUtils.parse;
import static com.aurea.caonb.egizatullin.utils.http.HttpUtils.addParam;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.springframework.http.ResponseEntity.status;

import com.aurea.caonb.egizatullin.controllers.commons.AbstractController;
import com.aurea.caonb.egizatullin.controllers.commons.AbstractResponse;
import com.aurea.caonb.egizatullin.controllers.commons.pagination.Pagination;
import com.aurea.caonb.egizatullin.data.AddRepositoryResult;
import com.aurea.caonb.egizatullin.data.Repository;
import com.aurea.caonb.egizatullin.data.RepositoryDao;
import com.aurea.caonb.egizatullin.processing.ProcessingService;
import com.aurea.caonb.egizatullin.utils.collection.SubListResult;
import com.aurea.caonb.egizatullin.utils.github.GithubService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/repositories", produces = "application/json")
public class RepositoryController extends AbstractController {

    private static final Pattern BRANCH_ANTI_PATTERN = Pattern.compile("\\s");
    private static final Pattern COMMIT_HASH_PATTERN = Pattern.compile("[0-9A-Fa-f]{7,40}");
    public static final String OWNER_PARAM_NAME = "owner";
    public static final String REPO_PARAM_NAME = "repo";
    public static final String BRANCH_PARAM_NAME = "branch";

    private final GithubService githubService;
    private final RepositoryDao repositoryDao;
    private final ProcessingService processingService;

    @Autowired
    public RepositoryController(
        GithubService githubService,
        RepositoryDao repositoryDao,
        ProcessingService processingService) {
        this.githubService = githubService;
        this.repositoryDao = repositoryDao;
        this.processingService = processingService;
    }

    @ApiOperation(
        value = "Add a Github repository",
        notes = "Adding a repository will automatically trigger an algorithm to detect dead code in"
            + " that repository.",
        tags = {"github"})
    @ApiResponses({
        @ApiResponse(code = HTTP_OK, message = "Nice!", response = AddRepositoryResponse.class),
        @ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = AddRepositoryResponse.class),
        @ApiResponse(code = HTTP_INTERNAL_ERROR, message = HTTP_INTERNAL_ERROR_MESSAGE, response = AbstractResponse.class)})
    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<AddRepositoryResponse> addRepository(
        @ApiParam(value = "Reference to Github", required = true)
        @RequestBody AddRepositoryRequest req) {

        try {
            String owner = trimToNull(req.getOwner());
            if (owner == null) {
                return badAddRequest("Undefined github owner");
            }
            String repo = trimToNull(req.getRepo());
            if (repo == null) {
                return badAddRequest("Undefined github repository name");
            }
            String branch = trimToNull(req.getBranch());
            if (branch != null) {
                if (BRANCH_ANTI_PATTERN.matcher(branch).matches()) {
                    return badAddRequest("Incorrect branch");
                }
            } else {
                branch = githubService.getDefaultBranch(owner, repo); // validate git repository
            }

            String commitHash = trimToNull(req.getCommitHash());
            if (commitHash != null && !COMMIT_HASH_PATTERN.matcher(commitHash).matches()) {
                return badAddRequest("Incorrect commitHash");
            }
            if (commitHash == null) {
                commitHash = githubService.getLastCommit(owner, repo, branch);
            } else {
                if (isBlank(req.getBranch())) {
                    return badAddRequest("commitHash is not allowed without branch");
                }
            }

            AddRepositoryResult arr = repositoryDao.addRepository(owner, repo, branch, commitHash);
            Repository r = arr.repository;

            long lastModified = r.history.get(r.history.size() - 1).date.getTime();
            if (!arr.exists) {
                processingService.process(arr.repository);
                return status(HTTP_OK).lastModified(lastModified).body(
                    new AddRepositoryResponse(null, r));
            } else {
                return status(HTTP_BAD_REQUEST).lastModified(lastModified).body(
                    new AddRepositoryResponse("Github reference was already added", r));
            }
        } catch (Exception e) {
            return badAddRequest("Incorrect url: " + e.getMessage());
        }
    }

    private ResponseEntity<AddRepositoryResponse> badAddRequest(String msg) {
        return status(HTTP_BAD_REQUEST).body(new AddRepositoryResponse(msg, null));
    }


    @ApiOperation(
        value = "Deletes repository in the system",
        tags = {"github"})
    @ApiResponses({
        @ApiResponse(code = HTTP_OK, message = "Nice!", response = DeleteRepositoryResponse.class),
        @ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = AbstractResponse.class),
        @ApiResponse(code = HTTP_INTERNAL_ERROR, message = HTTP_INTERNAL_ERROR_MESSAGE, response = AbstractResponse.class)
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<DeleteRepositoryResponse> remove(
        @ApiParam(value = "Identifier of repository ", required = true)
        @PathVariable("id") String repositoryId) {
        int id;
        try {
            id = Integer.parseInt(repositoryId);
        } catch (NumberFormatException ex) {
            return status(HTTP_BAD_REQUEST).body(new DeleteRepositoryResponse(
                "Incorrect repository id", null));
        }

        Repository deletedRepo = repositoryDao.remove(id);
        if (deletedRepo == null) {
            return status(HTTP_BAD_REQUEST).body(new DeleteRepositoryResponse(
                "No such repository id", null));
        } else {
            return status(HTTP_OK).body(new DeleteRepositoryResponse(
                null, deletedRepo));
        }
    }


    @ApiOperation(
        value = "Lists added repositories or an error message",
        notes = "Lists all repositories that have already been added along with their history."
            + "Can filters repos by the given substrings of an owner, a repo or a branch."
            +  "Supports pagination in according to rfc5988",
        tags = {"github"})
    @ApiResponses({
        @ApiResponse(code = HTTP_OK, message = "Nice!", response = ListRepositoryResponse.class),
        @ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = AbstractResponse.class),
        @ApiResponse(code = HTTP_INTERNAL_ERROR, message = HTTP_INTERNAL_ERROR_MESSAGE, response = AbstractResponse.class)
    })
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<ListRepositoryResponse> list(
        @ApiParam(value = "Owner of repository")
        @RequestParam(name = OWNER_PARAM_NAME, required = false) String owner,
        @ApiParam(value = "Repository name")
        @RequestParam(name = REPO_PARAM_NAME, required = false) String repo,
        @ApiParam(value = "Branch of repository")
        @RequestParam(name = BRANCH_PARAM_NAME, required = false) String branch,
        @ApiParam(value = "Page number", defaultValue = "1")
        @RequestParam(name = PAGE_PARAM_NAME, required = false) String page,
        @ApiParam(value = "How many items you want each page to return")
        @RequestParam(name = PER_PAGE_PARAM_NAME, required = false) String perPage
    ){

        Pagination p;
        try {
            p = parse(page, perPage);
        } catch (IllegalArgumentException e) {
            return status(HTTP_BAD_REQUEST).body(new ListRepositoryResponse(e.getMessage(), null));
        }

        SubListResult<Repository> slr = repositoryDao.find(
            trimToNull(owner),
            trimToNull(repo),
            trimToNull(branch),
            getOffset(p),
            p.perPage);

        p.count = slr.totalSize;

        addParam(p.queryBase, OWNER_PARAM_NAME, owner);
        addParam(p.queryBase, REPO_PARAM_NAME, repo);
        addParam(p.queryBase, BRANCH_PARAM_NAME, branch);

        return paginationHeader(status(HTTP_OK), p)
            .body(new ListRepositoryResponse(null, slr.items));
    }
}