package com.fintech.insurance.claims.dto;


import com.fintech.insurance.claims.model.ClaimStatus;
import com.fintech.insurance.claims.model.ClaimType;
import com.fintech.insurance.claims.model.FraudRiskLevel;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimDTO {
    
    private Long id;
    private String claimNumber;
    
    @NotNull(message = "Policy ID is required")
    private Long policyId;
    
    private String policyNumber;
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    @NotNull(message = "Claim type is required")
    private ClaimType claimType;
    
    @NotNull(message = "Claim amount is required")
    @DecimalMin(value = "0.01", message = "Claim amount must be greater than 0")
    private BigDecimal claimAmount;
    
    private BigDecimal approvedAmount;
    private ClaimStatus status;
    
    @NotNull(message = "Incident date is required")
    @Past(message = "Incident date must be in the past")
    private LocalDate incidentDate;
    
    @NotNull(message = "Incident description is required")
    private String incidentDescription;
    
    private String incidentLocation;
    private Double fraudScore;
    private FraudRiskLevel fraudRiskLevel;
    private Long assignedAdjusterId;
    private String adjusterNotes;
    private String rejectionReason;
    private LocalDate filedDate;
    private LocalDate assessmentDate;
    private LocalDate settlementDate;
}
