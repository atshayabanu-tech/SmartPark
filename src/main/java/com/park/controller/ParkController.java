package com.park.controller;

import com.park.model.MaintenanceTask;
import com.park.model.Park;
import com.park.repository.SensorReadingRepository;
import com.park.service.AlertService;
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
@RequestMapping("/parks")
public class ParkController {

    private final ParkService parkService;
    private final ZoneService zoneService;
    private final MaintenanceService maintenanceService;
    private final AlertService alertService;
    private final SensorReadingRepository sensorReadingRepository;

    public ParkController(ParkService parkService, ZoneService zoneService,
                           MaintenanceService maintenanceService, AlertService alertService,
                           SensorReadingRepository sensorReadingRepository) {
        this.sensorReadingRepository = sensorReadingRepository;
        this.parkService = parkService;
        this.zoneService = zoneService;
        this.maintenanceService = maintenanceService;
        this.alertService = alertService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String search,
                        @RequestParam(required = false) String status,
                        @RequestParam(required = false) String location,
                        Model model) {
        model.addAttribute("parks", parkService.search(search, status, location));
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("location", location);
        model.addAttribute("activePage", "parks");
        return "parks";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("park", new Park());
        model.addAttribute("activePage", "parks");
        return "park-form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("park") Park park, BindingResult bindingResult,
                        Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("activePage", "parks");
            return "park-form";
        }
        try {
            parkService.save(park);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("activePage", "parks");
            return "park-form";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Park saved successfully.");
        return "redirect:/parks";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        Park park = parkService.findById(id);
        model.addAttribute("park", park);
        model.addAttribute("zones", zoneService.findByParkId(id));
        model.addAttribute("tasks", maintenanceService.findByParkId(id));
        model.addAttribute("alerts", alertService.findActive().stream()
                .filter(a -> a.getPark() != null && a.getPark().getId().equals(id)).toList());
        model.addAttribute("readings", sensorReadingRepository.findTop10ByZonePark_IdOrderByRecordedAtDesc(id));
        model.addAttribute("activePage", "parks");
        return "park-details";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("park", parkService.findById(id));
        model.addAttribute("activePage", "parks");
        return "park-form";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("park") Park park,
                          BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        park.setId(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("activePage", "parks");
            return "park-form";
        }
        try {
            parkService.update(id, park);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("activePage", "parks");
            return "park-form";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Park updated successfully.");
        return "redirect:/parks/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        parkService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Park deleted successfully.");
        return "redirect:/parks";
    }
}
