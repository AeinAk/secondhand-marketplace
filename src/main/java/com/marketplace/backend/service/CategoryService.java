package com.marketplace.backend.service;

import com.marketplace.backend.dto.CategoryDto;
import com.marketplace.backend.entity.Category;
import com.marketplace.backend.exception.BusinessException;
import com.marketplace.backend.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing product categories.
 * <p>
 * Provides business logic for CRUD operations on categories, including
 * retrieving all categories, creating new ones, updating existing ones,
 * and deleting categories. This service ensures data integrity by validating
 * that category names are unique and that referenced categories exist.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Constructs a CategoryService with the required repository.
     *
     * @param categoryRepository the repository for category data access
     */
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Retrieves all categories.
     * <p>
     * Returns a list of all categories in the system, mapped to DTO objects.
     * </p>
     *
     * @return a list of {@link CategoryDto} representing all categories
     */
    public List<CategoryDto> getAll() {
        return categoryRepository.findAll().stream().map(this::toDto).toList();
    }

    /**
     * Creates a new category.
     * <p>
     * Validates that the category name is unique. If the name already exists,
     * a {@link BusinessException} is thrown. The category is then saved with
     * the provided name and optional description.
     * </p>
     *
     * @param name        the name of the new category (must be unique)
     * @param description an optional description of the category
     * @return the created {@link CategoryDto}
     * @throws BusinessException if a category with the same name already exists
     */
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

    /**
     * Updates an existing category.
     * <p>
     * Finds the category by its ID, updates its name and description,
     * and saves the changes. If the category does not exist, a
     * {@link BusinessException} is thrown.
     * </p>
     *
     * @param id          the unique identifier of the category to update
     * @param name        the new name for the category
     * @param description the new description for the category
     * @return the updated {@link CategoryDto}
     * @throws BusinessException if the category does not exist
     */
    @Transactional
    public CategoryDto update(Long id, String name, String description) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Category not found"));
        category.setName(name);
        category.setDescription(description);
        return toDto(categoryRepository.save(category));
    }

    /**
     * Deletes a category by its ID.
     * <p>
     * If the category does not exist, a {@link BusinessException} is thrown.
     * Note that categories referenced by existing listings cannot be deleted
     * due to database referential integrity constraints.
     * </p>
     *
     * @param id the unique identifier of the category to delete
     * @throws BusinessException if the category does not exist
     */
    @Transactional
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new BusinessException("Category not found");
        }
        categoryRepository.deleteById(id);
    }

    /**
     * Retrieves a category by its ID.
     * <p>
     * This method is used internally to fetch category entities for other
     * service operations. If the category does not exist, a
     * {@link BusinessException} is thrown.
     * </p>
     *
     * @param id the unique identifier of the category
     * @return the {@link Category} entity
     * @throws BusinessException if the category does not exist
     */
    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Category not found"));
    }

    /**
     * Converts a Category entity to a CategoryDto.
     * <p>
     * This method maps the entity fields to a DTO for safe data transfer
     * to the frontend, excluding any internal JPA-related fields.
     * </p>
     *
     * @param category the Category entity to convert
     * @return the corresponding {@link CategoryDto}
     */
    public CategoryDto toDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }
}