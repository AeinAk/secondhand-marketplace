package com.marketplace.ui.view;

import com.marketplace.backend.dto.LoginRequest;
import com.marketplace.ui.navigation.SceneNavigator;
import com.marketplace.ui.service.ApiClient;
import com.marketplace.ui.session.UserSession;
import com.marketplace.ui.util.UiTasks;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class LoginView {

    private final SceneNavigator navigator;
    private final ApiClient apiClient;
    private final UserSession session;

    public LoginView(SceneNavigator navigator, ApiClient apiClient, UserSession session) {
        this.navigator = navigator;
        this.apiClient = apiClient;
        this.session = session;
    }

    public BorderPane build() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");

        Label title = new Label("Second-Hand Marketplace");
        title.getStyleClass().add("app-title");
        Label subtitle = new Label("Sign in to browse and sell items");
        subtitle.getStyleClass().add("subtitle");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.getStyleClass().add("form-field");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("form-field");

        Label statusLabel = new Label();
        Label backendStatus = new Label("Checking backend...");

        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("primary-button");
        loginButton.setOnAction(e -> {
            LoginRequest request = new LoginRequest();
            request.setUsername(usernameField.getText().trim());
            request.setPassword(passwordField.getText());
            UiTasks.runAsync(statusLabel, () -> apiClient.login(request), auth -> {
                session.applyAuth(auth);
                navigator.showListings();
            }, ex -> statusLabel.setText(apiClient.extractErrorMessage(ex)));
        });

        Hyperlink registerLink = new Hyperlink("Create an account");
        registerLink.setOnAction(e -> navigator.showRegister());

        Hyperlink browseLink = new Hyperlink("Browse listings without login");
        browseLink.setOnAction(e -> navigator.showListings());

        VBox form = new VBox(12, title, subtitle, usernameField, passwordField, loginButton, registerLink, browseLink, statusLabel);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(30));
        form.getStyleClass().add("card");
        form.setMaxWidth(420);

        VBox center = new VBox(form, backendStatus);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(40));
        root.setCenter(center);

        UiTasks.runAsync(null, apiClient::health, health -> {
            backendStatus.setText("Backend: " + health);
            backendStatus.getStyleClass().add("success-label");
        }, ex -> {
            backendStatus.setText("Backend unavailable. Start the app again or check port 8080.");
            backendStatus.getStyleClass().add("error-label");
        });

        return root;
    }
}
