
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

// Actividad encargada de mostrar, guardar y compartir la imagen descifrada mediante AES.
class AesResultadoActivity : AppCompatActivity() {

    // Constantes que permiten recibir los datos enviados desde AesActivity.
    companion object {
        // Clave utilizada para recibir la ruta del archivo de imagen descifrado.
        const val EXTRA_RUTA_IMAGEN = "ruta_imagen_descifrada"

        // Clave utilizada para recibir el nombre de la imagen descifrada.
        const val EXTRA_NOMBRE = "nombre_imagen"
    }

    // Archivo temporal que contiene la imagen descifrada.
    private var archivoImagen: File? = null

    // Nombre que se utilizará al guardar o compartir la imagen.
    // Se establece un nombre predeterminado por si no se recibe uno desde la actividad anterior.
    private var nombreArchivo: String = "imagen_descifrada.png"

    // Referencia al TextView utilizado para mostrar mensajes de estado y errores.
    private lateinit var tvEstado: TextView

    // Referencia al diálogo que se muestra mientras se carga la imagen.
    private var dialogoCarga: AlertDialog? = null

    // Selector del sistema Android para guardar la imagen en una ubicación elegida por el usuario.
    // Se crea un documento con formato PNG y devuelve su URI al finalizar.
    private val selectorGuardar =
        registerForActivityResult(ActivityResultContracts.CreateDocument("image/png")) { uri: Uri? ->

            // Si el usuario ha elegido una ubicación, se inicia el proceso de guardado.
            if (uri != null) guardarImagen(uri)
        }

    // Método ejecutado al crear la actividad.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Carga el diseño XML correspondiente a la pantalla de resultados AES.
        setContentView(R.layout.activity_aes_resultado)

        // Obtiene la referencia al elemento que muestra el estado de la operación.
        tvEstado = findViewById(R.id.tvEstadoResultado)

        // Configura las acciones de los botones de la pantalla.
        configurarBotones()

