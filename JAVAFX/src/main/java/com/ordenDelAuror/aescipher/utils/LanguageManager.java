package com.ordenDelAuror.aescipher.utils;

import java.util.Locale;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LanguageManager {
    private static final Logger logger = LoggerFactory.getLogger(LanguageManager.class);
    private static ResourceBundle bundle =
            ResourceBundle.getBundle(
                    "lang.messages",
                    new Locale("es"));

    public static void setLanguage(
            String languageCode) {
        logger.info("Idioma cambiado a: "+ languageCode);
        bundle =
                ResourceBundle.getBundle(
                        "lang.messages",
                        new Locale(languageCode));
    }

    public static String get(
            String key) {

        return bundle.getString(key);
    }
}