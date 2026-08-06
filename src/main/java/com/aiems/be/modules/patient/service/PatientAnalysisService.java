package com.aiems.be.modules.patient.service;

import com.aiems.be.common.exception.BusinessException;
import com.aiems.be.common.exception.CommonErrorCode;
import com.aiems.be.modules.patient.service.request.PatientAnalysisRequest;
import com.aiems.be.modules.patient.service.result.PatientAnalysisResult;
import com.aiems.be.modules.transfer.service.ProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientAnalysisService {

    private final ProducerService producerService;
    public PatientAnalysisResult analyze(PatientAnalysisRequest patientAnalysisRequest) {
        PatientAnalysisResult result = doAnalyze(patientAnalysisRequest);
        if (result == null) {
            throw new BusinessException(CommonErrorCode.INTERNAL_ERROR, "AI 분석 요청이 타임아웃되었습니다.");
        }
        return result;
    }

    private PatientAnalysisResult doAnalyze(PatientAnalysisRequest patientAnalysisRequest) {
        long startTime = System.currentTimeMillis();

        PatientAnalysisResult result = producerService.sendAnalysisMessage(patientAnalysisRequest);

        long endTime = System.currentTimeMillis();
        log.info("환자 분석 소요시간 {} ms", (endTime - startTime));

        return result;
    }

}
