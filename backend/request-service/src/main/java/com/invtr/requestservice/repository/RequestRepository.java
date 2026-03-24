package com.invtr.requestservice.repository;

import com.invtr.requestservice.entity.Request;
import com.invtr.requestservice.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Request> findAllByOrderByCreatedAtDesc();

    @Query("SELECT COUNT(ri) > 0 FROM RequestItem ri WHERE ri.id.equipmentId = :equipmentId AND ri.request.status IN :statuses")
    boolean existsByEquipmentIdAndStatusIn(@Param("equipmentId") Long equipmentId,
                                           @Param("statuses") Collection<RequestStatus> statuses);
}