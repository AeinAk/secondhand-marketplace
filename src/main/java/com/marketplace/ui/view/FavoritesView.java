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
import javafx.scene.layout.VBox;

/**
 * View that displays the current user's favorite listings.
 * Fetches the list of favorites from the API and shows them in a table
 * with a "View" button to navigate to the listing detail.
 */
public class FavoritesView {

    /** The scene navigator used to switch to the listing detail view. */
    private final SceneNavigator navigator;

    /** The API client for fetching favorite listings. */
    private final ApiClient apiClient;

    /** The current user session. */
    private final UserSession session;

    /**
     * Constructs a FavoritesView with the required dependencies.
     *
     * @param navigator the scene navigator
     * @param apiClient the API client
     * @param session   the user session
     */
    public FavoritesView(SceneNavigator navigator, ApiClient apiClient, UserSession session) {
        this.navigator = navigator;
        this.apiClient = apiClient;
        this.session = session;
    }

    /**
     * Builds and returns the main UI for the favorites view.
     * The view consists of a toolbar, a table showing the favorite listings,
     * and a status label for loading/error messages.
     *
     * @return the root BorderPane containing the favorites interface
     */
    public BorderPane build() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");
        root.setTop(AppToolbar.create(navigator, apiClient, session, "Favorites"));

        Label statusLabel = new Label();
        TableView<ListingDto> table = new TableView<>();
        TableColumn<ListingDto, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<ListingDto, String> sellerCol = new TableColumn<>("Seller");
        sellerCol.setCellValueFactory(new PropertyValueFactory<>("sellerUsername"));
        TableColumn<ListingDto, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button viewBtn = new Button("View");
            {
                viewBtn.getStyleClass().add("secondary-button");
                viewBtn.setOnAction(e -> navigator.showListingDetail(getTableView().getItems().get(getIndex()).getId()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : viewBtn);
            }
        });
        table.getColumns().addAll(titleCol, sellerCol, actionCol);

        VBox center = new VBox(10, table, statusLabel);
        center.setPadding(new Insets(20));
        VBox.setVgrow(table, javafx.scene.layout.Priority.ALWAYS);
        root.setCenter(center);

        UiTasks.runAsync(statusLabel, apiClient::getFavorites,
                listings -> table.setItems(FXCollections.observableArrayList(listings)),
                ex -> statusLabel.setText(apiClient.extractErrorMessage(ex)));

        return root;
    }
}