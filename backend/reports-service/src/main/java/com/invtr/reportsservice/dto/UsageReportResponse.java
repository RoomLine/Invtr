package com.invtr.reportsservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageReportResponse {

    @NotNull(message = "Equipment ID cannot be null")
    private Long equipmentId;

    @Min(value = 0, message = "Total requests cannot be negative")
    private int totalRequests;

    @Min(value = 0, message = "Total usage hours cannot be negative")
    private long totalUsageHours;
}