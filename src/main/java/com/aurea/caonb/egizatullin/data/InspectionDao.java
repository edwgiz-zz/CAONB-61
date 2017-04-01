package com.aurea.caonb.egizatullin.data;

import com.aurea.caonb.egizatullin.processing.CodeInspectionItem;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@org.springframework.stereotype.Repository
public class InspectionDao {

    private final ConcurrentHashMap<Integer, byte[]> inspectionsByRepository;


    public InspectionDao() {
        this.inspectionsByRepository = new ConcurrentHashMap<>();
    }

    public void addInspections(int repositoryId, List<CodeInspectionItem> inspectionItems) {
        inspectionItems.sort(Comparator.comparingInt(o -> o.line));
        byte[] content = serialize(inspectionItems);
        inspectionsByRepository.put(repositoryId, content);
    }

    byte[] serialize(Collection<CodeInspectionItem> inspectionItems) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(8096);
        try {
            GZIPOutputStream gzip = new GZIPOutputStream(baos, 8096, false);
            ObjectOutputStream oos = new ObjectOutputStream(gzip);
            oos.writeObject(inspectionItems);
            oos.flush();
            gzip.finish();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return baos.toByteArray();
    }

    /**
     * @param repositoryId repo id
     * @return inspections in gzip-json format or {@code null}
     */
    public List<CodeInspectionItem> getInspections(int repositoryId, String file) {
        byte[] content = inspectionsByRepository.get(repositoryId);
        if(content == null) {
            return null;
        }

        List<CodeInspectionItem> codeInspectionItems;
        try {
            ObjectInputStream ois = new ObjectInputStream(
                new GZIPInputStream(new ByteArrayInputStream(content)));
            //noinspection unchecked
            codeInspectionItems = (List<CodeInspectionItem>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(
                "Can't deserialize inspection by repository id: " + repositoryId);
        }

        if(file != null) {
            codeInspectionItems = codeInspectionItems.stream()
                .filter(e -> e.file.contains(file))
                .collect(Collectors.toList());
        }
        return codeInspectionItems;
    }

    void removeInspections(int repositoryId) {
        inspectionsByRepository.remove(repositoryId);
    }
}