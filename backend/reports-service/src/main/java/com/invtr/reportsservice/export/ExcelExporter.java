package com.invtr.reportsservice.export;

import com.invtr.reportsservice.dto.EquipmentResponse;
import com.invtr.reportsservice.dto.HistoryReportResponse;
import com.invtr.reportsservice.dto.RequestResponse;
import com.invtr.reportsservice.dto.UsageReportResponse;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class ExcelExporter {

    public byte[] exportExcelEquipment(List<EquipmentResponse> equipment) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        try {
            Sheet sheet = workbook.createSheet("Equipment");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Id");
            header.createCell(1).setCellValue("Name");
            header.createCell(2).setCellValue("Type");
            header.createCell(3).setCellValue("Status");
            header.createCell(4).setCellValue("Condition");
            header.createCell(5).setCellValue("Location");
            header.createCell(6).setCellValue("Is Sensitive");
            header.createCell(7).setCellValue("Created At");

            int rowIndex = 1;
            for (EquipmentResponse e : equipment) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(e.getId());
                row.createCell(1).setCellValue(e.getName());
                row.createCell(2).setCellValue(e.getType());
                row.createCell(3).setCellValue(e.getStatus());
                row.createCell(4).setCellValue(e.getCondition());
                row.createCell(5).setCellValue(e.getLocation());
                row.createCell(6).setCellValue(e.getIsSensitive() != null && e.getIsSensitive() ? "Yes" : "No");
                row.createCell(7).setCellValue(e.getCreatedAt() != null ? e.getCreatedAt().toString() : "");
            }
            for (int i = 0; i < 8; i++) sheet.autoSizeColumn(i);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } finally {
            workbook.close();
        }
    }

    public byte[] exportExcelRequests(List<RequestResponse> requests) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        try {
            Sheet sheet = workbook.createSheet("Requests");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Id");
            header.createCell(1).setCellValue("User Id");
            header.createCell(2).setCellValue("Equipment Ids");
            header.createCell(3).setCellValue("Status");
            header.createCell(4).setCellValue("Start Date");
            header.createCell(5).setCellValue("End Date");
            header.createCell(6).setCellValue("Request Date");
            header.createCell(7).setCellValue("Approved By");
            header.createCell(8).setCellValue("Created At");
            header.createCell(9).setCellValue("Updated At");

            int rowIndex = 1;
            for (RequestResponse r : requests) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(r.getId() != null ? r.getId() : 0);
                row.createCell(1).setCellValue(r.getUserId() != null ? r.getUserId() : 0);
                row.createCell(2).setCellValue(r.getEquipmentIds() != null ? r.getEquipmentIds().toString() : "[]");
                row.createCell(3).setCellValue(r.getStatus() != null ? r.getStatus() : "");
                row.createCell(4).setCellValue(r.getStartDateTime() != null ? r.getStartDateTime().toString() : "");
                row.createCell(5).setCellValue(r.getEndDateTime() != null ? r.getEndDateTime().toString() : "");
                row.createCell(6).setCellValue(r.getRequestDate() != null ? r.getRequestDate().toString() : "");
                row.createCell(7).setCellValue(r.getApprovedBy() != null ? r.getApprovedBy().toString() : "N/A");
                row.createCell(8).setCellValue(r.getCreatedAt() != null ? r.getCreatedAt().toString() : "");
                row.createCell(9).setCellValue(r.getUpdatedAt() != null ? r.getUpdatedAt().toString() : "");
            }
            for (int i = 0; i < 10; i++) sheet.autoSizeColumn(i);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } finally {
            workbook.close();
        }
    }

    public byte[] exportExcelUsage(List<UsageReportResponse> usageReports) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        try {
            Sheet sheet = workbook.createSheet("Usage Report");

            // Header row
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Equipment Id");
            header.createCell(1).setCellValue("Total Requests");
            header.createCell(2).setCellValue("Total Usage Hours");

            // Data rows
            int rowIndex = 1;
            for (UsageReportResponse u : usageReports) {
                Row row = sheet.createRow(rowIndex++);

                // Using String.valueOf() for the ID so Excel doesn't format it with commas (e.g., 1,005)
                row.createCell(0).setCellValue(u.getEquipmentId() != null ? String.valueOf(u.getEquipmentId()) : "");
                row.createCell(1).setCellValue(u.getTotalRequests());
                row.createCell(2).setCellValue(u.getTotalUsageHours());
            }

            // Auto-size columns for a clean look
            for (int i = 0; i < 3; i++) sheet.autoSizeColumn(i);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } finally {
            workbook.close();
        }
    }

    public byte[] exportExcelHistory(HistoryReportResponse history) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        try {
            Sheet sheet = workbook.createSheet("User History");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("User Id");
            header.createCell(1).setCellValue("Username");
            header.createCell(2).setCellValue("Request Id");
            header.createCell(3).setCellValue("Equipment Ids");
            header.createCell(4).setCellValue("Status");
            header.createCell(5).setCellValue("Start Date");
            header.createCell(6).setCellValue("End Date");
            header.createCell(7).setCellValue("Request Date");
            header.createCell(8).setCellValue("Approved By");
            header.createCell(9).setCellValue("Created At");
            header.createCell(10).setCellValue("Updated At");

            int rowIndex = 1;
            for (RequestResponse r : history.getRequests()) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(history.getUserId() != null ? history.getUserId() : 0);
                row.createCell(1).setCellValue(history.getUsername() != null ? history.getUsername() : "");
                row.createCell(2).setCellValue(r.getId() != null ? r.getId() : 0);
                row.createCell(3).setCellValue(r.getEquipmentIds() != null ? r.getEquipmentIds().toString() : "[]");
                row.createCell(4).setCellValue(r.getStatus() != null ? r.getStatus() : "");
                row.createCell(5).setCellValue(r.getStartDateTime() != null ? r.getStartDateTime().toString() : "");
                row.createCell(6).setCellValue(r.getEndDateTime() != null ? r.getEndDateTime().toString() : "");
                row.createCell(7).setCellValue(r.getRequestDate() != null ? r.getRequestDate().toString() : "");
                row.createCell(8).setCellValue(r.getApprovedBy() != null ? r.getApprovedBy().toString() : "N/A");
                row.createCell(9).setCellValue(r.getCreatedAt() != null ? r.getCreatedAt().toString() : "");
                row.createCell(10).setCellValue(r.getUpdatedAt() != null ? r.getUpdatedAt().toString() : "");
            }

            for (int i = 0; i < 11; i++) sheet.autoSizeColumn(i);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } finally {
            workbook.close();
        }
    }
}
