package com.ordenDelAuror.aescipher.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import java.io.File;

public class MainController {

    @FXML
    private Label statusLabel;

    private File selectedFile;

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
}