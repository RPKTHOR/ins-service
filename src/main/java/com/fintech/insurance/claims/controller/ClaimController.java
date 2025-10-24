package com.fintech.insurance.claims.controller;

import com.fintech.insurance.claims.dto.ClaimDTO;
import com.fintech.insurance.claims.service.ClaimService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
public class ClaimController {
    
    private final ClaimService claimService;
    
    @PostMapping
    @Operation(summary = "File new claim")
    public ResponseEntity<ClaimDTO> fileClaim(@Valid @RequestBody ClaimDTO dto) {
        ClaimDTO filed = claimService.fileClaim(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(filed);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get claim by ID")
    public ResponseEntity<ClaimDTO> getClaim(@PathVariable Long id) {
        ClaimDTO claim = claimService.getClaim(id);
        return ResponseEntity.ok(claim);
    }
    
    @GetMapping("/number/{claimNumber}")
    @Operation(summary = "Get claim by number")
    public ResponseEntity<ClaimDTO> getClaimByNumber(@PathVariable String claimNumber) {
        ClaimDTO claim = claimService.getClaimByNumber(claimNumber);
        return ResponseEntity.ok(claim);
    }
    
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get all claims for customer")
    public ResponseEntity<List<ClaimDTO>> getCustomerClaims(@PathVariable Long customerId) {
        List<ClaimDTO> claims = claimService.getCustomerClaims(customerId);
        return ResponseEntity.ok(claims);
    }
    
    @GetMapping("/policy/{policyId}")
    @Operation(summary = "Get all claims for policy")
    public ResponseEntity<List<ClaimDTO>> getClaimsByPolicy(@PathVariable Long policyId) {
        List<ClaimDTO> claims = claimService.getClaimsByPolicy(policyId);
        return ResponseEntity.ok(claims);
    }
    
    @PostMapping("/{id}/approve")
    @Operation(summary = "Approve claim")
    public ResponseEntity<ClaimDTO> approveClaim(
            @PathVariable Long id,
            @RequestParam BigDecimal approvedAmount,
            @RequestParam(required = false) String notes) {
        ClaimDTO approved = claimService.approveClaim(id, approvedAmount, notes);
        return ResponseEntity.ok(approved);
    }
    
    @PostMapping("/{id}/settle")
    @Operation(summary = "Settle claim")
    public ResponseEntity<ClaimDTO> settleClaim(@PathVariable Long id) {
        ClaimDTO settled = claimService.settleClaim(id);
        return ResponseEntity.ok(settled);
    }
    
    @PostMapping("/{id}/reject")
    @Operation(summary = "Reject claim")
    public ResponseEntity<ClaimDTO> rejectClaim(
            @PathVariable Long id,
            @RequestParam String reason) {
        ClaimDTO rejected = claimService.rejectClaim(id, reason);
        return ResponseEntity.ok(rejected);
    }
}
