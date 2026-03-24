package com.invtr.reportsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestResponse {
    private Long id;
    private Long userId;
    private List<Long> equipmentIds;
    private String status;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private LocalDate requestDate;
    private Long approvedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}