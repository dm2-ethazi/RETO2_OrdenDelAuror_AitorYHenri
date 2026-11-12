package com.ordenDelAuror.aescipher.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Tests de {@link LanguageOption}, la opción de idioma del ComboBox.
 *
 * <p>Verifica el código de idioma, el nombre visible que se muestra en la
 * interfaz y la coherencia entre ambos.</p>
 *
 * @author Henri Tejada
 */
class LanguageOptionTest {

    /** getCode debe devolver el código de idioma pasado al constructor. */
    @Test
    void getCode_devuelveElCodigoDelIdioma() {
        LanguageOption espanol = new LanguageOption("es", "Español");
        LanguageOption ingles = new LanguageOption("en", "English");
        LanguageOption euskera = new LanguageOption("eus", "Euskara");

        assertEquals("es", espanol.getCode());
        assertEquals("en", ingles.getCode());
        assertEquals("eus", euskera.getCode());
    }

    /** toString debe devolver el nombre visible, que es lo que pinta el ComboBox. */
    @Test
    void toString_devuelveElNombreVisibleParaElComboBox() {
        LanguageOption espanol = new LanguageOption("es", "Español");
        LanguageOption ingles = new LanguageOption("en", "English");
        LanguageOption euskera = new LanguageOption("eus", "Euskara");

        assertEquals("Español", espanol.toString());
        assertEquals("English", ingles.toString());
        assertEquals("Euskara", euskera.toString());
    }

    /** Las opciones que usa la aplicación deben tener los códigos esperados. */
    @Test
    void lasTresOpcionesQueUsaLaAplicacion_tienenCodigosEsperados() {
        // Mismas opciones que añade MainController.initialize()
        LanguageOption[] opciones = {
                new LanguageOption("es", "Español"),
                new LanguageOption("en", "English"),
                new LanguageOption("eus", "Euskara")
        };

        assertEquals("es", opciones[0].getCode());
        assertEquals("en", opciones[1].getCode());
        assertEquals("eus", opciones[2].getCode());
    }

    /** Dos opciones distintas no deben compartir código ni nombre visible. */
    @Test
    void opcionesDistintas_noTienenElMismoCodigo() {
        LanguageOption espanol = new LanguageOption("es", "Español");
        LanguageOption ingles = new LanguageOption("en", "English");

        assertNotEquals(espanol.getCode(), ingles.getCode());
        assertNotEquals(espanol.toString(), ingles.toString());
    }

    /** Dos opciones con el mismo código pueden tener distinto nombre visible. */
    @Test
    void toString_reflejaElCambioDeNombreVisible() {
        // El mismo código con distinto nombre visible se muestra distinto
        LanguageOption nombreCorto = new LanguageOption("en", "EN");
        LanguageOption nombreLargo = new LanguageOption("en", "English");

        assertEquals("EN", nombreCorto.toString());
        assertEquals("English", nombreLargo.toString());
        assertEquals(nombreCorto.getCode(), nombreLargo.getCode());
    }
}
