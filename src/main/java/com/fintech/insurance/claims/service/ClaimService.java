package com.fintech.insurance.claims.service;
import com.fintech.insurance.claims.dto.ClaimDTO;
import com.fintech.insurance.claims.exception.ClaimNotFoundException;
import com.fintech.insurance.claims.exception.InvalidClaimException;
import com.fintech.insurance.claims.model.*;
import com.fintech.insurance.claims.repository.ClaimRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClaimService {
    
    private final ClaimRepository claimRepository;
    private final FraudDetectionService fraudDetectionService;
    
    @Transactional
    public ClaimDTO fileClaim(ClaimDTO dto) {
        log.info("Filing claim for policy: {}, customer: {}", dto.getPolicyId(), dto.getCustomerId());
        
        Claim claim = Claim.builder()
            .policyId(dto.getPolicyId())
            .customerId(dto.getCustomerId())
            .claimType(dto.getClaimType())
            .claimAmount(dto.getClaimAmount())
            .incidentDate(dto.getIncidentDate())
            .incidentDescription(dto.getIncidentDescription())
            .incidentLocation(dto.getIncidentLocation())
            .filedDate(LocalDate.now())
            .build();
        
        // Calculate fraud score
        double fraudScore = fraudDetectionService.calculateFraudScore(claim);
        FraudRiskLevel riskLevel = fraudDetectionService.determineFraudRiskLevel(fraudScore);
        
        claim.setFraudScore(fraudScore);
        claim.setFraudRiskLevel(riskLevel);
        
        // Auto-triage based on fraud risk
        if (riskLevel == FraudRiskLevel.CRITICAL || riskLevel == FraudRiskLevel.HIGH) {
            claim.setStatus(ClaimStatus.INVESTIGATING);
            log.warn("High fraud risk detected. Score: {}, Level: {}", fraudScore, riskLevel);
        } else {
            claim.setStatus(ClaimStatus.UNDER_REVIEW);
        }
        
        Claim saved = claimRepository.save(claim);
        log.info("Claim filed: {}, Fraud Score: {}, Risk Level: {}", 
            saved.getClaimNumber(), fraudScore, riskLevel);
        
        return mapToDTO(saved);
    }
    
    public ClaimDTO getClaim(Long id) {
        log.info("Fetching claim: {}", id);
        Claim claim = claimRepository.findById(id)
            .orElseThrow(() -> new ClaimNotFoundException("Claim not found with id: " + id));
        return mapToDTO(claim);
    }
    
    public ClaimDTO getClaimByNumber(String claimNumber) {
        log.info("Fetching claim by number: {}", claimNumber);
        Claim claim = claimRepository.findByClaimNumber(claimNumber)
            .orElseThrow(() -> new ClaimNotFoundException("Claim not found: " + claimNumber));
        return mapToDTO(claim);
    }
    
    public List<ClaimDTO> getCustomerClaims(Long customerId) {
        log.info("Fetching claims for customer: {}", customerId);
        return claimRepository.findByCustomerId(customerId).stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }
    
    public List<ClaimDTO> getClaimsByPolicy(Long policyId) {
        log.info("Fetching claims for policy: {}", policyId);
        return claimRepository.findByPolicyId(policyId).stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public ClaimDTO approveClaim(Long id, BigDecimal approvedAmount, String notes) {
        log.info("Approving claim: {} with amount: {}", id, approvedAmount);
        
        Claim claim = claimRepository.findById(id)
            .orElseThrow(() -> new ClaimNotFoundException("Claim not found"));
        
        if (claim.getStatus() == ClaimStatus.APPROVED || claim.getStatus() == ClaimStatus.SETTLED) {
            throw new InvalidClaimException("Claim already approved/settled");
        }
        
        if (approvedAmount.compareTo(claim.getClaimAmount()) > 0) {
            throw new InvalidClaimException("Approved amount cannot exceed claimed amount");
        }
        
        claim.setStatus(ClaimStatus.APPROVED);
        claim.setApprovedAmount(approvedAmount);
        claim.setAdjusterNotes(notes);
        claim.setAssessmentDate(LocalDate.now());
        
        Claim approved = claimRepository.save(claim);
        log.info("Claim approved: {}, Amount: {}", approved.getClaimNumber(), approvedAmount);
        
        return mapToDTO(approved);
    }
    
    @Transactional
    public ClaimDTO settleClaim(Long id) {
        log.info("Settling claim: {}", id);
        
        Claim claim = claimRepository.findById(id)
            .orElseThrow(() -> new ClaimNotFoundException("Claim not found"));
        
        if (claim.getStatus() != ClaimStatus.APPROVED) {
            throw new InvalidClaimException("Can only settle approved claims. Current status: " + claim.getStatus());
        }
        
        claim.setStatus(ClaimStatus.SETTLED);
        claim.setSettlementDate(LocalDate.now());
        
        Claim settled = claimRepository.save(claim);
        log.info("Claim settled: {}, Amount: {}", settled.getClaimNumber(), settled.getApprovedAmount());
        
        return mapToDTO(settled);
    }
    
    @Transactional
    public ClaimDTO rejectClaim(Long id, String reason) {
        log.info("Rejecting claim: {}", id);
        
        Claim claim = claimRepository.findById(id)
            .orElseThrow(() -> new ClaimNotFoundException("Claim not found"));
        
        claim.setStatus(ClaimStatus.REJECTED);
        claim.setRejectionReason(reason);
        claim.setAssessmentDate(LocalDate.now());
        
        Claim rejected = claimRepository.save(claim);
        log.info("Claim rejected: {}, Reason: {}", rejected.getClaimNumber(), reason);
        
        return mapToDTO(rejected);
    }
    
    private ClaimDTO mapToDTO(Claim claim) {
        return ClaimDTO.builder()
            .id(claim.getId())
            .claimNumber(claim.getClaimNumber())
            .policyId(claim.getPolicyId())
            .customerId(claim.getCustomerId())
            .claimType(claim.getClaimType())
            .claimAmount(claim.getClaimAmount())
            .approvedAmount(claim.getApprovedAmount())
            .status(claim.getStatus())
            .incidentDate(claim.getIncidentDate())
            .incidentDescription(claim.getIncidentDescription())
            .incidentLocation(claim.getIncidentLocation())
            .fraudScore(claim.getFraudScore())
            .fraudRiskLevel(claim.getFraudRiskLevel())
            .assignedAdjusterId(claim.getAssignedAdjusterId())
            .adjusterNotes(claim.getAdjusterNotes())
            .rejectionReason(claim.getRejectionReason())
            .filedDate(claim.getFiledDate())
            .assessmentDate(claim.getAssessmentDate())
            .settlementDate(claim.getSettlementDate())
            .build();
    }
}
