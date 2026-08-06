package com.aiems.be.common.web.response;

import com.aiems.be.common.domain.Location;
import lombok.Builder;

@Builder
public record LocationResponse(
        Double lat,
        Double lon,
        String address
) {

    public static LocationResponse from(Location location) {
        return LocationResponse.builder()
                .lat(location.getCoordinates().getY())
                .lon(location.getCoordinates().getX())
                .address(location.getAddress())
                .build();
    }

    public Location toEntity() {
        return Location.of(Location.createCoordinates(lon, lat), address);
    }

}

