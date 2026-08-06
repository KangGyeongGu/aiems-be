package com.aiems.be.modules.transfer.repository;

import com.aiems.be.modules.transfer.domain.Gender;
import com.aiems.be.modules.transfer.domain.PreKTAS;
import com.aiems.be.modules.transfer.domain.TransferRecord;
import com.aiems.be.modules.transfer.repository.projection.TransferRecordDetail;
import com.aiems.be.modules.transfer.repository.projection.TransferRecordSummary;
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
            new com.aiems.be.modules.transfer.repository.projection.TransferRecordDetail(
                tr.id,
                a,
                h,
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
        JOIN Ambulance a ON tr.ambulanceId = a.id
        JOIN Hospital h ON tr.hospitalId = h.id
        WHERE tr.id = :id
    """)
    Optional<TransferRecordDetail> findByTransferRecordId(@Param("id") Long id);

    @Query("""
    SELECT
        new com.aiems.be.modules.transfer.repository.projection.TransferRecordSummary(
            tr.id,
            tr.endedAt,
            a.licensePlate,
            a.fireStationName,
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
    JOIN Ambulance a ON tr.ambulanceId = a.id
    WHERE tr.hospitalId = :hospitalId
      AND (:patientName IS NULL OR p.name LIKE %:patientName%)
      AND (:patientAge IS NULL OR p.age = :patientAge)
      AND (:patientGender IS NULL OR p.gender = :patientGender)
      AND (:symptoms IS NULL OR p.symptoms LIKE %:symptoms%)
      AND (:preKTAS IS NULL OR p.preKtas = :preKTAS)
      AND (:ambulanceLicensePlate IS NULL OR a.licensePlate LIKE %:ambulanceLicensePlate%)
      AND (:fireStationName IS NULL OR a.fireStationName LIKE %:fireStationName%)
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
}
