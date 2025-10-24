package com.fintech.insurance.underwriting.controller;

import com.fintech.insurance.underwriting.dto.UnderwritingCaseDTO;
import com.fintech.insurance.underwriting.model.UnderwritingDecision;
import com.fintech.insurance.underwriting.service.UnderwritingService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/underwriting")
@RequiredArgsConstructor
public class UnderwritingController {
    
    private final UnderwritingService underwritingService;
    
    @PostMapping("/cases")
    @Operation(summary = "Create underwriting case")
    public ResponseEntity<UnderwritingCaseDTO> createCase(@Valid @RequestBody UnderwritingCaseDTO dto) {
        UnderwritingCaseDTO created = underwritingService.createCase(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @GetMapping("/cases/{id}")
    @Operation(summary = "Get case by ID")
    public ResponseEntity<UnderwritingCaseDTO> getCase(@PathVariable Long id) {
        UnderwritingCaseDTO uwCase = underwritingService.getCase(id);
        return ResponseEntity.ok(uwCase);
    }
    
    @GetMapping("/cases/policy/{policyId}")
    @Operation(summary = "Get cases by policy")
    public ResponseEntity<List<UnderwritingCaseDTO>> getCasesByPolicy(@PathVariable Long policyId) {
        List<UnderwritingCaseDTO> cases = underwritingService.getCasesByPolicy(policyId);
        return ResponseEntity.ok(cases);
    }
    
    @PostMapping("/cases/{id}/review")
    @Operation(summary = "Manual underwriting review")
    public ResponseEntity<UnderwritingCaseDTO> manualReview(
            @PathVariable Long id,
            @RequestParam UnderwritingDecision decision,
            @RequestParam String notes) {
        UnderwritingCaseDTO reviewed = underwritingService.manualReview(id, decision, notes);
        return ResponseEntity.ok(reviewed);
    }
}