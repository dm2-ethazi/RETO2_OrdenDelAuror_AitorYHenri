
package com.dm2.orden_del_auror.files

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException

/**
 * Gestiona la lectura, escritura y compartición de archivos.
 */
object FileManager {

    /**
     * Lee el contenido de un archivo seleccionado.
     *
     * @param context Contexto de la aplicación.
     * @param uri URI del archivo seleccionado.
     * @return Contenido del archivo en bytes.
     * @throws IOException Si no se puede abrir o leer el archivo.
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
            inputStream.use {
                it.readBytes()
            }
        } catch (e: SecurityException) {
            throw e
        } catch (e: Exception) {
            throw IOException("Error al leer el archivo.", e)
        }
    }

    /**
     * Guarda contenido en la ubicación seleccionada por el usuario.
     *
     * @param context Contexto de la aplicación.
     * @param uri URI de destino.
     * @param datos Contenido que se quiere guardar.
     * @throws IOException Si no se puede guardar el archivo.
     */
    fun guardarArchivo(
        context: Context,
        uri: Uri,
        datos: ByteArray
    ) {

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
     * Obtiene el nombre de un archivo a partir de su URI.
     *
     * @param context Contexto de la aplicación.
     * @param uri URI del archivo.
     * @return Nombre del archivo o un nombre genérico.
     */
    fun obtenerNombreArchivo(
        context: Context,
        uri: Uri
    ): String {

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

                    val columna = cursor.getColumnIndex(
                        OpenableColumns.DISPLAY_NAME
                    )

                    if (columna != -1) {
                        nombre = cursor.getString(columna) ?: nombre
                    }
                }
            }
        } catch (_: Exception) {
            // Si no se puede obtener el nombre,
            // se mantiene el nombre genérico.
        }

        return nombre
    }

    /**
     * Genera el nombre del archivo descifrado.
     *
     * @param nombreOriginal Nombre del archivo original.
     * @return Nombre del archivo descifrado.
     */
    fun generarNombreDescifrado(nombreOriginal: String): String {

        val extension = nombreOriginal.substringAfterLast(".", "")

        val nombreBase = nombreOriginal.substringBeforeLast(
            ".",
            nombreOriginal
        )

        return if (extension.isNotEmpty()) {
            "${nombreBase}_descifrado.$extension"
        } else {
            "${nombreOriginal}_descifrado"
        }
    }

    /**
     * Obtiene el tipo MIME de un archivo.
     *
     * @param context Contexto de la aplicación.
     * @param uri URI del archivo.
     * @return Tipo MIME del archivo.
     */
    fun obtenerTipoMime(
        context: Context,
        uri: Uri
    ): String {

        return try {
            context.contentResolver.getType(uri)
                ?: "application/octet-stream"
        } catch (_: Exception) {
            "application/octet-stream"
        }
    }

    /**
     * Crea una copia temporal y devuelve una URI segura
     * para compartir el archivo con otras aplicaciones.
     *
     * @param context Contexto de la aplicación.
     * @param nombreArchivo Nombre del archivo que se compartirá.
     * @param datos Contenido del archivo.
     * @return URI segura proporcionada por FileProvider.
     * @throws IOException Si no se puede crear el archivo temporal.
     */
    fun crearUriCompartible(
        context: Context,
        nombreArchivo: String,
        datos: ByteArray
    ): Uri {

        val directorioCompartido = File(context.cacheDir, "shared")

        // Crear la carpeta temporal si no existe.
        if (!directorioCompartido.exists()) {
            val creada = directorioCompartido.mkdirs()

            if (!creada && !directorioCompartido.exists()) {
                throw IOException(
                    "No se pudo crear la carpeta para compartir."
                )
            }
        }

        if (!directorioCompartido.isDirectory) {
            throw IOException(
                "La ubicación temporal no es una carpeta válida."
            )
        }

        // Evitar caracteres no válidos en el nombre.
        val nombreSeguro = nombreArchivo
            .replace(Regex("[^a-zA-Z0-9._-]"), "_")
            .ifBlank { "archivo_descifrado" }

        // Crear un nombre único para evitar sobrescribir
        // otros archivos que se estén compartiendo.
        val nombreBase = nombreSeguro.substringBeforeLast(
            ".",
            nombreSeguro
        )

        val extension = nombreSeguro.substringAfterLast(
            ".",
            ""
        )

        val nombreUnico = if (extension.isNotEmpty()) {
            "${nombreBase}_${System.currentTimeMillis()}.$extension"
        } else {
            "${nombreBase}_${System.currentTimeMillis()}"
        }

        val archivo = File(
            directorioCompartido,
            nombreUnico
        )

        try {

            archivo.writeBytes(datos)

            if (!archivo.exists() || archivo.length() != datos.size.toLong()) {
                throw IOException(
                    "No se pudo completar la copia del archivo."
                )
            }

            return FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                archivo
            )

        } catch (e: Exception) {

            // Eliminar el archivo incompleto si se produce un error.
            archivo.delete()

            if (e is IOException) {
                throw e
            }

            throw IOException(
                "No se pudo preparar el archivo para compartir.",
                e
            )
        }
    }
}
