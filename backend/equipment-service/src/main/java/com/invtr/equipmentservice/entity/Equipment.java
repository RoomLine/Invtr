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
    private String type;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "serial_no", nullable = false, unique = true)
    private String serialNumber;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "qr_code_url", nullable = false)
    private String qrCodeUrl;

    @Column(name = "condition", nullable = false)
    private String condition;

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
}