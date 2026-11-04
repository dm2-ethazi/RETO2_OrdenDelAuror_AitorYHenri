package com.ordenDelAuror.aescipher.controller;

import com.ordenDelAuror.aescipher.crypto.AESService;
import com.ordenDelAuror.aescipher.utils.AlertUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.FileChooser;

import java.io.File;

public class MainController {

    @FXML
    private Label statusLabel;

    private File selectedFile;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void selectFile() {

        FileChooser chooser =
                new FileChooser();

        selectedFile =
                chooser.showOpenDialog(null);

        if (selectedFile != null) {
            statusLabel.setText(selectedFile.getName());
        }
    }

    @FXML
    private void encrypt() {

        if (selectedFile == null) {
            AlertUtils.showWarning(
                    "Archivo no seleccionado",
                    "Debes seleccionar un archivo antes de cifrar.");
            return;
        }

        if (passwordField.getText().trim().isEmpty()) {
            AlertUtils.showWarning(
                    "Contraseña vacía",
                    "Debes introducir una contraseña.");
            return;
        }

        try {

            File output = new File(
                    selectedFile.getAbsolutePath() + ".aes");

            AESService.encryptFile(
                    selectedFile,
                    output,
                    passwordField.getText());

            AlertUtils.showInfo(
                    "Cifrado completado",
                    "Archivo guardado en:\n\n" +
                            output.getAbsolutePath());

        } catch (Exception ex) {

            AlertUtils.showError(
                    "Error al cifrar",
                    ex.getMessage());
        }
    }

    @FXML
    private void decrypt() {

        if (selectedFile == null) {

            AlertUtils.showWarning(
                    "Archivo no seleccionado",
                    "Debes seleccionar un archivo antes de descifrar.");

            return;
        }

        if (passwordField.getText().trim().isEmpty()) {

            AlertUtils.showWarning(
                    "Contraseña vacía",
                    "Debes introducir una contraseña.");

            return;
        }

        try {

            String path = selectedFile.getAbsolutePath();

            if (!path.endsWith(".aes")) {

                AlertUtils.showWarning(
                        "Archivo incorrecto",
                        "Debes seleccionar un archivo .aes");

                return;
            }

            File output = new File(
                    path.substring(0, path.length() - 4));

            AESService.decryptFile(
                    selectedFile,
                    output,
                    passwordField.getText());

            AlertUtils.showInfo(
                    "Descifrado completado",
                    "El archivo se ha descifrado correctamente.");

        } catch (Exception ex) {

            AlertUtils.showError(
                    "Error al descifrar",
                    ex.getMessage());
        }
    }

}