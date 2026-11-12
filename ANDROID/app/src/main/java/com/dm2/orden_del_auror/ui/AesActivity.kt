
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

// Actividad encargada de seleccionar y descifrar imágenes protegidas mediante AES.
class AesActivity : AppCompatActivity() {

    // Referencias a los elementos de la interfaz gráfica.
    private lateinit var tvArchivoSeleccionado: TextView
    private lateinit var etContrasena: EditText
    private lateinit var btnDescifrar: Button

    // URI del archivo cifrado seleccionado por el usuario.
    // Se utiliza para acceder al contenido del archivo sin cargarlo directamente en memoria.
    private var archivoSeleccionado: Uri? = null

    // Referencia al diálogo que informa de que el descifrado está en curso.
    private var dialogoDescifrado: AlertDialog? = null

    // Objeto que almacena constantes y valores compartidos de la actividad.
    companion object {
        // Etiqueta utilizada para identificar los mensajes de esta actividad en Logcat.
        private const val TAG = "PRUEBA_AES"
    }

    // Selector de archivos del sistema Android.
    // Permite al usuario elegir un documento y devuelve su URI cuando se selecciona.
    private val selectorArchivo =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->

            // Comprueba que el usuario haya seleccionado un archivo.
            if (uri != null) {

                // Obtiene el nombre del archivo seleccionado a partir de su URI.
                val nombreArchivo = FileManager.obtenerNombreArchivo(this, uri)

                // Comprueba que el archivo tenga la extensión esperada para AES.
                if (!nombreArchivo.endsWith(".png.aes", ignoreCase = true)) {

                    // Si la extensión no es válida, se elimina la selección.
                    archivoSeleccionado = null

                    // Actualiza el texto de la interfaz para indicar que no hay archivo seleccionado.
                    tvArchivoSeleccionado.text =
                        getString(R.string.aes_no_file_selected)

                    // Muestra un mensaje indicando que el formato del archivo no es válido.
                    AppUtils.mostrarMensaje(
                        this,
                        getString(R.string.aes_error_file_extension)
                    )

                } else {

                    // Guarda la URI del archivo para utilizarla posteriormente en el descifrado.
                    archivoSeleccionado = uri

                    // Muestra el nombre del archivo seleccionado en la interfaz.
                    tvArchivoSeleccionado.text = nombreArchivo

                    // Registra en Logcat el nombre del archivo seleccionado.
                    Log.d(TAG, "Archivo seleccionado: $nombreArchivo")
                }
            }
        }

    // Método que se ejecuta al crear la actividad.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Carga el diseño XML correspondiente a la pantalla AES.
        setContentView(R.layout.activity_aes)

        // Inicializa las referencias a los elementos de la interfaz.
        inicializarVistas()

        // Configura las acciones de los botones.
        configurarBotones()
    }

    // Localiza los elementos de la interfaz mediante sus identificadores XML.
    private fun inicializarVistas() {
        tvArchivoSeleccionado = findViewById(R.id.tvArchivoSeleccionado)
        etContrasena = findViewById(R.id.etContrasena)
        btnDescifrar = findViewById(R.id.btnDescifrar)
    }

    // Asocia las acciones que se ejecutarán al pulsar cada botón.
    private fun configurarBotones() {

        // Abre el selector de archivos del dispositivo.
        findViewById<Button>(R.id.btnSeleccionarArchivo).setOnClickListener {
            seleccionarArchivo()
        }

        // Inicia el proceso de descifrado de la imagen.
        btnDescifrar.setOnClickListener {
            descifrarImagen()
        }

        // Finaliza la actividad y vuelve a la pantalla anterior.
        findViewById<ImageButton>(R.id.btnVolver).setOnClickListener {
            finish()
        }
    }

    // Abre el selector de documentos para que el usuario elija un archivo.
    private fun seleccionarArchivo() {
        selectorArchivo.launch(arrayOf("*/*"))
    }

    /**
     * Muestra un diálogo no cancelable mientras se descifra el archivo.
     * Se construye directamente desde Kotlin para no necesitar modificar los XML.
     */
    private fun mostrarDialogoDescifrado() {

        // Crea un contenedor horizontal para colocar el indicador y el mensaje.
        val contenido = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(48, 32, 48, 16)
        }

        // Crea un indicador de progreso indeterminado.
        // Permanece animado mientras el proceso de descifrado está en ejecución.
        val progreso = ProgressBar(this).apply {
            isIndeterminate = true
        }

        // Crea el texto que informa al usuario de que la imagen se está descifrando.
        val mensaje = TextView(this).apply {
            text = getString(R.string.aes_decrypting_image)
            textSize = 16f
            setPadding(32, 0, 0, 0)
        }

        // Añade el indicador de progreso al contenedor.
        contenido.addView(
            progreso,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        // Añade el mensaje ocupando el espacio horizontal restante.
        contenido.addView(
            mensaje,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        // Construye el diálogo con un título, el contenido personalizado
        // y la opción de impedir que el usuario lo cierre manualmente.
        dialogoDescifrado = AlertDialog.Builder(this)
            .setTitle(getString(R.string.aes_decrypting_title))
            .setView(contenido)
            .setCancelable(false)
            .create()

        // Muestra el diálogo en pantalla.
        dialogoDescifrado?.show()
    }

    // Cierra el diálogo de progreso y libera su referencia.
    private fun ocultarDialogoDescifrado() {
        dialogoDescifrado?.dismiss()
        dialogoDescifrado = null
    }

    // Comprueba los datos necesarios y ejecuta el descifrado de la imagen.
    private fun descifrarImagen() {

        // Recupera la URI del archivo seleccionado.
        val uri = archivoSeleccionado

        // Comprueba que exista un archivo seleccionado antes de continuar.
        if (uri == null) {
            AppUtils.mostrarMensaje(this, getString(R.string.aes_error_select_file))
            return
        }

        // Obtiene la contraseña introducida por el usuario.
        val contrasena = etContrasena.text.toString()

        // Comprueba que la contraseña no esté vacía.
        if (contrasena.isBlank()) {
            etContrasena.error = getString(R.string.aes_no_password)
            etContrasena.requestFocus()
            return
        }

        // Desactiva el botón para evitar que se inicie más de un descifrado simultáneamente.
        btnDescifrar.isEnabled = false

        // Muestra el diálogo de progreso.
        mostrarDialogoDescifrado()

        // Registra el inicio del proceso en Logcat.
        Log.d(TAG, "Comenzando descifrado AES por bloques")

        // Ejecuta el proceso dentro del ciclo de vida de la actividad.
        // De esta forma, la corrutina queda vinculada a la actividad.
        lifecycleScope.launch {

            // Variable que almacenará el archivo temporal con la imagen descifrada.
            // Se inicializa a null para poder eliminarlo si ocurre algún error.
            var archivoTemporal: File? = null

            try {

                // Obtiene el nombre original del archivo cifrado.
                val nombreOriginal =
                    FileManager.obtenerNombreArchivo(this@AesActivity, uri)

                // Genera el nombre que tendrá el archivo una vez descifrado.
                val nombreDescifrado =
                    FileManager.generarNombreDescifrado(nombreOriginal)

                // Crea un archivo temporal dentro del directorio privado de caché.
                // Se utiliza para guardar el resultado sin cargar toda la imagen en memoria.
                archivoTemporal = File(
                    cacheDir,
                    "aes_${System.currentTimeMillis()}_$nombreDescifrado"
                )

                // Ejecuta las operaciones de lectura, escritura y validación
                // en un hilo de entrada y salida para no bloquear la interfaz.
                withContext(Dispatchers.IO) {

                    // Abre un flujo de entrada para leer el archivo cifrado.
                    // Si no se puede abrir, se lanza una excepción controlada.
                    val input = contentResolver.openInputStream(uri)
                        ?: throw IOException("No se pudo abrir el archivo seleccionado.")

                    // Utiliza use para cerrar automáticamente el flujo de entrada.
                    input.use { entrada ->

                        // Abre el archivo temporal como flujo de salida.
                        // También se cierra automáticamente al finalizar.
                        FileOutputStream(archivoTemporal!!).use { salida ->

                            // Descifra el archivo por bloques y escribe el resultado
                            // directamente en el archivo temporal.
                            // Así se evita almacenar la imagen completa en memoria.
                            AESCrypto.descifrarArchivo(
                                entrada,
                                salida,
                                contrasena
                            )
                        }
                    }

                    // Registra el tamaño del archivo descifrado en Logcat.
                    Log.d(
                        TAG,
                        "Archivo descifrado en disco: ${archivoTemporal!!.length()} bytes"
                    )

                    // VALIDADOR DE IMAGEN
                    // Comprueba las dimensiones de la imagen sin decodificar todos sus píxeles.
                    // Esto permite verificar el formato sin consumir grandes cantidades de memoria.
                    val opciones = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }

                    // Lee las dimensiones de la imagen descifrada.
                    BitmapFactory.decodeFile(archivoTemporal!!.absolutePath, opciones)

                    // Si las dimensiones no son válidas, se considera que el archivo
                    // no contiene una imagen reconocible.
                    if (opciones.outWidth <= 0 || opciones.outHeight <= 0) {
                        throw IOException(getString(R.string.aes_error_invalid_image))
                    }

                    // Registra las dimensiones de la imagen validada.
                    Log.d(
                        TAG,
                        "Imagen validada: ${opciones.outWidth}x${opciones.outHeight}"
                    )
                }

                // Prepara la navegación hacia la pantalla que muestra el resultado.
                val intent = Intent(
                    this@AesActivity,
                    AesResultadoActivity::class.java
                ).apply {

                    // Envía la ruta absoluta del archivo temporal con la imagen descifrada.
                    // Se pasa la ruta en lugar de un ByteArray para evitar enviar grandes
                    // cantidades de datos mediante un Intent.
                    putExtra(
                        AesResultadoActivity.EXTRA_RUTA_IMAGEN,
                        archivoTemporal!!.absolutePath
                    )

                    // Envía también el nombre que tendrá la imagen descifrada.
                    putExtra(
                        AesResultadoActivity.EXTRA_NOMBRE,
                        nombreDescifrado
                    )
                }

                // Abre la pantalla donde se muestra la imagen descifrada.
                startActivity(intent)

            } catch (e: CancellationException) {

                // Propaga la cancelación de la corrutina para mantener
                // el comportamiento correcto del ciclo de vida.
                throw e

            } catch (e: BadPaddingException) {

                // Se produce normalmente cuando la contraseña es incorrecta
                // o los datos cifrados están dañados.
                Log.e(TAG, "Contraseña incorrecta o archivo dañado", e)

                // Elimina el archivo temporal si se ha creado.
                archivoTemporal?.delete()

                // Informa al usuario del problema.
                mostrarError(getString(R.string.aes_error_wrong_password))

            } catch (e: IllegalBlockSizeException) {

                // Indica que el tamaño de los datos cifrados no es válido
                // para las operaciones del algoritmo AES.
                Log.e(TAG, "Tamaño de archivo cifrado incorrecto", e)

                // Elimina el archivo temporal si se ha creado.
                archivoTemporal?.delete()

                // Muestra un mensaje de error relacionado con el archivo cifrado.
                mostrarError(getString(R.string.aes_error_invalid_encrypted_file))

            } catch (e: SecurityException) {

                // Controla los errores relacionados con permisos de acceso al archivo.
                Log.e(TAG, "Error de permisos", e)

                // Elimina el archivo temporal si se ha creado.
                archivoTemporal?.delete()

                // Informa al usuario de que no se ha podido acceder al archivo.
                mostrarError(getString(R.string.error_file_permission))

            } catch (e: IOException) {

                // Controla los errores de lectura, escritura o validación de la imagen.
                Log.e(TAG, "Error de lectura o validación", e)

                // Elimina el archivo temporal si se ha creado.
                archivoTemporal?.delete()

                // Muestra un mensaje de error relacionado con la lectura de la imagen.
                mostrarError(getString(R.string.aes_error_read_image))

            } catch (e: IllegalArgumentException) {

                // Controla los argumentos o datos no válidos durante el descifrado.
                Log.e(TAG, "Datos AES no válidos", e)

                // Elimina el archivo temporal si se ha creado.
                archivoTemporal?.delete()

                // Informa al usuario del problema con los datos.
                mostrarError(getString(R.string.error_invalid_data))

            } catch (e: Exception) {

                // Captura cualquier otro error inesperado que no haya sido contemplado.
                Log.e(TAG, "Error inesperado durante el descifrado", e)

                // Elimina el archivo temporal si se ha creado.
                archivoTemporal?.delete()

                // Muestra un mensaje de error general.
                mostrarError(getString(R.string.aes_error_unexpected))

            } finally {

                // Cierra el diálogo de progreso tanto si el proceso termina
                // correctamente como si se produce algún error.
                ocultarDialogoDescifrado()

                // Reactiva el botón únicamente si la actividad sigue disponible.
                if (!isFinishing && !isDestroyed) {
                    btnDescifrar.isEnabled = true
                }
            }
        }
    }

    // Muestra un mensaje de error siempre que la actividad siga activa.
    private fun mostrarError(mensaje: String) {
        if (!isFinishing && !isDestroyed) {
            AppUtils.mostrarMensaje(this, mensaje)
        }
    }

    // Método ejecutado cuando la actividad está a punto de destruirse.
    override fun onDestroy() {

        // Cierra cualquier diálogo que pudiera seguir abierto.
        ocultarDialogoDescifrado()

        // Ejecuta el método original de la clase padre.
        super.onDestroy()
    }
}
