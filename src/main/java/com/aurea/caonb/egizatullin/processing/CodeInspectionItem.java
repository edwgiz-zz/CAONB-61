package com.aurea.caonb.egizatullin.processing;


import com.aurea.caonb.egizatullin.und.commons.CodeInspectionType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(

)
public class CodeInspectionItem {

    @ApiModelProperty(
        required = true,
        example = "UNUSED_FIELD",
        notes = "Relative path to source file in a project",
        position = 0)
    public final CodeInspectionType type;
    @ApiModelProperty(
        required = true,
        example = "src/main/java/com/aurea/caonb/egizatullin/utils/github/GithubService.java",
        notes = "Relative path to source file in a project",
        position = 1)
    public final String file;
    @ApiModelProperty(
        required = true,
        example = "43",
        notes = "Entity position line number in the file",
        position = 2)
    public final int line;
    @ApiModelProperty(
        required = true,
        example = "44",
        notes = "Entity position column number in the file",
        position = 3)
    public final int column;
    @ApiModelProperty(
        required = true,
        example = "b",
        position = 4)
    public final String entityName;

    public CodeInspectionItem(CodeInspectionType type, String file, String entityName, int line,
        int column) {
        this.type = type;
        this.file = file;
        this.entityName = entityName;
        this.line = line;
        this.column = column;
    }
}
