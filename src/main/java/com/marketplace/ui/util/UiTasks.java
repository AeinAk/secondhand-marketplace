package com.marketplace.ui.util;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.util.function.Consumer;

public final class UiTasks {

    private UiTasks() {
    }

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

    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FunctionalInterface
    public interface TaskSupplier<T> {
        T get() throws Exception;
    }
}