        // Recupera y muestra la imagen descifrada.
        mostrarImagen()
    }

    /**
     * Muestra una alerta mientras se comprueba y decodifica la imagen en segundo plano.
     */
    private fun mostrarImagen() {

        // Recupera la ruta del archivo temporal enviada desde AesActivity.
        val rutaImagen = intent.getStringExtra(EXTRA_RUTA_IMAGEN)

        // Recupera el nombre de la imagen.
        // Si no se ha recibido ningún nombre, utiliza uno predeterminado.
        nombreArchivo = intent.getStringExtra(EXTRA_NOMBRE)
            ?: "imagen_descifrada.png"

        // Comprueba que se haya recibido una ruta válida.
        if (rutaImagen.isNullOrBlank()) {
            mostrarError(getString(R.string.aes_result_error_missing_path))
            return
        }

        // Crea un objeto File a partir de la ruta recibida.
        val archivo = File(rutaImagen)

        // Comprueba que el archivo exista y que sea un archivo normal.
        if (!archivo.exists() || !archivo.isFile) {
            mostrarError(getString(R.string.aes_result_error_not_found))
            return
        }

        // Comprueba que el archivo no esté vacío.
        if (archivo.length() == 0L) {
            mostrarError(getString(R.string.error_empty_file))
            return
        }

        // Obtiene la referencia a la imagen donde se mostrará el resultado.
        val imageView = findViewById<ImageView>(R.id.ivImagenDescifrada)

        // Obtiene el ancho disponible para mostrar la imagen.
        // Si la vista todavía no tiene dimensiones, utiliza 1600 como valor predeterminado.
        val anchoObjetivo = imageView.width.takeIf { it > 0 } ?: 1600

        // Obtiene la altura disponible para mostrar la imagen.
        // Si la vista todavía no tiene dimensiones, utiliza 1600 como valor predeterminado.
        val altoObjetivo = imageView.height.takeIf { it > 0 } ?: 1600

        // Muestra el diálogo que informa de que la imagen se está cargando.
        mostrarDialogoCarga()

        // Ejecuta la carga de la imagen dentro del ciclo de vida de la actividad.
        lifecycleScope.launch {

            try {

                // Decodifica la imagen en un hilo secundario para no bloquear la interfaz.
                val bitmap = withContext(Dispatchers.IO) {

                    // PRIMERA FASE: OBTENER LAS DIMENSIONES
                    // Configura BitmapFactory para leer únicamente las dimensiones de la imagen.
                    // De esta forma no se cargan todavía todos los píxeles en memoria.
                    val opcionesBounds = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }

                    // Lee las dimensiones del archivo sin crear el Bitmap completo.
                    BitmapFactory.decodeFile(archivo.absolutePath, opcionesBounds)

                    // Comprueba que las dimensiones obtenidas sean válidas.
                    if (opcionesBounds.outWidth <= 0 || opcionesBounds.outHeight <= 0) {
                        return@withContext null
                    }

                    // SEGUNDA FASE: REDUCIR EL TAMAÑO DE DECODIFICACIÓN
                    // Calcula una versión reducida de la imagen para evitar consumir
                    // demasiada memoria RAM al cargar imágenes de gran tamaño.
                    val opcionesFinales = BitmapFactory.Options().apply {
                        inSampleSize = calcularInSampleSize(
                            opcionesBounds.outWidth,
                            opcionesBounds.outHeight,
                            anchoObjetivo,
                            altoObjetivo
                        )

                        // Utiliza RGB_565 para reducir el consumo de memoria.
                        // Este formato emplea menos memoria por píxel que ARGB_8888.
                        inPreferredConfig = Bitmap.Config.RGB_565
                    }

                    // Decodifica la imagen aplicando el factor de reducción calculado.
                    BitmapFactory.decodeFile(archivo.absolutePath, opcionesFinales)
                }

                // Comprueba que la imagen se haya podido decodificar correctamente.
                if (bitmap == null) {
                    mostrarError(getString(R.string.aes_error_invalid_image))
                    return@launch
                }

                // Guarda la referencia al archivo para permitir su posterior guardado o compartición.
                archivoImagen = archivo

                // Coloca el Bitmap obtenido dentro del ImageView.
                imageView.setImageBitmap(bitmap)

                // Hace visible la imagen una vez que se ha cargado correctamente.
                imageView.visibility = View.VISIBLE

                // Muestra también el contenedor desplazable si existe en el diseño actual.
                // El operador ? evita errores si ese elemento no está presente en el XML.
                findViewById<ScrollView>(R.id.scrollImagenDescifrada)
                    ?.visibility = View.VISIBLE

                // Oculta el mensaje de estado cuando la imagen se ha cargado correctamente.
                tvEstado.visibility = View.GONE

            } catch (e: CancellationException) {

                // Propaga la cancelación para respetar el ciclo de vida de la corrutina.
                throw e

            } catch (e: OutOfMemoryError) {

                // Controla los errores producidos cuando no hay suficiente memoria
                // para cargar la imagen.
                mostrarError(getString(R.string.aes_result_error_load))

            } catch (e: Exception) {

                // Controla cualquier otro error inesperado durante la carga de la imagen.
                mostrarError(getString(R.string.aes_result_error_load))

            } finally {

                // Cierra el diálogo de carga independientemente del resultado del proceso.
                ocultarDialogoCarga()
            }
        }
    }

    // Crea y muestra un diálogo con un indicador de progreso indeterminado.
    private fun mostrarDialogoCarga() {

        // Crea una barra de progreso que indica que se está realizando una operación.
        val progressBar = ProgressBar(this).apply {
            isIndeterminate = true
        }

        // Construye el diálogo de carga con su título y la barra de progreso.
        // No permite cerrarlo manualmente mientras se está procesando la imagen.
        dialogoCarga = AlertDialog.Builder(this)
            .setTitle(R.string.aes_result_loading_image)
            .setView(progressBar)
            .setCancelable(false)
            .create()

        // Muestra el diálogo en pantalla.
        dialogoCarga?.show()
    }

    // Cierra el diálogo de carga y libera su referencia.
    private fun ocultarDialogoCarga() {
        dialogoCarga?.dismiss()
        dialogoCarga = null
    }

    // Calcula el factor de reducción necesario para cargar una imagen a menor resolución.
    private fun calcularInSampleSize(
        ancho: Int,
        alto: Int,
        anchoObjetivo: Int,
        altoObjetivo: Int
    ): Int {

        // Factor de reducción inicial. El valor 1 significa que no se reduce la imagen.
        var sample = 1

        // Duplica progresivamente el factor de reducción mientras la imagen resultante
        // siga teniendo unas dimensiones iguales o superiores a las dimensiones objetivo.
        while (ancho / (sample * 2) >= anchoObjetivo &&
            alto / (sample * 2) >= altoObjetivo
        ) {
            sample *= 2
        }

        // Devuelve el factor de reducción calculado.
        return sample
    }

    // Muestra un mensaje de error y oculta los elementos de visualización de la imagen.
    private fun mostrarError(mensaje: String) {

        // Establece el texto del mensaje de error.
        tvEstado.text = mensaje

        // Hace visible el TextView que informa del problema.
        tvEstado.visibility = View.VISIBLE

        // Oculta la imagen si estaba mostrándose.
        findViewById<ImageView>(R.id.ivImagenDescifrada)?.visibility = View.GONE

        // Oculta el contenedor desplazable si existe en el diseño.
        findViewById<ScrollView>(R.id.scrollImagenDescifrada)?.visibility = View.GONE
    }

    // Configura las acciones de los botones de guardar, compartir y volver.
    private fun configurarBotones() {

        // BOTÓN GUARDAR
        findViewById<Button>(R.id.btnGuardar).setOnClickListener {

            // Comprueba que exista una imagen cargada antes de iniciar el guardado.
            if (archivoImagen == null) {
                AppUtils.mostrarMensaje(
                    this,
                    getString(R.string.aes_result_error_nothing_to_save)
                )
                return@setOnClickListener
            }

            // Abre el selector del sistema para que el usuario elija dónde guardar la imagen.
            // Se utiliza el nombre original de la imagen descifrada.
            selectorGuardar.launch(nombreArchivo)
        }

        // BOTÓN COMPARTIR
        findViewById<Button>(R.id.btnCompartir).setOnClickListener {
            compartirImagen()
        }

        // BOTÓN VOLVER
        // Finaliza esta actividad y regresa a la pantalla anterior.
        findViewById<ImageButton>(R.id.btnVolver).setOnClickListener {
            finish()
        }
    }

    // Guarda la imagen descifrada en la ubicación seleccionada por el usuario.
    private fun guardarImagen(uri: Uri) {

        // Recupera el archivo temporal.
        // Si no existe, no se realiza ninguna operación.
        val archivo = archivoImagen ?: return

        try {

            // Copia el contenido del archivo temporal al destino elegido por el usuario.
            FileManager.guardarArchivo(this, uri, archivo)

            // Informa de que la imagen se ha guardado correctamente.
            AppUtils.mostrarMensaje(this, getString(R.string.aes_result_saved))

        } catch (e: Exception) {

            // Informa de que se ha producido un error durante el guardado.
            AppUtils.mostrarMensaje(this, getString(R.string.aes_result_error_save))
        }
    }

    // Prepara y ejecuta el proceso para compartir la imagen descifrada con otras aplicaciones.
    private fun compartirImagen() {

        // Recupera el archivo que contiene la imagen.
        val archivo = archivoImagen

        // Comprueba que exista una imagen disponible y que el archivo siga existiendo.
        if (archivo == null || !archivo.exists()) {
            AppUtils.mostrarMensaje(
                this,
                getString(R.string.aes_result_error_nothing_to_share)
            )
            return
        }

        try {

            // Genera una URI compartible mediante FileProvider.
            // Esto permite que otras aplicaciones accedan al archivo sin exponer su ruta privada.
            val uriCompartible = FileManager.crearUriCompartible(this, archivo)

            // Crea un Intent de tipo ACTION_SEND para compartir la imagen.
            val compartirIntent = Intent(Intent.ACTION_SEND).apply {

                // Indica que el contenido que se va a compartir es una imagen PNG.
                type = "image/png"

                // Adjunta la URI del archivo como contenido que se va a compartir.
                putExtra(Intent.EXTRA_STREAM, uriCompartible)

                // Añade los datos del archivo al ClipData para facilitar el acceso
                // a la URI por parte de las aplicaciones receptoras.
                clipData = android.content.ClipData.newUri(
                    contentResolver,
                    nombreArchivo,
                    uriCompartible
                )

                // Concede permiso temporal de lectura a la aplicación receptora.
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            // Abre el selector de aplicaciones disponibles para compartir la imagen.
            startActivity(
                Intent.createChooser(
                    compartirIntent,
                    getString(R.string.aes_result_share_title)
                )
            )

        } catch (e: Exception) {

            // Muestra un mensaje si se produce un error al preparar o compartir la imagen.
            AppUtils.mostrarMensaje(this, getString(R.string.aes_result_error_share))
        }
    }

    // Método ejecutado cuando la actividad está a punto de destruirse.
    override fun onDestroy() {

        // Cierra el diálogo de carga si todavía está abierto.
        ocultarDialogoCarga()

        // Ejecuta el método original de la clase padre.
        super.onDestroy()
    }
}
