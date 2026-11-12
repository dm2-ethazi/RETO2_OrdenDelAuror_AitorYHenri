package com.ordenDelAuror.aescipher;

import com.ordenDelAuror.aescipher.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase principal de la aplicación JavaFX "Orden del Auror".
 *
 * <p>Carga la interfaz desde el fichero FXML {@code /view/main-view.fxml},
 * aplica la hoja de estilos, el icono, el tamaño de la ventana y registra
 * los atajos de teclado (Ctrl+O, Ctrl+E, Ctrl+D y F1).</p>
 *
 * @author Henri Tejada
 */
public class Main extends Application {

    /** Logger de la aplicación (SLF4J + Logback). */
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    /**
     * Configura y muestra la ventana principal de la aplicación.
     *
     * @param stage ventana principal (Stage) que entrega JavaFX al iniciar
     * @throws Exception si no se puede cargar el FXML, la imagen o el CSS
     */
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

        MainController controller =
                loader.getController();

        controller.initializeShortcuts(scene);
        scene.setOnKeyPressed(event -> {

            scene.getAccelerators().put(
                    new KeyCodeCombination(
                            KeyCode.O,
                            KeyCombination.CONTROL_DOWN),
                    () -> controller.executeSelectFile()
            );

            scene.getAccelerators().put(
                    new KeyCodeCombination(
                            KeyCode.E,
                            KeyCombination.CONTROL_DOWN),
                    () -> controller.executeEncrypt()
            );

            scene.getAccelerators().put(
                    new KeyCodeCombination(
                            KeyCode.D,
                            KeyCombination.CONTROL_DOWN),
                    () -> controller.executeDecrypt()
            );

            scene.getAccelerators().put(
                    new KeyCodeCombination(
                            KeyCode.F1),
                    () -> controller.executeShowInfo()
            );
        });

        scene.getStylesheets().add(
                getClass()
                        .getResource("/css/style.css")
                        .toExternalForm());

        stage.setTitle("Orden del Auror");
        stage.setScene(scene);
        stage.setMinWidth(500);
        stage.setMinHeight(500);
        stage.setMaxWidth(650);
        stage.setMaxHeight(650);
        stage.show();
    }

    /**
     * Método de arranque estándar de toda aplicación JavaFX.
     *
     * @param args argumentos de línea de comandos; no se utilizan
     */
    public static void main(String[] args) {
        launch(args);
    }
}