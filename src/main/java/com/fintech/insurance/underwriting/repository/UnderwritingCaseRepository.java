package com.fintech.insurance.underwriting.repository;


import com.fintech.insurance.underwriting.model.UnderwritingCase;
import com.fintech.insurance.underwriting.model.UnderwritingDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnderwritingCaseRepository extends JpaRepository<UnderwritingCase, Long> {
    
    Optional<UnderwritingCase> findByCaseNumber(String caseNumber);
    
    List<UnderwritingCase> findByPolicyId(Long policyId);
    
    List<UnderwritingCase> findByCustomerId(Long customerId);
    
    List<UnderwritingCase> findByDecision(UnderwritingDecision decision);
    
    List<UnderwritingCase> findByAssignedUnderwriterId(Long underwriterId);
}