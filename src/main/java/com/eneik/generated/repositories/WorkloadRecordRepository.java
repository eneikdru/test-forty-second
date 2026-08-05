package com.eneik.generated.repositories;

import com.eneik.generated.models.persistence.WorkloadRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface WorkloadRecordRepository extends JpaRepository<WorkloadRecord, String> {

    @Query("SELECT w FROM WorkloadRecord w WHERE w.term.id = :termId")
    List<WorkloadRecord> findByTermId(@Param("termId") String termId);
}
