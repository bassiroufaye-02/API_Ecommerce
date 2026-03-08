package com.ecommerce.mulboutique.controller;

import com.ecommerce.mulboutique.dto.analytics.AnalyticsResponse;
import com.ecommerce.mulboutique.entity.Store;
import com.ecommerce.mulboutique.entity.User;
import com.ecommerce.mulboutique.exception.ForbiddenException;
import com.ecommerce.mulboutique.exception.NotFoundException;
import com.ecommerce.mulboutique.repository.StoreRepository;
import com.ecommerce.mulboutique.service.CurrentUserService;
import com.ecommerce.mulboutique.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/store-owners/analytics")
@Tag(name = "Analytics", description = "Statistiques boutique")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private StoreRepository storeRepository;

    @GetMapping("/store/{storeId}")
    @PreAuthorize("hasRole('STORE_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Statistiques d'une boutique")
    public ResponseEntity<AnalyticsResponse> getAnalytics(@PathVariable Long storeId) {
        ensureStoreAccess(storeId);
        return ResponseEntity.ok(analyticsService.getStoreAnalytics(storeId));
    }

    @GetMapping("/store/{storeId}/export")
    @PreAuthorize("hasRole('STORE_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Exporter les statistiques en CSV")
    public ResponseEntity<String> exportAnalytics(@PathVariable Long storeId) {
        ensureStoreAccess(storeId);
        AnalyticsResponse response = analyticsService.getStoreAnalytics(storeId);
        StringBuilder csv = new StringBuilder();
        csv.append("summary\n");
        csv.append("storeId,totalRevenue,conversionRate\n");
        csv.append(storeId).append(",")
           .append(response.getTotalRevenue()).append(",")
           .append(response.getConversionRate()).append("\n\n");

        csv.append("revenue_by_period\n");
        csv.append("period,revenue\n");
        response.getRevenueByPeriod().forEach((period, revenue) -> {
            csv.append(period).append(",").append(revenue).append("\n");
        });
        csv.append("\n");

        csv.append("orders_by_period\n");
        csv.append("period,orders\n");
        response.getOrdersByPeriod().forEach((period, count) -> {
            csv.append(period).append(",").append(count).append("\n");
        });
        csv.append("\n");

        csv.append("top_products\n");
        csv.append("productId,productName,totalQuantity,totalRevenue\n");
        response.getTopProducts().forEach(p -> {
            csv.append(p.getProductId()).append(",")
               .append(p.getProductName()).append(",")
               .append(p.getTotalQuantity()).append(",")
               .append(p.getTotalRevenue()).append("\n");
        });

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=analytics-" + storeId + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv.toString());
    }

    @GetMapping("/store/{storeId}/export/xlsx")
    @PreAuthorize("hasRole('STORE_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Exporter les statistiques en Excel")
    public ResponseEntity<byte[]> exportAnalyticsExcel(@PathVariable Long storeId) throws IOException {
        ensureStoreAccess(storeId);
        AnalyticsResponse response = analyticsService.getStoreAnalytics(storeId);

        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet summary = workbook.createSheet("summary");
            Row summaryHeader = summary.createRow(0);
            summaryHeader.createCell(0).setCellValue("storeId");
            summaryHeader.createCell(1).setCellValue("totalRevenue");
            summaryHeader.createCell(2).setCellValue("conversionRate");
            Row summaryRow = summary.createRow(1);
            summaryRow.createCell(0).setCellValue(storeId);
            summaryRow.createCell(1).setCellValue(response.getTotalRevenue().doubleValue());
            summaryRow.createCell(2).setCellValue(response.getConversionRate());

            Sheet revenue = workbook.createSheet("revenue_by_period");
            Row revenueHeader = revenue.createRow(0);
            revenueHeader.createCell(0).setCellValue("period");
            revenueHeader.createCell(1).setCellValue("revenue");
            int r = 1;
            for (var entry : response.getRevenueByPeriod().entrySet()) {
                Row row = revenue.createRow(r++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue().doubleValue());
            }

            Sheet orders = workbook.createSheet("orders_by_period");
            Row ordersHeader = orders.createRow(0);
            ordersHeader.createCell(0).setCellValue("period");
            ordersHeader.createCell(1).setCellValue("orders");
            int o = 1;
            for (var entry : response.getOrdersByPeriod().entrySet()) {
                Row row = orders.createRow(o++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

            Sheet top = workbook.createSheet("top_products");
            Row topHeader = top.createRow(0);
            topHeader.createCell(0).setCellValue("productId");
            topHeader.createCell(1).setCellValue("productName");
            topHeader.createCell(2).setCellValue("totalQuantity");
            topHeader.createCell(3).setCellValue("totalRevenue");
            int t = 1;
            for (var p : response.getTopProducts()) {
                Row row = top.createRow(t++);
                row.createCell(0).setCellValue(p.getProductId());
                row.createCell(1).setCellValue(p.getProductName());
                row.createCell(2).setCellValue(p.getTotalQuantity());
                row.createCell(3).setCellValue(p.getTotalRevenue().doubleValue());
            }

            workbook.write(out);
            byte[] data = out.toByteArray();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=analytics-" + storeId + ".xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(data);
        }
    }

    private void ensureStoreAccess(Long storeId) {
        User user = currentUserService.getCurrentUser();
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException("Boutique non trouvee"));
        if (!store.getOwner().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new ForbiddenException("Acces refuse");
        }
    }
}

