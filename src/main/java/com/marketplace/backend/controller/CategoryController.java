package com.marketplace.backend.controller;

import com.marketplace.backend.dto.CategoryDto;
import com.marketplace.backend.service.CategoryService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing product categories.
 * <p>
 * Provides endpoints for retrieving, creating, updating, and deleting categories.
 * Category data is used for classifying listings and enabling filtered searches.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Constructs a {@code CategoryController} with the required category service.
     *
     * @param categoryService the service handling category business logic
     */
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Retrieves all available categories.
     *
     * @return a list of {@link CategoryDto} objects representing all categories
     */
    @GetMapping
    public List<CategoryDto> getAll() {
        return categoryService.getAll();
    }

    /**
     * Creates a new category.
     * <p>
     * Expects a JSON object containing the category name and an optional description.
     * The category name must be unique across the system.
     * </p>
     *
     * @param body the request body containing {@code "name"} and optionally {@code "description"} fields
     * @return the created {@link CategoryDto} with its generated ID
     * @throws com.marketplace.backend.exception.BusinessException if a category with the same name already exists
     */
    @PostMapping
    public CategoryDto create(@RequestBody Map<String, String> body) {
        return categoryService.create(body.get("name"), body.get("description"));
    }

    /**
     * Updates an existing category.
     * <p>
     * The category name and description can be updated. The name must remain unique
     * across the system (excluding the current category itself).
     * </p>
     *
     * @param id   the unique identifier of the category to update
     * @param body the request body containing {@code "name"} and optionally {@code "description"} fields
     * @return the updated {@link CategoryDto}
     * @throws com.marketplace.backend.exception.BusinessException if the category does not exist or the name conflicts with another category
     */
    @PutMapping("/{id}")
    public CategoryDto update(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return categoryService.update(id, body.get("name"), body.get("description"));
    }

    /**
     * Deletes a category by its unique identifier.
     * <p>
     * Categories that are referenced by existing listings cannot be deleted
     * (referential integrity constraint). This operation is permanent.
     * </p>
     *
     * @param id the unique identifier of the category to delete
     * @throws com.marketplace.backend.exception.BusinessException if the category is referenced by listings or does not exist
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }
}