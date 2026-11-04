package com.dm2.orden_del_auror.ui

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.dm2.orden_del_auror.R
import com.dm2.orden_del_auror.crypto.CesarCrypto

class CesarActivity : AppCompatActivity() {

    private lateinit var tvArchivoSeleccionado: TextView
    private lateinit var tvEstado: TextView
    private lateinit var etDesplazamiento: EditText
    private lateinit var etTextoDescifrado: EditText
    private lateinit var btnDescifrar: Button
    private lateinit var btnGuardar: Button

    private var archivoSeleccionado: Uri? = null
    private var textoDescifrado: String = ""

    // Selector de archivos TXT
    private val selectorArchivo =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->

            if (uri != null) {
                archivoSeleccionado = uri

                tvArchivoSeleccionado.text = obtenerNombreArchivo(uri)

                tvEstado.text = "Archivo seleccionado. Introduce el desplazamiento."

                etTextoDescifrado.setText("")
                textoDescifrado = ""
                btnGuardar.isEnabled = false
            }
        }

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

        tvEstado =
            findViewById(R.id.tvEstado)

        etDesplazamiento =
            findViewById(R.id.etDesplazamiento)

        etTextoDescifrado =
            findViewById(R.id.etTextoDescifrado)

        btnDescifrar =
            findViewById(R.id.btnDescifrar)

        btnGuardar =
            findViewById(R.id.btnGuardar)
    }

    /**
     * Configura las acciones de los botones.
     */
    private fun configurarBotones() {

        findViewById<Button>(R.id.btnSeleccionarArchivo)
            .setOnClickListener {
                seleccionarArchivo()
            }

        btnDescifrar.setOnClickListener {
            descifrarArchivo()
        }

        btnGuardar.setOnClickListener {
            solicitarGuardado()
        }

        // Volver a la pantalla principal.
        findViewById<Button>(R.id.btnVolver).setOnClickListener {
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
            textoDescifrado = CesarCrypto.descifrar(
                textoCifrado,
                desplazamiento
            )

            // Mostrar el resultado
            etTextoDescifrado.setText(textoDescifrado)

            tvEstado.text = "Mensaje descifrado correctamente."

            btnGuardar.isEnabled = true

            mostrarMensaje("Descifrado completado.")

        } catch (e: Exception) {

            tvEstado.text = "Error durante el descifrado."

            mostrarMensaje(
                "No se pudo descifrar el archivo: ${e.message}"
            )
        }
    }

    /**
     * Solicita dónde guardar el mensaje recuperado.
     */
    private fun solicitarGuardado() {

        if (textoDescifrado.isEmpty()) {
            mostrarMensaje("No hay ningún mensaje que guardar.")
            return
        }

        guardarArchivo.launch("mensaje_descifrado.txt")
    }

    /**
     * Guarda el texto descifrado en el archivo elegido.
     */
    private fun guardarTexto(uri: Uri) {

        try {

            contentResolver.openOutputStream(uri)?.use { output ->

                output.write(
                    textoDescifrado.toByteArray(Charsets.UTF_8)
                )
            } ?: throw Exception("No se pudo crear el archivo.")

            tvEstado.text = "Mensaje guardado correctamente."

            mostrarMensaje("Archivo guardado correctamente.")

        } catch (e: Exception) {

            tvEstado.text = "Error al guardar el archivo."

            mostrarMensaje(
                "No se pudo guardar: ${e.message}"
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