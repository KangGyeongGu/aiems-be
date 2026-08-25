package com.aiems.be.transfer.domain;

import com.aiems.be.common.domain.BaseTimeEntity;
import com.aiems.be.common.domain.Gender;
import com.aiems.be.common.domain.Location;
import com.aiems.be.common.domain.PreKTAS;
import com.aiems.be.common.domain.VitalSign;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Patient extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id", updatable = false)
    private Long id;

    @Column(name = "ambulance_id", nullable = false)
    private Long ambulanceId;

    private String ambulanceLicensePlate;

    private String ambulanceFireStationName;

    private String ambulanceDeviceId;

    private String ambulanceJurisdiction;

    private String ambulanceOperationStatus;

    @Column(name = "patient_name", nullable = false)
    private String name;

    @Column(name = "patient_age", nullable = false)
    private Integer age;

    @Column(name = "patient_gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Embedded
    private VitalSign vitalSign;

    @Column(nullable = false)
    private String symptoms;

    @Enumerated(EnumType.STRING)
    private PreKTAS preKtas;

    private String firstAid;

    private String cause;

    private String underlyingDisease;

    @Embedded
    @AttributeOverride(
            name="coordinates",
            column = @Column(
                    name="accident_coordinates",
                    columnDefinition = "POINT SRID 4326"))
    @AttributeOverride(
            name="address",
            column = @Column(
                    name="accident_address"))
    private Location location;

    public void assignPreKtas(PreKTAS preKtas) {
        this.preKtas = preKtas;
    }
}
