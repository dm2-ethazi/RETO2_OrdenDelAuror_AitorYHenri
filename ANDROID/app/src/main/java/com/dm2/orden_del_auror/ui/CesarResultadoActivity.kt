
package com.dm2.orden_del_auror.ui

import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.dm2.orden_del_auror.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

/**
 * Actividad que muestra una vista previa del texto descifrado mediante César
 * y permite guardar o compartir el archivo TXT completo.
 */
class CesarResultadoActivity : AppCompatActivity() {

    // Referencia al TextView donde se muestra el contenido del texto descifrado.
    private lateinit var tvResultado: TextView

    // Referencia al TextView utilizado para mostrar mensajes de estado y errores.
    private lateinit var tvEstado: TextView

    // Archivo temporal que contiene el resultado completo del descifrado.
    private var archivoResultado: File? = null

    // Nombre que tendrá el archivo al guardarlo o compartirlo.
    // Se establece un nombre predeterminado por si no se recibe otro.
    private var nombreResultado: String = "mensaje_descifrado.txt"

    // Selector del sistema Android para guardar documentos.
    // Crea un documento de texto y devuelve su URI cuando el usuario elige una ubicación.
    private val guardarArchivo = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->

        // Si el usuario ha seleccionado una ubicación, inicia el guardado del texto.
        if (uri != null) guardarTexto(uri)
    }

    // Método que se ejecuta al crear la actividad.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Carga el diseño XML correspondiente a la pantalla de resultados César.
        setContentView(R.layout.activity_cesar_resultado)

        // Obtiene las referencias a los elementos de la interfaz.
        tvResultado = findViewById(R.id.tvResultadoCesar)
        tvEstado = findViewById(R.id.tvEstadoCesar)

        // Recupera la ruta del archivo descifrado enviada desde la actividad anterior.
        // Convierte la ruta recibida en un objeto File.
        archivoResultado = intent.getStringExtra(EXTRA_RUTA_ARCHIVO)?.let(::File)

        // Recupera el nombre del archivo enviado desde la actividad anterior.
        // Si no se recibe ninguno, mantiene el nombre predeterminado.
        nombreResultado = intent.getStringExtra(EXTRA_NOMBRE) ?: nombreResultado

        // Carga una vista previa del contenido del archivo.
        cargarVistaPrevia()

        // Configura el botón para guardar el resultado completo.
        findViewById<Button>(R.id.btnGuardarCesar).setOnClickListener {
            solicitarGuardado()
        }

        // Configura el botón para compartir el archivo TXT.
        findViewById<Button>(R.id.btnCompartirCesar).setOnClickListener {
            compartirArchivo()
        }

        // Configura el botón para volver a la pantalla anterior.
        findViewById<ImageButton>(R.id.btnVolverResultado).setOnClickListener {
            finish()
        }
    }

    // Carga una vista previa del contenido del archivo descifrado.
    private fun cargarVistaPrevia() {

        // Recupera el archivo que contiene el resultado.
        val archivo = archivoResultado

        // Comprueba que el archivo exista y que se pueda localizar.
        if (archivo == null || !archivo.exists()) {
            mostrarErrorResultado()
            return
        }

        // Muestra un mensaje indicando que se está cargando el resultado.
        tvEstado.text = getString(R.string.cesar_loading_result)
        tvEstado.visibility = View.VISIBLE

        // Ejecuta la lectura del archivo en una corrutina.
        lifecycleScope.launch {

            try {

                // Lee el contenido del archivo en un hilo secundario.
                val vistaPrevia = withContext(Dispatchers.IO) {

                    // Abre el archivo como texto UTF-8 y utiliza un buffer de caracteres.
                    // De esta forma solo se leen los primeros caracteres necesarios
                    // para mostrar la vista previa, sin cargar todo el archivo en memoria.
                    archivo.inputStream().bufferedReader(Charsets.UTF_8).use { reader ->

                        // Reserva un array con el límite máximo de caracteres de la vista previa.
                        val buffer = CharArray(LIMITE_VISTA_PREVIA)

                        // Lee el contenido disponible hasta completar el tamaño del buffer.
                        val leidos = reader.read(buffer)

                        // Si no se ha leído ningún carácter, devuelve un texto vacío.
                        // En caso contrario, construye una cadena con los caracteres leídos.
                        if (leidos <= 0) "" else String(buffer, 0, leidos)
                    }
                }

                // Comprueba si el archivo original contiene más información
                // que la que se ha cargado en la vista previa.
                tvResultado.text = if (archivo.length() > vistaPrevia.toByteArray(Charsets.UTF_8).size) {

                    // Añade un aviso indicando que la vista previa está limitada.
                    vistaPrevia + "\n\n" + getString(R.string.cesar_preview_limited)

                } else {

                    // Si el contenido cabe en la vista previa, muestra el texto completo.
                    vistaPrevia
                }

                // Hace visible el resultado una vez cargado.
                tvResultado.visibility = View.VISIBLE

                // Oculta el mensaje de estado porque la carga ha finalizado correctamente.
                tvEstado.visibility = View.GONE

                // Muestra el contenedor desplazable si existe en el diseño XML.
                // También coloca el desplazamiento al principio del texto.
                findViewById<ScrollView>(R.id.scrollResultadoCesar)?.apply {
                    visibility = View.VISIBLE
                    post { scrollTo(0, 0) }
                }

            } catch (_: Exception) {

                // Si ocurre algún error al leer el archivo, muestra el mensaje correspondiente.
                mostrarErrorResultado()
            }
        }
    }

    // Muestra un mensaje cuando no se puede recuperar el archivo de resultado.
    private fun mostrarErrorResultado() {

        // Establece el texto de error.
        tvEstado.text = getString(R.string.cesar_error_result_file)

        // Hace visible el mensaje de error.
        tvEstado.visibility = View.VISIBLE

        // Oculta el contenedor del resultado si estaba visible.
        findViewById<ScrollView>(R.id.scrollResultadoCesar)?.visibility = View.GONE
    }

    // Comprueba que exista el resultado y solicita al sistema dónde guardarlo.
    private fun solicitarGuardado() {

        // Comprueba que el archivo temporal exista antes de iniciar el guardado.
        if (archivoResultado?.exists() != true) {
            mostrarMensaje(getString(R.string.cesar_error_result_file))
            return
        }

        // Abre el selector de documentos con el nombre del archivo descifrado.
        guardarArchivo.launch(nombreResultado)
    }

    // Copia el contenido completo del archivo descifrado a la ubicación elegida.
    private fun guardarTexto(uri: Uri) {

        // Ejecuta las operaciones de lectura y escritura en una corrutina.
        lifecycleScope.launch {

            try {

                // Realiza las operaciones de entrada y salida en un hilo secundario.
                withContext(Dispatchers.IO) {

                    // Recupera el archivo original que contiene el texto completo.
                    // Si no existe la referencia, lanza una excepción.
                    val origen = archivoResultado ?: throw IOException("No existe el resultado.")

                    // Abre un flujo de salida hacia la ubicación elegida por el usuario.
                    contentResolver.openOutputStream(uri)?.use { salida ->

                        // Abre el archivo de origen y copia todo su contenido al destino.
                        // Se utiliza un buffer de 8192 bytes para realizar la copia por bloques.
                        origen.inputStream().buffered().use { entrada ->
                            entrada.copyTo(salida, 8192)
                        }

                    } ?: throw IOException("No se pudo crear el archivo.")
                }

                // Informa al usuario de que el archivo se ha guardado correctamente.
                mostrarMensaje(getString(R.string.cesar_saved))

            } catch (_: Exception) {

                // Muestra un mensaje si se produce algún error durante el guardado.
                mostrarMensaje(getString(R.string.cesar_error_save))
            }
        }
    }

    // Prepara y ejecuta el proceso para compartir el archivo TXT completo.
    private fun compartirArchivo() {

        // Ejecuta la preparación del archivo y el envío mediante una corrutina.
        lifecycleScope.launch {

            try {

                // Prepara una URI compartible en un hilo de entrada y salida.
                val uriCompartible = withContext(Dispatchers.IO) {

                    // Recupera el archivo original con el texto descifrado.
                    val origen = archivoResultado ?: throw IOException("No existe el resultado.")

                    // Crea una carpeta llamada shared dentro del directorio de caché.
                    val carpeta = File(cacheDir, "shared")

                    // Comprueba si la carpeta existe.
                    // Si no existe, intenta crearla y controla posibles errores.
                    if (!carpeta.exists() && !carpeta.mkdirs()) {
                        throw IOException("No se pudo crear la carpeta temporal.")
                    }

                    // Crea un archivo temporal con un nombre único para compartirlo.
                    val destino = File(carpeta, "cesar_${System.currentTimeMillis()}.txt")

                    // Copia el contenido completo del archivo original al archivo temporal.
                    // La copia se realiza mediante buffers para evitar cargarlo entero en memoria.
                    origen.inputStream().buffered().use { entrada ->
                        destino.outputStream().buffered().use { salida ->
                            entrada.copyTo(salida, 8192)
                        }
                    }

                    // Genera una URI mediante FileProvider para permitir que otras
                    // aplicaciones puedan acceder al archivo temporal de forma segura.
                    FileProvider.getUriForFile(
                        this@CesarResultadoActivity,
                        "${packageName}.fileprovider",
                        destino
                    )
                }

                // Crea el Intent que permitirá compartir el archivo con otras aplicaciones.
                val compartirIntent = Intent(Intent.ACTION_SEND).apply {

                    // Indica que el contenido que se va a compartir es texto.
                    type = "text/plain"

                    // Adjunta la URI del archivo como contenido que se va a compartir.
                    putExtra(Intent.EXTRA_STREAM, uriCompartible)

                    // Añade la URI al ClipData para facilitar el acceso al archivo
                    // por parte de la aplicación que lo recibe.
                    clipData = ClipData.newUri(contentResolver, nombreResultado, uriCompartible)

                    // Concede permiso temporal de lectura a la aplicación receptora.
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                // Abre el selector de aplicaciones disponibles para compartir el archivo.
                startActivity(
                    Intent.createChooser(
                        compartirIntent,
                        getString(R.string.cesar_share_title)
                    )
                )

            } catch (_: Exception) {

                // Informa al usuario si se produce algún error al preparar o compartir el archivo.
                mostrarMensaje(getString(R.string.cesar_error_share))
            }
        }
    }

    // Muestra mensajes breves al usuario mediante un Toast.
    private fun mostrarMensaje(mensaje: String) {

        // Comprueba que la actividad siga activa antes de mostrar el mensaje.
        if (!isFinishing && !isDestroyed) {
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        }
    }

    // Constantes utilizadas para intercambiar información y limitar la vista previa.
    companion object {

        // Clave utilizada para recibir la ruta del archivo TXT descifrado.
        const val EXTRA_RUTA_ARCHIVO = "ruta_archivo_descifrado_cesar"

        // Clave utilizada para recibir el nombre del archivo TXT descifrado.
        const val EXTRA_NOMBRE = "nombre_archivo_descifrado_cesar"

        // Número máximo de caracteres que se mostrarán en la vista previa.
        // El archivo completo se conserva para las operaciones de guardado y compartición.
        private const val LIMITE_VISTA_PREVIA = 100_000
    }
}
