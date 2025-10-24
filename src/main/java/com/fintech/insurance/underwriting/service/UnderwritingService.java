package com.fintech.insurance.underwriting.service;

import com.fintech.insurance.underwriting.dto.UnderwritingCaseDTO;
import com.fintech.insurance.underwriting.exception.UnderwritingException;
import com.fintech.insurance.underwriting.model.*;
import com.fintech.insurance.underwriting.repository.UnderwritingCaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UnderwritingService {
    
    private final UnderwritingCaseRepository underwritingRepository;
    
    @Transactional
    public UnderwritingCaseDTO createCase(UnderwritingCaseDTO dto) {
        log.info("Creating underwriting case for policy: {}", dto.getPolicyId());
        
        double riskScore = calculateRiskScore(dto);
        RiskLevel riskLevel = determineRiskLevel(riskScore);
        UnderwritingDecision decision = autoDecide(riskLevel);
        BigDecimal recommendedPremium = calculateRecommendedPremium(dto.getRecommendedPremium(), riskLevel);
        
        UnderwritingCase uwCase = UnderwritingCase.builder()
            .policyId(dto.getPolicyId())
            .customerId(dto.getCustomerId())
            .riskScore(riskScore)
            .riskLevel(riskLevel)
            .decision(decision)
            .recommendedPremium(recommendedPremium)
            .build();
        
        UnderwritingCase saved = underwritingRepository.save(uwCase);
        log.info("Underwriting case created: {}, Risk: {}, Decision: {}", 
            saved.getCaseNumber(), riskLevel, decision);
        
        return mapToDTO(saved);
    }
    
    public UnderwritingCaseDTO getCase(Long id) {
        log.info("Fetching underwriting case: {}", id);
        UnderwritingCase uwCase = underwritingRepository.findById(id)
            .orElseThrow(() -> new UnderwritingException("Case not found with id: " + id));
        return mapToDTO(uwCase);
    }
    
    public List<UnderwritingCaseDTO> getCasesByPolicy(Long policyId) {
        log.info("Fetching cases for policy: {}", policyId);
        return underwritingRepository.findByPolicyId(policyId).stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public UnderwritingCaseDTO manualReview(Long id, UnderwritingDecision decision, String notes) {
        log.info("Manual review for case: {} with decision: {}", id, decision);
        
        UnderwritingCase uwCase = underwritingRepository.findById(id)
            .orElseThrow(() -> new UnderwritingException("Case not found"));
        
        uwCase.setDecision(decision);
        uwCase.setUnderwriterNotes(notes);
        
        UnderwritingCase reviewed = underwritingRepository.save(uwCase);
        log.info("Case reviewed: {}, Decision: {}", reviewed.getCaseNumber(), decision);
        
        return mapToDTO(reviewed);
    }
    
    private double calculateRiskScore(UnderwritingCaseDTO dto) {
        return 0.5;
    }
    
    private RiskLevel determineRiskLevel(double score) {
        if (score >= 0.75) return RiskLevel.VERY_HIGH;
        if (score >= 0.50) return RiskLevel.HIGH;
        if (score >= 0.25) return RiskLevel.MEDIUM;
        return RiskLevel.LOW;
    }
    
    private UnderwritingDecision autoDecide(RiskLevel level) {
        return switch (level) {
            case LOW, MEDIUM -> UnderwritingDecision.APPROVED;
            case HIGH, VERY_HIGH -> UnderwritingDecision.REFERRED;
        };
    }
    
    private BigDecimal calculateRecommendedPremium(BigDecimal basePremium, RiskLevel riskLevel) {
        if (basePremium == null) return BigDecimal.ZERO;
        
        BigDecimal loadingFactor = switch (riskLevel) {
            case LOW -> BigDecimal.valueOf(1.0);
            case MEDIUM -> BigDecimal.valueOf(1.15);
            case HIGH -> BigDecimal.valueOf(1.30);
            case VERY_HIGH -> BigDecimal.valueOf(1.50);
        };
        
        return basePremium.multiply(loadingFactor);
    }
    
    private UnderwritingCaseDTO mapToDTO(UnderwritingCase uwCase) {
        return UnderwritingCaseDTO.builder()
            .id(uwCase.getId())
            .caseNumber(uwCase.getCaseNumber())
            .policyId(uwCase.getPolicyId())
            .customerId(uwCase.getCustomerId())
            .riskScore(uwCase.getRiskScore())
            .riskLevel(uwCase.getRiskLevel())
            .decision(uwCase.getDecision())
            .underwriterNotes(uwCase.getUnderwriterNotes())
            .assignedUnderwriterId(uwCase.getAssignedUnderwriterId())
            .recommendedPremium(uwCase.getRecommendedPremium())
            .build();
    }
}