package com.aiems.be.transfer.client;

import com.aiems.be.contracts.ambulance.AmbulanceSnapshot;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ambulance", url = "${app.services.ambulance-url}")
public interface AmbulanceClient {

    @GetMapping("/internal/ambulances/{id}")
    AmbulanceSnapshot getSnapshot(@PathVariable Long id);
}
