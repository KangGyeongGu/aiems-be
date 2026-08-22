package com.aiems.be.contracts.transfer.record;

import com.aiems.be.common.exception.BusinessException;
import com.aiems.be.common.exception.CommonErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import java.util.List;

public record TreatmentRecordResponse(
        String id,
        String object,
        Long created,
        String model,
        TreatmentInfo treatmentInfo,
        String summaryHtml
) {

    public record TreatmentInfo(
            AirwayManagement airwayManagement,
            OxygenTherapy oxygenTherapy,
            CirculatorySupport circulatorySupport,
            List<String> medicationsGiven,
            CprPerformed cprPerformed,
            WoundTreatment woundTreatment,
            Splinting splinting,
            Delivery delivery,
            TemperatureControl temperatureControl,
            List<String> otherTreatments
    ) {}

    public record AirwayManagement(
            boolean performed,
            String device,
            String details
    ) {}

    public record OxygenTherapy(
            boolean performed,
            String method,
            String flowRate,
            String details
    ) {}

    public record CirculatorySupport(
            boolean performed,
            boolean ivAccess,
            boolean fluidSupply,
            String amount,
            String details
    ) {}

    public record CprPerformed(
            boolean performed,
            String startTime,
            String endTime,
            boolean defibrillation,
            String shockCount,
            String details
    ) {}

    public record WoundTreatment(
            boolean bleedingControl,
            boolean dressing,
            String details
    ) {}

    public record Splinting(
            boolean performed,
            String location,
            String details
    ) {}

    public record Delivery(
            boolean performed,
            String details
    ) {}

    public record TemperatureControl(
            boolean warming,
            boolean cooling,
            String details
    ) {}

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    public static TreatmentRecordResponse parse(String journalJson) {
        if (journalJson == null || journalJson.isBlank()) {
            throw new BusinessException(CommonErrorCode.NOT_FOUND);
        }
        try {
            return MAPPER.readValue(journalJson, TreatmentRecordResponse.class);
        } catch (Exception e) {
            throw new BusinessException(CommonErrorCode.INTERNAL_ERROR);
        }
    }
}
