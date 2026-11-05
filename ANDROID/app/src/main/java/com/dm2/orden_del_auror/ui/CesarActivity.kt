
package com.dm2.orden_del_auror.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.dm2.orden_del_auror.R
import com.dm2.orden_del_auror.crypto.CesarCrypto

class CesarActivity : AppCompatActivity() {

    private lateinit var tvArchivoSeleccionado: TextView
    private lateinit var etDesplazamiento: EditText

    private var archivoSeleccionado: Uri? = null

    // Selector de archivos TXT
    private val selectorArchivo =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->

            if (uri != null) {
                archivoSeleccionado = uri

                tvArchivoSeleccionado.text = obtenerNombreArchivo(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cesar)

        inicializarVistas()
        configurarBotones()
    }

    /**
     * Inicializa los componentes de la interfaz.
     */
    private fun inicializarVistas() {

        tvArchivoSeleccionado =
            findViewById(R.id.tvArchivoSeleccionado)

        etDesplazamiento =
            findViewById(R.id.etDesplazamiento)
    }

    /**
     * Configura las acciones de los botones.
     */
    private fun configurarBotones() {

        findViewById<Button>(R.id.btnSeleccionarArchivo)
            .setOnClickListener {
                seleccionarArchivo()
            }

        findViewById<Button>(R.id.btnDescifrar)
            .setOnClickListener {
                descifrarArchivo()
            }

        findViewById<ImageButton>(R.id.btnVolver)
            .setOnClickListener {
                finish()
            }
    }

    /**
     * Abre el selector de archivos del dispositivo.
     */
    private fun seleccionarArchivo() {

        selectorArchivo.launch(
            arrayOf("text/plain")
        )
    }

    /**
     * Descifra el contenido del archivo seleccionado.
     */
    private fun descifrarArchivo() {

        val uri = archivoSeleccionado

        if (uri == null) {
            mostrarMensaje("Primero debes seleccionar un archivo.")
            return
        }

        val desplazamientoTexto =
            etDesplazamiento.text.toString().trim()

        if (desplazamientoTexto.isEmpty()) {
            etDesplazamiento.error = "Introduce un desplazamiento"
            return
        }

        val desplazamiento =
            desplazamientoTexto.toIntOrNull()

        if (desplazamiento == null || desplazamiento !in 0..25) {
            etDesplazamiento.error = "Introduce un número entre 0 y 25"
            return
        }

        try {

            // Leer el contenido del archivo
            val textoCifrado = contentResolver
                .openInputStream(uri)
                ?.bufferedReader(Charsets.UTF_8)
                ?.use { it.readText() }
                ?: throw Exception("No se pudo leer el archivo.")

            if (textoCifrado.isEmpty()) {
                mostrarMensaje("El archivo está vacío.")
                return
            }

            // Aplicar el algoritmo César
            val textoDescifrado = CesarCrypto.descifrar(
                textoCifrado,
                desplazamiento
            )

            // Abrir la pantalla del resultado
            val intent = Intent(
                this,
                CesarResultadoActivity::class.java
            )

            intent.putExtra(
                CesarResultadoActivity.EXTRA_TEXTO,
                textoDescifrado
            )

            startActivity(intent)

        } catch (e: Exception) {

            mostrarMensaje(
                "No se pudo descifrar el archivo: ${e.message}"
            )
        }
    }

    /**
     * Obtiene el nombre del archivo seleccionado.
     */
    private fun obtenerNombreArchivo(uri: Uri): String {

        var nombre = "Archivo seleccionado"

        contentResolver.query(
            uri,
            null,
            null,
            null,
            null
        )?.use { cursor ->

            val columna =
                cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)

            if (cursor.moveToFirst() && columna >= 0) {
                nombre = cursor.getString(columna)
            }
        }

        return nombre
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
}
