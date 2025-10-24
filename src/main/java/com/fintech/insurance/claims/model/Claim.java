package com.fintech.insurance.claims.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "claims")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Claim {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 50)
    private String claimNumber;
    
    @Column(nullable = false)
    private Long policyId;
    
    @Column(nullable = false)
    private Long customerId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ClaimType claimType;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal claimAmount;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal approvedAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ClaimStatus status = ClaimStatus.SUBMITTED;
    
    @Column(nullable = false)
    private LocalDate incidentDate;
    
    @Column(length = 2000)
    private String incidentDescription;
    
    @Column(length = 255)
    private String incidentLocation;
    
    // FIXED: Removed precision and scale for Double type
    @Column
    private Double fraudScore;  // Value between 0.0 and 1.0
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private FraudRiskLevel fraudRiskLevel;
    
    private Long assignedAdjusterId;
    
    @Column(length = 1000)
    private String adjusterNotes;
    
    @Column(length = 1000)
    private String rejectionReason;
    
    private LocalDate filedDate;
    private LocalDate assessmentDate;
    private LocalDate settlementDate;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    @PrePersist
    public void prePersist() {
        if (claimNumber == null) {
            claimNumber = "CLM" + System.currentTimeMillis();
        }
        if (filedDate == null) {
            filedDate = LocalDate.now();
        }
    }
}