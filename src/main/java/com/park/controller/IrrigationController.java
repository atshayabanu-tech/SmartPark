package com.park.controller;

import com.park.model.IrrigationSchedule;
import com.park.service.IrrigationService;
import com.park.service.ZoneService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/irrigation")
public class IrrigationController {

    private final IrrigationService irrigationService;
    private final ZoneService zoneService;

    public IrrigationController(IrrigationService irrigationService, ZoneService zoneService) {
        this.irrigationService = irrigationService;
        this.zoneService = zoneService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("schedules", irrigationService.findAll());
        model.addAttribute("activePage", "irrigation");
        return "irrigation";
    }

    @GetMapping("/new")
    public String newForm(@RequestParam(required = false) Long zoneId, Model model) {
        IrrigationSchedule schedule = new IrrigationSchedule();
        if (zoneId != null) {
            schedule.setZone(zoneService.findById(zoneId));
        }
        model.addAttribute("schedule", schedule);
        model.addAttribute("zones", zoneService.findAll());
        model.addAttribute("activePage", "irrigation");
        return "irrigation-form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("schedule") IrrigationSchedule schedule, BindingResult bindingResult,
                        Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("zones", zoneService.findAll());
            model.addAttribute("activePage", "irrigation");
            return "irrigation-form";
        }
        irrigationService.save(schedule);
        redirectAttributes.addFlashAttribute("successMessage", "Irrigation schedule saved successfully.");
        return "redirect:/irrigation";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("schedule", irrigationService.findById(id));
        model.addAttribute("zones", zoneService.findAll());
        model.addAttribute("activePage", "irrigation");
        return "irrigation-form";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("schedule") IrrigationSchedule schedule,
                          BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        schedule.setId(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("zones", zoneService.findAll());
            model.addAttribute("activePage", "irrigation");
            return "irrigation-form";
        }
        irrigationService.update(id, schedule);
        redirectAttributes.addFlashAttribute("successMessage", "Irrigation schedule updated successfully.");
        return "redirect:/irrigation";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        irrigationService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Irrigation schedule deleted successfully.");
        return "redirect:/irrigation";
    }

    @PostMapping("/{id}/start")
    public String start(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        irrigationService.start(id);
        redirectAttributes.addFlashAttribute("successMessage", "Irrigation started. Valve is now OPEN.");
        return "redirect:/irrigation";
    }

    @PostMapping("/{id}/complete")
    public String complete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        irrigationService.complete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Irrigation completed. Valve is now CLOSED.");
        return "redirect:/irrigation";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        irrigationService.cancel(id);
        redirectAttributes.addFlashAttribute("successMessage", "Irrigation schedule cancelled.");
        return "redirect:/irrigation";
    }
}
