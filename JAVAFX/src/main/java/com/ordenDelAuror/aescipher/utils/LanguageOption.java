package com.ordenDelAuror.aescipher.utils;

/**
 * Opción de idioma mostrada en el ComboBox de idiomas de la interfaz.
 *
 * <p>Combina el código de idioma, que es el que se usa internamente, con el
 * nombre visible que se muestra en la ventana.</p>
 *
 * @author Henri Tejada
 */
public class LanguageOption {

    /** Código de idioma: {@code es}, {@code en} o {@code eus}. */
    private final String code;

    /** Nombre del idioma tal y como aparece en la interfaz. */
    private final String displayName;

    /**
     * Crea una opción de idioma.
     *
     * @param code        código de idioma entendido por {@link LanguageManager}
     * @param displayName nombre visible del idioma en el ComboBox
     */
    public LanguageOption(
            String code,
            String displayName) {

        this.code = code;
        this.displayName = displayName;
    }

    /**
     * Devuelve el código de idioma.
     *
     * @return código de idioma
     */
    public String getCode() {
        return code;
    }

    /**
     * Devuelve el nombre visible, que es lo que muestra el ComboBox.
     *
     * @return nombre visible del idioma
     */
    @Override
    public String toString() {
        return displayName;
    }
}