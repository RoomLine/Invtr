package com.invtr.equipmentservice.repository;

import com.invtr.equipmentservice.entity.ConditionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ConditionLogRepository extends JpaRepository<ConditionLog, Long> {
    ConditionLog findByEquipmentId(String equipmentId);
    ConditionLog findByCondition(String condition);
    ConditionLog findByEquipmentIdAndCondition(String equipmentId, String condition);
    ConditionLog findByDateRange(LocalDateTime start, LocalDateTime end);
}
