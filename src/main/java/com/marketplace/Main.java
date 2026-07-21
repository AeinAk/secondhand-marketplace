package com.marketplace;

import com.marketplace.ui.navigation.SceneNavigator;
import com.marketplace.ui.service.ApiClient;
import com.marketplace.ui.session.UserSession;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class Main extends Application {

    private ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(MarketplaceApplication.class)
                .headless(false)
                .run();
    }

    @Override
    public void start(Stage stage) {
        ApiClient apiClient = springContext.getBean(ApiClient.class);
        UserSession session = springContext.getBean(UserSession.class);
        SceneNavigator navigator = new SceneNavigator(stage, apiClient, session);
        navigator.showLogin();
    }

    @Override
    public void stop() {
        if (springContext != null) {
            springContext.close();
        }
        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
