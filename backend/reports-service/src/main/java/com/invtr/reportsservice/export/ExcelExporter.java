package com.invtr.reportsservice.export;

import com.invtr.reportsservice.dto.EquipmentResponse;
import com.invtr.reportsservice.dto.RequestResponse;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class ExcelExporter {

    public byte[] exportEquipmentAsExcel(List<EquipmentResponse> equipment) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        try {
            Sheet sheet = workbook.createSheet("Equipment");

            // Header row
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Id");
            header.createCell(1).setCellValue("Name");
            header.createCell(2).setCellValue("Type");
            header.createCell(3).setCellValue("Status");
            header.createCell(4).setCellValue("Condition");
            header.createCell(5).setCellValue("Location");
            header.createCell(6).setCellValue("Is Sensitive");
            header.createCell(7).setCellValue("Created At");
            header.createCell(8).setCellValue("Photo URL");

            // Data rows
            int rowIndex = 1;
            for (EquipmentResponse e : equipment) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(e.getId());
                row.createCell(1).setCellValue(e.getName());
                row.createCell(2).setCellValue(e.getType());
                row.createCell(3).setCellValue(e.getStatus());
                row.createCell(4).setCellValue(e.getCondition());
                row.createCell(5).setCellValue(e.getLocation());
                row.createCell(6).setCellValue(e.getIsSensitive());
                row.createCell(7).setCellValue(e.getCreatedAt() != null ? e.getCreatedAt().toString() : "");
                row.createCell(8).setCellValue(e.getPhotoUrl() != null ? e.getPhotoUrl() : "");
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } finally {
            workbook.close();
        }
    }

    public byte[] exportRequestsAsExcel(List<RequestResponse> requests) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        try {
            Sheet sheet = workbook.createSheet("Requests");

            // Header row
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Id");
            header.createCell(1).setCellValue("User Id");
            header.createCell(2).setCellValue("Equipment Id's");
            header.createCell(3).setCellValue("Status");
            header.createCell(4).setCellValue("Start Date Time");
            header.createCell(5).setCellValue("End Date Time");
            header.createCell(6).setCellValue("Request Date");
            header.createCell(7).setCellValue("Approved By");
            header.createCell(8).setCellValue("Created At");
            header.createCell(9).setCellValue("Updated At");

            // Data rows
            int rowIndex = 1;
            for (RequestResponse requestResponse : requests) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(requestResponse.getId() != null ? String.valueOf(requestResponse.getId()) : "");
                row.createCell(1).setCellValue(requestResponse.getUserId() != null ? String.valueOf(requestResponse.getUserId()) : "");
                String equipmentIdsStr = requestResponse.getEquipmentIds() != null ? requestResponse.getEquipmentIds().toString() : "[]";
                row.createCell(2).setCellValue(equipmentIdsStr);
                row.createCell(3).setCellValue(requestResponse.getStatus() != null ? requestResponse.getStatus() : "");
                row.createCell(4).setCellValue(requestResponse.getStartDateTime() != null ? requestResponse.getStartDateTime().toString() : "");
                row.createCell(5).setCellValue(requestResponse.getEndDateTime() != null ? requestResponse.getEndDateTime().toString() : "");
                row.createCell(6).setCellValue(requestResponse.getRequestDate() != null ? requestResponse.getRequestDate().toString() : "");
                row.createCell(7).setCellValue(requestResponse.getApprovedBy() != null ? String.valueOf(requestResponse.getApprovedBy()) : "N/A");
                row.createCell(8).setCellValue(requestResponse.getCreatedAt() != null ? requestResponse.getCreatedAt().toString() : "");
                row.createCell(9).setCellValue(requestResponse.getUpdatedAt() != null ? requestResponse.getUpdatedAt().toString() : "");
            }

            // Auto-size columns for a clean look
            for (int i = 0; i < 10; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } finally {
            workbook.close();
        }
    }
}