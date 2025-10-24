package com.fintech.insurance.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Insurance Event - Base event class for all insurance-related events
 * Used for publishing events to Kafka for event-driven architecture
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // Event Metadata
    private String eventId;              // Unique event identifier
    private String eventType;            // Type of event (POLICY_CREATED, CLAIM_FILED, etc.)
    private LocalDateTime timestamp;     // When event occurred
    private String source;               // Which service generated the event
    
    // Policy Related Fields
    private Long policyId;
    private String policyNumber;
    private BigDecimal premium;
    private BigDecimal coverageAmount;
    private String policyStatus;
    
    // Claim Related Fields
    private Long claimId;
    private String claimNumber;
    private BigDecimal claimAmount;
    private BigDecimal approvedAmount;
    private String claimStatus;
    private Double fraudScore;
    private String fraudRiskLevel;
    
    // Underwriting Related Fields
    private Long underwritingCaseId;
    private String caseNumber;
    private Double riskScore;
    private String riskLevel;
    private String underwritingDecision;
    
    // Common Fields
    private Long customerId;
    private String productType;
    private String additionalInfo;
    
    // Generate event ID automatically
    public void generateEventId() {
        if (this.eventId == null) {
            this.eventId = "EVT-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
        }
    }
    
    // Set timestamp automatically
    public void setTimestampNow() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }
}
