package com.ordenDelAuror.aescipher.utils;

import java.util.Locale;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gestor de idiomas de la aplicación.
 *
 * <p>Mantiene un {@link ResourceBundle} estático con los textos de los
 * ficheros {@code lang/messages_es.properties},
 * {@code lang/messages_en.properties} y {@code lang/messages_eus.properties},
 * y permite cambiar el idioma en tiempo de ejecución.</p>
 *
 * @author Henri Tejada
 */
public class LanguageManager {

    /** Logger de la clase (SLF4J + Logback). */
    private static final Logger logger = LoggerFactory.getLogger(LanguageManager.class);

    /** Bundle activo; la aplicación arranca en español ({@code es}). */
    private static ResourceBundle bundle =
            ResourceBundle.getBundle(
                    "lang.messages",
                    new Locale("es"));

    /**
     * Cambia el idioma activo de la aplicación recargando el bundle.
     *
     * @param languageCode código de idioma: {@code es}, {@code en} o {@code eus}
     */
    public static void setLanguage(
            String languageCode) {
        logger.info("Idioma cambiado a: "+ languageCode);
        bundle =
                ResourceBundle.getBundle(
                        "lang.messages",
                        new Locale(languageCode));
    }

    /**
     * Devuelve el texto asociado a una clave en el idioma activo.
     *
     * @param key clave del fichero de recursos, p. ej. {@code "button.encrypt"}
     * @return el texto traducido para el idioma activo
     * @throws java.util.MissingResourceException si la clave no existe en el bundle activo
     */
    public static String get(
            String key) {

        return bundle.getString(key);
    }
}