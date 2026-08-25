package com.aiems.be.common.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Location {

    @Column(columnDefinition="POINT SRID 4326", nullable=false)
    private Point coordinates;

    private String address;

    @Builder(access = AccessLevel.PRIVATE)
    private Location(Point coordinates, String address) {
        this.coordinates = coordinates;
        this.address = address;
    }

    public static Location of(Point coordinates, String address) {
        return Location.builder()
                .coordinates(coordinates)
                .address(address)
                .build();
    }

    public static Point createCoordinates(double longitude, double latitude) {
        return new org.locationtech.jts.geom.GeometryFactory(new PrecisionModel(), 4326)
                .createPoint(new org.locationtech.jts.geom.Coordinate(longitude, latitude));
    }

}
