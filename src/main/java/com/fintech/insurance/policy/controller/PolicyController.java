package com.fintech.insurance.policy.controller;

import com.fintech.insurance.policy.dto.PolicyDTO;
import com.fintech.insurance.policy.service.PolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
@Tag(name = "Policy Management")
public class PolicyController {
    
    private final PolicyService policyService;
    
    @PostMapping
    @Operation(summary = "Create new policy")
    public ResponseEntity<PolicyDTO> createPolicy(@Valid @RequestBody PolicyDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(policyService.createPolicy(dto));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get policy by ID")
    public ResponseEntity<PolicyDTO> getPolicy(@PathVariable Long id) {
        return ResponseEntity.ok(policyService.getPolicy(id));
    }
    
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get customer policies")
    public ResponseEntity<List<PolicyDTO>> getCustomerPolicies(@PathVariable Long customerId) {
        return ResponseEntity.ok(policyService.getCustomerPolicies(customerId));
    }
    
    @PostMapping("/{id}/activate")
    @Operation(summary = "Activate policy")
    public ResponseEntity<PolicyDTO> activatePolicy(@PathVariable Long id) {
        return ResponseEntity.ok(policyService.activatePolicy(id));
    }
    
    @PostMapping("/{id}/renew")
    @Operation(summary = "Renew policy")
    public ResponseEntity<PolicyDTO> renewPolicy(@PathVariable Long id) {
        return ResponseEntity.ok(policyService.renewPolicy(id));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel policy")
    public ResponseEntity<Void> cancelPolicy(@PathVariable Long id, 
                                            @RequestParam String reason) {
        policyService.cancelPolicy(id, reason);
        return ResponseEntity.noContent().build();
    }
}