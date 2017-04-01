package com.aurea.caonb.egizatullin.data;

import com.aurea.caonb.egizatullin.processing.CodeInspectionItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPOutputStream;

@org.springframework.stereotype.Repository
public class InspectionDao {

    private static final ObjectMapper JSON = new ObjectMapper();

    private final ConcurrentHashMap<Integer, byte[]> inspectionsByRepository;


    public InspectionDao() {
        this.inspectionsByRepository = new ConcurrentHashMap<>();
    }

    public void addInspections(int repositoryId, Collection<CodeInspectionItem> inspectionItems) {
        byte[] content = toGzJson(inspectionItems);
        inspectionsByRepository.put(repositoryId, content);
    }

    byte[] toGzJson(Collection<CodeInspectionItem> inspectionItems) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(8096);
        try {
            GZIPOutputStream out = new GZIPOutputStream(baos, 8096, false);
            JSON.writeValue(out, inspectionItems);
            out.finish();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return baos.toByteArray();
    }

    /**
     * @param repositoryId repo id
     * @return inspections in gzip-json format or {@code null}
     */
    public byte[] getInspections(int repositoryId) {
        return inspectionsByRepository.get(repositoryId);
    }

    void removeInspections(int repositoryId) {
        inspectionsByRepository.remove(repositoryId);
    }
}