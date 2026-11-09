package com.dm2.orden_del_auror.ui

import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.dm2.orden_del_auror.R
import com.dm2.orden_del_auror.files.FileManager

class CesarResultadoActivity : AppCompatActivity() {

    private lateinit var tvResultado: TextView

    private var textoDescifrado: String = ""

    private val guardarArchivo =
        registerForActivityResult(
            ActivityResultContracts.CreateDocument("text/plain")
        ) { uri: Uri? ->

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
        tvResultado = findViewById(R.id.tvResultadoCesar)
    }

    /**
     * Recupera el texto enviado desde CesarActivity.
     */
    private fun cargarResultado() {

        textoDescifrado =
            intent.getStringExtra(EXTRA_TEXTO) ?: ""

        tvResultado.text = textoDescifrado

        findViewById<TextView>(R.id.tvEstadoCesar).visibility =
            android.view.View.GONE

        findViewById<android.widget.ScrollView>(
            R.id.scrollResultadoCesar
        ).visibility = android.view.View.VISIBLE
    }

    /**
     * Configura los botones.
     */
    private fun configurarBotones() {

        findViewById<Button>(R.id.btnGuardarCesar)
            .setOnClickListener {
                solicitarGuardado()
            }

        findViewById<Button>(R.id.btnCompartirCesar)
            .setOnClickListener {
                compartirTexto()
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
     * Comparte el mensaje descifrado como archivo TXT.
     */
    private fun compartirTexto() {

        if (textoDescifrado.isEmpty()) {
            mostrarMensaje("No hay ningún mensaje que compartir.")
            return
        }

        try {

            val datos = textoDescifrado.toByteArray(Charsets.UTF_8)

            val uriCompartible = FileManager.crearUriCompartible(
                this,
                "mensaje_descifrado.txt",
                datos
            )

            val compartirIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_STREAM, uriCompartible)
                clipData = ClipData.newUri(
                    contentResolver,
                    "mensaje_descifrado.txt",
                    uriCompartible
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(
                Intent.createChooser(
                    compartirIntent,
                    "Compartir mensaje recuperado"
                )
            )

        } catch (e: Exception) {

            mostrarMensaje("No se ha podido compartir el mensaje.")
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
