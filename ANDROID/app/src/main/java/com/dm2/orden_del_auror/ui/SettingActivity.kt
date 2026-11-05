package com.dm2.orden_del_auror.ui

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.dm2.orden_del_auror.R
import java.util.Locale

/**
 * Pantalla de ajustes de la aplicación.
 *
 * Permite cambiar el idioma y consultar
 * información sobre la aplicación.
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

        val idiomas = resources.getStringArray(R.array.languages)

        val adaptador = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            idiomas
        ) {

            override fun getView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                return crearVistaIdioma(
                    position,
                    convertView,
                    false
                )
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                return crearVistaIdioma(
                    position,
                    convertView,
                    true
                )
            }
        }

        spinnerIdioma.adapter = adaptador

        val locales = AppCompatDelegate.getApplicationLocales()

        val idiomaActual = if (!locales.isEmpty) {
            locales[0]?.language ?: Locale.getDefault().language
        } else {
            Locale.getDefault().language
        }

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
                    // No se requiere ninguna acción.
                }
            }
    }

    /**
     * Crea las vistas de las opciones del Spinner.
     *
     * Las opciones del desplegable tienen 55dp de altura.
     * La opción seleccionada mantiene una altura de 42dp.
     */
    private fun crearVistaIdioma(
        position: Int,
        convertView: View?,
        esDesplegable: Boolean
    ): View {

        val texto = (convertView as? TextView) ?: TextView(this)

        texto.text = resources.getStringArray(R.array.languages)[position]
        texto.setTextColor(getColor(R.color.texto_claro))
        texto.textSize = 14f
        texto.gravity = Gravity.CENTER_VERTICAL

        val paddingHorizontal = dpApx(16)
        val paddingVertical = if (esDesplegable) dpApx(12) else dpApx(10)

        texto.setPadding(
            paddingHorizontal,
            paddingVertical,
            paddingHorizontal,
            paddingVertical
        )

        texto.setBackgroundColor(getColor(R.color.azul_hechicero))

        val altura = if (esDesplegable) dpApx(55) else dpApx(42)

        texto.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            altura
        )

        return texto
    }

    /**
     * Convierte dp a píxeles.
     */
    private fun dpApx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    /**
     * Cambia el idioma de toda la aplicación.
     */
    private fun cambiarIdioma(idioma: String) {

        val locales = AppCompatDelegate.getApplicationLocales()

        val idiomaActual = if (!locales.isEmpty) {
            locales[0]?.language
        } else {
            Locale.getDefault().language
        }

        if (idiomaActual == idioma) return

        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(idioma)
        )
    }

    /**
     * Configura el botón de volver.
     */
    private fun configurarBotonVolver() {

        findViewById<ImageButton>(R.id.btnVolverAjustes)
            .setOnClickListener {
                finish()
            }
    }
}
