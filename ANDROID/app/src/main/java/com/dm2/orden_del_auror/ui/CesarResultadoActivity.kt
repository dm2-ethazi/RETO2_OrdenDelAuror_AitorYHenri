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

/** Muestra una vista previa y permite guardar/compartir el TXT completo. */
class CesarResultadoActivity : AppCompatActivity() {

    private lateinit var tvResultado: TextView
    private lateinit var tvEstado: TextView
    private var archivoResultado: File? = null
    private var nombreResultado: String = "mensaje_descifrado.txt"

    private val guardarArchivo = registerForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri ->
        if (uri != null) guardarTexto(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cesar_resultado)
        tvResultado = findViewById(R.id.tvResultadoCesar)
        tvEstado = findViewById(R.id.tvEstadoCesar)
        archivoResultado = intent.getStringExtra(EXTRA_RUTA_ARCHIVO)?.let(::File)
        nombreResultado = intent.getStringExtra(EXTRA_NOMBRE) ?: nombreResultado
        cargarVistaPrevia()

        findViewById<Button>(R.id.btnGuardarCesar).setOnClickListener { solicitarGuardado() }
        findViewById<Button>(R.id.btnCompartirCesar).setOnClickListener { compartirArchivo() }
        findViewById<ImageButton>(R.id.btnVolverResultado).setOnClickListener { finish() }
    }

    private fun cargarVistaPrevia() {
        val archivo = archivoResultado
        if (archivo == null || !archivo.exists()) {
            mostrarErrorResultado()
            return
        }
        tvEstado.text = getString(R.string.cesar_loading_result)
        tvEstado.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val vistaPrevia = withContext(Dispatchers.IO) {
                    archivo.inputStream().bufferedReader(Charsets.UTF_8).use { reader ->
                        val buffer = CharArray(LIMITE_VISTA_PREVIA)
                        val leidos = reader.read(buffer)
                        if (leidos <= 0) "" else String(buffer, 0, leidos)
                    }
                }
                tvResultado.text = if (archivo.length() > vistaPrevia.toByteArray(Charsets.UTF_8).size) {
                    vistaPrevia + "\n\n" + getString(R.string.cesar_preview_limited)
                } else vistaPrevia
                tvResultado.visibility = View.VISIBLE
                tvEstado.visibility = View.GONE
                findViewById<ScrollView>(R.id.scrollResultadoCesar)?.apply {
                    visibility = View.VISIBLE
                    post { scrollTo(0, 0) }
                }
            } catch (_: Exception) {
                mostrarErrorResultado()
            }
        }
    }

    private fun mostrarErrorResultado() {
        tvEstado.text = getString(R.string.cesar_error_result_file)
        tvEstado.visibility = View.VISIBLE
        findViewById<ScrollView>(R.id.scrollResultadoCesar)?.visibility = View.GONE
    }

    private fun solicitarGuardado() {
        if (archivoResultado?.exists() != true) {
            mostrarMensaje(getString(R.string.cesar_error_result_file))
            return
        }
        guardarArchivo.launch(nombreResultado)
    }

    private fun guardarTexto(uri: Uri) {
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val origen = archivoResultado ?: throw IOException("No existe el resultado.")
                    contentResolver.openOutputStream(uri)?.use { salida ->
                        origen.inputStream().buffered().use { entrada -> entrada.copyTo(salida, 8192) }
                    } ?: throw IOException("No se pudo crear el archivo.")
                }
                mostrarMensaje(getString(R.string.cesar_saved))
            } catch (_: Exception) {
                mostrarMensaje(getString(R.string.cesar_error_save))
            }
        }
    }

    private fun compartirArchivo() {
        lifecycleScope.launch {
            try {
                val uriCompartible = withContext(Dispatchers.IO) {
                    val origen = archivoResultado ?: throw IOException("No existe el resultado.")
                    val carpeta = File(cacheDir, "shared")
                    if (!carpeta.exists() && !carpeta.mkdirs()) throw IOException("No se pudo crear la carpeta temporal.")
                    val destino = File(carpeta, "cesar_${System.currentTimeMillis()}.txt")
                    origen.inputStream().buffered().use { entrada ->
                        destino.outputStream().buffered().use { salida -> entrada.copyTo(salida, 8192) }
                    }
                    FileProvider.getUriForFile(this@CesarResultadoActivity, "${packageName}.fileprovider", destino)
                }
                val compartirIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_STREAM, uriCompartible)
                    clipData = ClipData.newUri(contentResolver, nombreResultado, uriCompartible)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                startActivity(Intent.createChooser(compartirIntent, getString(R.string.cesar_share_title)))
            } catch (_: Exception) {
                mostrarMensaje(getString(R.string.cesar_error_share))
            }
        }
    }

    private fun mostrarMensaje(mensaje: String) {
        if (!isFinishing && !isDestroyed) Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_RUTA_ARCHIVO = "ruta_archivo_descifrado_cesar"
        const val EXTRA_NOMBRE = "nombre_archivo_descifrado_cesar"
        private const val LIMITE_VISTA_PREVIA = 100_000
    }
}
