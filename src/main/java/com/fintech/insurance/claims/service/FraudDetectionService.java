package com.fintech.insurance.claims.service;


import com.fintech.insurance.claims.model.Claim;
import com.fintech.insurance.claims.model.FraudRiskLevel;
import com.fintech.insurance.claims.repository.ClaimRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class FraudDetectionService {
    
    private final ClaimRepository claimRepository;
    
    public double calculateFraudScore(Claim claim) {
        log.info("Calculating fraud score for claim: {}", claim.getClaimNumber());
        
        double score = 0.0;
        
        // Factor 1: Claim frequency (30%)
        Long previousClaims = claimRepository.countSettledClaimsByCustomer(claim.getCustomerId());
        if (previousClaims > 3) {
            score += 0.30;
        } else if (previousClaims > 1) {
            score += 0.15;
        }
        
        // Factor 2: Late filing (15%)
        long daysSinceIncident = ChronoUnit.DAYS.between(claim.getIncidentDate(), claim.getFiledDate());
        if (daysSinceIncident > 180) {
            score += 0.15;
        } else if (daysSinceIncident > 90) {
            score += 0.08;
        }
        
        // Factor 3: High claim amount (20%)
        if (claim.getClaimAmount().doubleValue() > 100000) {
            score += 0.20;
        } else if (claim.getClaimAmount().doubleValue() > 50000) {
            score += 0.10;
        }
        
        // Factor 4: Missing information (15%)
        if (claim.getIncidentLocation() == null || claim.getIncidentLocation().trim().isEmpty()) {
            score += 0.05;
        }
        if (claim.getIncidentDescription() == null || claim.getIncidentDescription().length() < 20) {
            score += 0.10;
        }
        
        double finalScore = Math.min(score, 1.0);
        log.info("Fraud score calculated: {} for claim: {}", finalScore, claim.getClaimNumber());
        
        return finalScore;
    }
    
    public FraudRiskLevel determineFraudRiskLevel(double fraudScore) {
        if (fraudScore >= 0.75) {
            return FraudRiskLevel.CRITICAL;
        } else if (fraudScore >= 0.50) {
            return FraudRiskLevel.HIGH;
        } else if (fraudScore >= 0.25) {
            return FraudRiskLevel.MEDIUM;
        }
        return FraudRiskLevel.LOW;
    }
}
