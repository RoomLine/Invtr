package com.invtr.equipmentservice.repository;

import com.invtr.equipmentservice.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    @Query("SELECT COUNT(e) FROM Equipment e " + "WHERE e.name = :name " + "AND e.status = 'Available' " + "AND e.condition != 'Broken'")
    long countAvailableByName(@Param("name") String name);
    Equipment findById(long id);
    Equipment findByName(String name);
    Equipment findByStatus(String status);
    Equipment findByType(String type);
    Equipment findBySerialNumber(String serialNumber);
    Equipment findByIdAndStatus(Long id, String equipmentStatus);
    Equipment findByCondition(String condition);
}
