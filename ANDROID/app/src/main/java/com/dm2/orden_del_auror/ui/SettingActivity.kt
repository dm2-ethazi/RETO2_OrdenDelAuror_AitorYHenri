package com.dm2.orden_del_auror.ui

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.dm2.orden_del_auror.R
import java.util.Locale

/**
 * Pantalla de ajustes de la aplicación.
 *
 * Permite seleccionar el idioma de la interfaz.
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var spinnerIdioma: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        spinnerIdioma = findViewById(R.id.spinnerIdioma)

        configurarIdioma()
        configurarBotonVolver()
    }

    /**
     * Configura el desplegable de idiomas.
     */
    private fun configurarIdioma() {

        // Detectar el idioma que está utilizando actualmente la aplicación.
        val idiomasAplicacion =
            AppCompatDelegate.getApplicationLocales()

        val idiomaActual = if (!idiomasAplicacion.isEmpty) {
            idiomasAplicacion[0]?.language ?: Locale.getDefault().language
        } else {
            Locale.getDefault().language
        }

        // Seleccionar el idioma actual en el desplegable.
        val posicionInicial = when (idiomaActual) {
            "en" -> 1
            "eu" -> 2
            else -> 0
        }

        spinnerIdioma.setSelection(posicionInicial, false)

        spinnerIdioma.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    val idiomaSeleccionado = when (position) {
                        1 -> "en"
                        2 -> "eu"
                        else -> "es"
                    }

                    cambiarIdioma(idiomaSeleccionado)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // No es necesario realizar ninguna acción.
                }
            }
    }

    /**
     * Cambia el idioma de toda la aplicación.
     */
    private fun cambiarIdioma(idioma: String) {

        val idiomasActuales =
            AppCompatDelegate.getApplicationLocales()

        val idiomaActual = if (!idiomasActuales.isEmpty) {
            idiomasActuales[0]?.language
        } else {
            Locale.getDefault().language
        }

        if (idiomaActual == idioma) {
            return
        }

        val nuevaConfiguracion =
            LocaleListCompat.forLanguageTags(idioma)

        // AppCompat guarda la preferencia y actualiza las actividades.
        AppCompatDelegate.setApplicationLocales(nuevaConfiguracion)
    }

    /**
     * Configura el botón para regresar al menú principal.
     */
    private fun configurarBotonVolver() {

        findViewById<Button>(R.id.btnVolverAjustes)
            .setOnClickListener {
                finish()
            }
    }
}
