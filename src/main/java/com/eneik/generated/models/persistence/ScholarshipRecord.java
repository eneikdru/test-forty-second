package com.eneik.generated.models.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "scholarship_record")
public class ScholarshipRecord {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @Column(name = "student_name", nullable = false)
    private String studentName;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id", nullable = false)
    private AcademicTerm term;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "calculated_at", nullable = false)
    private LocalDateTime calculatedAt;

    @Column(name = "hash_checksum", nullable = false, length = 64)
    private String hashChecksum;

    @Transient
    private boolean loaded = false;

    public ScholarshipRecord() {}

    public ScholarshipRecord(String id, String studentId, String studentName, AcademicTerm term, BigDecimal amount, LocalDateTime calculatedAt, String hashChecksum) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.term = term;
        this.amount = amount;
        this.calculatedAt = calculatedAt;
        this.hashChecksum = hashChecksum;
    }

    @PostLoad
    public void postLoad() {
        this.loaded = true;
    }

    @PreUpdate
    @PrePersist
    public void preWrite() {
        if (loaded) {
            throw new IllegalStateException("Scholarship records are immutable and cannot be updated.");
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public AcademicTerm getTerm() {
        return term;
    }

    public void setTerm(AcademicTerm term) {
        this.term = term;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }

    public String getHashChecksum() {
        return hashChecksum;
    }

    public void setHashChecksum(String hashChecksum) {
        this.hashChecksum = hashChecksum;
    }
}
