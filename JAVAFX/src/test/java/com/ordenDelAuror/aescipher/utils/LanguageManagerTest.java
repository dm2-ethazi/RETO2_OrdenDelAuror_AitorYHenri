package com.ordenDelAuror.aescipher.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.MissingResourceException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests de {@link LanguageManager}, el gestor de idiomas (ResourceBundle).
 *
 * <p>Verifica el idioma por defecto, el cambio entre español, inglés y
 * euskera, la existencia de todas las claves usadas por la interfaz en los
 * tres ficheros de idioma y el manejo de errores.</p>
 *
 * Nota: LanguageManager mantiene el bundle en estado estático, así que
 * cada test fija explícitamente el idioma en {@code setUp()}.
 *
 * @author Henri Tejada
 */
class LanguageManagerTest {

    /** Clave del botón de cifrado, usada como muestra en varios tests. */
    private static final String CLAVE_BOTON_CIFRAR = "button.encrypt";

    /** Clave del botón de descifrado. */
    private static final String CLAVE_BOTON_DESCIFRAR = "button.decrypt";

    /** Clave del mensaje de estado "listo". */
    private static final String CLAVE_ESTADO_LISTO = "status.ready";

    /**
     * Deja el gestor en un estado conocido (español) antes de cada test.
     */
    @BeforeEach
    void setUp() {
        // Estado inicial conocido: español
        LanguageManager.setLanguage("es");
    }

    // ---------------------------------------------------------------
    // Idioma por defecto
    // ---------------------------------------------------------------

    /** Con el idioma por defecto los textos deben estar en español. */
    @Test
    void idiomaPorDefecto_es_espanol() {
        LanguageManager.setLanguage("es");

        assertEquals("Cifrar", LanguageManager.get(CLAVE_BOTON_CIFRAR));
        assertEquals("Descifrar", LanguageManager.get(CLAVE_BOTON_DESCIFRAR));
        assertEquals("Listo", LanguageManager.get(CLAVE_ESTADO_LISTO));
    }

    // ---------------------------------------------------------------
    // Cambio de idioma
    // ---------------------------------------------------------------

    /** Al cambiar a inglés, las mismas claves deben devolver textos en inglés. */
    @Test
    void cambiarAIngles_devuelveLosTextosEnIngles() {
        LanguageManager.setLanguage("en");

        assertEquals("Encrypt", LanguageManager.get(CLAVE_BOTON_CIFRAR));
        assertEquals("Decrypt", LanguageManager.get(CLAVE_BOTON_DESCIFRAR));
        assertEquals("Ready", LanguageManager.get(CLAVE_ESTADO_LISTO));
    }

    /** Al cambiar a euskera, las mismas claves deben devolver textos en euskera. */
    @Test
    void cambiarAEuskara_devuelveLosTextosEnEuskera() {
        LanguageManager.setLanguage("eus");

        assertEquals("Zifratu", LanguageManager.get(CLAVE_BOTON_CIFRAR));
        assertEquals("Deszifratu", LanguageManager.get(CLAVE_BOTON_DESCIFRAR));
        assertEquals("Prest", LanguageManager.get(CLAVE_ESTADO_LISTO));
    }

    /** Si se cambia de idioma varias veces, se aplica siempre el último. */
    @Test
    void cambiarDeIdiomaDosVeces_aplicaElUltimoSeleccionado() {
        LanguageManager.setLanguage("en");
        LanguageManager.setLanguage("eus");

        assertEquals("Zifratu", LanguageManager.get(CLAVE_BOTON_CIFRAR));

        LanguageManager.setLanguage("es");

        assertEquals("Cifrar", LanguageManager.get(CLAVE_BOTON_CIFRAR));
    }

    /** La misma clave debe traducirse de forma distinta en los tres idiomas. */
    @Test
    void losMismosTextosCambianSegunElIdioma() {
        String enEspañol = LanguageManager.get(CLAVE_BOTON_CIFRAR);

        LanguageManager.setLanguage("en");
        String enIngles = LanguageManager.get(CLAVE_BOTON_CIFRAR);

        LanguageManager.setLanguage("eus");
        String enEuskera = LanguageManager.get(CLAVE_BOTON_CIFRAR);

        assertNotEquals(enEspañol, enIngles, "Español e inglés deben diferir");
        assertNotEquals(enIngles, enEuskera, "Inglés y euskera deben diferir");
        assertNotEquals(enEspañol, enEuskera, "Español y euskera deben diferir");
    }

    // ---------------------------------------------------------------
    // Claves comunes a los tres ficheros de idioma
    // ---------------------------------------------------------------

    /**
     * Todas las claves que usa la interfaz deben estar definidas y sin valor
     * vacío en los tres ficheros de idioma.
     */
    @Test
    void todasLasClavesDeLosTresIdiomas_estanDefinidas() {
        String[] claves = {
                "app.title",
                "button.select", "button.encrypt", "button.decrypt",
                "password.prompt",
                "status.no_file", "status.finish", "status.ready",
                "alert.file_missing.title", "alert.file_missing.message",
                "alert.password_missing.title", "alert.password_missing.message",
                "alert.encrypt_success.title", "alert.encrypt_success.message",
                "alert.decrypt_success.title", "alert.decrypt_success.message",
                "alert.error.encrypt", "alert.error.decrypt",
                "alert.error.decrypt.notaes",
                "progress.encrypt", "progress.decrypt",
                "info.title", "info.header", "info.content",
                "tooltip.select", "tooltip.encrypt", "tooltip.decrypt",
                "tooltip.language", "tooltip.info"
        };

        for (String idioma : new String[]{"es", "en", "eus"}) {
            LanguageManager.setLanguage(idioma);

            for (String clave : claves) {
                String valor = LanguageManager.get(clave);
                assertNotEquals(
                        null,
                        valor,
                        "La clave '" + clave + "' no debe ser null en '" + idioma + "'");
                assertNotEquals(
                        "",
                        valor.trim(),
                        "La clave '" + clave + "' no debe estar vacía en '" + idioma + "'");
            }
        }
    }

    // ---------------------------------------------------------------
    // Manejo de errores
    // ---------------------------------------------------------------

    /** Pedir una clave inexistente debe lanzar MissingResourceException. */
    @Test
    void claveInexistente_lanzaMissingResourceException() {
        LanguageManager.setLanguage("es");

        assertThrows(
                MissingResourceException.class,
                () -> LanguageManager.get("clave.que.no.existe"));
    }

    /** Consultar dos veces la misma clave devuelve siempre el mismo valor. */
    @Test
    void get_devuelveElMismoValorEnLlamadasSucesivas() {
        LanguageManager.setLanguage("es");

        assertEquals(
                LanguageManager.get(CLAVE_BOTON_CIFRAR),
                LanguageManager.get(CLAVE_BOTON_CIFRAR));
    }
}
