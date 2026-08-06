package com.aiems.be.modules.hospital.domain;

import com.aiems.be.common.domain.Specialty;
import com.aiems.be.common.domain.BaseTimeEntity;
import com.aiems.be.modules.auth.domain.Hospital;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class HospitalSpecialty extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hospital_specialty_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Hospital hospital;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Specialty specialty;
}
