package com.park.controller;

import com.park.model.IrrigationZone;
import com.park.service.ParkService;
import com.park.service.ZoneService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/zones")
public class ZoneController {

    private final ZoneService zoneService;
    private final ParkService parkService;

    public ZoneController(ZoneService zoneService, ParkService parkService) {
        this.zoneService = zoneService;
        this.parkService = parkService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String search,
                        @RequestParam(required = false) Long parkId,
                        @RequestParam(required = false) String irrigationStatus,
                        @RequestParam(required = false) String plantType,
                        @RequestParam(required = false) String soilType,
                        Model model) {
        model.addAttribute("zones", zoneService.search(search, parkId, irrigationStatus, plantType, soilType));
        model.addAttribute("parks", parkService.findAll());
        model.addAttribute("search", search);
        model.addAttribute("parkId", parkId);
        model.addAttribute("irrigationStatus", irrigationStatus);
        model.addAttribute("plantType", plantType);
        model.addAttribute("soilType", soilType);
        model.addAttribute("activePage", "zones");
        return "zones";
    }

    @GetMapping("/new")
    public String newForm(@RequestParam(required = false) Long parkId, Model model) {
        IrrigationZone zone = new IrrigationZone();
        if (parkId != null) {
            zone.setPark(parkService.findById(parkId));
        }
        model.addAttribute("zone", zone);
        model.addAttribute("parks", parkService.findAll());
        model.addAttribute("activePage", "zones");
        return "zone-form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("zone") IrrigationZone zone, BindingResult bindingResult,
                        Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("parks", parkService.findAll());
            model.addAttribute("activePage", "zones");
            return "zone-form";
        }
        try {
            zoneService.save(zone);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("parks", parkService.findAll());
            model.addAttribute("activePage", "zones");
            return "zone-form";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Irrigation zone saved successfully.");
        return "redirect:/zones";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        return "redirect:/zones/" + id + "/edit";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("zone", zoneService.findById(id));
        model.addAttribute("parks", parkService.findAll());
        model.addAttribute("activePage", "zones");
        return "zone-form";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("zone") IrrigationZone zone,
                          BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        zone.setId(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("parks", parkService.findAll());
            model.addAttribute("activePage", "zones");
            return "zone-form";
        }
        try {
            zoneService.update(id, zone);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("parks", parkService.findAll());
            model.addAttribute("activePage", "zones");
            return "zone-form";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Irrigation zone updated successfully.");
        return "redirect:/zones";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        zoneService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Irrigation zone deleted successfully.");
        return "redirect:/zones";
    }
}
