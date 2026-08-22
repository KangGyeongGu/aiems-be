package com.aiems.be.hospital.client;

import com.aiems.be.contracts.common.PageResponse;
import com.aiems.be.contracts.transfer.record.TransferRecordResponse;
import com.aiems.be.contracts.transfer.record.TransferRecordSearchRequest;
import com.aiems.be.contracts.transfer.record.TransferRecordSummaryResponse;
import com.aiems.be.contracts.transfer.record.TreatmentRecordResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "transferRecords", url = "${app.services.transfer-url}")
public interface TransferRecordClient {

    @GetMapping("/internal/transfers/records/{id}")
    TransferRecordResponse getDetail(@PathVariable Long id);

    @GetMapping("/internal/transfers/records")
    PageResponse<TransferRecordSummaryResponse> getSummaries(
            @RequestParam Long hospitalId,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) List<String> sort,
            @SpringQueryMap TransferRecordSearchRequest search);

    @GetMapping("/internal/transfers/records/{id}/treatments")
    TreatmentRecordResponse getTreatment(@PathVariable Long id);
}
