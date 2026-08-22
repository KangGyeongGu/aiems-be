package com.aiems.be.transfer.repository;

import com.aiems.be.common.domain.Gender;
import com.aiems.be.common.domain.PreKTAS;
import com.aiems.be.transfer.domain.TransferRecord;
import com.aiems.be.transfer.repository.projection.AmbulanceTransferSummary;
import com.aiems.be.transfer.repository.projection.TransferRecordDetail;
import com.aiems.be.transfer.repository.projection.TransferRecordSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface TransferRecordRepository extends JpaRepository<TransferRecord, Long> {

    @Query("""
        SELECT
            new com.aiems.be.transfer.repository.projection.TransferRecordDetail(
                tr.id,
                tr.ambulanceId,
                p.ambulanceLicensePlate,
                p.ambulanceFireStationName,
                tr.hospitalId,
                tr.hospitalName,
                tr.hospitalAddress,
                p,
                tr.transferReport,
                tr.treatmentRecord,
                p.location,
                tr.startedAt,
                tr.endedAt,
                tr.createdAt,
                tr.updatedAt
            )
        FROM TransferRecord tr
        JOIN Patient p ON tr.patientId = p.id
        WHERE tr.id = :id
    """)
    Optional<TransferRecordDetail> findByTransferRecordId(@Param("id") Long id);

    @Query("""
    SELECT
        new com.aiems.be.transfer.repository.projection.TransferRecordSummary(
            tr.id,
            tr.endedAt,
            p.ambulanceLicensePlate,
            p.ambulanceFireStationName,
            p.id,
            p.name,
            p.age,
            p.gender,
            p.symptoms,
            p.preKtas
        )
    FROM
        TransferRecord tr
    JOIN Patient p ON tr.patientId = p.id
    WHERE tr.hospitalId = :hospitalId
      AND (:patientName IS NULL OR p.name LIKE %:patientName%)
      AND (:patientAge IS NULL OR p.age = :patientAge)
      AND (:patientGender IS NULL OR p.gender = :patientGender)
      AND (:symptoms IS NULL OR p.symptoms LIKE %:symptoms%)
      AND (:preKTAS IS NULL OR p.preKtas = :preKTAS)
      AND (:ambulanceLicensePlate IS NULL OR p.ambulanceLicensePlate LIKE %:ambulanceLicensePlate%)
      AND (:fireStationName IS NULL OR p.ambulanceFireStationName LIKE %:fireStationName%)
      AND (:startedAtFrom IS NULL OR tr.startedAt >= :startedAtFrom)
      AND (:startedAtTo IS NULL OR tr.startedAt <= :startedAtTo)
      AND (:endedAtFrom IS NULL OR tr.endedAt >= :endedAtFrom)
      AND (:endedAtTo IS NULL OR tr.endedAt <= :endedAtTo)
""")
    Page<TransferRecordSummary> findAllSummaries(
            @Param("hospitalId") Long hospitalId,
            @Param("patientName") String patientName,
            @Param("patientAge") Integer patientAge,
            @Param("patientGender") Gender patientGender,
            @Param("symptoms") String symptoms,
            @Param("preKTAS") PreKTAS preKTAS,
            @Param("ambulanceLicensePlate") String ambulanceLicensePlate,
            @Param("fireStationName") String fireStationName,
            @Param("startedAtFrom") Instant startedAtFrom,
            @Param("startedAtTo") Instant startedAtTo,
            @Param("endedAtFrom") Instant endedAtFrom,
            @Param("endedAtTo") Instant endedAtTo,
            Pageable pageable
    );

    Optional<TransferRecord> findByPatientId(Long patientId);

    @Query("""
        SELECT tr
        FROM TransferRecord tr
        WHERE tr.ambulanceId = :ambulanceId
            AND tr.endedAt IS NULL
        ORDER BY tr.startedAt DESC
        LIMIT 1
    """)
    Optional<TransferRecord> findOngoingTransferRecord(Long ambulanceId);

    Optional<TransferRecord> findByAmbulanceIdAndPatientId(Long ambulanceId, Long patientId);

    @Query("""
        SELECT new com.aiems.be.transfer.repository.projection.AmbulanceTransferSummary(
            tr.id, tr.startedAt, tr.endedAt, tr.hospitalName, p.name, p.preKtas, tr.transferReport)
        FROM TransferRecord tr
        JOIN Patient p ON tr.patientId = p.id
        WHERE tr.ambulanceId = :ambulanceId
    """)
    Page<AmbulanceTransferSummary> findSummariesByAmbulanceId(
            @Param("ambulanceId") Long ambulanceId, Pageable pageable);
}
