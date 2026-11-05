package com.ordenDelAuror.aescipher;

import com.ordenDelAuror.aescipher.utils.LanguageManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        LanguageManager.setLanguage("eus");
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/com/ordenDelAuror/aescipher/view/main-view.fxml"));

        Scene scene = new Scene(loader.load());

        scene.getStylesheets().add(
                getClass()
                        .getResource("/com/ordenDelAuror/aescipher/css/style.css")
                        .toExternalForm());

        stage.setTitle("Orden del Auror");
        stage.setScene(scene);
        stage.setMinWidth(400);
        stage.setMinHeight(400);
        stage.setMaxWidth(650);
        stage.setMaxHeight(650);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}