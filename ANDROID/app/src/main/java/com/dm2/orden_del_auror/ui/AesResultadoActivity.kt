package com.dm2.orden_del_auror.ui

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.dm2.orden_del_auror.R
import com.dm2.orden_del_auror.files.FileManager
import com.dm2.orden_del_auror.utils.AppUtils
import java.io.File
import java.io.IOException

/**
 * Pantalla que muestra y permite guardar o compartir
 * la imagen recuperada mediante AES.
 */
class AesResultadoActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_RUTA_IMAGEN = "ruta_imagen_descifrada"
        const val EXTRA_NOMBRE = "nombre_imagen"
    }

    private var imagenDescifrada: ByteArray? = null
    private var nombreArchivo: String = "imagen_descifrada.png"

    private lateinit var tvEstado: TextView

    private val selectorGuardar =
        registerForActivityResult(
            ActivityResultContracts.CreateDocument("image/png")
        ) { uri: Uri? ->
            if (uri != null) {
                guardarImagen(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aes_resultado)

        inicializarVistas()
        mostrarImagen()
        configurarBotones()
    }

    private fun inicializarVistas() {
        tvEstado = findViewById(R.id.tvEstadoResultado)
    }

    /**
     * Recupera la ruta recibida y carga la imagen descifrada.
     */

    private fun mostrarImagen() {

        val rutaImagen = intent.getStringExtra(EXTRA_RUTA_IMAGEN)

        nombreArchivo = intent.getStringExtra(EXTRA_NOMBRE)
            ?: "imagen_descifrada.png"

        if (rutaImagen == null) {
            tvEstado.text = "No se ha podido cargar la imagen."
            return
        }

        val archivo = File(rutaImagen)

        if (!archivo.exists()) {
            tvEstado.text = "No se ha encontrado la imagen."
            return
        }

        try {
            val datos = archivo.readBytes()

            if (datos.isEmpty()) {
                tvEstado.text = "La imagen está vacía."
                return
            }

            val bitmap = BitmapFactory.decodeByteArray(
                datos,
                0,
                datos.size
            )

            if (bitmap == null) {
                tvEstado.text = "La imagen no es válida."
                return
            }

            imagenDescifrada = datos

            val imagen = findViewById<ImageView>(R.id.ivImagenDescifrada)

            // Mostrar la imagen dentro del pergamino.
            imagen.setImageBitmap(bitmap)
            imagen.visibility = android.view.View.VISIBLE

            // Ocultar el mensaje de espera.
            tvEstado.visibility = android.view.View.GONE

        } catch (e: IOException) {
            tvEstado.text = "No se ha podido leer la imagen."

            AppUtils.mostrarMensaje(
                this,
                "Error al cargar la imagen recuperada."
            )
        }
    }


    /**
     * Configura los botones de la pantalla.
     */
    private fun configurarBotones() {

        findViewById<Button>(R.id.btnGuardar)
            .setOnClickListener {

                if (imagenDescifrada == null) {
                    AppUtils.mostrarMensaje(
                        this,
                        "No hay ninguna imagen recuperada."
                    )
                    return@setOnClickListener
                }

                selectorGuardar.launch(nombreArchivo)
            }

        findViewById<Button>(R.id.btnCompartir)
            .setOnClickListener {
                compartirImagen()
            }

        findViewById<ImageButton>(R.id.btnVolver)
            .setOnClickListener {
                finish()
            }
    }

    /**
     * Guarda la imagen recuperada en la ubicación elegida.
     */
    private fun guardarImagen(uri: Uri) {

        val datos = imagenDescifrada ?: return

        try {
            FileManager.guardarArchivo(
                this,
                uri,
                datos
            )

            tvEstado.text = "Imagen guardada correctamente."

            AppUtils.mostrarMensaje(
                this,
                "Imagen guardada correctamente."
            )

        } catch (e: Exception) {
            AppUtils.mostrarMensaje(
                this,
                "Error al guardar la imagen."
            )
        }
    }

    /**
     * Comparte la imagen recuperada con otras aplicaciones.
     */
    private fun compartirImagen() {

        val datos = imagenDescifrada

        if (datos == null) {
            AppUtils.mostrarMensaje(
                this,
                "No hay ninguna imagen recuperada para compartir."
            )
            return
        }

        try {
            val uriCompartible = FileManager.crearUriCompartible(
                this,
                nombreArchivo,
                datos
            )

            val compartirIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"

                putExtra(
                    Intent.EXTRA_STREAM,
                    uriCompartible
                )

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
                    "Compartir imagen recuperada"
                )
            )

        } catch (e: Exception) {
            AppUtils.mostrarMensaje(
                this,
                "No se ha podido compartir la imagen."
            )
        }
    }
}
