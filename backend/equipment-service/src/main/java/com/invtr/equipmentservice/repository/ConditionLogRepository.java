package com.invtr.equipmentservice.repository;

import com.invtr.equipmentservice.entity.ConditionLog;
import com.invtr.equipmentservice.enums.EquipmentCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConditionLogRepository extends JpaRepository<ConditionLog, Long> {
    List<ConditionLog> findByEquipmentId(Long equipmentId);
    List<ConditionLog> findByCondition(EquipmentCondition condition);
    List<ConditionLog> findByEquipmentIdAndCondition(Long equipmentId, String condition);

    @Query("SELECT c FROM ConditionLog c WHERE " +
            "(:equipmentId IS NULL OR c.equipmentId = :equipmentId) AND " +
            "(:condition IS NULL OR c.condition = :condition)")
    List<ConditionLog> findWithFilters(@Param("equipmentId") Long equipmentId,
                                       @Param("condition") EquipmentCondition condition);
}