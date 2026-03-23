package com.invtr.reportsservice.export;

import com.invtr.reportsservice.dto.EquipmentResponse;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CsvExporter {

    public byte[] exportEquipmentAsCSV(List<EquipmentResponse> equipment) {
        StringBuilder builder = new StringBuilder();
        String file;
        builder.append("id,name,type,status,condition,location,isSensitive,createdAt\n");
        for  (EquipmentResponse equipmentResponse : equipment) {
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
}
