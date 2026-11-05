package com.ordenDelAuror.aescipher.controller;

import com.ordenDelAuror.aescipher.crypto.AESService;
import com.ordenDelAuror.aescipher.utils.AlertUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.FileChooser;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;

import java.io.File;

public class MainController {

    @FXML
    private Label statusLabel;

    private File selectedFile;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label progressLabel;

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
                    "Debes seleccionar un archivo.");

            return;
        }

        if (passwordField.getText().trim().isEmpty()) {

            AlertUtils.showWarning(
                    "Contraseña vacía",
                    "Debes introducir una contraseña.");

            return;
        }

        Task<Void> task = new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                File output = new File(
                        selectedFile.getAbsolutePath() + ".aes");

                updateMessage("Cifrando...");

                AESService.encryptFile(
                        selectedFile,
                        output,
                        passwordField.getText(),
                        (processed, total) -> {

                            updateProgress(
                                    processed,
                                    total);

                            updateMessage(
                                    String.format(
                                            "%.1f%%",
                                            (processed * 100.0) / total));
                        });

                updateProgress(1, 1);

                return null;
            }
        };

        progressBar.progressProperty()
                .bind(task.progressProperty());

        progressLabel.textProperty()
                .bind(task.messageProperty());

        task.setOnSucceeded(event -> {

            progressLabel.textProperty().unbind();
            progressLabel.setText("Completado");

            AlertUtils.showInfo(
                    "Cifrado completado",
                    "Archivo cifrado correctamente.");
        });

        task.setOnFailed(event -> {

            progressLabel.textProperty().unbind();

            AlertUtils.showError(
                    "Error al cifrar",
                    task.getException().getMessage());
        });

        new Thread(task).start();
    }

    @FXML
    private void decrypt() {

        if (selectedFile == null) {

            AlertUtils.showWarning(
                    "Archivo no seleccionado",
                    "Debes seleccionar un archivo.");

            return;
        }

        if (passwordField.getText()
                .trim()
                .isEmpty()) {

            AlertUtils.showWarning(
                    "Contraseña vacía",
                    "Debes introducir una contraseña.");

            return;
        }

        Task<Void> task =
                new Task<Void>() {

                    @Override
                    protected Void call()
                            throws Exception {

                        String path =
                                selectedFile
                                        .getAbsolutePath();

                        if (!path.endsWith(".aes")) {

                            throw new Exception(
                                    "Debe seleccionar un archivo .aes");
                        }

                        File output =
                                new File(
                                        path.substring(
                                                0,
                                                path.length() - 4));

                        updateMessage(
                                "Descifrando...");

                        AESService.decryptFile(
                                selectedFile,
                                output,
                                passwordField.getText(),
                                (processed, total) -> {

                                    updateProgress(
                                            processed,
                                            total);

                                    updateMessage(
                                            String.format(
                                                    "Descifrando %.1f%%",
                                                    processed
                                                            * 100.0
                                                            / total));
                                });

                        return null;
                    }
                };

        progressBar.progressProperty()
                .bind(
                        task.progressProperty());

        progressLabel.textProperty()
                .bind(
                        task.messageProperty());

        task.setOnSucceeded(event -> {

            progressLabel
                    .textProperty()
                    .unbind();

            progressLabel.setText(
                    "Descifrado completado");

            AlertUtils.showInfo(
                    "Descifrado completado",
                    "El archivo se ha descifrado correctamente.");
        });

        task.setOnFailed(event -> {

            progressLabel
                    .textProperty()
                    .unbind();

            Throwable ex =
                    task.getException();

            AlertUtils.showError(
                    "Error al descifrar",
                    ex.getMessage());
        });

        Thread thread =
                new Thread(task);

        thread.setDaemon(true);

        thread.start();
    }

}