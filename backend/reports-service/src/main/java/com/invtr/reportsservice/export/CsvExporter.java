package com.invtr.reportsservice.export;

import com.invtr.reportsservice.dto.EquipmentResponse;
import com.invtr.reportsservice.dto.HistoryReportResponse;
import com.invtr.reportsservice.dto.RequestResponse;
import com.invtr.reportsservice.dto.UsageReportResponse;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CsvExporter {

    public byte[] exportCSVEquipment(List<EquipmentResponse> equipment) {
        StringBuilder builder = new StringBuilder();
        builder.append("id,name,type,status,condition,location,isSensitive,createdAt\n");
        for (EquipmentResponse e : equipment) {
            builder.append(e.getId()).append(",");
            builder.append(e.getName()).append(",");
            builder.append(e.getType()).append(",");
            builder.append(e.getStatus()).append(",");
            builder.append(e.getCondition()).append(",");
            builder.append(e.getLocation()).append(",");
            builder.append(e.getIsSensitive()).append(",");
            builder.append(e.getCreatedAt() != null ? e.getCreatedAt() : "").append("\n");
        }
        return builder.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] exportCSVRequests(List<RequestResponse> requests) {
        StringBuilder builder = new StringBuilder();
        builder.append("id,userId,equipmentIds,status,startDateTime,endDateTime,requestDate,approvedBy,createdAt,updatedAt\n");
        for (RequestResponse r : requests) {
            builder.append(r.getId()).append(",");
            builder.append(r.getUserId()).append(",");
            builder.append(r.getEquipmentIds()).append(",");
            builder.append(r.getStatus()).append(",");
            builder.append(r.getStartDateTime()).append(",");
            builder.append(r.getEndDateTime()).append(",");
            builder.append(r.getRequestDate()).append(",");
            builder.append(r.getApprovedBy() != null ? r.getApprovedBy() : "N/A").append(",");
            builder.append(r.getCreatedAt()).append(",");
            builder.append(r.getUpdatedAt()).append("\n");
        }
        return builder.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] exportCSVUsage(List<UsageReportResponse> usageReports) {
        StringBuilder builder = new StringBuilder();
        // Header row
        builder.append("equipmentId,totalRequests,totalUsageHours\n");

        // Data rows
        for (UsageReportResponse u : usageReports) {
            builder.append(u.getEquipmentId()).append(",");
            builder.append(u.getTotalRequests()).append(",");
            builder.append(u.getTotalUsageHours()).append("\n");
        }
        return builder.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] exportCSVHistory(HistoryReportResponse history) {
        StringBuilder builder = new StringBuilder();

        builder.append("userId,username,id,equipmentIds,status,startDateTime,endDateTime,requestDate,approvedBy,createdAt,updatedAt\n");

        for (RequestResponse r : history.getRequests()) {
            builder.append(history.getUserId()).append(",");
            builder.append(history.getUsername() != null ? history.getUsername() : "").append(",");
            builder.append(r.getId()).append(",");
            builder.append(r.getEquipmentIds() != null ? r.getEquipmentIds().toString().replace(",", ";") : "[]").append(",");
            builder.append(r.getStatus() != null ? r.getStatus() : "").append(",");
            builder.append(r.getStartDateTime() != null ? r.getStartDateTime() : "").append(",");
            builder.append(r.getEndDateTime() != null ? r.getEndDateTime() : "").append(",");
            builder.append(r.getRequestDate() != null ? r.getRequestDate() : "").append(",");
            builder.append(r.getApprovedBy() != null ? r.getApprovedBy() : "N/A").append(",");
            builder.append(r.getCreatedAt() != null ? r.getCreatedAt() : "").append(",");
            builder.append(r.getUpdatedAt() != null ? r.getUpdatedAt() : "").append("\n");
        }
        return builder.toString().getBytes(StandardCharsets.UTF_8);
    }

}
