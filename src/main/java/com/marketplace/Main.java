package com.marketplace;

import com.marketplace.ui.navigation.SceneNavigator;
import com.marketplace.ui.service.ApiClient;
import com.marketplace.ui.session.UserSession;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * The main entry point of the JavaFX application.
 * Initializes the Spring Boot context and launches the JavaFX UI.
 * Acts as the bridge between the Spring Boot backend and the JavaFX frontend.
 */
public class Main extends Application {

    /** The Spring application context holding all backend beans. */
    private ConfigurableApplicationContext springContext;

    /**
     * Initializes the Spring Boot application context before the JavaFX stage is started.
     * This method runs on the JavaFX Launcher thread and sets up the non-headless Spring context.
     */
    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(MarketplaceApplication.class)
                .headless(false)
                .run();
    }

    /**
     * Starts the JavaFX application by retrieving the necessary Spring beans,
     * creating the SceneNavigator, and displaying the login screen.
     *
     * @param stage the primary stage for this application, onto which the login scene is set
     */
    @Override
    public void start(Stage stage) {
        ApiClient apiClient = springContext.getBean(ApiClient.class);
        UserSession session = springContext.getBean(UserSession.class);
        SceneNavigator navigator = new SceneNavigator(stage, apiClient, session);
        navigator.showLogin();
    }

    /**
     * Called when the application is stopped. Closes the Spring context
     * and exits the JavaFX platform.
     */
    @Override
    public void stop() {
        if (springContext != null) {
            springContext.close();
        }
        Platform.exit();
    }

    /**
     * The main method that launches the JavaFX application.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        launch(args);
    }
}