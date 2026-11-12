package com.ordenDelAuror.aescipher.crypto;

/**
 * Callback que recibe el progreso de una operación de cifrado o descifrado.
 *
 * <p>Permite desacoplar el servicio de cifrado de la interfaz gráfica:
 * {@link AESService} sólo notifica y es quien llama quien decide actualizar
 * una barra de progreso o simplemente ignorar la notificación.</p>
 *
 * @author Henri Tejada
 */
public interface ProgressCallback {

    /**
     * Notifica el avance actual de la operación.
     *
     * @param processed número de bytes procesados hasta el momento
     * @param total     número total de bytes que se van a procesar
     */
    void update(long processed,
                long total);
}