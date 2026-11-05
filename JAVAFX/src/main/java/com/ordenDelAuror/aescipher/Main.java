package com.ordenDelAuror.aescipher;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Image icon = new Image(getClass().getResourceAsStream("/com/ordenDelAuror/aescipher/images/icon.png"));

        // 2. Añadir el icono a la lista de iconos del Stage
        stage.getIcons().add(icon);
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