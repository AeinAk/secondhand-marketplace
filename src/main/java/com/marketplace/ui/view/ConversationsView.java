package com.marketplace.ui.view;

import com.marketplace.backend.dto.ConversationDto;
import com.marketplace.backend.dto.MessageDto;
import com.marketplace.ui.navigation.SceneNavigator;
import com.marketplace.ui.service.ApiClient;
import com.marketplace.ui.session.UserSession;
import com.marketplace.ui.util.UiTasks;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;


import java.util.List;

/**
 * View that displays the user's conversations (messages) with other users.
 * It shows a list of conversations on the left and the selected conversation's
 * messages on the right, along with an input area for sending new messages.
 */
public class ConversationsView {

    /** The scene navigator used to switch views. */
    private final SceneNavigator navigator;

    /** The API client for backend communication. */
    private final ApiClient apiClient;

    /** The current user session. */
    private final UserSession session;

    /** The ID of a specific conversation to select and display on load, or null. */
    private final Long selectedConversationId;

    /**
     * Constructs a ConversationsView.
     *
     * @param navigator             the scene navigator
     * @param apiClient             the API client
     * @param session               the user session
     * @param selectedConversationId the ID of the conversation to pre-select,
     *                               or null to show the list without selection
     */
    public ConversationsView(SceneNavigator navigator, ApiClient apiClient, UserSession session, Long selectedConversationId) {
        this.navigator = navigator;
        this.apiClient = apiClient;
        this.session = session;
        this.selectedConversationId = selectedConversationId;
    }

    /**
     * Builds and returns the main UI for the conversations view.
     * The layout consists of a left panel with a list of conversations and a right
     * panel showing the messages of the selected conversation, with a text area
     * and send button for replying.
     *
     * @return the root BorderPane containing the entire conversations interface
     */
    public BorderPane build() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");
        root.setTop(AppToolbar.create(navigator, apiClient, session, "Messages"));

        Label statusLabel = new Label();
        ListView<ConversationDto> conversationList = new ListView<>();
        conversationList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(ConversationDto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String time = item.getLastMessageTime() != null
                            ? item.getLastMessageTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                            : "";
                    setText(item.getListingTitle() + " (" + item.getBuyerUsername() + " ↔ " + item.getSellerUsername() + ") - Last: " + time);
                }
            }
        });

        VBox messagesBox = new VBox(8);
        messagesBox.setPadding(new Insets(10));
        TextArea messageInput = new TextArea();
        messageInput.setPromptText("Type your message...");
        messageInput.setPrefRowCount(3);
        Button sendBtn = new Button("Send");
        sendBtn.getStyleClass().add("primary-button");

        Runnable loadMessages = () -> {
            ConversationDto selected = conversationList.getSelectionModel().getSelectedItem();
            if (selected == null) {
                messagesBox.getChildren().clear();
                return;
            }
            UiTasks.runAsync(statusLabel, () -> apiClient.getConversation(selected.getId()), conversation -> {
                messagesBox.getChildren().clear();
                for (MessageDto message : conversation.getMessages()) {
                    String time = message.getSentAt() != null
                            ? message.getSentAt().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
                            : "";
                    Label line = new Label(
                            message.getSenderUsername() + " (" + time + "): " + message.getContent()
                    );
                    line.setWrapText(true);
                    messagesBox.getChildren().add(line);
                }
            }, ex -> statusLabel.setText(apiClient.extractErrorMessage(ex)));
        };

        conversationList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> loadMessages.run());
        sendBtn.setOnAction(e -> {
            ConversationDto selected = conversationList.getSelectionModel().getSelectedItem();
            if (selected == null || messageInput.getText().isBlank()) {
                return;
            }
            String content = messageInput.getText().trim();
            UiTasks.runAsync(statusLabel, () -> {
                apiClient.sendMessage(selected.getId(), content);
                return apiClient.getConversation(selected.getId());
            }, conversation -> {
                messageInput.clear();
                messagesBox.getChildren().clear();
                for (MessageDto message : conversation.getMessages()) {
                    Label line = new Label(message.getSenderUsername() + ": " + message.getContent());
                    line.setWrapText(true);
                    messagesBox.getChildren().add(line);
                }
            }, ex -> statusLabel.setText(apiClient.extractErrorMessage(ex)));
        });

        HBox center = new HBox(12);
        VBox left = new VBox(8, new Label("Conversations"), conversationList, statusLabel);
        left.setPadding(new Insets(20, 0, 20, 20));
        left.setPrefWidth(320);
        ScrollPane scrollPane = new ScrollPane(messagesBox);
        scrollPane.setFitToWidth(true);
        scrollPane.vvalueProperty().bind(messagesBox.heightProperty());
        VBox right = new VBox(8, new Label("Chat"), scrollPane, messageInput, sendBtn);
        VBox.setVgrow(scrollPane, javafx.scene.layout.Priority.ALWAYS);
        right.setPadding(new Insets(20, 20, 20, 0));
        VBox.setVgrow(messagesBox, javafx.scene.layout.Priority.ALWAYS);
        center.getChildren().addAll(left, right);
        HBox.setHgrow(right, javafx.scene.layout.Priority.ALWAYS);
        root.setCenter(center);

        UiTasks.runAsync(statusLabel, apiClient::getConversations, (List<ConversationDto> conversations) -> {
            conversationList.setItems(FXCollections.observableArrayList(conversations));
            if (selectedConversationId != null) {
                conversations.stream().filter(c -> c.getId().equals(selectedConversationId)).findFirst()
                        .ifPresent(c -> {
                            conversationList.getSelectionModel().select(c);
                            loadMessages.run();
                        });
            }
        }, ex -> statusLabel.setText(apiClient.extractErrorMessage(ex)));

        return root;
    }
}