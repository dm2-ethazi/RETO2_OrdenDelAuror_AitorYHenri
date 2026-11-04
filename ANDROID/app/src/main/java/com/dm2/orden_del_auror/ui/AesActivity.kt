package com.dm2.orden_del_auror.ui

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.dm2.orden_del_auror.R
import com.dm2.orden_del_auror.crypto.AESCrypto
import com.dm2.orden_del_auror.files.FileManager
import com.dm2.orden_del_auror.utils.AppUtils

/**
 * Pantalla encargada de recuperar imágenes cifradas mediante AES.
 */
class AesActivity : AppCompatActivity() {

    private lateinit var tvArchivoSeleccionado: TextView
    private lateinit var tvEstado: TextView
    private lateinit var etContrasena: EditText
    private lateinit var ivImagenDescifrada: ImageView
    private lateinit var btnDescifrar: Button
    private lateinit var btnGuardar: Button

    private var archivoSeleccionado: Uri? = null
    private var imagenDescifrada: ByteArray? = null
    private var nombreArchivo: String = "imagen.png"

    // Selector de imágenes PNG.
    private val selectorArchivo =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->

            if (uri != null) {
                archivoSeleccionado = uri
                imagenDescifrada = null

                nombreArchivo = FileManager.obtenerNombreArchivo(
                    this,
                    uri
                )

                tvArchivoSeleccionado.text = nombreArchivo
                tvEstado.text = "Archivo seleccionado correctamente."

                ivImagenDescifrada.setImageDrawable(null)
                ivImagenDescifrada.visibility = ImageView.GONE
                btnGuardar.isEnabled = false
            }
        }

    // Selector del destino donde guardar la imagen.
    private val selectorGuardar =
        registerForActivityResult(
            ActivityResultContracts.CreateDocument("image/png")
        ) { uri ->

            if (uri != null) {
                guardarImagen(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_aes)

        inicializarVistas()
        configurarBotones()
    }

    /**
     * Inicializa los elementos de la interfaz.
     */
    private fun inicializarVistas() {

        tvArchivoSeleccionado =
            findViewById(R.id.tvArchivoSeleccionado)

        tvEstado =
            findViewById(R.id.tvEstado)

        etContrasena =
            findViewById(R.id.etContrasena)

        ivImagenDescifrada =
            findViewById(R.id.ivImagenDescifrada)

        btnDescifrar =
            findViewById(R.id.btnDescifrar)

        btnGuardar =
            findViewById(R.id.btnGuardar)
    }

    /**
     * Configura los eventos de los botones.
     */
    private fun configurarBotones() {

        findViewById<Button>(R.id.btnSeleccionarArchivo)
            .setOnClickListener {
                selectorArchivo.launch(arrayOf("image/png"))
            }

        btnDescifrar.setOnClickListener {
            descifrarImagen()
        }

        btnGuardar.setOnClickListener {
            val nombreSalida =
                FileManager.generarNombreDescifrado(nombreArchivo)

            selectorGuardar.launch(nombreSalida)
        }

        // Volver a la pantalla principal.
        findViewById<Button>(R.id.btnVolver).setOnClickListener {
            finish()
        }
    }

    /**
     * Descifra la imagen seleccionada mediante AES.
     */
    private fun descifrarImagen() {

        val uri = archivoSeleccionado

        if (uri == null) {
            AppUtils.mostrarMensaje(
                this,
                "Selecciona primero una imagen."
            )
            return
        }

        val contrasena = etContrasena.text.toString()

        if (contrasena.isBlank()) {
            etContrasena.error = "Introduce la contraseña"
            return
        }

        btnDescifrar.isEnabled = false
        btnGuardar.isEnabled = false

        tvEstado.text = "Descifrando imagen..."

        Thread {

            try {
                // 1. Leer el archivo cifrado.
                val datosCifrados = FileManager.leerArchivo(
                    this,
                    uri
                )

                // 2. Descifrar mediante AES.
                val resultado = AESCrypto.descifrar(
                    datosCifrados,
                    contrasena
                )

                // 3. Comprobar que el resultado es una imagen válida.
                val bitmap = BitmapFactory.decodeByteArray(
                    resultado,
                    0,
                    resultado.size
                )

                if (bitmap == null) {
                    throw IllegalArgumentException(
                        "El contenido recuperado no es una imagen válida."
                    )
                }

                runOnUiThread {

                    imagenDescifrada = resultado

                    ivImagenDescifrada.setImageBitmap(bitmap)
                    ivImagenDescifrada.visibility = ImageView.VISIBLE

                    tvEstado.text = "¡Imagen recuperada correctamente!"

                    btnGuardar.isEnabled = true
                    btnDescifrar.isEnabled = true

                    AppUtils.mostrarMensaje(
                        this,
                        "Descifrado AES completado."
                    )
                }

            } catch (e: Exception) {

                runOnUiThread {

                    imagenDescifrada = null

                    ivImagenDescifrada.setImageDrawable(null)
                    ivImagenDescifrada.visibility = ImageView.GONE

                    tvEstado.text = "No se ha podido recuperar la imagen."

                    btnGuardar.isEnabled = false
                    btnDescifrar.isEnabled = true

                    AppUtils.mostrarMensaje(
                        this,
                        "Error al descifrar: contraseña incorrecta o archivo inválido."
                    )
                }
            }

        }.start()
    }

    /**
     * Guarda la imagen descifrada en la ubicación elegida.
     */
    private fun guardarImagen(uri: Uri) {

        val datos = imagenDescifrada

        if (datos == null) {
            AppUtils.mostrarMensaje(
                this,
                "No hay ninguna imagen recuperada."
            )
            return
        }

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