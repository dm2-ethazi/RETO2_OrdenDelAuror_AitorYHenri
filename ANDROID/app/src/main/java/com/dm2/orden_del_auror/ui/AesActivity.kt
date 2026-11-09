
package com.dm2.orden_del_auror.ui

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
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

    companion object {
        private const val TAG = "PRUEBA_AES"
    }

    // Selector de archivos AES cifrados.
    private val selectorArchivo =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->

            if (uri != null) {
                val nombreArchivo =
                    FileManager.obtenerNombreArchivo(this, uri)

                if (!nombreArchivo.endsWith(".png.aes", ignoreCase = true)) {
                    archivoSeleccionado = null
                    tvArchivoSeleccionado.text = "Ningún archivo seleccionado"

                    AppUtils.mostrarMensaje(
                        this,
                        "Selecciona un archivo con extensión .png.aes."
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

    // Permitir seleccionar archivos cifrados.
    private fun seleccionarArchivo() {
        selectorArchivo.launch(arrayOf("*/*"))
    }

    private fun descifrarImagen() {

        val uri = archivoSeleccionado

        if (uri == null) {
            AppUtils.mostrarMensaje(
                this,
                "Selecciona primero un archivo .png.aes."
            )
            return
        }

        val contrasena = etContrasena.text.toString()

        if (contrasena.isBlank()) {
            etContrasena.error = "Introduce la contraseña."
            etContrasena.requestFocus()
            return
        }

        btnDescifrar.isEnabled = false

        Log.d(TAG, "Comenzando el descifrado")

        lifecycleScope.launch {
            try {

                // Leer y descifrar la imagen en segundo plano.
                val resultado = withContext(Dispatchers.IO) {

                    val datosCifrados = FileManager.leerArchivo(
                        this@AesActivity,
                        uri
                    )

                    Log.d(
                        TAG,
                        "1. Archivo leído: ${datosCifrados.size} bytes"
                    )

                    if (datosCifrados.isEmpty()) {
                        throw IOException("El archivo está vacío.")
                    }

                    val datosDescifrados = AESCrypto.descifrar(
                        datosCifrados,
                        contrasena
                    )

                    Log.d(
                        TAG,
                        "2. Archivo descifrado: ${datosDescifrados.size} bytes"
                    )

                    // Comprobar que los datos corresponden a una imagen.
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

                    Log.d(
                        TAG,
                        "3. Imagen validada: ${opciones.outWidth}x${opciones.outHeight}"
                    )

                    datosDescifrados
                }

                // Obtener el nombre del archivo original.
                val nombreOriginal =
                    FileManager.obtenerNombreArchivo(this@AesActivity, uri)

                val nombreDescifrado =
                    FileManager.generarNombreDescifrado(nombreOriginal)

                // Guardar la imagen descifrada en la caché.
                val archivoTemporal = File(cacheDir, nombreDescifrado)

                withContext(Dispatchers.IO) {
                    FileOutputStream(archivoTemporal).use { output ->
                        output.write(resultado)
                    }
                }

                Log.d(
                    TAG,
                    "4. Imagen guardada en: ${archivoTemporal.absolutePath}"
                )

                // Enviar solamente la ruta del archivo.
                val intent = Intent(
                    this@AesActivity,
                    AesResultadoActivity::class.java
                )

                intent.putExtra(
                    AesResultadoActivity.EXTRA_RUTA_IMAGEN,
                    archivoTemporal.absolutePath
                )

                intent.putExtra(
                    AesResultadoActivity.EXTRA_NOMBRE,
                    nombreDescifrado
                )

                Log.d(TAG, "5. Abriendo pantalla de resultado")

                startActivity(intent)

            } catch (e: CancellationException) {
                throw e

            } catch (e: BadPaddingException) {
                Log.e(TAG, "Error: contraseña incorrecta o archivo dañado", e)
                mostrarError("Contraseña incorrecta o archivo dañado.")

            } catch (e: IllegalBlockSizeException) {
                Log.e(TAG, "Error: tamaño del archivo incorrecto", e)
                mostrarError("El archivo cifrado tiene un formato incorrecto.")

            } catch (e: SecurityException) {
                Log.e(TAG, "Error de permisos", e)
                mostrarError("No tienes permiso para acceder a este archivo.")

            } catch (e: IOException) {
                Log.e(TAG, "Error de lectura o validación", e)
                mostrarError(
                    e.message ?: "No se pudo leer o validar la imagen."
                )

            } catch (e: IllegalArgumentException) {
                Log.e(TAG, "Error de argumentos", e)
                mostrarError(
                    e.message ?: "Los datos introducidos no son válidos."
                )

            } catch (e: Exception) {
                Log.e(TAG, "Error inesperado durante el descifrado", e)
                mostrarError(
                    "Ha ocurrido un error inesperado al descifrar la imagen."
                )

            } finally {
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
}
