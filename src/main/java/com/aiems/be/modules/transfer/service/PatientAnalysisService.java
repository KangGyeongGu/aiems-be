package com.aiems.be.modules.transfer.service;

import com.aiems.be.common.exception.BusinessException;
import com.aiems.be.common.exception.CommonErrorCode;
import com.aiems.be.modules.patient.service.request.PatientAnalysisRequest;
import com.aiems.be.modules.patient.service.result.PatientAnalysisResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientAnalysisService {

    private final ProducerService producerService;

    public PatientAnalysisResult analyze(PatientAnalysisRequest request) {
        PatientAnalysisResult result = producerService.sendAnalysisMessage(request);

        if (result == null) {
            throw new BusinessException(CommonErrorCode.INTERNAL_ERROR, "AI 분석 요청 타임아웃");
        }

        return result;
    }
}
