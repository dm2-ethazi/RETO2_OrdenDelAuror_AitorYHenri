package com.ordenDelAuror.aescipher;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/com/ordenDelAuror/aescipher/view/main-view.fxml"));

        Scene scene = new Scene(loader.load());

        stage.setTitle("Orden del Auror");
        stage.setScene(scene);
        stage.setMinWidth(400);
        stage.setMinHeight(400);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}