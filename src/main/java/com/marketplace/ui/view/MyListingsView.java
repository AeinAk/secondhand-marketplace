package com.marketplace.ui.view;

import com.marketplace.backend.dto.ListingDto;
import com.marketplace.ui.navigation.SceneNavigator;
import com.marketplace.ui.service.ApiClient;
import com.marketplace.ui.session.UserSession;
import com.marketplace.ui.util.UiTasks;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * View that displays the current user's own listings.
 * Shows a table with title, status, and action buttons for each listing:
 * Edit, Mark as Sold, and Delete. Fetches data from the API upon loading
 * and refreshes after each operation.
 */
public class MyListingsView {

    /** The scene navigator for switching views. */
    private final SceneNavigator navigator;

    /** The API client for fetching and modifying the user's listings. */
    private final ApiClient apiClient;

    /** The current user session. */
    private final UserSession session;

    /**
     * Constructs a MyListingsView with the required dependencies.
     *
     * @param navigator the scene navigator
     * @param apiClient the API client
     * @param session   the user session
     */
    public MyListingsView(SceneNavigator navigator, ApiClient apiClient, UserSession session) {
        this.navigator = navigator;
        this.apiClient = apiClient;
        this.session = session;
    }

    /**
     * Builds and returns the main UI for the "My Listings" view.
     * The view includes a toolbar, a table displaying the user's listings,
     * and a status label for operation feedback. Each row includes Edit,
     * Mark Sold, and Delete buttons.
     *
     * @return the root BorderPane containing the interface
     */
    public BorderPane build() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");
        root.setTop(AppToolbar.create(navigator, apiClient, session, "My Advertisements"));

        Label statusLabel = new Label();
        TableView<ListingDto> table = new TableView<>();

        TableColumn<ListingDto, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<ListingDto, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableColumn<ListingDto, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(225);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button soldBtn = new Button("Mark Sold");
            private final Button deleteBtn = new Button("Delete");
            private final HBox box = new HBox(6, editBtn, soldBtn, deleteBtn);

            {
                editBtn.getStyleClass().add("secondary-button");
                soldBtn.getStyleClass().add("secondary-button");
                deleteBtn.getStyleClass().add("danger-button");
                editBtn.setOnAction(e -> {
                    ListingDto item = getTableView().getItems().get(getIndex());
                    navigator.showEditListing(item);
                });
                soldBtn.setOnAction(e -> {
                    ListingDto item = getTableView().getItems().get(getIndex());
                    UiTasks.runAsync(statusLabel, () -> apiClient.markSold(item.getId()), updated -> load(table, statusLabel),
                            ex -> statusLabel.setText(apiClient.extractErrorMessage(ex)));
                });
                deleteBtn.setOnAction(e -> {
                    ListingDto item = getTableView().getItems().get(getIndex());
                    UiTasks.runAsync(statusLabel, () -> {
                                apiClient.deleteListing(item.getId());
                                return apiClient.getMyListings();
                            }, listings -> table.setItems(FXCollections.observableArrayList(listings)),
                            ex -> statusLabel.setText(apiClient.extractErrorMessage(ex)));
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        table.getColumns().addAll(titleCol, statusCol, actionsCol);
        VBox center = new VBox(10, table, statusLabel);
        center.setPadding(new Insets(20));
        VBox.setVgrow(table, javafx.scene.layout.Priority.ALWAYS);
        root.setCenter(center);

        load(table, statusLabel);
        return root;
    }

    /**
     * Loads the user's listings asynchronously and populates the table.
     *
     * @param table        the TableView to update
     * @param statusLabel  the label for displaying loading/error messages
     */
    private void load(TableView<ListingDto> table, Label statusLabel) {
        UiTasks.runAsync(statusLabel, apiClient::getMyListings,
                listings -> table.setItems(FXCollections.observableArrayList(listings)),
                ex -> statusLabel.setText(apiClient.extractErrorMessage(ex)));
    }
}