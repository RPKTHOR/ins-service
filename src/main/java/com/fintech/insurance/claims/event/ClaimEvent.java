package com.fintech.insurance.claims.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimEvent {
    private String eventType;
    private Long claimId;
    private String claimNumber;
    private Long policyId;
    private Long customerId;
    private BigDecimal claimAmount;
    private BigDecimal approvedAmount;
    private String status;
}