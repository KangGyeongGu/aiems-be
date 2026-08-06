package com.aiems.be.modules.auth.domain;

import com.aiems.be.common.domain.Location;
import com.aiems.be.common.domain.Role;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ControlSystem extends Member {

    @Column(name = "system_name", nullable = false)
    private String name;

    @Embedded
    @AttributeOverride(
            name="coordinates",
            column = @Column(
                    name="control_system_coordinates",
                    columnDefinition = "POINT SRID 4326",
                    nullable = false))
    @AttributeOverride(
            name="address",
            column = @Column(
                    name="control_system_address",
                    nullable = false))
    private Location location;

    @Override
    public String getRole() {
        return Role.CONTROL_SYSTEM.name();
    }
}
