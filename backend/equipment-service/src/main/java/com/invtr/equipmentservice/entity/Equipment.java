package com.invtr.equipmentservice.entity;

import java.time.LocalDateTime;

import com.invtr.equipmentservice.enums.EquipmentCondition;
import com.invtr.equipmentservice.enums.EquipmentStatus;
import com.invtr.equipmentservice.enums.EquipmentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "equipment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private EquipmentType type;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "serial_no", nullable = false, unique = true)
    private String serialNumber;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private EquipmentStatus status;

    @Column(name = "qr_code_url", nullable = false)
    private String qrCodeUrl;

    @Column(name = "condition", nullable = false)
    @Enumerated(EnumType.STRING)
    private EquipmentCondition condition;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "is_sensitive")
    private Boolean isSensitive;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Column(name = "photo_url")
    private String photoUrl;
}