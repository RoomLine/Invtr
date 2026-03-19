package com.invtr.equipmentservice.repository;

import com.invtr.equipmentservice.entity.ConditionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ConditionLogRepository extends JpaRepository<ConditionLog, Long> {
    ConditionLog findByEquipmentId(Long equipmentId);
    ConditionLog findByCondition(String condition);
    ConditionLog findByEquipmentIdAndCondition(Long equipmentId, String condition);
    ConditionLog findByDateRange(LocalDateTime start, LocalDateTime end);
}
