package com.eneik.generated.dtos;

import java.time.LocalDateTime;
import java.util.List;

public class IndexableDocumentDto {
    private String id;
    private String title;
    private String content;
    private String category;
    private List<String> tags;
    private List<String> synonyms;
    private String documentType;
    private LocalDateTime lastModified;

    public IndexableDocumentDto() {}

    public IndexableDocumentDto(String id, String title, String content, String category, List<String> tags, List<String> synonyms, String documentType, LocalDateTime lastModified) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.category = category;
        this.tags = tags;
        this.synonyms = synonyms;
        this.documentType = documentType;
        this.lastModified = lastModified;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public List<String> getSynonyms() { return synonyms; }
    public void setSynonyms(List<String> synonyms) { this.synonyms = synonyms; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public LocalDateTime getLastModified() { return lastModified; }
    public void setLastModified(LocalDateTime lastModified) { this.lastModified = lastModified; }
}
