package com.invtr.reportsservice.controller;

import com.invtr.reportsservice.dto.HistoryReportResponse;
import com.invtr.reportsservice.dto.UsageReportResponse;
import com.invtr.reportsservice.enums.Format;
import com.invtr.reportsservice.service.ReportService;
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

    @GetMapping("/usage")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsageReportResponse>> getUsageReport() {
        return ResponseEntity.ok(reportService.getUsageReport());
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<HistoryReportResponse>> getHistoryReport() {
        return ResponseEntity.ok(reportService.getHistoryReport());
    }

    @GetMapping("/export-equipment")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportReportEquipment(@RequestParam Format format) {
        byte[] file = reportService.exportReport(format);

        String filename = Format.CSV.equals(format) ? "report.csv" : "report.xlsx";
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
    public ResponseEntity<byte[]> exportReportRequests(@RequestParam Format format) {
        byte[] file = reportService.exportReport(format);

        String filename = Format.CSV.equals(format) ? "report.csv" : "report.xlsx";
        String contentType = Format.CSV.equals(format)
                ? "text/csv"
                : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType(contentType))
                .body(file);
    }
}