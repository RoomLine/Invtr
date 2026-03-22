package com.invtr.equipmentservice.entity;

import java.time.LocalDateTime;

import com.invtr.equipmentservice.enums.EquipmentCondition;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "condition_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConditionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "equipment_id", nullable = false)
    private Long equipmentId;

    @Column(name = "condition", nullable = false)
    @Enumerated(EnumType.STRING)
    private EquipmentCondition condition;

    @Column(name = "logged_at", updatable = false)
    private LocalDateTime loggedAt;

    @PrePersist
    protected void onCreate() {
        loggedAt = LocalDateTime.now();
    }
}