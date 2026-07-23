package com.marketplace.ui.view;

import com.marketplace.backend.dto.AdminReviewRequest;
import com.marketplace.backend.dto.CategoryDto;
import com.marketplace.backend.dto.ListingDto;
import com.marketplace.backend.dto.UserDto;
import com.marketplace.backend.entity.ReviewDecision;
import com.marketplace.ui.navigation.SceneNavigator;
import com.marketplace.ui.service.ApiClient;
import com.marketplace.ui.session.UserSession;
import com.marketplace.ui.util.UiTasks;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * The administration panel view for managing pending listings, users, and categories.
 * This view is accessible only to users with the ADMIN role and provides
 * administrative operations such as approving/rejecting listings, blocking/unblocking users,
 * and creating/deleting categories.
 */
public class AdminPanelView {

    /** The scene navigator for switching views. */
    private final SceneNavigator navigator;

    /** The API client for backend communication. */
    private final ApiClient apiClient;

    /** The current user session used to verify admin privileges. */
    private final UserSession session;

    /**
     * Constructs an AdminPanelView with the required dependencies.
     *
     * @param navigator the scene navigator
     * @param apiClient the API client
     * @param session   the user session
     */
    public AdminPanelView(SceneNavigator navigator, ApiClient apiClient, UserSession session) {
        this.navigator = navigator;
        this.apiClient = apiClient;
        this.session = session;
    }

    /**
     * Builds the complete admin panel UI containing three tabs:
     * pending advertisements, user management, and category management.
     *
     * @return the root BorderPane containing the admin interface
     */
    public BorderPane build() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");
        root.setTop(AppToolbar.create(navigator, apiClient, session, "Admin Panel"));

        if (!session.isAdmin()) {
            root.setCenter(new Label("Admin access required"));
            return root;
        }

        Label statusLabel = new Label();
        TabPane tabs = new TabPane();

        TableView<ListingDto> pendingTable = buildPendingTable(statusLabel);
        TableView<UserDto> usersTable = buildUsersTable(statusLabel);
        VBox categoriesBox = buildCategoriesPanel(statusLabel);

        tabs.getTabs().add(new Tab("Pending Advertisements", pendingTable));
        tabs.getTabs().add(new Tab("Users Info", usersTable));
        tabs.getTabs().add(new Tab("Categories", categoriesBox));

        VBox center = new VBox(10, tabs, statusLabel);
        center.setPadding(new Insets(20));
        VBox.setVgrow(tabs, javafx.scene.layout.Priority.ALWAYS);
        root.setCenter(center);

