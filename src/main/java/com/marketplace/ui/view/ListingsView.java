package com.marketplace.ui.view;

import com.marketplace.backend.dto.CategoryDto;
import com.marketplace.backend.dto.CityDto;
import com.marketplace.backend.dto.ListingDto;
import com.marketplace.backend.dto.ListingSearchRequest;
import com.marketplace.ui.navigation.SceneNavigator;
import com.marketplace.ui.service.ApiClient;
import com.marketplace.ui.session.UserSession;
import com.marketplace.ui.util.UiTasks;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.util.List;

public class ListingsView {

    private final SceneNavigator navigator;
    private final ApiClient apiClient;
    private final UserSession session;

    public ListingsView(SceneNavigator navigator, ApiClient apiClient, UserSession session) {
        this.navigator = navigator;
        this.apiClient = apiClient;
        this.session = session;
    }

    public BorderPane build() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");
        root.setTop(AppToolbar.create(navigator, apiClient, session, "Active Advertisements"));

        TextField keywordField = new TextField();
        keywordField.setPromptText("Keyword");
        TextField minPriceField = new TextField();
        minPriceField.setPromptText("Min price");
        TextField maxPriceField = new TextField();
        maxPriceField.setPromptText("Max price");
        TextField specsField = new TextField();
        specsField.setPromptText("Specifications");

        ComboBox<CategoryDto> categoryBox = new ComboBox<>();
        categoryBox.setPromptText("Category");
        ComboBox<CityDto> cityBox = new ComboBox<>();
        cityBox.setPromptText("City");

        Label statusLabel = new Label();

        TableView<ListingDto> table = new TableView<>();
        TableColumn<ListingDto, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(220);
        TableColumn<ListingDto, BigDecimal> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        TableColumn<ListingDto, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        TableColumn<ListingDto, String> cityCol = new TableColumn<>("City");
        cityCol.setCellValueFactory(new PropertyValueFactory<>("cityName"));
        TableColumn<ListingDto, String> sellerCol = new TableColumn<>("Seller");
        sellerCol.setCellValueFactory(new PropertyValueFactory<>("sellerUsername"));
        TableColumn<ListingDto, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button viewBtn = new Button("View");
            {
                viewBtn.getStyleClass().add("secondary-button");
                viewBtn.setOnAction(e -> {
                    ListingDto item = getTableView().getItems().get(getIndex());
                    navigator.showListingDetail(item.getId());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : viewBtn);
            }
        });
        table.getColumns().addAll(titleCol, priceCol, categoryCol, cityCol, sellerCol, actionCol);

        Runnable loadListings = () -> UiTasks.runAsync(statusLabel, () -> {
            ListingSearchRequest search = new ListingSearchRequest();
            search.setKeyword(keywordField.getText().trim());
            search.setSpecifications(specsField.getText().trim());
            if (!minPriceField.getText().isBlank()) {
                search.setMinPrice(new BigDecimal(minPriceField.getText().trim()));
            }
            if (!maxPriceField.getText().isBlank()) {
                search.setMaxPrice(new BigDecimal(maxPriceField.getText().trim()));
            }
            if (categoryBox.getValue() != null) {
                search.setCategoryId(categoryBox.getValue().getId());
            }
            if (cityBox.getValue() != null) {
                search.setCityId(cityBox.getValue().getId());
            }
            boolean hasFilters = !keywordField.getText().isBlank() || !specsField.getText().isBlank()
                    || !minPriceField.getText().isBlank() || !maxPriceField.getText().isBlank()
                    || categoryBox.getValue() != null || cityBox.getValue() != null;
            return hasFilters ? apiClient.searchListings(search) : apiClient.getActiveListings();
        }, listings -> table.setItems(FXCollections.observableArrayList(listings)),
                ex -> statusLabel.setText(apiClient.extractErrorMessage(ex)));

        Button searchBtn = new Button("Search");
        searchBtn.getStyleClass().add("primary-button");
        searchBtn.setOnAction(e -> loadListings.run());
        Button resetBtn = new Button("Reset");
        resetBtn.getStyleClass().add("secondary-button");
        resetBtn.setOnAction(e -> {
            keywordField.clear();
            minPriceField.clear();
            maxPriceField.clear();
            specsField.clear();
            categoryBox.getSelectionModel().clearSelection();
            cityBox.getSelectionModel().clearSelection();
            loadListings.run();
        });

        HBox filters = new HBox(10, keywordField, minPriceField, maxPriceField, specsField, categoryBox, cityBox, searchBtn, resetBtn);
        filters.setPadding(new Insets(16, 20, 8, 20));

        VBox center = new VBox(10, filters, table, statusLabel);
        center.setPadding(new Insets(0, 20, 20, 20));
        VBox.setVgrow(table, javafx.scene.layout.Priority.ALWAYS);
        root.setCenter(center);

        UiTasks.runAsync(statusLabel, () -> {
            List<CategoryDto> categories = apiClient.getCategories();
            List<CityDto> cities = apiClient.getCities();
            return new Object[] { categories, cities };
        }, result -> {
            categoryBox.setItems(FXCollections.observableArrayList((List<CategoryDto>) result[0]));
            cityBox.setItems(FXCollections.observableArrayList((List<CityDto>) result[1]));
            loadListings.run();
        }, ex -> statusLabel.setText(apiClient.extractErrorMessage(ex)));

        return root;
    }
}
