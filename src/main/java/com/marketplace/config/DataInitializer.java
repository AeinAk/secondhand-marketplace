package com.marketplace.config;

import com.marketplace.backend.entity.Category;
import com.marketplace.backend.entity.City;
import com.marketplace.backend.entity.User;
import com.marketplace.backend.entity.UserRole;
import com.marketplace.backend.repository.CategoryRepository;
import com.marketplace.backend.repository.CityRepository;
import com.marketplace.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CityRepository cityRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           CityRepository cityRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.cityRepository = cityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        seedCategories();
        seedCities();
    }

    private void seedUsers() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@marketplace.local");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("System Admin");
            admin.setRole(UserRole.ADMIN);
            userRepository.save(admin);
        }
        if (userRepository.findByUsername("alice").isEmpty()) {
            User user = new User();
            user.setUsername("alice");
            user.setEmail("alice@marketplace.local");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setFullName("Alice Seller");
            user.setRole(UserRole.USER);
            userRepository.save(user);
        }
        if (userRepository.findByUsername("bob").isEmpty()) {
            User user = new User();
            user.setUsername("bob");
            user.setEmail("bob@marketplace.local");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setFullName("Bob Buyer");
            user.setRole(UserRole.USER);
            userRepository.save(user);
        }
    }

    private void seedCategories() {
        createCategoryIfMissing("Electronics", "Phones, laptops, and gadgets");
        createCategoryIfMissing("Furniture", "Home and office furniture");
        createCategoryIfMissing("Clothing", "New and used apparel");
        createCategoryIfMissing("Books", "Textbooks and novels");
        createCategoryIfMissing("Sports", "Sports equipment and gear");
    }

    private void seedCities() {
        createCityIfMissing("Tehran", "Tehran");
        createCityIfMissing("Isfahan", "Isfahan");
        createCityIfMissing("Shiraz", "Fars");
        createCityIfMissing("Tabriz", "East Azerbaijan");
        createCityIfMissing("Mashhad", "Razavi Khorasan");
    }

    private void createCategoryIfMissing(String name, String description) {
        if (categoryRepository.findByNameIgnoreCase(name).isEmpty()) {
            Category category = new Category();
            category.setName(name);
            category.setDescription(description);
            categoryRepository.save(category);
        }
    }

    private void createCityIfMissing(String name, String province) {
        if (cityRepository.findByNameIgnoreCase(name).isEmpty()) {
            City city = new City();
            city.setName(name);
            city.setProvince(province);
            cityRepository.save(city);
        }
    }
}
