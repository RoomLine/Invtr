package com.invtr.reportsservice.export;

import com.invtr.reportsservice.dto.EquipmentResponse;
import com.invtr.reportsservice.dto.RequestResponse;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CsvExporter {

    public byte[] exportEquipmentAsCSV(List<EquipmentResponse> equipment) {
        StringBuilder builder = new StringBuilder();
        String file;
        builder.append("id,name,type,status,condition,location,isSensitive,createdAt\n");
        for (EquipmentResponse equipmentResponse : equipment) {
            builder.append(equipmentResponse.getId()).append(",");
            builder.append(equipmentResponse.getName()).append(",");
            builder.append(equipmentResponse.getType()).append(",");
            builder.append(equipmentResponse.getStatus()).append(",");
            builder.append(equipmentResponse.getCondition()).append(",");
            builder.append(equipmentResponse.getLocation()).append(",");
            builder.append(equipmentResponse.getIsSensitive()).append(",");
            builder.append(equipmentResponse.getCreatedAt()).append("\n");
        }

        return builder.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] exportRequestAsCSV(List<RequestResponse> requests) {
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
}