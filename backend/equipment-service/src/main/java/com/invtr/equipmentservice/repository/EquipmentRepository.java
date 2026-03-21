package com.invtr.equipmentservice.repository;

import com.invtr.equipmentservice.entity.Equipment;
import com.invtr.equipmentservice.enums.EquipmentCondition;
import com.invtr.equipmentservice.enums.EquipmentStatus;
import com.invtr.equipmentservice.enums.EquipmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    @Query("SELECT COUNT(e) FROM Equipment e " +
            "WHERE e.name = :name " +
            "AND e.status = 'AVAILABLE' " +
            "AND e.condition != 'BROKEN'")
    long countAvailableByName(@Param("name") String name);

    @Query("SELECT e FROM Equipment e WHERE " +
            "(:status IS NULL OR e.status = :status) AND " +
            "(:type IS NULL OR e.type = :type) AND " +
            "(:condition IS NULL OR e.condition = :condition)")
    List<Equipment> findWithFilters(@Param("status") EquipmentStatus status,
                                    @Param("type") EquipmentType type,
                                    @Param("condition") EquipmentCondition condition);
    Optional<Equipment> findByName(String name);
    Optional<Equipment> findBySerialNumber(String serialNumber);
    List<Equipment> findByStatus(String status);
    List<Equipment> findByType(String type);
    List<Equipment> findByCondition(String condition);
}