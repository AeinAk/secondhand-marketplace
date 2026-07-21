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

public class SceneNavigator {

    private final Stage stage;
    private final ApiClient apiClient;
    private final UserSession session;

    public SceneNavigator(Stage stage, ApiClient apiClient, UserSession session) {
        this.stage = stage;
        this.apiClient = apiClient;
        this.session = session;
        stage.setTitle("Second-Hand Marketplace");
        stage.setMinWidth(980);
        stage.setMinHeight(640);
    }

    public void showLogin() {
        setScene(new LoginView(this, apiClient, session).build());
    }

    public void showRegister() {
        setScene(new RegisterView(this, apiClient, session).build());
    }

    public void showListings() {
        setScene(new ListingsView(this, apiClient, session).build());
    }

    public void showCreateListing() {
        setScene(new CreateListingView(this, apiClient, session, null).build());
    }

    public void showEditListing(com.marketplace.backend.dto.ListingDto listing) {
        setScene(new CreateListingView(this, apiClient, session, listing).build());
    }

    public void showListingDetail(Long listingId) {
        setScene(new ListingDetailView(this, apiClient, session, listingId).build());
    }

    public void showMyListings() {
        setScene(new MyListingsView(this, apiClient, session).build());
    }

    public void showFavorites() {
        setScene(new FavoritesView(this, apiClient, session).build());
    }

    public void showConversations() {
        setScene(new ConversationsView(this, apiClient, session, null).build());
    }

    public void showConversation(Long conversationId) {
        setScene(new ConversationsView(this, apiClient, session, conversationId).build());
    }

    public void showAdminPanel() {
        setScene(new AdminPanelView(this, apiClient, session).build());
    }

    public void logout() {
        session.clear();
        showLogin();
    }

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
