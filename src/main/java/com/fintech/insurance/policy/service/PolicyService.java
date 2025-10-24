package com.fintech.insurance.policy.service;


import com.fintech.insurance.policy.dto.PolicyDTO;
import com.fintech.insurance.policy.model.*;
import com.fintech.insurance.policy.repository.PolicyRepository;
import com.fintech.insurance.common.event.InsuranceEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PolicyService {
    
    private final PolicyRepository policyRepository;
    private final KafkaTemplate<String, InsuranceEvent> kafkaTemplate;
    
    @Transactional
    public PolicyDTO createPolicy(PolicyDTO dto) {
        log.info("Creating policy for customer: {}", dto.getCustomerId());
        
        Policy policy = Policy.builder()
            .customerId(dto.getCustomerId())
            .productType(dto.getProductType())
            .premium(dto.getPremium())
            .coverageAmount(dto.getCoverageAmount())
            .startDate(dto.getStartDate())
            .endDate(dto.getEndDate())
            .description(dto.getDescription())
            .beneficiary(dto.getBeneficiary())
            .paymentFrequency(dto.getPaymentFrequency())
            .build();
        
        Policy saved = policyRepository.save(policy);
        publishEvent("POLICY_CREATED", saved);
        return mapToDTO(saved);
    }
    
    @Cacheable(value = "policies", key = "#id")
    public PolicyDTO getPolicy(Long id) {
        return policyRepository.findById(id)
            .map(this::mapToDTO)
            .orElseThrow(() -> new RuntimeException("Policy not found"));
    }
    
    public List<PolicyDTO> getCustomerPolicies(Long customerId) {
        return policyRepository.findByCustomerId(customerId).stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    @CacheEvict(value = "policies", key = "#id")
    public PolicyDTO activatePolicy(Long id) {
        log.info("Activating policy: {}", id);
        Policy policy = policyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Policy not found"));
        
        policy.setStatus(PolicyStatus.ACTIVE);
        Policy activated = policyRepository.save(policy);
        publishEvent("POLICY_ACTIVATED", activated);
        
        return mapToDTO(activated);
    }
    
    @Transactional
    @CacheEvict(value = "policies", key = "#id")
    public PolicyDTO renewPolicy(Long id) {
        log.info("Renewing policy: {}", id);
        Policy policy = policyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Policy not found"));
        
        // Extend end date by 1 year
        policy.setEndDate(policy.getEndDate().plusYears(1));
        Policy renewed = policyRepository.save(policy);
        publishEvent("POLICY_RENEWED", renewed);
        
        return mapToDTO(renewed);
    }
    
    @Transactional
    @CacheEvict(value = "policies", key = "#id")
    public void cancelPolicy(Long id, String reason) {
        log.info("Cancelling policy: {}", id);
        Policy policy = policyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Policy not found"));
        
        policy.setStatus(PolicyStatus.CANCELLED);
        policyRepository.save(policy);
        publishEvent("POLICY_CANCELLED", policy);
    }
    
    private void publishEvent(String eventType, Policy policy) {
        InsuranceEvent event = InsuranceEvent.builder()
            .eventType(eventType)
            .policyId(policy.getId())
            .policyNumber(policy.getPolicyNumber())
            .customerId(policy.getCustomerId())
            .premium(policy.getPremium())
            .build();
        kafkaTemplate.send("insurance-events", event);
    }
    
    private PolicyDTO mapToDTO(Policy policy) {
        return PolicyDTO.builder()
            .id(policy.getId())
            .policyNumber(policy.getPolicyNumber())
            .customerId(policy.getCustomerId())
            .productType(policy.getProductType())
            .premium(policy.getPremium())
            .coverageAmount(policy.getCoverageAmount())
            .startDate(policy.getStartDate())
            .endDate(policy.getEndDate())
            .status(policy.getStatus())
            .description(policy.getDescription())
            .beneficiary(policy.getBeneficiary())
            .paymentFrequency(policy.getPaymentFrequency())
            .build();
    }
}
