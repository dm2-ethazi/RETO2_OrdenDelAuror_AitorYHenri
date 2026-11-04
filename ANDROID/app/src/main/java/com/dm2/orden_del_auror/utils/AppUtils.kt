package com.dm2.orden_del_auror.utils

import android.content.Context
import android.widget.Toast

object AppUtils {

    /**
     * Muestra un mensaje al usuario mediante un Toast.
     *
     * @param context Contexto de la aplicación.
     * @param mensaje Mensaje que se mostrará.
     */
    fun mostrarMensaje(context: Context, mensaje: String) {
        Toast.makeText(
            context,
            mensaje,
            Toast.LENGTH_SHORT
        ).show()
    }
}