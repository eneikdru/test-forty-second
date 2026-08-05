package com.eneik.generated.repositories;

import com.eneik.generated.models.persistence.BudgetRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface BudgetRecordRepository extends JpaRepository<BudgetRecord, String> {

    @Query("SELECT b FROM BudgetRecord b WHERE b.term.id = :termId")
    List<BudgetRecord> findByTermId(@Param("termId") String termId);
}
