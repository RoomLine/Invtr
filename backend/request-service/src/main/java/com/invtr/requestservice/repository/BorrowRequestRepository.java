package com.invtr.requestservice.repository;

import com.invtr.requestservice.entity.BorrowRequest;
import com.invtr.requestservice.enums.BorrowRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface BorrowRequestRepository extends JpaRepository<BorrowRequest, Long> {

	List<BorrowRequest> findByUserIdOrderByCreatedAtDesc(Long userId);

	List<BorrowRequest> findAllByOrderByCreatedAtDesc();

	boolean existsByEquipmentIdAndStatusIn(Long equipmentId, Collection<BorrowRequestStatus> statuses);
}
