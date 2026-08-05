package com.eneik.generated.dtos;

import java.math.BigDecimal;

public class BudgetSubmissionRequest {
    private String termId;
    private String category;
    private BigDecimal amount;
    private BigDecimal allocatedAmount;

    public BudgetSubmissionRequest() {}

    public BudgetSubmissionRequest(String termId, String category, BigDecimal amount, BigDecimal allocatedAmount) {
        this.termId = termId;
        this.category = category;
        this.amount = amount;
        this.allocatedAmount = allocatedAmount;
    }

    public String getTermId() { return termId; }
    public void setTermId(String termId) { this.termId = termId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getAllocatedAmount() { return allocatedAmount; }
    public void setAllocatedAmount(BigDecimal allocatedAmount) { this.allocatedAmount = allocatedAmount; }
}
