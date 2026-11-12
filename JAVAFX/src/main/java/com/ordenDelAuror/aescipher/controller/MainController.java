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

/**
 * Controlador de la vista principal ({@code /view/main-view.fxml}).
 *
 * <p>Gestiona la selección del fichero, el cifrado y el descifrado en un
 * hilo aparte (JavaFX {@link Task}) con su barra de progreso, los avisos al
 * usuario, el selector de idioma y los atajos de teclado.</p>
 *
 * @author Henri Tejada
 */
public class MainController {

    /** Etiqueta de estado; muestra el nombre del fichero seleccionado. */
    @FXML
    private Label statusLabel;

    /** Fichero elegido por el usuario; {@code null} mientras no se elija ninguno. */
    private File selectedFile;

    /** Campo donde el usuario escribe la contraseña de cifrado. */
    @FXML
    private PasswordField passwordField;

    /** Barra de progreso de la operación en curso. */
    @FXML
    private ProgressBar progressBar;

    /** Etiqueta con el porcentaje de progreso y el mensaje de estado. */
    @FXML
    private Label progressLabel;

    /** Botón para abrir el diálogo de selección de ficheros. */
    @FXML
    private Button selectButton;

    /** Botón que dispara el cifrado del fichero seleccionado. */
    @FXML
    private Button encryptButton;

    /** Botón que dispara el descifrado del fichero seleccionado. */
    @FXML
    private Button decryptButton;

    /** Desplegable con los idiomas disponibles (español, inglés y euskera). */
    @FXML
    private ComboBox<LanguageOption> languageComboBox;

    /** Botón que muestra el diálogo de información de la aplicación. */
    @FXML
    private Button infoButton;

    /**
     * Muestra el diálogo de información de la aplicación (atajo F1).
     *
     * <p>Los textos se recuperan del bundle de idioma activo y el diálogo se
     * aplica la hoja de estilos de la aplicación.</p>
     */
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

    /** Logger de la clase (SLF4J + Logback). */
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    /**
     * Actualiza todos los textos, tooltips y mensajes de la interfaz según el
     * idioma activo.
     *
     * <p>Si no hay fichero seleccionado se restablece el mensaje de estado
     * por defecto.</p>
     */
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

    /**
     * Inicialización invocada automáticamente por el FXMLLoader al cargar el
     * FXML.
     *
     * <p>Rellena el ComboBox de idiomas, selecciona el primero por defecto,
     * asocia el cambio de idioma a la recarga de textos y aplica los textos
     * iniciales.</p>
     */
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


    /**
     * Abre el diálogo de selección de ficheros y guarda la elección del
     * usuario en {@link #selectedFile}.
     *
     * <p>Si el usuario cancela el diálogo no se modifica el fichero
     * seleccionado previamente.</p>
     */
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

    /**
     * Cifra el fichero seleccionado con la contraseña introducida.
     *
     * <p>Si no hay fichero o la contraseña está vacía se muestra un aviso y
     * no se hace nada. El cifrado se ejecuta en un hilo aparte
     * ({@link Task}) para no bloquear la interfaz, actualizando la barra y
     * la etiqueta de progreso; al terminar se muestra un diálogo de éxito o
     * de error.</p>
     */
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

    /**
     * Descifra el fichero seleccionado con la contraseña introducida.
     *
     * <p>Si no hay fichero o la contraseña está vacía se muestra un aviso y
     * no se hace nada. El fichero debe terminar en {@code .aes}; en caso
     * contrario la tarea falla y se notifica el error. El descifrado se
     * ejecuta en un hilo aparte ({@link Task}) actualizando la barra de
     * progreso.</p>
     */
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

    /**
     * Registra los atajos de teclado de la aplicación en la escena.
     *
     * @param scene escena sobre la que se registran los aceleradores:
     *              Ctrl+O (seleccionar fichero), Ctrl+E (cifrar),
     *              Ctrl+D (descifrar) y F1 (información)
     */
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

    /**
     * Método puente para invocar la selección de fichero desde los aceleradores.
     */
    public void executeSelectFile() {
        selectFile();
    }

    /**
     * Método puente para invocar el cifrado desde los aceleradores.
     */
    public void executeEncrypt() {
        encrypt();
    }

    /**
     * Método puente para invocar el descifrado desde los aceleradores.
     */
    public void executeDecrypt() {
        decrypt();
    }

    /**
     * Método puente para mostrar la información desde los aceleradores.
     */
    public void executeShowInfo() {
        showInfo();
    }

}