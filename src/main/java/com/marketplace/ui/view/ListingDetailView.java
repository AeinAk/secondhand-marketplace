package com.marketplace.ui.view;

import com.marketplace.backend.dto.ConversationDto;
import com.marketplace.backend.dto.ListingDto;
import com.marketplace.backend.dto.RatingRequest;
import com.marketplace.backend.dto.SellerRatingDto;
import com.marketplace.ui.navigation.SceneNavigator;
import com.marketplace.ui.service.ApiClient;
import com.marketplace.ui.session.UserSession;
import com.marketplace.ui.util.UiTasks;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.awt.*;

import java.util.List;

public class ListingDetailView {

    private final SceneNavigator navigator;
    private final ApiClient apiClient;
    private final UserSession session;
    private final Long listingId;

    public ListingDetailView(SceneNavigator navigator, ApiClient apiClient, UserSession session, Long listingId) {
        this.navigator = navigator;
        this.apiClient = apiClient;
        this.session = session;
        this.listingId = listingId;
    }

    public BorderPane build() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");
        root.setTop(AppToolbar.create(navigator, apiClient, session, "Advertisement Details"));

        VBox content = new VBox(12);
        content.setPadding(new Insets(20));
        Label statusLabel = new Label("Loading advertisement...");
        content.getChildren().add(statusLabel);
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        root.setCenter(scrollPane);

        UiTasks.runAsync(statusLabel, () -> apiClient.getListing(listingId), listing ->
                renderListing(content, statusLabel, listing),
                ex -> statusLabel.setText(apiClient.extractErrorMessage(ex)));

        return root;
    }

    private void renderListing(VBox content, Label statusLabel, ListingDto listing) {
        content.getChildren().clear();

        Label title = new Label(listing.getTitle());
        title.getStyleClass().add("app-title");
        Label price = new Label("Price: " + listing.getPrice());
        Label meta = new Label("Category: " + listing.getCategoryName() + " | City: " + listing.getCityName()
                + " | Seller: " + listing.getSellerUsername() + " | Status: " + listing.getStatus());
        Label description = new Label(listing.getDescription());
        description.setWrapText(true);
        Label specs = new Label("Specifications: " + (listing.getSpecifications() == null ? "-" : listing.getSpecifications()));
        specs.setWrapText(true);

        content.getChildren().addAll(title, price, meta, new Separator(), description, specs);

        if (listing.getImageUrls() != null && !listing.getImageUrls().isEmpty()) {
            Label imagesTitle = new Label("Images:");
            VBox imagesBox = new VBox(8);
            for (String url : listing.getImageUrls()) {
                String fullUrl = ApiClient.BASE_URL + url;
                Image image = new Image(fullUrl, true);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(300);
                imageView.setFitHeight(300);
                imageView.setPreserveRatio(true);
                imageView.setOnMouseClicked(e -> {});
                imagesBox.getChildren().add(imageView);
            }
            content.getChildren().addAll(imagesTitle, imagesBox);
        }

        HBox actions = new HBox(10);
        if (session.isLoggedIn()) {
            Button favoriteBtn = new Button(listing.isFavorite() ? "Remove Favorite" : "Add Favorite");
            favoriteBtn.getStyleClass().add("secondary-button");
            favoriteBtn.setOnAction(e -> UiTasks.runAsync(statusLabel, () -> {
                if (listing.isFavorite()) {
                    apiClient.removeFavorite(listing.getId());
                } else {
                    apiClient.addFavorite(listing.getId());
                }
                return apiClient.getListing(listing.getId());
            }, refreshed -> renderListing(content, statusLabel, refreshed),
                    ex -> statusLabel.setText(apiClient.extractErrorMessage(ex))));
            actions.getChildren().add(favoriteBtn);

            if (!session.getUserId().equals(listing.getSellerId())) {
                Button messageBtn = new Button("Message Seller");
                messageBtn.getStyleClass().add("primary-button");
                messageBtn.setOnAction(e -> UiTasks.runAsync(statusLabel, () -> apiClient.startConversation(listing.getId()),
                        (ConversationDto conversation) -> navigator.showConversation(conversation.getId()),
                        ex -> statusLabel.setText(apiClient.extractErrorMessage(ex))));
                actions.getChildren().add(messageBtn);

                Spinner<Integer> ratingSpinner = new Spinner<>(1, 5, 5);
                TextArea reviewArea = new TextArea();
                reviewArea.setPromptText("Write a review (optional)");
                reviewArea.setPrefRowCount(3);
                Button rateBtn = new Button("Rate Seller");
                rateBtn.getStyleClass().add("secondary-button");
                rateBtn.setOnAction(e -> {
                    RatingRequest request = new RatingRequest();
                    request.setListingId(listing.getId());
                    request.setRating(ratingSpinner.getValue());
                    request.setReviewText(reviewArea.getText().trim());
                    UiTasks.runAsync(statusLabel, () -> {
                        apiClient.rateSeller(request);
                        return apiClient.getListing(listing.getId());
                    }, (ListingDto updatedListing) -> {
                        UiTasks.showInfo("Thanks", "Your rating was submitted");
                        renderListing(content, statusLabel, updatedListing);
                    }, ex -> {
                        statusLabel.setText(apiClient.extractErrorMessage(ex));
                        ex.printStackTrace();
                    });
                });
                actions.getChildren().addAll(new Label("Rating:"), ratingSpinner, reviewArea, rateBtn);
            }
        }
        content.getChildren().add(actions);

        UiTasks.runAsync(null, () -> apiClient.getSellerRatings(listing.getSellerId()),
                (List<SellerRatingDto> ratings) -> {
                    if (!ratings.isEmpty()) {
                        Label ratingsTitle = new Label("Reviews");
                        ratingsTitle.getStyleClass().add("subtitle");
                        content.getChildren().add(ratingsTitle);
                        for (SellerRatingDto rating : ratings) {
                            Label review = new Label(rating.getReviewerUsername() + " - " + rating.getRating() + "/5: "
                                    + (rating.getReviewText() == null ? "" : rating.getReviewText()));
                            review.setWrapText(true);
                            content.getChildren().add(review);
                        }
                    }
                }, ex -> {
                });
    }
}
