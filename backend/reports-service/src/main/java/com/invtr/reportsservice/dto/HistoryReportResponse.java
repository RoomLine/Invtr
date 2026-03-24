package com.invtr.reportsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryReportResponse {
    private Long userId;
    private String username;
    private List<RequestResponse> requests;
}