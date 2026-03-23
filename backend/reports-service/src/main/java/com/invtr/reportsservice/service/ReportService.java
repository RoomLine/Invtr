package com.invtr.reportsservice.service;

import com.invtr.reportsservice.client.AuthServiceClient;
import com.invtr.reportsservice.client.EquipmentServiceClient;
import com.invtr.reportsservice.dto.EquipmentResponse;
import com.invtr.reportsservice.dto.HistoryReportResponse;
import com.invtr.reportsservice.dto.UsageReportResponse;
import com.invtr.reportsservice.enums.Format;
import com.invtr.reportsservice.export.CsvExporter;
import com.invtr.reportsservice.export.ExcelExporter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final EquipmentServiceClient equipmentServiceClient;
    private final AuthServiceClient authServiceClient;
    private final CsvExporter csvExporter;
    private final ExcelExporter excelExporter;

    public List<UsageReportResponse> getUsageReport() {
        // will be implemented once request-service is ready
        return List.of();
    }

    public List<HistoryReportResponse> getHistoryReport() {
        // will be implemented once request-service is ready
        return List.of();
    }

    public byte[] exportReport(Format format) {
        List<EquipmentResponse> equipment = equipmentServiceClient.getAllEquipment();
        if (Format.CSV.equals(format)) {
            return csvExporter.exportEquipmentAsCSV(equipment);
        } else if (Format.EXCEL.equals(format)) {
            try {
                return excelExporter.exportEquipmentAsExcel(equipment);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalArgumentException("Unsupported format: " + format);
        }
    }
}