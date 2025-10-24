package com.fintech.insurance.underwriting.dto;

import com.fintech.insurance.underwriting.model.RiskLevel;
import com.fintech.insurance.underwriting.model.UnderwritingDecision;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnderwritingCaseDTO {
    
    private Long id;
    private String caseNumber;
    
    @NotNull(message = "Policy ID is required")
    private Long policyId;
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    private Double riskScore;
    private RiskLevel riskLevel;
    private UnderwritingDecision decision;
    private String underwriterNotes;
    private Long assignedUnderwriterId;
    private BigDecimal recommendedPremium;
}