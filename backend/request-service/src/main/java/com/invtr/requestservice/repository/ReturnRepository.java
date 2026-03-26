package com.invtr.requestservice.repository;

import com.invtr.requestservice.entity.Return;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReturnRepository extends JpaRepository<Return, Long> {
    List<Return> findByRequestId(Long requestId);
}