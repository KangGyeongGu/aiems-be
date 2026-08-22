package com.aiems.be.contracts.ai;

import com.aiems.be.common.domain.PreKTAS;

import java.util.List;

public record PatientClassified(
        Long patientId,
        Long ambulanceId,
        List<SpecialtyConfidence> specialtyConfidences,
        PreKTAS preKTAS
) {
}
