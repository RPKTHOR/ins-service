package com.fintech.insurance.claims.repository;

import com.fintech.insurance.claims.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    Optional<Claim> findByClaimNumber(String claimNumber);
    List<Claim> findByCustomerId(Long customerId);
    List<Claim> findByPolicyId(Long policyId);
    List<Claim> findByStatus(ClaimStatus status);
    
    @Query("SELECT COUNT(c) FROM Claim c WHERE c.customerId = :customerId " +
           "AND c.status = 'SETTLED'")
    Long countSettledClaimsByCustomer(Long customerId);
}