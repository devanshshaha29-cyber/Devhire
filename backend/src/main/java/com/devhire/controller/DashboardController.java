package com.devhire.controller;

import com.devhire.dto.DashboardResponse;
import com.devhire.service.DashboardService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService
            dashboardService;

    public DashboardController(
            DashboardService dashboardService
    ) {
        this.dashboardService =
                dashboardService;
    }

    @GetMapping
    public ResponseEntity<DashboardResponse>
    getDashboard(
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                dashboardService.getDashboard(
                        authentication.getName()
                )
        );
    }
}