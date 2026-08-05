package com.eneik.generated.repositories;

import com.eneik.generated.models.persistence.ScholarshipRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ScholarshipRecordRepository extends JpaRepository<ScholarshipRecord, String> {

    @Query("SELECT s FROM ScholarshipRecord s WHERE s.term.id = :termId")
    List<ScholarshipRecord> findByTermId(@Param("termId") String termId);
}
