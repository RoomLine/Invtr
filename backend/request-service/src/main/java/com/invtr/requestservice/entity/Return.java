package com.invtr.requestservice.entity;

import com.invtr.requestservice.enums.EquipmentCondition;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "returns")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Return {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_id", nullable = false)
    private Long requestId;

    @Column(name = "equipment_id", nullable = false)
    private Long equipmentId;

    @Column(name = "return_date_time")
    private LocalDateTime returnDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition", nullable = false)
    private EquipmentCondition condition;

    @PrePersist
    protected void onCreate() {
        returnDateTime = LocalDateTime.now();
    }
}
