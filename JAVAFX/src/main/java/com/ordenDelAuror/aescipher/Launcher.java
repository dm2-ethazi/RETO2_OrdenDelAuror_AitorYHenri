package com.ordenDelAuror.aescipher;

/**
 * Punto de entrada de la aplicación "Orden del Auror".
 *
 * <p>Esta clase existe para poder arrancar la aplicación JavaFX desde un JAR
 * empaquetado, ya que la clase principal ({@link Main}) hereda de
 * {@code Application} y no puede ejecutarse directamente como módulo.</p>
 *
 * @author Henri Tejada
 */
public class Launcher {

    /**
     * Lanza la aplicación JavaFX.
     *
     * @param args argumentos de línea de comandos; no se utilizan
     */
    public static void main(String[] args) {
        Main.main(args);
    }
}
