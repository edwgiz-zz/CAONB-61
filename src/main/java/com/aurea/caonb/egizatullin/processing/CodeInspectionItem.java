package com.aurea.caonb.egizatullin.processing;


import com.aurea.caonb.egizatullin.und.commons.CodeInspectionType;
import io.swagger.annotations.ApiModelProperty;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;


public class CodeInspectionItem implements Externalizable {

    @ApiModelProperty(
        required = true,
        example = "UNUSED_FIELD",
        notes = "Relative path to source file in a project",
        position = 0)
    public CodeInspectionType type;
    @ApiModelProperty(
        required = true,
        example = "src/main/java/com/aurea/caonb/egizatullin/utils/github/GithubService.java",
        notes = "Relative path to source file in a project",
        position = 1)
    public String file;
    @ApiModelProperty(
        required = true,
        example = "43",
        notes = "Entity position line number in the file",
        position = 2)
    public int line;
    @ApiModelProperty(
        required = true,
        example = "44",
        notes = "Entity position column number in the file",
        position = 3)
    public int column;
    @ApiModelProperty(
        required = true,
        example = "b",
        position = 4)
    public String entityName;


    public CodeInspectionItem() {
    }

    public CodeInspectionItem(CodeInspectionType type, String file, String entityName, int line,
        int column) {
        this.type = type;
        this.file = file;
        this.entityName = entityName;
        this.line = line;
        this.column = column;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(type);
        out.writeUTF(file);
        out.writeInt(line);
        out.writeInt(column);
        out.writeUTF(entityName);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        type = (CodeInspectionType) in.readObject();
        file = in.readUTF();
        line = in.readInt();
        column = in.readInt();
        entityName = in.readUTF();
    }
}
