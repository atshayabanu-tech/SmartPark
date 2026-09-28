package com.park.controller;

import com.park.model.MaintenanceTask;
import com.park.service.MaintenanceService;
import com.park.service.ParkService;
import com.park.service.ZoneService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;
    private final ParkService parkService;
    private final ZoneService zoneService;

    public MaintenanceController(MaintenanceService maintenanceService, ParkService parkService, ZoneService zoneService) {
        this.maintenanceService = maintenanceService;
        this.parkService = parkService;
        this.zoneService = zoneService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String search,
                        @RequestParam(required = false) String status,
                        @RequestParam(required = false) String priority,
                        @RequestParam(required = false) String category,
                        @RequestParam(required = false) String assignedTo,
                        Model model) {
        model.addAttribute("tasks", maintenanceService.search(search, status, priority, category, assignedTo));
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("priority", priority);
        model.addAttribute("category", category);
        model.addAttribute("assignedTo", assignedTo);
        model.addAttribute("activePage", "maintenance");
        return "maintenance";
    }

    @GetMapping("/new")
    public String newForm(@RequestParam(required = false) Long parkId, Model model) {
        MaintenanceTask task = new MaintenanceTask();
        if (parkId != null) {
            task.setPark(parkService.findById(parkId));
        }
        model.addAttribute("task", task);
        model.addAttribute("parks", parkService.findAll());
        model.addAttribute("zones", zoneService.findAll());
        model.addAttribute("activePage", "maintenance");
        return "maintenance-form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("task") MaintenanceTask task, BindingResult bindingResult,
                        Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("parks", parkService.findAll());
            model.addAttribute("zones", zoneService.findAll());
            model.addAttribute("activePage", "maintenance");
            return "maintenance-form";
        }
        maintenanceService.save(task);
        redirectAttributes.addFlashAttribute("successMessage", "Maintenance task saved successfully.");
        return "redirect:/maintenance";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("task", maintenanceService.findById(id));
        model.addAttribute("parks", parkService.findAll());
        model.addAttribute("zones", zoneService.findAll());
        model.addAttribute("activePage", "maintenance");
        return "maintenance-form";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("task") MaintenanceTask task,
                          BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        task.setId(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("parks", parkService.findAll());
            model.addAttribute("zones", zoneService.findAll());
            model.addAttribute("activePage", "maintenance");
            return "maintenance-form";
        }
        maintenanceService.update(id, task);
        redirectAttributes.addFlashAttribute("successMessage", "Maintenance task updated successfully.");
        return "redirect:/maintenance";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        maintenanceService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Maintenance task deleted successfully.");
        return "redirect:/maintenance";
    }

    @PostMapping("/{id}/complete")
    public String complete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        maintenanceService.complete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Maintenance task marked as completed.");
        return "redirect:/maintenance";
    }
}
