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

/**
 * Initializes the database with seed data on application startup.
 * <p>
 * This component implements {@link CommandLineRunner} to automatically populate
 * the database with default users, categories, and cities when the application
 * starts for the first time. It ensures that essential reference data and test
 * accounts are available for immediate use without manual setup.
 * </p>
 * <p>
 * The seed data includes:
 * <ul>
 *   <li>Administrator account (admin/admin123)</li>
 *   <li>Regular user accounts (alice/user123, bob/user123)</li>
 *   <li>Five default categories (Electronics, Furniture, Clothing, Books, Sports)</li>
 *   <li>Five major Iranian cities (Tehran, Isfahan, Shiraz, Tabriz, Mashhad)</li>
 * </ul>
 * All operations are idempotent – data is only inserted if it does not already exist.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CityRepository cityRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs a DataInitializer with the required repositories and encoder.
     *
     * @param userRepository     the repository for user data access
     * @param categoryRepository the repository for category data access
     * @param cityRepository     the repository for city data access
     * @param passwordEncoder    the encoder for hashing passwords
     */
    public DataInitializer(UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           CityRepository cityRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.cityRepository = cityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Executes the data seeding process when the application starts.
     * <p>
     * This method is automatically invoked by Spring Boot after the application
     * context is fully initialized. It sequentially seeds users, categories, and cities.
     * </p>
     *
     * @param args command-line arguments (not used)
     */
    @Override
    public void run(String... args) {
        seedUsers();
        seedCategories();
        seedCities();
    }

    /**
     * Seeds the database with default users.
     * <p>
     * Creates three users if they do not already exist:
     * <ul>
     *   <li>An administrator with username {@code admin} and password {@code admin123}</li>
     *   <li>A regular user with username {@code alice} and password {@code user123}</li>
     *   <li>A regular user with username {@code bob} and password {@code user123}</li>
     * </ul>
     * All passwords are encoded using the configured {@link PasswordEncoder}.
     * </p>
     */
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

    /**
     * Seeds the database with default categories.
     * <p>
     * Creates five default categories if they do not already exist:
     * Electronics, Furniture, Clothing, Books, and Sports.
     * Each category is created with a descriptive text.
     * </p>
     *
     * @see #createCategoryIfMissing(String, String)
     */
    private void seedCategories() {
        createCategoryIfMissing("Electronics", "Phones, laptops, and gadgets");
        createCategoryIfMissing("Furniture", "Home and office furniture");
        createCategoryIfMissing("Clothing", "New and used apparel");
        createCategoryIfMissing("Books", "Textbooks and novels");
        createCategoryIfMissing("Sports", "Sports equipment and gear");
    }

    /**
     * Seeds the database with default cities.
     * <p>
     * Creates five major Iranian cities if they do not already exist:
     * Tehran, Isfahan, Shiraz, Tabriz, and Mashhad.
     * Each city is associated with its respective province.
     * </p>
     *
     * @see #createCityIfMissing(String, String)
     */
    private void seedCities() {
        createCityIfMissing("Tehran", "Tehran");
        createCityIfMissing("Isfahan", "Isfahan");
        createCityIfMissing("Shiraz", "Fars");
        createCityIfMissing("Mashhad", "Razavi Khorasan");
        createCityIfMissing("Tabriz", "East Azerbaijan");
        createCityIfMissing("Karaj", "Alborz");
        createCityIfMissing("Ahvaz", "Khuzestan");
        createCityIfMissing("Rasht", "Gilan");
        createCityIfMissing("Kerman", "Kerman");
        createCityIfMissing("Yazd", "Yazd");
        createCityIfMissing("Ardabil", "Ardabil");
        createCityIfMissing("Urmia", "West Azerbaijan");
        createCityIfMissing("Ilam", "Ilam");
        createCityIfMissing("Bushehr", "Bushehr");
        createCityIfMissing("Shahr-e Kord", "Chaharmahal and Bakhtiari");
        createCityIfMissing("Birjand", "South Khorasan");
        createCityIfMissing("Bojnord", "North Khorasan");
        createCityIfMissing("Zahedan", "Sistan and Baluchestan");
        createCityIfMissing("Qazvin", "Qazvin");
        createCityIfMissing("Qom", "Qom");
        createCityIfMissing("Sanandaj", "Kurdistan");
        createCityIfMissing("Kermanshah", "Kermanshah");
        createCityIfMissing("Yasuj", "Kohgiluyeh and Boyer-Ahmad");
        createCityIfMissing("Gorgan", "Golestan");
        createCityIfMissing("Khorramabad", "Lorestan");
        createCityIfMissing("Sari", "Mazandaran");
        createCityIfMissing("Arak", "Markazi");
        createCityIfMissing("Bandar Abbas", "Hormozgan");
        createCityIfMissing("Hamedan", "Hamedan");
        createCityIfMissing("Zanjan", "Zanjan");
        createCityIfMissing("Semnan", "Semnan");
    }

    /**
     * Creates a new category if it does not already exist.
     * <p>
     * Checks the database for a category with the given name (case-insensitive).
     * If none exists, a new category is created with the provided name and description.
     * </p>
     *
     * @param name        the category name (must be unique)
     * @param description the category description
     */
    private void createCategoryIfMissing(String name, String description) {
        if (categoryRepository.findByNameIgnoreCase(name).isEmpty()) {
            Category category = new Category();
            category.setName(name);
            category.setDescription(description);
            categoryRepository.save(category);
        }
    }

    /**
     * Creates a new city if it does not already exist.
     * <p>
     * Checks the database for a city with the given name (case-insensitive).
     * If none exists, a new city is created with the provided name and province.
     * </p>
     *
     * @param name     the city name (must be unique)
     * @param province the province or state the city belongs to
     */
    private void createCityIfMissing(String name, String province) {
        if (cityRepository.findByNameIgnoreCase(name).isEmpty()) {
            City city = new City();
            city.setName(name);
            city.setProvince(province);
            cityRepository.save(city);
        }
    }
}