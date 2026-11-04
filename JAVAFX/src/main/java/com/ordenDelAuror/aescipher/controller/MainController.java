package com.ordenDelAuror.aescipher.controller;

import com.ordenDelAuror.aescipher.crypto.AESService;
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

        try {

            File output =
                    new File(
                            selectedFile.getAbsolutePath()
                                    + ".aes");

            AESService.encryptFile(
                    selectedFile,
                    output,
                    passwordField.getText());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void decrypt() {

        try {

            String path =
                    selectedFile.getAbsolutePath();

            File output =
                    new File(
                            path.substring(
                                    0,
                                    path.length() - 4));

            AESService.decryptFile(
                    selectedFile,
                    output,
                    passwordField.getText());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}