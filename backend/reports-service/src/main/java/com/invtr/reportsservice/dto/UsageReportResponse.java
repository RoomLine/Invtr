package com.invtr.reportsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageReportResponse {
    private Long equipmentId;
    private int totalRequests;
    private long totalUsageHours;
}