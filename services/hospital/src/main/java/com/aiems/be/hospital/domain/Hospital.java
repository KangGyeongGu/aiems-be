package com.aiems.be.hospital.domain;

import com.aiems.be.common.domain.BaseTimeEntity;
import com.aiems.be.common.domain.Location;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(
        name = "idx_hospital_coordinates",
        columnList = "hospital_coordinates"))
@Entity
public class Hospital extends BaseTimeEntity {

    @Id
    @Column(name = "hospital_id")
    private Long id;

    @Column(name = "hospital_name", nullable = false)
    private String name;

    @Comment(value = "국립중앙의료원 제공 코드")
    @Column(nullable = false)
    private String hpid;

    @Embedded
    @AttributeOverride(
            name = "coordinates",
            column = @Column(
                    name = "hospital_coordinates",
                    columnDefinition = "POINT SRID 4326",
                    nullable = false))
    @AttributeOverride(
            name = "address",
            column = @Column(
                    name = "hospital_address",
                    nullable = false))
    private Location location;

    @Column(name = "hospital_level")
    private Integer level;
}
