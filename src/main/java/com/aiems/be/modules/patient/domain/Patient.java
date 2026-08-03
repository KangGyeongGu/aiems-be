package com.aiems.be.modules.patient.domain;

import com.aiems.be.common.domain.BaseTimeEntity;
import com.aiems.be.common.domain.Location;
import com.aiems.be.modules.ambulance.domain.Ambulance;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ambulance_id", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Ambulance ambulance;

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
}
