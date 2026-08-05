package com.eneik.generated.dtos;

import java.util.List;

public class SearchIndexResponseDto {
    private String status;
    private Integer indexedCount;
    private List<IndexingFailureDto> failures;

    public SearchIndexResponseDto() {}

    public SearchIndexResponseDto(String status, Integer indexedCount, List<IndexingFailureDto> failures) {
        this.status = status;
        this.indexedCount = indexedCount;
        this.failures = failures;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getIndexedCount() { return indexedCount; }
    public void setIndexedCount(Integer indexedCount) { this.indexedCount = indexedCount; }

    public List<IndexingFailureDto> getFailures() { return failures; }
    public void setFailures(List<IndexingFailureDto> failures) { this.failures = failures; }
}
