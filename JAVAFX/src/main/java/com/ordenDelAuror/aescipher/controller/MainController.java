package com.ordenDelAuror.aescipher.controller;

import com.ordenDelAuror.aescipher.crypto.AESService;
import com.ordenDelAuror.aescipher.utils.AlertUtils;
import com.ordenDelAuror.aescipher.utils.LanguageManager;
import com.ordenDelAuror.aescipher.utils.LanguageManager;
import com.ordenDelAuror.aescipher.utils.LanguageOption;


import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
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
    private Button selectButton;

    @FXML
    private Button encryptButton;

    @FXML
    private Button decryptButton;

    @FXML
    private ComboBox<LanguageOption> languageComboBox;

    private void updateTexts() {

        selectButton.setText(
                LanguageManager.get(
                        "button.select"));

        encryptButton.setText(
                LanguageManager.get(
                        "button.encrypt"));

        decryptButton.setText(
                LanguageManager.get(
                        "button.decrypt"));

        passwordField.setPromptText(
                LanguageManager.get(
                        "password.prompt"));

        if (selectedFile == null) {

            statusLabel.setText(
                    LanguageManager.get(
                            "status.no_file"));
        }

        progressLabel.setText(
                LanguageManager.get(
                        "status.ready"));
    }

    @FXML
    private void initialize() {

        languageComboBox.getItems().addAll(

                new LanguageOption(
                        "es",
                        "Español"),

                new LanguageOption(
                        "en",
                        "English"),

                new LanguageOption(
                        "eus",
                        "Euskara")
        );

        languageComboBox.getSelectionModel()
                .selectFirst();

        languageComboBox.setOnAction(event -> {

            LanguageOption option =
                    languageComboBox.getValue();

            LanguageManager.setLanguage(
                    option.getCode());

            updateTexts();
        });

        updateTexts();
    }


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
                    LanguageManager.get("alert.file_missing.title"),
                    LanguageManager.get("alert.file_missing.message"));

            return;
        }

        if (passwordField.getText().trim().isEmpty()) {

            AlertUtils.showWarning(
                    LanguageManager.get("alert.password_missing.title"),
                    LanguageManager.get("alert.password_missing.message"));

            return;
        }

        Task<Void> task = new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                File output = new File(
                        selectedFile.getAbsolutePath() + ".aes");

                updateMessage(LanguageManager.get("progress.encrypt"));

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
            progressLabel.setText(LanguageManager.get("status.finish"));

            AlertUtils.showInfo(
                    LanguageManager.get("alert.encrypt_success.title"),
                    LanguageManager.get("alert.encrypt_success.message"));
        });

        task.setOnFailed(event -> {

            progressLabel.textProperty().unbind();

            AlertUtils.showError(
                    LanguageManager.get("alert.error.encrypt"),
                    task.getException().getMessage());
        });

        new Thread(task).start();
    }

    @FXML
    private void decrypt() {

        if (selectedFile == null) {

            AlertUtils.showWarning(
                    LanguageManager.get("alert.file_missing.title"),
                    LanguageManager.get("alert.file_missing.message"));

            return;
        }

        if (passwordField.getText()
                .trim()
                .isEmpty()) {

            AlertUtils.showWarning(
                    LanguageManager.get("alert.password_missing.title"),
                    LanguageManager.get("alert.password_missing.message"));

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
                                    LanguageManager.get("alert.error.decrypt.notaes"));
                        }

                        File output =
                                new File(
                                        path.substring(
                                                0,
                                                path.length() - 4));

                        updateMessage(
                                LanguageManager.get("progress.decrypt"));

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
                                                    "%.1f%%",
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
                    LanguageManager.get("status.finish"));

            AlertUtils.showInfo(
                    LanguageManager.get("alert.decrypt_success.title"),
                    LanguageManager.get("alert.decrypt_success.message"));
        });

        task.setOnFailed(event -> {

            progressLabel
                    .textProperty()
                    .unbind();

            Throwable ex =
                    task.getException();

            AlertUtils.showError(
                    LanguageManager.get("alert.error.decrypt"),
                    ex.getMessage());
        });

        Thread thread =
                new Thread(task);

        thread.setDaemon(true);

        thread.start();
    }

}