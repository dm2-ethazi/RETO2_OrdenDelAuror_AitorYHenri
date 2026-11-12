package com.dm2.orden_del_auror.files

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

/**
 * Gestiona la lectura, escritura y compartición de archivos.
 */
object FileManager {

    /**
     * Lee el contenido completo de un archivo.
     * Para archivos grandes, preferir trabajar directamente con streams.
     */
    fun leerArchivo(context: Context, uri: Uri): ByteArray {
        val inputStream = try {
            context.contentResolver.openInputStream(uri)
        } catch (e: SecurityException) {
            throw e
        } catch (e: Exception) {
            throw IOException("No se pudo abrir el archivo.", e)
        } ?: throw IOException("No se puede acceder al archivo seleccionado.")

        return try {
            inputStream.use { it.readBytes() }
        } catch (e: SecurityException) {
            throw e
        } catch (e: Exception) {
            throw IOException("Error al leer el archivo.", e)
        }
    }

    /**
     * Guarda un ByteArray. Se mantiene para los archivos pequeños y César.
     */
    fun guardarArchivo(context: Context, uri: Uri, datos: ByteArray) {
        val outputStream = try {
            context.contentResolver.openOutputStream(uri)
        } catch (e: SecurityException) {
            throw e
        } catch (e: Exception) {
            throw IOException("No se pudo crear el archivo.", e)
        } ?: throw IOException("No se puede acceder a la ubicación seleccionada.")

        try {
            outputStream.use {
                it.write(datos)
                it.flush()
            }
        } catch (e: SecurityException) {
            throw e
        } catch (e: Exception) {
            throw IOException("Error al guardar el archivo.", e)
        }
    }

    /**
     * Copia un archivo directamente a la ubicación elegida, por bloques.
     */
    fun guardarArchivo(context: Context, uri: Uri, archivo: File) {
        val outputStream = try {
            context.contentResolver.openOutputStream(uri)
        } catch (e: SecurityException) {
            throw e
        } catch (e: Exception) {
            throw IOException("No se pudo crear el archivo.", e)
        } ?: throw IOException("No se puede acceder a la ubicación seleccionada.")

        try {
            FileInputStream(archivo).use { input ->
                outputStream.use { output ->
                    val buffer = ByteArray(64 * 1024)
                    while (true) {
                        val leidos = input.read(buffer)
                        if (leidos == -1) break
                        output.write(buffer, 0, leidos)
                    }
                    output.flush()
                }
            }
        } catch (e: SecurityException) {
            throw e
        } catch (e: Exception) {
            throw IOException("Error al guardar el archivo.", e)
        }
    }

    fun obtenerNombreArchivo(context: Context, uri: Uri): String {
        var nombre = "archivo"

        try {
            context.contentResolver.query(
                uri,
                arrayOf(OpenableColumns.DISPLAY_NAME),
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val columna = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (columna != -1) {
                        nombre = cursor.getString(columna) ?: nombre
                    }
                }
            }
        } catch (_: Exception) {
            // Se conserva el nombre genérico.
        }

        return nombre
    }

    fun generarNombreDescifrado(nombreOriginal: String): String {
        return if (nombreOriginal.endsWith(".png.aes", ignoreCase = true)) {
            nombreOriginal.dropLast(4)
        } else {
            val extension = nombreOriginal.substringAfterLast(".", "")
            val nombreBase = nombreOriginal.substringBeforeLast(".", nombreOriginal)
            if (extension.isNotEmpty()) {
                "${nombreBase}_descifrado.$extension"
            } else {
                "${nombreOriginal}_descifrado"
            }
        }
    }

    fun obtenerTipoMime(context: Context, uri: Uri): String {
        return try {
            context.contentResolver.getType(uri) ?: "application/octet-stream"
        } catch (_: Exception) {
            "application/octet-stream"
        }
    }

    /**
     * Crea una copia temporal de un ByteArray para compartir.
     * Se mantiene por compatibilidad con las funciones existentes.
     */
    fun crearUriCompartible(
        context: Context,
        nombreArchivo: String,
        datos: ByteArray
    ): Uri {
        val archivo = crearArchivoCompartible(context, nombreArchivo)
        try {
            archivo.writeBytes(datos)
            return FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                archivo
            )
        } catch (e: Exception) {
            archivo.delete()
            if (e is IOException) throw e
            throw IOException("No se pudo preparar el archivo para compartir.", e)
        }
    }

    /**
     * Comparte un archivo existente sin copiarlo a otro ByteArray.
     * El archivo debe estar dentro de una ruta permitida por FileProvider.
     */
    fun crearUriCompartible(context: Context, archivo: File): Uri {
        if (!archivo.exists() || !archivo.isFile) {
            throw IOException("El archivo que se quiere compartir no existe.")
        }

        // Se copia por bloques a la carpeta shared, cuya ruta ya utilizaba
        // la versión anterior de FileManager y debe estar declarada en FileProvider.
        val copia = crearArchivoCompartible(context, archivo.name)

        try {
            FileInputStream(archivo).use { input ->
                FileOutputStream(copia).use { output ->
                    val buffer = ByteArray(64 * 1024)
                    while (true) {
                        val leidos = input.read(buffer)
                        if (leidos == -1) break
                        output.write(buffer, 0, leidos)
                    }
                    output.flush()
                }
            }

            return FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                copia
            )
        } catch (e: Exception) {
            copia.delete()
            if (e is IOException) throw e
            throw IOException("No se pudo preparar el archivo para compartir.", e)
        }
    }

    private fun crearArchivoCompartible(context: Context, nombreArchivo: String): File {
        val directorio = File(context.cacheDir, "shared")
        if (!directorio.exists() && !directorio.mkdirs() && !directorio.exists()) {
            throw IOException("No se pudo crear la carpeta para compartir.")
        }
        if (!directorio.isDirectory) {
            throw IOException("La ubicación temporal no es una carpeta válida.")
        }

        val nombreSeguro = nombreArchivo
            .replace(Regex("[^a-zA-Z0-9._-]"), "_")
            .ifBlank { "archivo_descifrado" }

        val nombreBase = nombreSeguro.substringBeforeLast(".", nombreSeguro)
        val extension = nombreSeguro.substringAfterLast(".", "")
        val nombreUnico = if (extension.isNotEmpty()) {
            "${nombreBase}_${System.currentTimeMillis()}.$extension"
        } else {
            "${nombreBase}_${System.currentTimeMillis()}"
        }

        return File(directorio, nombreUnico)
    }
}
