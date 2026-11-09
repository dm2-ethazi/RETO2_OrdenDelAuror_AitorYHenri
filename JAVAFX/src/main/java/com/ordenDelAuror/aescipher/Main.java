package com.ordenDelAuror.aescipher;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    @Override
    public void start(Stage stage) throws Exception {
        Image icon = new Image(getClass().getResourceAsStream("/images/icon.png"));

        // 2. Añadir el icono a la lista de iconos del Stage
        stage.getIcons().add(icon);
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/view/main-view.fxml"));
        logger.info("Iniciando Orden del Auror");

        Scene scene = new Scene(loader.load());

        scene.getStylesheets().add(
                getClass()
                        .getResource("/css/style.css")
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