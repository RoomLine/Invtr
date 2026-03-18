package com.invtr.equipmentservice.repository;

import com.invtr.equipmentservice.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    Equipment findByEquipmentId(String equipmentId);
    Equipment findByEquipmentName(String equipmentName);
    Equipment findByEquipmentStatus(String equipmentStatus);
    Equipment findByEquipmentType(String equipmentType);
    Equipment findBySerialNumber(String serialNumber);
    Equipment findByEquipmentIdAndEquipmentStatus(String equipmentId, String equipmentStatus);
    Equipment findByCondition(String condition);
}
