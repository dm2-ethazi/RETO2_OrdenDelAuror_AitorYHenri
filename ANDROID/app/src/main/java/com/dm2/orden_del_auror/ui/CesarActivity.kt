
package com.dm2.orden_del_auror.ui

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dm2.orden_del_auror.R
import com.dm2.orden_del_auror.crypto.CesarCrypto
import com.dm2.orden_del_auror.files.FileManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

/** Pantalla de selección y descifrado de archivos TXT mediante César. */
class CesarActivity : AppCompatActivity() {

    private lateinit var tvArchivoSeleccionado: TextView
    private lateinit var etDesplazamiento: EditText
    private lateinit var btnDescifrar: Button

    private var archivoSeleccionado: Uri? = null
    private var dialogoCarga: AlertDialog? = null

    // Selector de documentos: solamente solicita archivos de texto.
    private val selectorArchivo =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->

            if (uri != null) {
                val nombreArchivo =
                    FileManager.obtenerNombreArchivo(this, uri)

                // Comprobar que realmente tenga extensión TXT.
                if (!esArchivoTxt(nombreArchivo)) {
                    archivoSeleccionado = null

                    tvArchivoSeleccionado.text =
                        getString(R.string.cesar_error_select_file)

                    mostrarMensaje(
                        getString(R.string.cesar_error_select_file)
                    )
                } else {
                    archivoSeleccionado = uri
                    tvArchivoSeleccionado.text = nombreArchivo
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cesar)

        tvArchivoSeleccionado =
            findViewById(R.id.tvArchivoSeleccionado)

        etDesplazamiento =
            findViewById(R.id.etDesplazamiento)

        btnDescifrar =
            findViewById(R.id.btnDescifrar)

        findViewById<Button>(R.id.btnSeleccionarArchivo)
            .setOnClickListener {
                // Ya no se permite application/octet-stream ni */*.
                selectorArchivo.launch(arrayOf("text/plain"))
            }

        btnDescifrar.setOnClickListener {
            descifrarArchivo()
        }

        findViewById<ImageButton>(R.id.btnVolver)
            .setOnClickListener {
                finish()
            }
    }

    /**
     * Comprueba la extensión del archivo sin distinguir
     * entre mayúsculas y minúsculas.
     */
    private fun esArchivoTxt(nombre: String): Boolean {
        return nombre.trim().endsWith(".txt", ignoreCase = true)
    }

    private fun descifrarArchivo() {

        val uri = archivoSeleccionado

        if (uri == null) {
            mostrarMensaje(
                getString(R.string.cesar_error_select_file)
            )
            return
        }

        val desplazamientoTexto =
            etDesplazamiento.text.toString().trim()

        if (desplazamientoTexto.isEmpty()) {
            etDesplazamiento.error =
                getString(R.string.cesar_error_empty_shift)

            etDesplazamiento.requestFocus()
            return
        }

        val desplazamiento =
            desplazamientoTexto.toIntOrNull()

        if (desplazamiento == null || desplazamiento !in 0..25) {
            etDesplazamiento.error =
                getString(R.string.cesar_error_invalid_shift)

            etDesplazamiento.requestFocus()
            return
        }

        btnDescifrar.isEnabled = false
        mostrarDialogoCarga()

        lifecycleScope.launch {

            var archivoResultado: File? = null

            try {
                val nombreOriginal = withContext(Dispatchers.IO) {
                    FileManager.obtenerNombreArchivo(
                        this@CesarActivity,
                        uri
                    )
                }

                // Segunda comprobación antes de procesar el archivo.
                if (!esArchivoTxt(nombreOriginal)) {
                    throw IOException("El archivo seleccionado no es TXT.")
                }

                archivoResultado = withContext(Dispatchers.IO) {

                    val directorio = File(cacheDir, "cesar")

                    if (!directorio.exists() && !directorio.mkdirs()) {
                        throw IOException(
                            "No se pudo crear el almacenamiento temporal."
                        )
                    }

                    val destino = File(
                        directorio,
                        "resultado_${System.currentTimeMillis()}.txt"
                    )

                    try {
                        val input =
                            contentResolver.openInputStream(uri)
                                ?: throw IOException(
                                    "No se pudo abrir el archivo."
                                )

                        input.use { entrada ->
                            destino.outputStream().buffered().use { salida ->

                                // Descifrado por bloques, sin cargar
                                // todo el archivo en memoria.
                                CesarCrypto.descifrarArchivo(
                                    entrada,
                                    salida,
                                    desplazamiento
                                )
                            }
                        }

                        if (!destino.exists() || destino.length() == 0L) {
                            throw IOException("El resultado está vacío.")
                        }

                        destino

                    } catch (e: Exception) {
                        destino.delete()
                        throw e
                    }
                }

                ocultarDialogoCarga()

                // Preparar el nombre del archivo resultante.
                val nombreBase =
                    nombreOriginal.substring(0, nombreOriginal.length - 4)

                val intent = Intent(
                    this@CesarActivity,
                    CesarResultadoActivity::class.java
                ).apply {
                    putExtra(
                        CesarResultadoActivity.EXTRA_RUTA_ARCHIVO,
                        archivoResultado!!.absolutePath
                    )

                    putExtra(
                        CesarResultadoActivity.EXTRA_NOMBRE,
                        "${nombreBase}_descifrado.txt"
                    )
                }

                startActivity(intent)

            } catch (e: CancellationException) {
                archivoResultado?.delete()
                throw e

            } catch (e: SecurityException) {
                archivoResultado?.delete()
                mostrarMensaje(
                    getString(R.string.error_file_permission)
                )

            } catch (e: IOException) {
                archivoResultado?.delete()
                mostrarMensaje(
                    getString(R.string.cesar_error_read_file)
                )

            } catch (e: IllegalArgumentException) {
                archivoResultado?.delete()
                mostrarMensaje(
                    getString(R.string.cesar_error_invalid_shift)
                )

            } catch (e: Exception) {
                archivoResultado?.delete()
                mostrarMensaje(
                    getString(R.string.cesar_error_unexpected)
                )

            } finally {
                ocultarDialogoCarga()

                if (!isFinishing && !isDestroyed) {
                    btnDescifrar.isEnabled = true
                }
            }
        }
    }

    /**
     * Muestra el diálogo mientras se está descifrando el TXT.
     */
    private fun mostrarDialogoCarga() {

        val contenido = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.HORIZONTAL
            setPadding(48, 32, 32, 32)
            gravity = android.view.Gravity.CENTER_VERTICAL

            addView(
                ProgressBar(this@CesarActivity).apply {
                    isIndeterminate = true
                }
            )

            addView(
                TextView(this@CesarActivity).apply {
                    text = getString(R.string.cesar_decrypting_message)
                    setPadding(32, 0, 0, 0)
                }
            )
        }

        dialogoCarga = AlertDialog.Builder(this)
            .setTitle(R.string.cesar_decrypting_title)
            .setView(contenido)
            .setCancelable(false)
            .create()

        dialogoCarga?.show()
    }

    /**
     * Oculta el diálogo de carga.
     */
    private fun ocultarDialogoCarga() {

        if (!isFinishing && !isDestroyed) {
            dialogoCarga?.dismiss()
        }

        dialogoCarga = null
    }

    /**
     * Muestra un mensaje sin cerrar la aplicación.
     */
    private fun mostrarMensaje(mensaje: String) {

        if (!isFinishing && !isDestroyed) {
            Toast.makeText(
                this,
                mensaje,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroy() {
        ocultarDialogoCarga()
        super.onDestroy()
    }
}
