package com.dm2.orden_del_auror.ui

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dm2.orden_del_auror.R
import com.dm2.orden_del_auror.crypto.AESCrypto
import com.dm2.orden_del_auror.files.FileManager
import com.dm2.orden_del_auror.utils.AppUtils
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException

class AesActivity : AppCompatActivity() {

    private lateinit var tvArchivoSeleccionado: TextView
    private lateinit var etContrasena: EditText
    private lateinit var btnDescifrar: Button

    private var archivoSeleccionado: Uri? = null
    private var dialogoDescifrado: AlertDialog? = null

    companion object {
        private const val TAG = "PRUEBA_AES"
    }

    private val selectorArchivo =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                val nombreArchivo = FileManager.obtenerNombreArchivo(this, uri)

                if (!nombreArchivo.endsWith(".png.aes", ignoreCase = true)) {
                    archivoSeleccionado = null
                    tvArchivoSeleccionado.text =
                        getString(R.string.aes_no_file_selected)

                    AppUtils.mostrarMensaje(
                        this,
                        getString(R.string.aes_error_file_extension)
                    )
                } else {
                    archivoSeleccionado = uri
                    tvArchivoSeleccionado.text = nombreArchivo
                    Log.d(TAG, "Archivo seleccionado: $nombreArchivo")
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aes)
        inicializarVistas()
        configurarBotones()
    }

    private fun inicializarVistas() {
        tvArchivoSeleccionado = findViewById(R.id.tvArchivoSeleccionado)
        etContrasena = findViewById(R.id.etContrasena)
        btnDescifrar = findViewById(R.id.btnDescifrar)
    }

    private fun configurarBotones() {
        findViewById<Button>(R.id.btnSeleccionarArchivo).setOnClickListener {
            seleccionarArchivo()
        }

        btnDescifrar.setOnClickListener {
            descifrarImagen()
        }

        findViewById<ImageButton>(R.id.btnVolver).setOnClickListener {
            finish()
        }
    }

    private fun seleccionarArchivo() {
        selectorArchivo.launch(arrayOf("*/*"))
    }

    /**
     * Muestra una alerta no cancelable mientras se descifra el archivo.
     * Se crea por código para no necesitar modificar los XML.
     */
    private fun mostrarDialogoDescifrado() {
        val contenido = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(48, 32, 48, 16)
        }

        val progreso = ProgressBar(this).apply {
            isIndeterminate = true
        }

        val mensaje = TextView(this).apply {
            text = getString(R.string.aes_decrypting_image)
            textSize = 16f
            setPadding(32, 0, 0, 0)
        }

        contenido.addView(
            progreso,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
        contenido.addView(
            mensaje,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        dialogoDescifrado = AlertDialog.Builder(this)
            .setTitle(getString(R.string.aes_decrypting_title))
            .setView(contenido)
            .setCancelable(false)
            .create()

        dialogoDescifrado?.show()
    }

    private fun ocultarDialogoDescifrado() {
        dialogoDescifrado?.dismiss()
        dialogoDescifrado = null
    }

    private fun descifrarImagen() {
        val uri = archivoSeleccionado

        if (uri == null) {
            AppUtils.mostrarMensaje(this, getString(R.string.aes_error_select_file))
            return
        }

        val contrasena = etContrasena.text.toString()
        if (contrasena.isBlank()) {
            etContrasena.error = getString(R.string.aes_no_password)
            etContrasena.requestFocus()
            return
        }

        btnDescifrar.isEnabled = false
        mostrarDialogoDescifrado()
        Log.d(TAG, "Comenzando descifrado AES por bloques")

        lifecycleScope.launch {
            var archivoTemporal: File? = null

            try {
                val nombreOriginal =
                    FileManager.obtenerNombreArchivo(this@AesActivity, uri)
                val nombreDescifrado =
                    FileManager.generarNombreDescifrado(nombreOriginal)

                archivoTemporal = File(
                    cacheDir,
                    "aes_${System.currentTimeMillis()}_$nombreDescifrado"
                )

                withContext(Dispatchers.IO) {
                    val input = contentResolver.openInputStream(uri)
                        ?: throw IOException("No se pudo abrir el archivo seleccionado.")

                    input.use { entrada ->
                        FileOutputStream(archivoTemporal!!).use { salida ->
                            AESCrypto.descifrarArchivo(
                                entrada,
                                salida,
                                contrasena
                            )
                        }
                    }

                    Log.d(
                        TAG,
                        "Archivo descifrado en disco: ${archivoTemporal!!.length()} bytes"
                    )

                    // Validar dimensiones sin decodificar todos los píxeles.
                    val opciones = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }
                    BitmapFactory.decodeFile(archivoTemporal!!.absolutePath, opciones)

                    if (opciones.outWidth <= 0 || opciones.outHeight <= 0) {
                        throw IOException(getString(R.string.aes_error_invalid_image))
                    }

                    Log.d(
                        TAG,
                        "Imagen validada: ${opciones.outWidth}x${opciones.outHeight}"
                    )
                }

                val intent = Intent(
                    this@AesActivity,
                    AesResultadoActivity::class.java
                ).apply {
                    putExtra(
                        AesResultadoActivity.EXTRA_RUTA_IMAGEN,
                        archivoTemporal!!.absolutePath
                    )
                    putExtra(
                        AesResultadoActivity.EXTRA_NOMBRE,
                        nombreDescifrado
                    )
                }

                startActivity(intent)

            } catch (e: CancellationException) {
                throw e
            } catch (e: BadPaddingException) {
                Log.e(TAG, "Contraseña incorrecta o archivo dañado", e)
                archivoTemporal?.delete()
                mostrarError(getString(R.string.aes_error_wrong_password))
            } catch (e: IllegalBlockSizeException) {
                Log.e(TAG, "Tamaño de archivo cifrado incorrecto", e)
                archivoTemporal?.delete()
                mostrarError(getString(R.string.aes_error_invalid_encrypted_file))
            } catch (e: SecurityException) {
                Log.e(TAG, "Error de permisos", e)
                archivoTemporal?.delete()
                mostrarError(getString(R.string.error_file_permission))
            } catch (e: IOException) {
                Log.e(TAG, "Error de lectura o validación", e)
                archivoTemporal?.delete()
                mostrarError(getString(R.string.aes_error_read_image))
            } catch (e: IllegalArgumentException) {
                Log.e(TAG, "Datos AES no válidos", e)
                archivoTemporal?.delete()
                mostrarError(getString(R.string.error_invalid_data))
            } catch (e: Exception) {
                Log.e(TAG, "Error inesperado durante el descifrado", e)
                archivoTemporal?.delete()
                mostrarError(getString(R.string.aes_error_unexpected))
            } finally {
                ocultarDialogoDescifrado()
                if (!isFinishing && !isDestroyed) {
                    btnDescifrar.isEnabled = true
                }
            }
        }
    }

    private fun mostrarError(mensaje: String) {
        if (!isFinishing && !isDestroyed) {
            AppUtils.mostrarMensaje(this, mensaje)
        }
    }

    override fun onDestroy() {
        ocultarDialogoDescifrado()
        super.onDestroy()
    }
}
