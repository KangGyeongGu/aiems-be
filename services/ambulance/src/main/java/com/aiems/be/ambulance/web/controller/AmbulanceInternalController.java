package com.aiems.be.ambulance.web.controller;

import com.aiems.be.ambulance.domain.Ambulance;
import com.aiems.be.ambulance.exception.AmbulanceErrorCode;
import com.aiems.be.ambulance.repository.AmbulanceRepository;
import com.aiems.be.common.exception.BusinessException;
import com.aiems.be.contracts.ambulance.AmbulanceSnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/ambulances")
public class AmbulanceInternalController {

    private final AmbulanceRepository ambulanceRepository;

    @GetMapping("/{id}")
    public AmbulanceSnapshot getSnapshot(@PathVariable Long id) {
        Ambulance ambulance = ambulanceRepository.findById(id)
                .orElseThrow(() -> new BusinessException(AmbulanceErrorCode.AMBULANCE_NOT_FOUND));
        return new AmbulanceSnapshot(
                ambulance.getId(),
                ambulance.getDeviceId(),
                ambulance.getLicensePlate(),
                ambulance.getFireStationName(),
                ambulance.getJurisdiction() != null ? ambulance.getJurisdiction().name() : null,
                ambulance.getOperationStatus() != null ? ambulance.getOperationStatus().name() : null);
    }
}
