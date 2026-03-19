package com.invtr.equipmentservice.repository;

import com.invtr.equipmentservice.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    Equipment findByID(long id);
    Equipment findByName(String name);
    Equipment findByStatus(String status);
    Equipment findByType(String type);
    Equipment findBySerialNumber(String serialNumber);
    Equipment findByIdAndStatus(Long id, String equipmentStatus);
    Equipment findByCondition(String condition);
}
