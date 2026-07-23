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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Administrative panel view for managing listings, users, and categories.
 * <p>
 * This view is only accessible to users with the ADMIN role. It provides three
 * tabs for managing pending listings, user accounts, and product categories.
 * Administrators can review listings, block/unblock users, and create or delete
 * categories.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class AdminPanelView {

    private final SceneNavigator navigator;
    private final ApiClient apiClient;
    private final UserSession session;

    /**
     * Constructs an AdminPanelView with the required dependencies.
     *
     * @param navigator the scene navigator for switching views
     * @param apiClient the API client for backend communication
     * @param session   the user session for authentication state
     */
    public AdminPanelView(SceneNavigator navigator, ApiClient apiClient, UserSession session) {
        this.navigator = navigator;
        this.apiClient = apiClient;
        this.session = session;
    }

    /**
     * Builds and returns the admin panel view.
     *
     * @return the constructed BorderPane containing the admin panel
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
        tabs.getTabs().add(new Tab("Pending Listings", pendingTable));

        TableView<UserDto> usersTable = buildUsersTable(statusLabel);
        tabs.getTabs().add(new Tab("Users", usersTable));

        VBox categoriesBox = buildCategoriesPanel(statusLabel);
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
     * Builds the categories panel with a list view and an add form.
     *
     * @param statusLabel the label for displaying status messages
     * @return a VBox containing the categories management components
     */
    private VBox buildCategoriesPanel(Label statusLabel) {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(10));

        Label formTitle = new Label("Add New Category");
        formTitle.getStyleClass().add("subtitle");

        TextField nameField = new TextField();
        nameField.setPromptText("Category name");

        TextField descField = new TextField();
        descField.setPromptText("Description");

        Button addBtn = new Button("Add Category");
        addBtn.getStyleClass().add("primary-button");
        addBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String desc = descField.getText().trim();
            if (name.isEmpty()) {
                statusLabel.setText("Category name is required");
                return;
            }
            UiTasks.runAsync(statusLabel, () -> apiClient.createCategory(name, desc),
                    created -> {
                        nameField.clear();
                        descField.clear();
                        UiTasks.showInfo("Success", "Category created");
                        TableView<CategoryDto> table = findCategoryTable(panel);
                        if (table != null) {
                            reloadCategories(table, statusLabel);
                        }
                    },
                    ex -> statusLabel.setText(apiClient.extractErrorMessage(ex)));
        });

        HBox formBox = new HBox(10, nameField, descField, addBtn);
        formBox.setPadding(new Insets(5, 0, 15, 0));

        Label listTitle = new Label("All Categories");
        listTitle.getStyleClass().add("subtitle");

        TableView<CategoryDto> categoryTable = new TableView<>();

        TableColumn<CategoryDto, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<CategoryDto, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<CategoryDto, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(300);

        TableColumn<CategoryDto, Void> actionCol = new TableColumn<>("Action");
        actionCol.setPrefWidth(80);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("Delete");

            {
                deleteBtn.getStyleClass().add("danger-button");
                deleteBtn.setOnAction(e -> {
                    CategoryDto category = getTableView().getItems().get(getIndex());
                    UiTasks.runAsync(statusLabel, () -> {
                        apiClient.deleteCategory(category.getId());
                        return apiClient.getCategories();
                    }, (List<CategoryDto> updatedList) -> {
                        getTableView().setItems(FXCollections.observableArrayList(updatedList));
                        UiTasks.showInfo("Success", "Category deleted");
                    }, ex -> statusLabel.setText(apiClient.extractErrorMessage(ex)));
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });

        categoryTable.getColumns().addAll(idCol, nameCol, descCol, actionCol);
        categoryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox.setVgrow(categoryTable, javafx.scene.layout.Priority.ALWAYS);

        panel.getChildren().addAll(formTitle, formBox, listTitle, categoryTable);

        reloadCategories(categoryTable, statusLabel);

        return panel;
    }

    /**
     * Finds the Category TableView within the categories panel.
     *
     * @param panel the VBox containing the categories panel
     * @return the found TableView, or null if not found
     */
    private TableView<CategoryDto> findCategoryTable(VBox panel) {
        for (var node : panel.getChildren()) {
            if (node instanceof TableView) {
                @SuppressWarnings("unchecked")
                TableView<CategoryDto> table = (TableView<CategoryDto>) node;
                return table;
            }
        }
        return null;
    }

    /**
     * Reloads the category list from the server and updates the table.
     *
     * @param table        the TableView to populate
     * @param statusLabel  the label for displaying status messages
     */
    private void reloadCategories(TableView<CategoryDto> table, Label statusLabel) {
        UiTasks.runAsync(statusLabel, apiClient::getCategories,
                list -> table.setItems(FXCollections.observableArrayList(list)),
                ex -> statusLabel.setText(apiClient.extractErrorMessage(ex)));
    }

    /**
     * Builds the pending listings table for admin review.
     *
     * @param statusLabel the label for displaying status messages
     * @return a TableView configured for pending listings
     */
    private TableView<ListingDto> buildPendingTable(Label statusLabel) {
        TableView<ListingDto> table = new TableView<>();

        TableColumn<ListingDto, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<ListingDto, String> sellerCol = new TableColumn<>("Seller");
        sellerCol.setCellValueFactory(new PropertyValueFactory<>("sellerUsername"));

        TableColumn<ListingDto, Void> actionCol = new TableColumn<>("Review");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button approveBtn = new Button("Approve");
            private final Button rejectBtn = new Button("Reject");
            private final Button deleteBtn = new Button("Delete");
            private final HBox box = new HBox(6, approveBtn, rejectBtn, deleteBtn);

            {
                approveBtn.getStyleClass().add("primary-button");
                rejectBtn.getStyleClass().add("secondary-button");
                deleteBtn.getStyleClass().add("danger-button");

                approveBtn.setOnAction(e -> review(getTableView().getItems().get(getIndex()),
                        ReviewDecision.APPROVED, table, statusLabel));
                rejectBtn.setOnAction(e -> review(getTableView().getItems().get(getIndex()),
                        ReviewDecision.REJECTED, table, statusLabel));
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

        table.getColumns().addAll(titleCol, sellerCol, actionCol);
        return table;
    }

    /**
     * Reviews a listing with the given decision.
     *
     * @param listing      the listing to review
     * @param decision     the review decision (APPROVED or REJECTED)
     * @param table        the TableView to update after review
     * @param statusLabel  the label for displaying status messages
     */
    private void review(ListingDto listing, ReviewDecision decision,
                        TableView<ListingDto> table, Label statusLabel) {
        AdminReviewRequest request = new AdminReviewRequest();
        request.setDecision(decision);
        request.setComment(decision == ReviewDecision.APPROVED
                ? "Approved by admin" : "Rejected by admin");

        UiTasks.runAsync(statusLabel, () -> {
                    apiClient.reviewListing(listing.getId(), request);
                    return apiClient.getPendingListings();
                }, listings -> table.setItems(FXCollections.observableArrayList(listings)),
                ex -> statusLabel.setText(apiClient.extractErrorMessage(ex)));
    }

    /**
     * Builds the users table for admin user management.
     *
     * @param statusLabel the label for displaying status messages
     * @return a TableView configured for user management
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
            private final Button toggleBtn = new Button("Toggle Block");

            {
                toggleBtn.getStyleClass().add("secondary-button");
                toggleBtn.setOnAction(e -> {
                    UserDto user = getTableView().getItems().get(getIndex());
                    UiTasks.runAsync(statusLabel,
                            () -> apiClient.blockUser(user.getId(), !user.isBlocked()),
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
     * Reloads the pending listings from the server.
     *
     * @param table        the TableView to populate
     * @param statusLabel  the label for displaying status messages
     */
    private void reloadPending(TableView<ListingDto> table, Label statusLabel) {
        UiTasks.runAsync(statusLabel, apiClient::getPendingListings,
                listings -> table.setItems(FXCollections.observableArrayList(listings)),
                ex -> statusLabel.setText(apiClient.extractErrorMessage(ex)));
    }

    /**
     * Reloads the user list from the server.
     *
     * @param table        the TableView to populate
     * @param statusLabel  the label for displaying status messages
     */
    private void reloadUsers(TableView<UserDto> table, Label statusLabel) {
        UiTasks.runAsync(statusLabel, apiClient::getUsers,
                users -> table.setItems(FXCollections.observableArrayList(users)),
                ex -> statusLabel.setText(apiClient.extractErrorMessage(ex)));
    }
}