package com.park.controller;

import com.park.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        Map<String, Object> stats = dashboardService.buildStats();
        model.addAttribute("stats", stats);
        model.addAttribute("recentReadings", dashboardService.recentReadings());
        model.addAttribute("activePage", "dashboard");
        return "dashboard";
    }
}