        reloadPending(pendingTable, statusLabel);
        reloadUsers(usersTable, statusLabel);
        return root;
    }

    /**
     * Builds and configures the table that displays pending listings awaiting admin review.
     * Each row includes buttons to approve, reject, or delete the listing.
     *
     * @param statusLabel the label used to display operation status messages
     * @return the configured TableView for pending listings
     */
    private TableView<ListingDto> buildPendingTable(Label statusLabel) {
        TableView<ListingDto> table = new TableView<>();
        TableColumn<ListingDto, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<ListingDto, String> sellerCol = new TableColumn<>("Seller");
        sellerCol.setCellValueFactory(new PropertyValueFactory<>("sellerUsername"));
        TableColumn<ListingDto, Void> actionCol = new TableColumn<>("Review");
        actionCol.setPrefWidth(230);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button approveBtn = new Button("Approve");
            private final Button rejectBtn = new Button("Reject");
            private final Button deleteBtn = new Button("Delete");
            private final HBox box = new HBox(6, approveBtn, rejectBtn, deleteBtn);

            {
                approveBtn.getStyleClass().add("primary-button");
                rejectBtn.getStyleClass().add("secondary-button");
                deleteBtn.getStyleClass().add("danger-button");
                approveBtn.setOnAction(e -> review(getTableView().getItems().get(getIndex()), ReviewDecision.APPROVED, table, statusLabel));
                rejectBtn.setOnAction(e -> review(getTableView().getItems().get(getIndex()), ReviewDecision.REJECTED, table, statusLabel));
                deleteBtn.setOnAction(e -> UiTasks.runAsync(statusLabel, () -> {
                            apiClient.deleteListing(getTableView().getItems().get(getIndex()).getId());
                            return apiClient.getPendingListings();
                        }, listings -> table.setItems(FXCollections.observableArrayList(listings)),
                        ex -> statusLabel.setText(apiClient.extractErrorMessage(ex))));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        TableColumn<ListingDto, Void> viewCol = new TableColumn<>("Ad Details");
        viewCol.setPrefWidth(70);
        viewCol.setCellFactory(col -> new TableCell<>(){
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

        table.getColumns().addAll(titleCol, sellerCol, actionCol, viewCol);
        return table;
    }

    /**
     * Performs a review action on a pending listing (approve or reject).
     * After the review, the pending list is refreshed.
     *
     * @param listing      the listing to review
     * @param decision     the review decision (approved or rejected)
     * @param table        the table containing the pending listings
     * @param statusLabel  the label for status messages
     */
    private void review(ListingDto listing, ReviewDecision decision, TableView<ListingDto> table, Label statusLabel) {
        AdminReviewRequest request = new AdminReviewRequest();
        request.setDecision(decision);
        request.setComment(decision == ReviewDecision.APPROVED ? "Approved by admin" : "Rejected by admin");
        UiTasks.runAsync(statusLabel, () -> {
                    apiClient.reviewListing(listing.getId(), request);
                    return apiClient.getPendingListings();
                }, listings -> table.setItems(FXCollections.observableArrayList(listings)),
                ex -> statusLabel.setText(apiClient.extractErrorMessage(ex)));
    }

    /**
     * Builds and configures the table that displays all registered users.
     * Each row includes a button to block or unblock the user.
     *
     * @param statusLabel the label used to display operation status messages
     * @return the configured TableView for users
     */
    private TableView<UserDto> buildUsersTable(Label statusLabel) {
        TableView<UserDto> table = new TableView<>();
        TableColumn<UserDto, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        TableColumn<UserDto, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        TableColumn<UserDto, Boolean> blockedCol = new TableColumn<>("Blocked");
        blockedCol.setCellValueFactory(new PropertyValueFactory<>("blocked"));
        TableColumn<UserDto, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button toggleBtn = new Button("Block");
            {
                toggleBtn.getStyleClass().add("secondary-button");
                toggleBtn.setOnAction(e -> {
                    UserDto user = getTableView().getItems().get(getIndex());
                    UiTasks.runAsync(statusLabel, () -> apiClient.blockUser(user.getId(), !user.isBlocked()),
                            updated -> reloadUsers(table, statusLabel),
                            ex -> statusLabel.setText(apiClient.extractErrorMessage(ex)));
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : toggleBtn);
            }
        });
        table.getColumns().addAll(usernameCol, roleCol, blockedCol, actionCol);
        return table;
    }

    /**
     * Builds the category management panel with input fields for name and description,
     * and a button to add a new category.
     *
     * @param statusLabel the label used to display operation status messages
     * @return a VBox containing the category management controls
     */
    private VBox buildCategoriesPanel(Label statusLabel) {
        TextField nameField = new TextField();
        nameField.setPromptText("Category name");
        TextField descField = new TextField();
        descField.setPromptText("Description");
        Button addBtn = new Button("Add Category");
        addBtn.getStyleClass().add("primary-button");
        addBtn.setOnAction(e -> UiTasks.runAsync(statusLabel, () -> apiClient.createCategory(nameField.getText().trim(), descField.getText().trim()),
                created -> {
                    nameField.clear();
                    descField.clear();
                    UiTasks.showInfo("Success", "Category created");
                }, ex -> statusLabel.setText(apiClient.extractErrorMessage(ex))));
        return new VBox(10, new Label("Manage Categories"), nameField, descField, addBtn);
    }

    /**
     * Reloads the pending listings table by fetching fresh data from the API.
     *
     * @param table        the pending listings table to update
     * @param statusLabel  the label for status messages
     */
    private void reloadPending(TableView<ListingDto> table, Label statusLabel) {
        UiTasks.runAsync(statusLabel, apiClient::getPendingListings,
                listings -> table.setItems(FXCollections.observableArrayList(listings)),
                ex -> statusLabel.setText(apiClient.extractErrorMessage(ex)));
    }

    /**
     * Reloads the users table by fetching fresh user data from the API.
     *
     * @param table        the users table to update
     * @param statusLabel  the label for status messages
     */
    private void reloadUsers(TableView<UserDto> table, Label statusLabel) {
        UiTasks.runAsync(statusLabel, apiClient::getUsers,
                users -> table.setItems(FXCollections.observableArrayList(users)),
                ex -> statusLabel.setText(apiClient.extractErrorMessage(ex)));
    }
}