
package com.dm2.orden_del_auror.ui

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.dm2.orden_del_auror.R

class CesarResultadoActivity : AppCompatActivity() {

    private lateinit var tvResultado: TextView

    private var textoDescifrado: String = ""

    // Selector de ubicación para guardar el resultado
    private val guardarArchivo =
        registerForActivityResult(
            ActivityResultContracts.CreateDocument("text/plain")
        ) { uri ->

            if (uri != null) {
                guardarTexto(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cesar_resultado)

        inicializarVistas()
        cargarResultado()
        configurarBotones()
    }

    /**
     * Inicializa los componentes.
     */
    private fun inicializarVistas() {

        tvResultado =
            findViewById(R.id.tvResultadoCesar)
    }

    /**
     * Recupera el texto enviado desde CesarActivity.
     */
    private fun cargarResultado() {

        textoDescifrado =
            intent.getStringExtra(EXTRA_TEXTO) ?: ""

        tvResultado.text = textoDescifrado
    }

    /**
     * Configura los botones.
     */
    private fun configurarBotones() {

        findViewById<Button>(R.id.btnGuardarCesar)
            .setOnClickListener {
                solicitarGuardado()
            }

        findViewById<ImageButton>(R.id.btnVolverResultado)
            .setOnClickListener {
                finish()
            }
    }

    /**
     * Solicita dónde guardar el mensaje.
     */
    private fun solicitarGuardado() {

        if (textoDescifrado.isEmpty()) {
            mostrarMensaje("No hay ningún mensaje que guardar.")
            return
        }

        guardarArchivo.launch("mensaje_descifrado.txt")
    }

    /**
     * Guarda el mensaje en el archivo elegido.
     */
    private fun guardarTexto(uri: Uri) {

        try {

            contentResolver.openOutputStream(uri)?.use { output ->

                output.write(
                    textoDescifrado.toByteArray(Charsets.UTF_8)
                )

            } ?: throw Exception("No se pudo crear el archivo.")

            mostrarMensaje("Archivo guardado correctamente.")

        } catch (e: Exception) {

            mostrarMensaje(
                "No se pudo guardar el archivo: ${e.message}"
            )
        }
    }

    /**
     * Muestra un mensaje al usuario.
     */
    private fun mostrarMensaje(mensaje: String) {

        Toast.makeText(
            this,
            mensaje,
            Toast.LENGTH_SHORT
        ).show()
    }

    companion object {
        const val EXTRA_TEXTO = "texto_descifrado_cesar"
    }
}
