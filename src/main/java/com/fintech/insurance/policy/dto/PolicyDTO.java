package com.fintech.insurance.policy.dto;

import com.fintech.insurance.policy.model.PaymentFrequency;
import com.fintech.insurance.policy.model.PolicyStatus;
import com.fintech.insurance.policy.model.ProductType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
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
public class PolicyDTO {
    
    private Long id;
    private String policyNumber;
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    @NotNull(message = "Product type is required")
    private ProductType productType;
    
    @NotNull(message = "Premium is required")
    @DecimalMin(value = "0.01", message = "Premium must be greater than 0")
    private BigDecimal premium;
    
    @NotNull(message = "Coverage amount is required")
    @DecimalMin(value = "0.01", message = "Coverage amount must be greater than 0")
    private BigDecimal coverageAmount;
    
    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be present or future")
    private LocalDate startDate;
    
    @NotNull(message = "End date is required")
    private LocalDate endDate;
    
    private PolicyStatus status;
    private String description;
    private String beneficiary;
    private PaymentFrequency paymentFrequency;
}