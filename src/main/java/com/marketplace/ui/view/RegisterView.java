package com.marketplace.ui.view;

import com.marketplace.backend.dto.RegisterRequest;
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

/**
 * View that displays the registration screen for new users.
 * Provides input fields for username, email, full name, phone number,
 * and password, along with a registration button and a link to return
 * to the login screen. Upon successful registration, the user is
 * automatically logged in and redirected to the listings view.
 */
public class RegisterView {

    /** The scene navigator used to switch views after registration. */
    private final SceneNavigator navigator;

    /** The API client for sending registration requests. */
    private final ApiClient apiClient;

    /** The user session that will store authentication data upon successful registration. */
    private final UserSession session;

    /**
     * Constructs a RegisterView with the required dependencies.
     *
     * @param navigator the scene navigator
     * @param apiClient the API client
     * @param session   the user session
     */
    public RegisterView(SceneNavigator navigator, ApiClient apiClient, UserSession session) {
        this.navigator = navigator;
        this.apiClient = apiClient;
        this.session = session;
    }

    /**
     * Builds and returns the main UI for the registration view.
     * The view consists of a centered card with fields for user details,
     * a register button, and a link to navigate back to the login screen.
     *
     * @return the root BorderPane containing the registration interface
     */
    public BorderPane build() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");

        Label title = new Label("Create Account");
        title.getStyleClass().add("app-title");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Full name");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password (min 6 chars)");
        Label statusLabel = new Label();

        Button registerButton = new Button("Register");
        registerButton.getStyleClass().add("primary-button");
        registerButton.setOnAction(e -> {
            RegisterRequest request = new RegisterRequest();
            request.setUsername(usernameField.getText().trim());
            request.setEmail(emailField.getText().trim());
            request.setFullName(fullNameField.getText().trim());
            request.setPhone(phoneField.getText().trim());
            request.setPassword(passwordField.getText());
            UiTasks.runAsync(statusLabel, () -> apiClient.register(request), auth -> {
                session.applyAuth(auth);
                UiTasks.showInfo("Success", "Registration successful");
                navigator.showListings();
            }, ex -> statusLabel.setText(apiClient.extractErrorMessage(ex)));
        });

        Hyperlink backLink = new Hyperlink("Back to login");
        backLink.setOnAction(e -> navigator.showLogin());

        VBox form = new VBox(10, title, usernameField, emailField, fullNameField, phoneField, passwordField,
                registerButton, backLink, statusLabel);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(30));
        form.getStyleClass().add("card");
        form.setMaxWidth(420);

        VBox center = new VBox(form);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(40));
        root.setCenter(center);
        return root;
    }
}