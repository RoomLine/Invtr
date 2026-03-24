package com.invtr.reportsservice.service;

import com.invtr.reportsservice.client.AuthServiceClient;
import com.invtr.reportsservice.client.EquipmentServiceClient;
import com.invtr.reportsservice.client.RequestServiceClient;
import com.invtr.reportsservice.dto.*;
import com.invtr.reportsservice.enums.Format;
import com.invtr.reportsservice.exception.ReportGenerationException;
import com.invtr.reportsservice.export.CsvExporter;
import com.invtr.reportsservice.export.ExcelExporter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final EquipmentServiceClient equipmentServiceClient;
    private final RequestServiceClient requestServiceClient;
    private final AuthServiceClient authServiceClient;
    private final CsvExporter csvExporter;
    private final ExcelExporter excelExporter;

    public byte[] exportEquipmentReport(Format format, String authHeader) {
        List<EquipmentResponse> equipment = equipmentServiceClient.getAllEquipment(authHeader);

        if (Format.CSV.equals(format)) {
            return csvExporter.exportCSVEquipment(equipment);
        } else {
            try {
                return excelExporter.exportExcelEquipment(equipment);
            } catch (IOException e) {
                log.error("Failed to export Equipment Report to Excel", e);
                throw new ReportGenerationException("Excel export failed");
            }
        }
    }

    public byte[] exportRequestsReport(Format format, String authHeader) {
        List<RequestResponse> requests = requestServiceClient.getAllRequests(authHeader);

        if (Format.CSV.equals(format)) {
            return csvExporter.exportCSVRequests(requests);
        } else {
            try {
                return excelExporter.exportExcelRequests(requests);
            } catch (IOException e) {
                log.error("Failed to export Requests Report to Excel", e);
                throw new ReportGenerationException("Excel export failed");
            }
        }
    }

    public byte[] exportUsageReport(List<Long> equipmentIds, Format format, String authHeader) {
        List<UsageReportResponse> usageData = getUsageReport(equipmentIds, authHeader);

        if (Format.CSV.equals(format)) {
            return csvExporter.exportCSVUsage(usageData);
        } else {
            try {
                return excelExporter.exportExcelUsage(usageData);
            } catch (IOException e) {
                log.error("Failed to export Usage Report to Excel", e);
                throw new ReportGenerationException("Excel export failed");
            }
        }
    }

    private List<UsageReportResponse> getUsageReport(List<Long> equipmentIds, String authHeader) {
        List<RequestResponse> allRequests = requestServiceClient.getAllRequests(authHeader);
        List<UsageReportResponse> reports = new ArrayList<>();

        for (Long equipmentId : equipmentIds) {
            int totalRequests = 0;
            long totalUsageHours = 0;

            for (RequestResponse req : allRequests) {
                if (req.getEquipmentIds() != null && req.getEquipmentIds().contains(equipmentId)) {
                    totalRequests++;

                    if (req.getStartDateTime() != null && "RETURNED".equalsIgnoreCase(req.getStatus())) {
                        LocalDateTime actualEnd = req.getUpdatedAt() != null ? req.getUpdatedAt() : req.getEndDateTime();
                        if (actualEnd != null) {
                            long hours = Duration.between(req.getStartDateTime(), actualEnd).toHours();
                            totalUsageHours += Math.max(0, hours);
                        }
                    }
                }
            }

            reports.add(UsageReportResponse.builder()
                    .equipmentId(equipmentId)
                    .totalRequests(totalRequests)
                    .totalUsageHours(totalUsageHours)
                    .build());
        }
        return reports;
    }

    public byte[] exportHistoryReport(Long userId, Format format, String authHeader) {
        List<RequestResponse> allRequests = requestServiceClient.getAllRequests(authHeader);
        List<UsersResponse> users = authServiceClient.getAllUsers(authHeader);

        String username = users.stream()
                .filter(u -> u.getId().equals(userId))
                .map(u -> u.getFirstName() + " " + u.getFamilyName())
                .findFirst()
                .orElse("User_" + userId);

        List<RequestResponse> userRequests = allRequests.stream()
                .filter(req -> req.getUserId() != null && req.getUserId().equals(userId))
                .collect(Collectors.toList());

        HistoryReportResponse history = HistoryReportResponse.builder()
                .userId(userId)
                .username(username)
                .requests(userRequests)
                .build();

        if (Format.CSV.equals(format)) {
            return csvExporter.exportCSVHistory(history);
        } else {
            try {
                return excelExporter.exportExcelHistory(history);
            } catch (IOException e) {
                log.error("Failed to export History Report to Excel", e);
                throw new ReportGenerationException("Excel export failed");
            }
        }
    }
}