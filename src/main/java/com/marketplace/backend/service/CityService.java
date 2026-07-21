package com.marketplace.backend.service;

import com.marketplace.backend.dto.CityDto;
import com.marketplace.backend.entity.City;
import com.marketplace.backend.exception.BusinessException;
import com.marketplace.backend.repository.CityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CityService {

    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public List<CityDto> getAll() {
        return cityRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional
    public CityDto create(String name, String province) {
        if (cityRepository.findByNameIgnoreCase(name).isPresent()) {
            throw new BusinessException("City already exists");
        }
        City city = new City();
        city.setName(name);
        city.setProvince(province);
        return toDto(cityRepository.save(city));
    }

    public City getById(Long id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new BusinessException("City not found"));
    }

    public CityDto toDto(City city) {
        CityDto dto = new CityDto();
        dto.setId(city.getId());
        dto.setName(city.getName());
        dto.setProvince(city.getProvince());
        return dto;
    }
}
