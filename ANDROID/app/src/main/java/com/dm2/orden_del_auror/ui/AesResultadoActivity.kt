package com.dm2.orden_del_auror.ui

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dm2.orden_del_auror.R
import com.dm2.orden_del_auror.files.FileManager
import com.dm2.orden_del_auror.utils.AppUtils
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class AesResultadoActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_RUTA_IMAGEN = "ruta_imagen_descifrada"
        const val EXTRA_NOMBRE = "nombre_imagen"
    }

    private var archivoImagen: File? = null
    private var nombreArchivo: String = "imagen_descifrada.png"
    private lateinit var tvEstado: TextView
    private var dialogoCarga: AlertDialog? = null

    private val selectorGuardar =
        registerForActivityResult(ActivityResultContracts.CreateDocument("image/png")) {
                uri: Uri? ->
            if (uri != null) guardarImagen(uri)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aes_resultado)

        tvEstado = findViewById(R.id.tvEstadoResultado)
        configurarBotones()
        mostrarImagen()
    }

    /** Muestra una alerta mientras se comprueba y decodifica la imagen en segundo plano. */
    private fun mostrarImagen() {
        val rutaImagen = intent.getStringExtra(EXTRA_RUTA_IMAGEN)
        nombreArchivo = intent.getStringExtra(EXTRA_NOMBRE)
            ?: "imagen_descifrada.png"

        if (rutaImagen.isNullOrBlank()) {
            mostrarError(getString(R.string.aes_result_error_missing_path))
            return
        }

        val archivo = File(rutaImagen)
        if (!archivo.exists() || !archivo.isFile) {
            mostrarError(getString(R.string.aes_result_error_not_found))
            return
        }

        if (archivo.length() == 0L) {
            mostrarError(getString(R.string.error_empty_file))
            return
        }

        val imageView = findViewById<ImageView>(R.id.ivImagenDescifrada)
        val anchoObjetivo = imageView.width.takeIf { it > 0 } ?: 1600
        val altoObjetivo = imageView.height.takeIf { it > 0 } ?: 1600

        mostrarDialogoCarga()

        lifecycleScope.launch {
            try {
                val bitmap = withContext(Dispatchers.IO) {
                    // Primero leer solo las dimensiones, sin cargar los píxeles.
                    val opcionesBounds = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }
                    BitmapFactory.decodeFile(archivo.absolutePath, opcionesBounds)

                    if (opcionesBounds.outWidth <= 0 || opcionesBounds.outHeight <= 0) {
                        return@withContext null
                    }

                    // Calcular una versión reducida para evitar consumir demasiada RAM.
                    val opcionesFinales = BitmapFactory.Options().apply {
                        inSampleSize = calcularInSampleSize(
                            opcionesBounds.outWidth,
                            opcionesBounds.outHeight,
                            anchoObjetivo,
                            altoObjetivo
                        )
                        inPreferredConfig = Bitmap.Config.RGB_565
                    }

                    BitmapFactory.decodeFile(archivo.absolutePath, opcionesFinales)
                }

                if (bitmap == null) {
                    mostrarError(getString(R.string.aes_error_invalid_image))
                    return@launch
                }

                archivoImagen = archivo
                imageView.setImageBitmap(bitmap)
                imageView.visibility = View.VISIBLE

                findViewById<ScrollView>(R.id.scrollImagenDescifrada)
                    ?.visibility = View.VISIBLE
                tvEstado.visibility = View.GONE

            } catch (e: CancellationException) {
                throw e
            } catch (e: OutOfMemoryError) {
                mostrarError(getString(R.string.aes_result_error_load))
            } catch (e: Exception) {
                mostrarError(getString(R.string.aes_result_error_load))
            } finally {
                ocultarDialogoCarga()
            }
        }
    }

    private fun mostrarDialogoCarga() {
        val progressBar = ProgressBar(this).apply {
            isIndeterminate = true
        }

        dialogoCarga = AlertDialog.Builder(this)
            .setTitle(R.string.aes_result_loading_image)
            .setView(progressBar)
            .setCancelable(false)
            .create()

        dialogoCarga?.show()
    }

    private fun ocultarDialogoCarga() {
        dialogoCarga?.dismiss()
        dialogoCarga = null
    }

    private fun calcularInSampleSize(
        ancho: Int,
        alto: Int,
        anchoObjetivo: Int,
        altoObjetivo: Int
    ): Int {
        var sample = 1
        while (ancho / (sample * 2) >= anchoObjetivo &&
            alto / (sample * 2) >= altoObjetivo
        ) {
            sample *= 2
        }
        return sample
    }

    private fun mostrarError(mensaje: String) {
        tvEstado.text = mensaje
        tvEstado.visibility = View.VISIBLE
        findViewById<ImageView>(R.id.ivImagenDescifrada)?.visibility = View.GONE
        findViewById<ScrollView>(R.id.scrollImagenDescifrada)?.visibility = View.GONE
    }

    private fun configurarBotones() {
        findViewById<Button>(R.id.btnGuardar).setOnClickListener {
            if (archivoImagen == null) {
                AppUtils.mostrarMensaje(
                    this,
                    getString(R.string.aes_result_error_nothing_to_save)
                )
                return@setOnClickListener
            }
            selectorGuardar.launch(nombreArchivo)
        }

        findViewById<Button>(R.id.btnCompartir).setOnClickListener {
            compartirImagen()
        }

        findViewById<ImageButton>(R.id.btnVolver).setOnClickListener {
            finish()
        }
    }

    private fun guardarImagen(uri: Uri) {
        val archivo = archivoImagen ?: return
        try {
            FileManager.guardarArchivo(this, uri, archivo)
            AppUtils.mostrarMensaje(this, getString(R.string.aes_result_saved))
        } catch (e: Exception) {
            AppUtils.mostrarMensaje(this, getString(R.string.aes_result_error_save))
        }
    }

    private fun compartirImagen() {
        val archivo = archivoImagen
        if (archivo == null || !archivo.exists()) {
            AppUtils.mostrarMensaje(
                this,
                getString(R.string.aes_result_error_nothing_to_share)
            )
            return
        }

        try {
            val uriCompartible = FileManager.crearUriCompartible(this, archivo)
            val compartirIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uriCompartible)
                clipData = android.content.ClipData.newUri(
                    contentResolver,
                    nombreArchivo,
                    uriCompartible
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(
                Intent.createChooser(
                    compartirIntent,
                    getString(R.string.aes_result_share_title)
                )
            )
        } catch (e: Exception) {
            AppUtils.mostrarMensaje(this, getString(R.string.aes_result_error_share))
        }
    }

    override fun onDestroy() {
        ocultarDialogoCarga()
        super.onDestroy()
    }
}
