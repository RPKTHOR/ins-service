package com.fintech.insurance.underwriting.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "underwriting_cases")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UnderwritingCase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 50)
    private String caseNumber;
    
    @Column(nullable = false)
    private Long policyId;
    
    @Column(nullable = false)
    private Long customerId;
    
    // FIXED: Removed precision and scale for Double type
    @Column
    private Double riskScore;  // Value between 0.0 and 1.0
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private RiskLevel riskLevel;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UnderwritingDecision decision = UnderwritingDecision.PENDING;
    
    @Column(length = 1000)
    private String underwriterNotes;
    
    private Long assignedUnderwriterId;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal recommendedPremium;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    @PrePersist
    public void prePersist() {
        if (caseNumber == null) {
            caseNumber = "UW" + System.currentTimeMillis();
        }
    }
}