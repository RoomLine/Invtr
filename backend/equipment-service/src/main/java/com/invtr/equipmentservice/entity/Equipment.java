package com.invtr.equipmentservice.entity;

import java.time.LocalDateTime;
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
    private String equipmentType;

    @Column(name = "serial_no", nullable = false, unique = true)
    private String serialNumber;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "condition", nullable = false)
    private String condition;

    @Column(name = "location")
    private String location;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
