package com.invtr.reportsservice.controller;

import com.invtr.reportsservice.client.EquipmentServiceClient;
import com.invtr.reportsservice.enums.Format;
import com.invtr.reportsservice.exception.ResourceNotFoundException;
import com.invtr.reportsservice.service.ReportService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final EquipmentServiceClient equipmentServiceClient;

    @PostMapping("/usage")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportUsageReport(
            @RequestBody List<Long> equipmentIds,
            @RequestParam Format format,
            HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        // Simple check — fetch all equipment and validate IDs
        List<Long> existingIds = equipmentServiceClient.getAllEquipment(authHeader)
                .stream()
                .map(e -> e.getId())
                .toList();

        for (Long id : equipmentIds) {
            if (!existingIds.contains(id)) {
                throw new ResourceNotFoundException("Equipment with id " + id + " does not exist");
            }
        }

        byte[] file = reportService.exportUsageReport(equipmentIds, format, authHeader);
        String filename = Format.CSV.equals(format) ? "usage.csv" : "usage.xlsx";
        String contentType = Format.CSV.equals(format)
                ? "text/csv"
                : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType(contentType))
                .body(file);
    }

    @GetMapping("/history/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportHistoryReport(
            @PathVariable Long userId,
            @RequestParam Format format,
            HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        byte[] file = reportService.exportHistoryReport(userId, format, authHeader);

        String filename = Format.CSV.equals(format) ? "user_history.csv" : "user_history.xlsx";
        String contentType = Format.CSV.equals(format)
                ? "text/csv"
                : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType(contentType))
                .body(file);
    }

    @GetMapping("/export-equipment")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportReportEquipment(
            @RequestParam Format format,
            HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        byte[] file = reportService.exportEquipmentReport(format, authHeader);

        String filename = Format.CSV.equals(format) ? "equipment.csv" : "equipment.xlsx";
        String contentType = Format.CSV.equals(format)
                ? "text/csv"
                : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType(contentType))
                .body(file);
    }

    @GetMapping("/export-requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportReportRequests(
            @RequestParam Format format,
            HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        byte[] file = reportService.exportRequestsReport(format, authHeader);

        String filename = Format.CSV.equals(format) ? "requests.csv" : "requests.xlsx";
        String contentType = Format.CSV.equals(format)
                ? "text/csv"
                : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType(contentType))
                .body(file);
    }
}