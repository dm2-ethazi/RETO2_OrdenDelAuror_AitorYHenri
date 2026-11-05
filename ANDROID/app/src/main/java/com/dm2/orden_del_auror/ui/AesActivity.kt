package com.dm2.orden_del_auror.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.dm2.orden_del_auror.R
import com.dm2.orden_del_auror.crypto.AESCrypto
import com.dm2.orden_del_auror.files.FileManager
import com.dm2.orden_del_auror.utils.AppUtils

/**
 * Pantalla encargada de seleccionar una imagen y descifrarla mediante AES.
 */
class AesActivity : AppCompatActivity() {

    private lateinit var tvArchivoSeleccionado: TextView
    private lateinit var etContrasena: EditText
    private lateinit var btnDescifrar: Button

    private var archivoSeleccionado: Uri? = null

    private val selectorArchivo =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->

            if (uri != null) {
                archivoSeleccionado = uri

                tvArchivoSeleccionado.text =
                    FileManager.obtenerNombreArchivo(this, uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aes)

        inicializarVistas()
        configurarBotones()
    }

    private fun inicializarVistas() {
        tvArchivoSeleccionado =
            findViewById(R.id.tvArchivoSeleccionado)

        etContrasena =
            findViewById(R.id.etContrasena)

        btnDescifrar =
            findViewById(R.id.btnDescifrar)
    }

    private fun configurarBotones() {

        findViewById<Button>(R.id.btnSeleccionarArchivo)
            .setOnClickListener {
                selectorArchivo.launch(arrayOf("image/png"))
            }

        btnDescifrar.setOnClickListener {
            descifrarImagen()
        }

        findViewById<ImageButton>(R.id.btnVolver)
            .setOnClickListener {
                finish()
            }
    }

    /**
     * Descifra la imagen seleccionada y abre la pantalla de resultado.
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

        Thread {
            try {
                // Leer el archivo cifrado.
                val datosCifrados = FileManager.leerArchivo(
                    this,
                    uri
                )

                // Descifrar mediante AES.
                val resultado = AESCrypto.descifrar(
                    datosCifrados,
                    contrasena
                )

                // Comprobar que el resultado es una imagen válida.
                val bitmap = android.graphics.BitmapFactory.decodeByteArray(
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

                    // Abrir la pantalla del resultado.
                    val intent = Intent(
                        this,
                        AesResultadoActivity::class.java
                    )

                    intent.putExtra(
                        AesResultadoActivity.EXTRA_IMAGEN,
                        resultado
                    )

                    intent.putExtra(
                        AesResultadoActivity.EXTRA_NOMBRE,
                        FileManager.generarNombreDescifrado(
                            FileManager.obtenerNombreArchivo(this, uri)
                        )
                    )

                    startActivity(intent)

                    btnDescifrar.isEnabled = true
                }

            } catch (e: Exception) {

                runOnUiThread {
                    btnDescifrar.isEnabled = true

                    AppUtils.mostrarMensaje(
                        this,
                        "Error al descifrar: contraseña incorrecta o archivo inválido."
                    )
                }
            }

        }.start()
    }
}
