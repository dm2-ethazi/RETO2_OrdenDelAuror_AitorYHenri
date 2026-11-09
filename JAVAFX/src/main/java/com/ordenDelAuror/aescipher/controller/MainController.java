package com.ordenDelAuror.aescipher.controller;

import com.ordenDelAuror.aescipher.crypto.AESService;
import com.ordenDelAuror.aescipher.utils.AlertUtils;
import com.ordenDelAuror.aescipher.utils.LanguageManager;
import com.ordenDelAuror.aescipher.utils.LanguageOption;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import javafx.concurrent.Task;

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

    @FXML
    private Button infoButton;

    @FXML
    public void showInfo() {
        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(
                LanguageManager.get(
                        "info.title"));

        alert.setHeaderText(
                LanguageManager.get(
                        "info.header"));

        alert.setContentText(
                LanguageManager.get(
                        "info.content"));

        alert.getDialogPane()
                .getStylesheets()
                .add(
                        getClass()
                                .getResource(
                                        "/css/style.css")
                                .toExternalForm());

        alert.getDialogPane()
                .getStyleClass()
                .add("info-dialog");

        alert.showAndWait();
    }

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

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

        selectButton.setTooltip(
                new Tooltip(
                        LanguageManager.get(
                                "tooltip.select")));

        encryptButton.setTooltip(
                new Tooltip(
                        LanguageManager.get(
                                "tooltip.encrypt")));

        decryptButton.setTooltip(
                new Tooltip(
                        LanguageManager.get(
                                "tooltip.decrypt")));

        languageComboBox.setTooltip(
                new Tooltip(
                        LanguageManager.get(
                                "tooltip.language")));

        infoButton.setTooltip(
                new Tooltip(
                        LanguageManager.get(
                                "tooltip.info")));
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
        logger.info("Seleccionando archivos");
        FileChooser chooser =
                new FileChooser();

        selectedFile =
                chooser.showOpenDialog(null);

        if (selectedFile != null) {
            statusLabel.setText(selectedFile.getName());
            logger.info("Archivo seleccionado: " + selectedFile.getName());
        }
    }

    @FXML
    private void encrypt() {

        if (selectedFile == null) {
            logger.warn("No se ha seleccionado Archivo para cifrar");
            AlertUtils.showWarning(
                    LanguageManager.get("alert.file_missing.title"),
                    LanguageManager.get("alert.file_missing.message"));

            return;
        }

        if (passwordField.getText().trim().isEmpty()) {
            logger.warn("No se ha introducido contraseña para cifrar");
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
            logger.info("Archivo Cifrado Correctamente");
            AlertUtils.showInfo(
                    LanguageManager.get("alert.encrypt_success.title"),
                    LanguageManager.get("alert.encrypt_success.message"));
        });

        task.setOnFailed(event -> {

            progressLabel.textProperty().unbind();
            logger.error("Error al cifrar: "+ task.getException().getMessage());
            AlertUtils.showError(
                    LanguageManager.get("alert.error.encrypt"),
                    task.getException().getMessage());
        });

        new Thread(task).start();
    }

    @FXML
    private void decrypt() {

        if (selectedFile == null) {
            logger.warn("No se ha seleccionado Archivo a descifrar");
            AlertUtils.showWarning(
                    LanguageManager.get("alert.file_missing.title"),
                    LanguageManager.get("alert.file_missing.message"));

            return;
        }

        if (passwordField.getText().trim().isEmpty()) {
            logger.warn("No se ha introducido contraseña para descifrar");
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
                            logger.error("El archivo debe ser .aes");
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
            logger.info("Archivo descifrado correctamente");
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
            logger.error("Error al descifrar: "+task.getException().getMessage());
            AlertUtils.showError(
                    LanguageManager.get("alert.error.decrypt"),
                    ex.getMessage());
        });

        Thread thread =
                new Thread(task);

        thread.setDaemon(true);

        thread.start();
    }

    public void initializeShortcuts(Scene scene) {

        scene.getAccelerators().put(

                new KeyCodeCombination(
                        KeyCode.O,
                        KeyCombination.CONTROL_DOWN),

                this::selectFile
        );

        scene.getAccelerators().put(

                new KeyCodeCombination(
                        KeyCode.E,
                        KeyCombination.CONTROL_DOWN),

                this::encrypt
        );

        scene.getAccelerators().put(

                new KeyCodeCombination(
                        KeyCode.D,
                        KeyCombination.CONTROL_DOWN),

                this::decrypt
        );

        scene.getAccelerators().put(

                new KeyCodeCombination(
                        KeyCode.F1),

                this::showInfo
        );
    }

    public void executeSelectFile() {
        selectFile();
    }

    public void executeEncrypt() {
        encrypt();
    }

    public void executeDecrypt() {
        decrypt();
    }

    public void executeShowInfo() {
        showInfo();
    }

}