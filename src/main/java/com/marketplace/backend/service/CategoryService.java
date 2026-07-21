package com.marketplace.backend.service;

import com.marketplace.backend.dto.CategoryDto;
import com.marketplace.backend.entity.Category;
import com.marketplace.backend.exception.BusinessException;
import com.marketplace.backend.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDto> getAll() {
        return categoryRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional
    public CategoryDto create(String name, String description) {
        if (categoryRepository.findByNameIgnoreCase(name).isPresent()) {
            throw new BusinessException("Category already exists");
        }
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        return toDto(categoryRepository.save(category));
    }

    @Transactional
    public CategoryDto update(Long id, String name, String description) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Category not found"));
        category.setName(name);
        category.setDescription(description);
        return toDto(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new BusinessException("Category not found");
        }
        categoryRepository.deleteById(id);
    }

    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Category not found"));
    }

    public CategoryDto toDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }
}
