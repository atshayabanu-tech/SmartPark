package com.park.controller;

import com.park.config.IrrigationThresholds;
import com.park.model.SensorReading;
import com.park.service.SensorService;
import com.park.service.ZoneService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/sensors")
public class SensorController {

    private final SensorService sensorService;
    private final ZoneService zoneService;
    private final IrrigationThresholds thresholds;

    public SensorController(SensorService sensorService, ZoneService zoneService, IrrigationThresholds thresholds) {
        this.sensorService = sensorService;
        this.zoneService = zoneService;
        this.thresholds = thresholds;
    }

    private void populate(Model model) {
        model.addAttribute("readings", sensorService.findAll());
        model.addAttribute("zones", zoneService.findAll());
        model.addAttribute("tempThreshold", thresholds.getHighTemperatureThresholdC());
        model.addAttribute("rainThreshold", thresholds.getRainfallThresholdMm());
        model.addAttribute("activePage", "sensors");
    }

    @GetMapping
    public String list(Model model) {
        populate(model);
        model.addAttribute("reading", new SensorReading());
        return "sensors";
    }

    @GetMapping("/new")
    public String newForm(@RequestParam(required = false) Long zoneId, Model model) {
        SensorReading reading = new SensorReading();
        if (zoneId != null) {
            reading.setZone(zoneService.findById(zoneId));
        }
        populate(model);
        model.addAttribute("reading", reading);
        return "sensors";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("reading") SensorReading reading, BindingResult bindingResult,
                        Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populate(model);
            return "sensors";
        }
        sensorService.recordReading(reading);
        redirectAttributes.addFlashAttribute("successMessage", "Sensor reading recorded successfully.");
        return "redirect:/sensors";
    }
}
