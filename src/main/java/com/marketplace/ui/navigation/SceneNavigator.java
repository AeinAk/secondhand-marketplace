package com.marketplace.ui.navigation;

import com.marketplace.ui.service.ApiClient;
import com.marketplace.ui.session.UserSession;
import com.marketplace.ui.view.AdminPanelView;
import com.marketplace.ui.view.ConversationsView;
import com.marketplace.ui.view.CreateListingView;
import com.marketplace.ui.view.FavoritesView;
import com.marketplace.ui.view.ListingDetailView;
import com.marketplace.ui.view.ListingsView;
import com.marketplace.ui.view.LoginView;
import com.marketplace.ui.view.MyListingsView;
import com.marketplace.ui.view.RegisterView;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Manages navigation between different views (scenes) in the JavaFX application.
 * Acts as the central controller for switching the current view and holds the
 * primary stage, API client, and user session.
 */
public class SceneNavigator {

    /** The primary stage (window) of the application. */
    private final Stage stage;

    /** The API client used for backend communication. */
    private final ApiClient apiClient;

    /** The current user session holding authentication and user data. */
    private final UserSession session;

    /**
     * Constructs a new SceneNavigator.
     *
     * @param stage the primary application stage
     * @param apiClient the client for backend API calls
     * @param session the current user session
     */
    public SceneNavigator(Stage stage, ApiClient apiClient, UserSession session) {
        this.stage = stage;
        this.apiClient = apiClient;
        this.session = session;
        stage.setTitle("Second-Hand Marketplace");
        stage.setMinWidth(980);
        stage.setMinHeight(640);
    }

    /** Navigates to the login view. */
    public void showLogin() {
        setScene(new LoginView(this, apiClient, session).build());
    }

    /** Navigates to the registration view. */
    public void showRegister() {
        setScene(new RegisterView(this, apiClient, session).build());
    }

    /** Navigates to the main listings view. */
    public void showListings() {
        setScene(new ListingsView(this, apiClient, session).build());
    }

    /** Navigates to the view for creating a new listing. */
    public void showCreateListing() {
        setScene(new CreateListingView(this, apiClient, session, null).build());
    }

    /**
     * Navigates to the view for editing an existing listing.
     *
     * @param listing the ListingDto containing the data to be edited
     */
    public void showEditListing(com.marketplace.backend.dto.ListingDto listing) {
        setScene(new CreateListingView(this, apiClient, session, listing).build());
    }

    /**
     * Navigates to the detail view of a specific listing.
     *
     * @param listingId the unique identifier of the listing to display
     */
    public void showListingDetail(Long listingId) {
        setScene(new ListingDetailView(this, apiClient, session, listingId).build());
    }

    /** Navigates to the view showing the current user's own listings. */
    public void showMyListings() {
        setScene(new MyListingsView(this, apiClient, session).build());
    }

    /** Navigates to the view showing the current user's favorite listings. */
    public void showFavorites() {
        setScene(new FavoritesView(this, apiClient, session).build());
    }

    /** Navigates to the conversations overview. */
    public void showConversations() {
        setScene(new ConversationsView(this, apiClient, session, null).build());
    }

    /**
     * Navigates to a specific conversation thread.
     *
     * @param conversationId the unique identifier of the conversation to open
     */
    public void showConversation(Long conversationId) {
        setScene(new ConversationsView(this, apiClient, session, conversationId).build());
    }

    /** Navigates to the administrator panel view. */
    public void showAdminPanel() {
        setScene(new AdminPanelView(this, apiClient, session).build());
    }

    /** Clears the user session and navigates back to the login screen. */
    public void logout() {
        session.clear();
        showLogin();
    }

    /**
     * Sets or updates the current scene's root node. If no scene exists,
     * creates a new one; otherwise, replaces the root of the existing scene.
     *
     * @param root the Parent node to set as the root of the scene
     */
    private void setScene(javafx.scene.Parent root) {
        Scene scene = stage.getScene();
        if (scene == null) {
            scene = new Scene(root, 1100, 720);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            stage.setScene(scene);
        } else {
            scene.setRoot(root);
        }
        stage.show();
    }
}