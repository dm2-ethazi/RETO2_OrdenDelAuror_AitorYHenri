
package com.dm2.orden_del_auror.ui

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.dm2.orden_del_auror.R
import com.dm2.orden_del_auror.files.FileManager
import com.dm2.orden_del_auror.utils.AppUtils

/**
 * Pantalla que muestra y permite guardar la imagen recuperada mediante AES.
 */
class AesResultadoActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_IMAGEN = "imagen_descifrada"
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
     * Recupera los datos recibidos y muestra la imagen.
     */
    private fun mostrarImagen() {

        imagenDescifrada = intent.getByteArrayExtra(EXTRA_IMAGEN)

        nombreArchivo = intent.getStringExtra(EXTRA_NOMBRE)
            ?: "imagen_descifrada.png"

        val datos = imagenDescifrada

        if (datos == null) {
            tvEstado.text = "No se ha podido cargar la imagen."
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

        findViewById<ImageView>(R.id.ivImagenDescifrada)
            .setImageBitmap(bitmap)

        tvEstado.text = "¡Imagen recuperada correctamente!"
    }

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
}
