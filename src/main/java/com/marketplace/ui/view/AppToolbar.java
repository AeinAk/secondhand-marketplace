package com.marketplace.ui.view;

import com.marketplace.ui.navigation.SceneNavigator;
import com.marketplace.ui.service.ApiClient;
import com.marketplace.ui.session.UserSession;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * Utility class that builds the application toolbar (top navigation bar).
 * The toolbar displays the current view title, user information, and action buttons
 * based on the user's authentication state and role (e.g., Browse, New Ad, My Ads,
 * Favorites, Messages, Admin Panel, Login/Logout).
 */
public class AppToolbar {

    /** Private constructor to prevent instantiation of this utility class. */
    private AppToolbar() {
    }

    /**
     * Creates and returns the toolbar HBox with all navigation and action buttons.
     * The toolbar dynamically adapts to the user's login status and administrative
     * privileges.
     *
     * @param navigator   the scene navigator for view switching
     * @param apiClient   the API client (currently unused in this method but retained for consistency)
     * @param session     the current user session to determine logged-in state and role
     * @param titleText   the title text to display on the left side of the toolbar
     * @return an HBox containing the complete toolbar UI
     */
    public static HBox create(SceneNavigator navigator, ApiClient apiClient, UserSession session, String titleText) {
        Label title = new Label(titleText);
        title.getStyleClass().add("app-title");

        HBox buttons = new HBox(8);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Button listingsBtn = new Button("Browse");
        listingsBtn.getStyleClass().add("secondary-button");
        listingsBtn.setOnAction(e -> navigator.showListings());
        buttons.getChildren().add(listingsBtn);

        if (session.isLoggedIn()) {
            Label userLabel = new Label("Hello, " + session.getUsername());
            buttons.getChildren().add(userLabel);

            Button createBtn = new Button("New Advertisement");
            createBtn.getStyleClass().add("primary-button");
            createBtn.setOnAction(e -> navigator.showCreateListing());
            buttons.getChildren().add(createBtn);

            Button myBtn = new Button("My Ads");
            myBtn.getStyleClass().add("secondary-button");
            myBtn.setOnAction(e -> navigator.showMyListings());
            buttons.getChildren().add(myBtn);

            Button favBtn = new Button("Favorites");
            favBtn.getStyleClass().add("secondary-button");
            favBtn.setOnAction(e -> navigator.showFavorites());
            buttons.getChildren().add(favBtn);

            Button msgBtn = new Button("My Messages");
            msgBtn.getStyleClass().add("secondary-button");
            msgBtn.setOnAction(e -> navigator.showConversations());
            buttons.getChildren().add(msgBtn);

            if (session.isAdmin()) {
                Button adminBtn = new Button("Admin Panel");
                adminBtn.getStyleClass().add("primary-button");
                adminBtn.setOnAction(e -> navigator.showAdminPanel());
                buttons.getChildren().add(adminBtn);
            }

            Button logoutBtn = new Button("Logout");
            logoutBtn.getStyleClass().add("secondary-button");
            logoutBtn.setOnAction(e -> navigator.logout());
            buttons.getChildren().add(logoutBtn);
        } else {
            Button loginBtn = new Button("Login");
            loginBtn.getStyleClass().add("primary-button");
            loginBtn.setOnAction(e -> navigator.showLogin());
            buttons.getChildren().add(loginBtn);
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox toolbar = new HBox(16, title, spacer, buttons);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.getStyleClass().add("toolbar");
        toolbar.setPadding(new Insets(12, 20, 12, 20));
        return toolbar;
    }
}