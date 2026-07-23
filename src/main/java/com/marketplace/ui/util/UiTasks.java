package com.marketplace.ui.util;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.util.function.Consumer;

/**
 * Utility class providing helper methods for executing background tasks
 * with JavaFX UI updates, and displaying error/information dialogs.
 * All methods are static and designed to simplify common UI task patterns.
 */
public final class UiTasks {

    /** Private constructor to prevent instantiation of this utility class. */
    private UiTasks() {
    }

    /**
     * Executes a background task asynchronously on a separate thread.
     * Updates the provided status label during loading, and handles success
     * or failure callbacks on the JavaFX Application Thread.
     *
     * @param statusLabel the label to display status messages (may be null)
     * @param supplier    the task supplier that performs the background work
     * @param onSuccess   consumer to handle the result on success (called on FX thread)
     * @param onError     consumer to handle any exception on error (called on FX thread)
     * @param <T>         the result type of the background task
     */
    public static <T> void runAsync(Label statusLabel,
                                    TaskSupplier<T> supplier,
                                    Consumer<T> onSuccess,
                                    Consumer<Exception> onError) {
        if (statusLabel != null) {
            statusLabel.setText("Loading...");
            statusLabel.getStyleClass().removeAll("error-label", "success-label");
        }
        Task<T> task = new Task<>() {
            @Override
            protected T call() throws Exception {
                return supplier.get();
            }
        };
        task.setOnSucceeded(event -> Platform.runLater(() -> {
            if (statusLabel != null) {
                statusLabel.setText("");
            }
            onSuccess.accept(task.getValue());
        }));
        task.setOnFailed(event -> Platform.runLater(() -> {
            Throwable ex = task.getException();
            Exception exception = ex instanceof Exception e ? e : new Exception(ex);
            if (statusLabel != null) {
                statusLabel.setText(exception.getMessage());
                if (!statusLabel.getStyleClass().contains("error-label")) {
                    statusLabel.getStyleClass().add("error-label");
                }
            }
            onError.accept(exception);
        }));
        new Thread(task, "ui-background-task").start();
    }

    /**
     * Displays a modal error alert dialog with the given title and message.
     *
     * @param title   the title of the error dialog
     * @param message the content text of the error dialog
     */
    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays a modal information alert dialog with the given title and message.
     *
     * @param title   the title of the information dialog
     * @param message the content text of the information dialog
     */
    public static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Functional interface for a task that supplies a result and may throw an exception.
     * Used as a lambda-friendly abstraction for background work in {@link #runAsync}.
     *
     * @param <T> the type of the result produced by the task
     */
    @FunctionalInterface
    public interface TaskSupplier<T> {
        /**
         * Performs the background operation.
         *
         * @return the result of the operation
         * @throws Exception if an error occurs during execution
         */
        T get() throws Exception;
    }
}