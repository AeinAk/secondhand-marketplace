package com.marketplace.ui.view;

import com.marketplace.backend.dto.CategoryDto;
import com.marketplace.backend.dto.CityDto;
import com.marketplace.backend.dto.ListingDto;
import com.marketplace.ui.navigation.SceneNavigator;
import com.marketplace.ui.service.ApiClient;
import com.marketplace.ui.session.UserSession;
import com.marketplace.ui.util.UiTasks;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CreateListingView {

    private final SceneNavigator navigator;
    private final ApiClient apiClient;
    private final UserSession session;
    private final ListingDto existing;

    public CreateListingView(SceneNavigator navigator, ApiClient apiClient, UserSession session, ListingDto existing) {
        this.navigator = navigator;
        this.apiClient = apiClient;
        this.session = session;
        this.existing = existing;
    }

    public BorderPane build() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");
        root.setTop(AppToolbar.create(navigator, apiClient, session,
                existing == null ? "Create Listing" : "Edit Listing"));

        if (!session.isLoggedIn()) {
            Label loginRequired = new Label("Please login to manage Advertisements.");
            loginRequired.setPadding(new Insets(40));
            root.setCenter(loginRequired);
            return root;
        }

        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Description");
        descriptionArea.setPrefRowCount(4);
        TextField priceField = new TextField();
        priceField.setPromptText("Price");
        TextField specsField = new TextField();
        specsField.setPromptText("Specifications (e.g. color, size, model)");
        ComboBox<CategoryDto> categoryBox = new ComboBox<>();
        ComboBox<CityDto> cityBox = new ComboBox<>();
        Label imageLabel = new Label("No images selected");
        Label statusLabel = new Label();
        List<Path> selectedImages = new ArrayList<>();

        if (existing != null) {
            titleField.setText(existing.getTitle());
            descriptionArea.setText(existing.getDescription());
            priceField.setText(existing.getPrice().toPlainString());
            specsField.setText(existing.getSpecifications());
        }

        Button chooseImagesBtn = new Button("Choose Images");
        chooseImagesBtn.getStyleClass().add("secondary-button");
        chooseImagesBtn.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.webp"));
            List<File> files = chooser.showOpenMultipleDialog(root.getScene().getWindow());
            if (files != null && !files.isEmpty()) {
                selectedImages.clear();
                files.forEach(file -> selectedImages.add(file.toPath()));
                imageLabel.setText(files.size() + " image(s) selected");
            }
        });

        Button saveBtn = new Button(existing == null ? "Submit for Review" : "Update Advertisement");
        saveBtn.getStyleClass().add("primary-button");
        saveBtn.setOnAction(e -> {
            if (categoryBox.getValue() == null || cityBox.getValue() == null) {
                statusLabel.setText("Category and city are required");
                return;
            }
            ListingDto dto = new ListingDto();
            dto.setTitle(titleField.getText().trim());
            dto.setDescription(descriptionArea.getText().trim());
            dto.setPrice(new BigDecimal(priceField.getText().trim()));
            dto.setSpecifications(specsField.getText().trim());
            dto.setCategoryId(categoryBox.getValue().getId());
            dto.setCityId(cityBox.getValue().getId());

            UiTasks.runAsync(statusLabel, () -> existing == null
                            ? apiClient.createListing(dto, selectedImages)
                            : apiClient.updateListing(existing.getId(), dto, selectedImages),
                    saved -> {
                        UiTasks.showInfo("Success", existing == null
                                ? "Advertisement submitted and pending admin approval"
                                : "Advertisement updated");
                        navigator.showMyListings();
                    }, ex -> statusLabel.setText(apiClient.extractErrorMessage(ex)));
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(20));
        form.add(new Label("Title"), 0, 0);
        form.add(titleField, 1, 0);
        form.add(new Label("Description"), 0, 1);
        form.add(descriptionArea, 1, 1);
        form.add(new Label("Price"), 0, 2);
        form.add(priceField, 1, 2);
        form.add(new Label("Specifications"), 0, 3);
        form.add(specsField, 1, 3);
        form.add(new Label("Category"), 0, 4);
        form.add(categoryBox, 1, 4);
        form.add(new Label("City"), 0, 5);
        form.add(cityBox, 1, 5);
        form.add(new Label("Images"), 0, 6);
        form.add(new VBox(6, chooseImagesBtn, imageLabel), 1, 6);
        form.add(saveBtn, 1, 7);

        VBox center = new VBox(10, form, statusLabel);
        center.setPadding(new Insets(10, 20, 20, 20));
        root.setCenter(center);

        UiTasks.runAsync(statusLabel, () -> {
            List<CategoryDto> categories = apiClient.getCategories();
            List<CityDto> cities = apiClient.getCities();
            return new Object[] { categories, cities };
        }, result -> {
            List<CategoryDto> categories = (List<CategoryDto>) result[0];
            List<CityDto> cities = (List<CityDto>) result[1];
            categoryBox.setItems(FXCollections.observableArrayList(categories));
            cityBox.setItems(FXCollections.observableArrayList(cities));
            if (existing != null) {
                categories.stream().filter(c -> c.getId().equals(existing.getCategoryId())).findFirst()
                        .ifPresent(c -> categoryBox.getSelectionModel().select(c));
                cities.stream().filter(c -> c.getId().equals(existing.getCityId())).findFirst()
                        .ifPresent(c -> cityBox.getSelectionModel().select(c));
            }
        }, ex -> statusLabel.setText(apiClient.extractErrorMessage(ex)));

        return root;
    }
}
