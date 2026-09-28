package com.park.controller;

import com.park.service.AlertService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String severity,
                        @RequestParam(required = false) String type,
                        @RequestParam(required = false) String resolvedFilter,
                        Model model) {
        model.addAttribute("alerts", alertService.filter(severity, type, resolvedFilter));
        model.addAttribute("severity", severity);
        model.addAttribute("type", type);
        model.addAttribute("resolvedFilter", resolvedFilter);
        model.addAttribute("activePage", "alerts");
        return "alerts";
    }

    @PostMapping("/{id}/resolve")
    public String resolve(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        alertService.resolve(id);
        redirectAttributes.addFlashAttribute("successMessage", "Alert resolved.");
        return "redirect:/alerts";
    }
}
