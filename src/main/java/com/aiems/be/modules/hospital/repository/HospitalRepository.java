package com.aiems.be.modules.hospital.repository;

import com.aiems.be.modules.hospital.domain.Hospital;
import com.aiems.be.modules.hospital.repository.dto.HospitalWithDistanceDto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    @Query("""
        SELECT
          new com.aiems.be.modules.hospital.repository.dto.HospitalWithDistanceDto(
            h,
            cast(
              ST_Distance_Sphere(
                ST_PointFromText(CONCAT('POINT(', :latitude, ' ', :longitude, ')'), 4326),
                h.location.coordinates
              ) as double
            )
          )
        FROM Hospital h
        WHERE
          ST_Distance_Sphere(
            ST_PointFromText(CONCAT('POINT(', :latitude, ' ', :longitude, ')'), 4326),
            h.location.coordinates
          ) <= :radius
        ORDER BY
          ST_Distance_Sphere(
            ST_PointFromText(CONCAT('POINT(', :latitude, ' ', :longitude, ')'), 4326),
            h.location.coordinates
          ) ASC
    """)
    List<HospitalWithDistanceDto> findNearbyHospitals (
            @Param("longitude") double longitude,
            @Param("latitude") double latitude,
            @Param("radius") double radiusMeters
    );

    @Query("""
        SELECT
          new com.aiems.be.modules.hospital.repository.dto.HospitalWithDistanceDto(
            h,
            cast(
              ST_Distance_Sphere(
                ST_PointFromText(CONCAT('POINT(', :latitude, ' ', :longitude, ')'), 4326),
                h.location.coordinates
              ) as double
            )
          )
        FROM Hospital h
        WHERE ST_Contains(
                ST_Buffer(ST_PointFromText(CONCAT('POINT(', :latitude, ' ', :longitude, ')'), 4326), :radius), h.location.coordinates
              )
        ORDER BY
          ST_Distance_Sphere(
            ST_PointFromText(CONCAT('POINT(', :latitude, ' ', :longitude, ')'), 4326),
            h.location.coordinates
          ) ASC
    """)
    List<HospitalWithDistanceDto> findNearbyHospitalsV2 (
            @Param("longitude") double longitude,
            @Param("latitude") double latitude,
            @Param("radius") double radius
    );

    @Query("""
        SELECT
          new com.aiems.be.modules.hospital.repository.dto.HospitalWithDistanceDto(
            h,
            cast(
              ST_Distance_Sphere(
                ST_PointFromText(CONCAT('POINT(', :latitude, ' ', :longitude, ')'), 4326),
                h.location.coordinates
              ) as double
            )
          )
        FROM Hospital h
        WHERE ST_Contains(
                ST_Buffer(ST_PointFromText(CONCAT('POINT(', :latitude, ' ', :longitude, ')'), 4326), :radius), h.location.coordinates
              ) AND h.level = :level
        ORDER BY
          ST_Distance_Sphere(
            ST_PointFromText(CONCAT('POINT(', :latitude, ' ', :longitude, ')'), 4326),
            h.location.coordinates
          ) ASC
    """)
    List<HospitalWithDistanceDto> findNearbyHospitalsWithLevel (
            @Param("longitude") double longitude,
            @Param("latitude") double latitude,
            @Param("radius") double radius,
            @Param("level") int level
    );
}
