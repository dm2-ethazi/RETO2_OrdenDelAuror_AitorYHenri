package com.ordenDelAuror.aescipher.utils;

import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {

    private static ResourceBundle bundle =
            ResourceBundle.getBundle(
                    "com.ordenDelAuror.aescipher.lang.messages",
                    new Locale("es"));

    public static void setLanguage(
            String languageCode) {

        bundle =
                ResourceBundle.getBundle(
                        "com.ordenDelAuror.aescipher.lang.messages",
                        new Locale(languageCode));
    }

    public static String get(
            String key) {

        return bundle.getString(key);
    }
}