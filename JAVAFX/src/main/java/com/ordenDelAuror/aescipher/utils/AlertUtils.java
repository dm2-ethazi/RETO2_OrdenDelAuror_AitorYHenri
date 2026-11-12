package com.ordenDelAuror.aescipher.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;

/**
 * Utilidad para mostrar diálogos de JavaFX con la hoja de estilos de la
 * aplicación ({@code /css/style.css}) ya aplicada.
 *
 * @author Henri Tejada
 */
public class AlertUtils {

    /**
     * Aplica la hoja de estilos de la aplicación y la clase CSS indicada a un
     * diálogo.
     *
     * @param alert      diálogo al que aplicar el estilo
     * @param styleClass clase CSS que define la apariencia del diálogo
     */
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

    /**
     * Muestra un diálogo informativo y espera a que el usuario lo cierre.
     *
     * @param titulo  título del diálogo, que también se usa como cabecera
     * @param mensaje mensaje que se muestra en el cuerpo del diálogo
     */
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

    /**
     * Muestra un diálogo de advertencia y espera a que el usuario lo cierre.
     *
     * @param titulo  título del diálogo, que también se usa como cabecera
     * @param mensaje mensaje que se muestra en el cuerpo del diálogo
     */
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

    /**
     * Muestra un diálogo de error y espera a que el usuario lo cierre.
     *
     * @param titulo  título del diálogo, que también se usa como cabecera
     * @param mensaje mensaje que se muestra en el cuerpo del diálogo
     */
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