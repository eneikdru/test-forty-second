package com.eneik.generated.dtos;

import java.util.List;

public class SearchIndexRequestDto {
    private String batchId;
    private List<IndexableDocumentDto> documents;

    public SearchIndexRequestDto() {}

    public SearchIndexRequestDto(String batchId, List<IndexableDocumentDto> documents) {
        this.batchId = batchId;
        this.documents = documents;
    }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public List<IndexableDocumentDto> getDocuments() { return documents; }
    public void setDocuments(List<IndexableDocumentDto> documents) { this.documents = documents; }
}
