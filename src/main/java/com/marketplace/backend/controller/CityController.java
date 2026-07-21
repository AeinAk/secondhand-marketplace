package com.marketplace.backend.controller;

import com.marketplace.backend.dto.CityDto;
import com.marketplace.backend.service.CityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    public List<CityDto> getAll() {
        return cityService.getAll();
    }

    @PostMapping
    public CityDto create(@RequestBody Map<String, String> body) {
        return cityService.create(body.get("name"), body.get("province"));
    }
}
