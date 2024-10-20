package com.expensetracker.controller;

import com.expensetracker.services.ExpenseService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/api")
public class BalanceSheetController {

    private final ExpenseService expenseService;

    public BalanceSheetController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("/balancesheet")
    public ResponseEntity<Resource> generateBalanceSheetCSV() {
        ByteArrayInputStream csvData = expenseService.generateCSV();

        InputStreamResource resource = new InputStreamResource(csvData);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=balancesheet.csv")
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(resource);
    }
}
