
package com.dm2.orden_del_auror.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
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
import java.io.IOException

/**
 * Pantalla encargada de seleccionar y descifrar archivos de texto
 * mediante el algoritmo César.
 */
class CesarActivity : AppCompatActivity() {

    private lateinit var tvArchivoSeleccionado: TextView
    private lateinit var etDesplazamiento: EditText
    private lateinit var btnDescifrar: Button

    private var archivoSeleccionado: Uri? = null

    /**
     * Selector de archivos TXT.
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

        etDesplazamiento =
            findViewById(R.id.etDesplazamiento)

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
            descifrarArchivo()
        }

        findViewById<ImageButton>(R.id.btnVolver)
            .setOnClickListener {
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
     * Descifra el contenido del archivo seleccionado
     * utilizando una corrutina en segundo plano.
     */
    private fun descifrarArchivo() {

        // 1. Comprobar que se ha seleccionado un archivo.
        val uri = archivoSeleccionado

        if (uri == null) {
            mostrarMensaje(
                "Primero debes seleccionar un archivo."
            )
            return
        }

        // 2. Comprobar que se ha introducido el desplazamiento.
        val desplazamientoTexto =
            etDesplazamiento.text.toString().trim()

        if (desplazamientoTexto.isEmpty()) {

            etDesplazamiento.error =
                "Introduce un desplazamiento."

            etDesplazamiento.requestFocus()
            return
        }

        // 3. Comprobar que el desplazamiento es un número válido.
        val desplazamiento =
            desplazamientoTexto.toIntOrNull()

        if (desplazamiento == null || desplazamiento !in 0..25) {

            etDesplazamiento.error =
                "Introduce un número entre 0 y 25."

            etDesplazamiento.requestFocus()
            return
        }

        // Evitar que el usuario inicie varios descifrados a la vez.
        btnDescifrar.isEnabled = false

        lifecycleScope.launch {

            try {

                // 4. Leer el archivo y descifrarlo en segundo plano.
                val textoDescifrado = withContext(Dispatchers.IO) {

                    val datosCifrados = FileManager.leerArchivo(
                        this@CesarActivity,
                        uri
                    )

                    // Comprobar que el archivo no está vacío.
                    if (datosCifrados.isEmpty()) {
                        throw IOException(
                            "El archivo está vacío."
                        )
                    }

                    // Convertir los bytes a texto UTF-8.
                    val textoCifrado = String(
                        datosCifrados,
                        Charsets.UTF_8
                    )

                    if (textoCifrado.isBlank()) {
                        throw IOException(
                            "El archivo no contiene texto."
                        )
                    }

                    // Aplicar el algoritmo César.
                    CesarCrypto.descifrar(
                        textoCifrado,
                        desplazamiento
                    )
                }

                // 5. Abrir la pantalla del resultado.
                val intent = Intent(
                    this@CesarActivity,
                    CesarResultadoActivity::class.java
                )

                intent.putExtra(
                    CesarResultadoActivity.EXTRA_TEXTO,
                    textoDescifrado
                )

                startActivity(intent)

            } catch (e: CancellationException) {

                // No ocultar la cancelación de la corrutina.
                throw e

            } catch (e: SecurityException) {

                mostrarMensaje(
                    "No tienes permiso para acceder a este archivo."
                )

            } catch (e: IOException) {

                mostrarMensaje(
                    e.message ?: "No se pudo leer el archivo."
                )

            } catch (e: IllegalArgumentException) {

                mostrarMensaje(
                    e.message ?: "El desplazamiento no es válido."
                )

            } catch (e: Exception) {

                mostrarMensaje(
                    "Ha ocurrido un error inesperado al descifrar el texto."
                )

            } finally {

                // Recuperar el botón aunque se produzca un error.
                if (!isFinishing && !isDestroyed) {
                    btnDescifrar.isEnabled = true
                }
            }
        }
    }

    /**
     * Muestra un mensaje al usuario sin cerrar la aplicación.
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
}
