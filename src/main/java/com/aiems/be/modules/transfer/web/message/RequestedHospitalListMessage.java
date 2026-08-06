package com.aiems.be.modules.transfer.web.message;

import lombok.Builder;

import java.util.List;

@Builder
public record RequestedHospitalListMessage(
        List<RequestedHospitalMessage> hospitals
) {
}