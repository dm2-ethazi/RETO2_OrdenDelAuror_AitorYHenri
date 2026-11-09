package com.ordenDelAuror.aescipher.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;

public class AlertUtils {

    private static void applyStyle(
            Alert alert,
            String styleClass) {

        DialogPane dialogPane =
                alert.getDialogPane();

        dialogPane.getStylesheets().add(
                AlertUtils.class
                        .getResource(
                                "/css/style.css")
                        .toExternalForm());

        dialogPane.getStyleClass().add(styleClass);
    }

    public static void showInfo(
            String titulo,
            String mensaje) {

        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(titulo);
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);

        applyStyle(alert, "info-dialog");

        alert.showAndWait();
    }

    public static void showWarning(
            String titulo,
            String mensaje) {

        Alert alert =
                new Alert(Alert.AlertType.WARNING);

        alert.setTitle(titulo);
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);

        applyStyle(alert, "warning-dialog");

        alert.showAndWait();
    }

    public static void showError(
            String titulo,
            String mensaje) {

        Alert alert =
                new Alert(Alert.AlertType.ERROR);

        alert.setTitle(titulo);
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);

        applyStyle(alert, "error-dialog");

        alert.showAndWait();
    }
}