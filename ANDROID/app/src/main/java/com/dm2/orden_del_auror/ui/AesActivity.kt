
package com.dm2.orden_del_auror.ui

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
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
import java.io.IOException
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException

/**
 * Pantalla encargada de seleccionar una imagen
 * y descifrarla mediante el algoritmo AES.
 */
class AesActivity : AppCompatActivity() {

    private lateinit var tvArchivoSeleccionado: TextView
    private lateinit var etContrasena: EditText
    private lateinit var btnDescifrar: Button

    private var archivoSeleccionado: Uri? = null

    /**
     * Selector de imágenes PNG.
     */
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

    /**
     * Inicializa los componentes de la interfaz.
     */
    private fun inicializarVistas() {

        tvArchivoSeleccionado =
            findViewById(R.id.tvArchivoSeleccionado)

        etContrasena =
            findViewById(R.id.etContrasena)

        btnDescifrar =
            findViewById(R.id.btnDescifrar)
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
            descifrarImagen()
        }

        findViewById<ImageButton>(R.id.btnVolver)
            .setOnClickListener {
                finish()
            }
    }

    /**
     * Abre el selector de imágenes PNG.
     */
    private fun seleccionarArchivo() {

        selectorArchivo.launch(
            arrayOf("image/png")
        )
    }

    /**
     * Descifra la imagen seleccionada en segundo plano.
     */
    private fun descifrarImagen() {

        // 1. Comprobar que se ha seleccionado un archivo.
        val uri = archivoSeleccionado

        if (uri == null) {

            AppUtils.mostrarMensaje(
                this,
                "Selecciona primero una imagen."
            )

            return
        }

        // 2. Comprobar que se ha introducido la contraseña.
        val contrasena = etContrasena.text.toString()

        if (contrasena.isBlank()) {

            etContrasena.error =
                "Introduce la contraseña."

            etContrasena.requestFocus()
            return
        }

        // Evitar varios descifrados simultáneos.
        btnDescifrar.isEnabled = false

        lifecycleScope.launch {

            try {

                // 3. Leer y descifrar el archivo en segundo plano.
                val resultado = withContext(Dispatchers.IO) {

                    val datosCifrados = FileManager.leerArchivo(
                        this@AesActivity,
                        uri
                    )

                    // Comprobar que el archivo no está vacío.
                    if (datosCifrados.isEmpty()) {
                        throw IOException(
                            "El archivo está vacío."
                        )
                    }

                    // Descifrar el contenido mediante AES.
                    val datosDescifrados = AESCrypto.descifrar(
                        datosCifrados,
                        contrasena
                    )

                    // Comprobar que el resultado es una imagen válida.
                    val opciones = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }

                    BitmapFactory.decodeByteArray(
                        datosDescifrados,
                        0,
                        datosDescifrados.size,
                        opciones
                    )

                    if (opciones.outWidth <= 0 ||
                        opciones.outHeight <= 0
                    ) {
                        throw IOException(
                            "La contraseña puede ser incorrecta o el archivo no contiene una imagen válida."
                        )
                    }

                    datosDescifrados
                }

                // 4. Volver al hilo principal y abrir el resultado.
                val intent = Intent(
                    this@AesActivity,
                    AesResultadoActivity::class.java
                )

                intent.putExtra(
                    AesResultadoActivity.EXTRA_IMAGEN,
                    resultado
                )

                intent.putExtra(
                    AesResultadoActivity.EXTRA_NOMBRE,
                    FileManager.generarNombreDescifrado(
                        FileManager.obtenerNombreArchivo(
                            this@AesActivity,
                            uri
                        )
                    )
                )

                startActivity(intent)

            } catch (e: CancellationException) {

                // Respetar la cancelación de la corrutina.
                throw e

            } catch (e: BadPaddingException) {

                mostrarError(
                    "Contraseña incorrecta o archivo dañado."
                )

            } catch (e: IllegalBlockSizeException) {

                mostrarError(
                    "El archivo cifrado tiene un formato incorrecto."
                )

            } catch (e: SecurityException) {

                mostrarError(
                    "No tienes permiso para acceder a este archivo."
                )

            } catch (e: IOException) {

                mostrarError(
                    e.message ?: "No se pudo leer o validar la imagen."
                )

            } catch (e: IllegalArgumentException) {

                mostrarError(
                    e.message ?: "Los datos introducidos no son válidos."
                )

            } catch (e: Exception) {

                mostrarError(
                    "Ha ocurrido un error inesperado al descifrar la imagen."
                )

            } finally {

                // Recuperar el botón al terminar el proceso.
                if (!isFinishing && !isDestroyed) {
                    btnDescifrar.isEnabled = true
                }
            }
        }
    }

    /**
     * Muestra un error sin cerrar la aplicación.
     */
    private fun mostrarError(mensaje: String) {

        if (!isFinishing && !isDestroyed) {
            AppUtils.mostrarMensaje(
                this,
                mensaje
            )
        }
    }
}
