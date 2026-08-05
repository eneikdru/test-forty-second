package com.eneik.generated.dtos;

import java.math.BigDecimal;

public class BudgetRecordDto {
    private String id;
    private String termId;
    private String category;
    private BigDecimal amount;
    private BigDecimal allocatedAmount;

    public BudgetRecordDto() {}

    public BudgetRecordDto(String id, String termId, String category, BigDecimal amount, BigDecimal allocatedAmount) {
        this.id = id;
        this.termId = termId;
        this.category = category;
        this.amount = amount;
        this.allocatedAmount = allocatedAmount;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTermId() { return termId; }
    public void setTermId(String termId) { this.termId = termId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getAllocatedAmount() { return allocatedAmount; }
    public void setAllocatedAmount(BigDecimal allocatedAmount) { this.allocatedAmount = allocatedAmount; }
}
