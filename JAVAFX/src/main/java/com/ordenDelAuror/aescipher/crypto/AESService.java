package com.ordenDelAuror.aescipher.crypto;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.SecureRandom;

/**
 * Servicio de cifrado y descifrado de ficheros con AES-256 en modo CBC.
 *
 * <p>El fichero resultante tiene esta estructura: 16 bytes de salt + 16 bytes
 * de IV (ambos aleatorios) + contenido cifrado con relleno PKCS5. La clave se
 * deriva de la contraseña con PBKDF2-HMAC-SHA256 usando 65 536 iteraciones y
 * una clave de 256 bits.</p>
 *
 * <p>Todos los métodos son estáticos y trabajan sobre streams, por lo que
 * pueden usarlos con ficheros grandes sin cargarlos entero en memoria.</p>
 *
 * @author Henri Tejada
 */
public class AESService {

    /** Longitud (en bytes) del salt aleatorio almacenado en la cabecera. */
    private static final int SALT_LENGTH = 16;

    /** Longitud (en bytes) del vector de inicialización (IV). */
    private static final int IV_LENGTH = 16;

    /** Número de iteraciones de PBKDF2 para derivar la clave. */
    private static final int ITERATIONS = 65536;

    /** Longitud (en bits) de la clave AES derivada de la contraseña. */
    private static final int KEY_LENGTH = 256;

    /** Tamaño (en bytes) del buffer usado para leer y escribir los streams. */
    private static final int BUFFER_SIZE = 8192;

    /**
     * Cifra un fichero con AES-256/CBC y escribe salt e IV al principio del
     * fichero resultante.
     *
     * <p>Se genera un salt y un IV distintos en cada llamada, por lo que
     * cifrar siempre el mismo fichero con la misma contraseña produce
     * ficheros cifrados distintos.</p>
     *
     * @param inputFile  fichero original que se va a cifrar
     * @param outputFile fichero de salida con la cabecera y el contenido cifrado
     * @param password   contraseña de la que se deriva la clave
     * @param callback   notificación de progreso; se invoca tras cada bloque
     *                   leído con los bytes procesados y el total
     * @throws Exception si el fichero de entrada no existe, hay un error de
     *                   E/S o falla el cifrado
     */
    public static void encryptFile(
            File inputFile,
            File outputFile,
            String password,
            ProgressCallback callback)
            throws Exception
 {

        byte[] salt = new byte[SALT_LENGTH];
        byte[] iv = new byte[IV_LENGTH];

        SecureRandom random = new SecureRandom();

        random.nextBytes(salt);
        random.nextBytes(iv);

        SecretKey key =
                generateKey(password, salt);

        Cipher cipher =
                Cipher.getInstance(
                        "AES/CBC/PKCS5Padding");

        cipher.init(
                Cipher.ENCRYPT_MODE,
                key,
                new IvParameterSpec(iv));

        try (
                FileInputStream fis =
                        new FileInputStream(inputFile);

                FileOutputStream fos =
                        new FileOutputStream(outputFile)
        ) {

            fos.write(salt);
            fos.write(iv);

            try (
                    CipherOutputStream cos =
                            new CipherOutputStream(
                                    fos,
                                    cipher)
            ) {

                byte[] buffer =
                        new byte[BUFFER_SIZE];

                int bytesRead;

                long totalSize = inputFile.length();
                long processed = 0;

                while ((bytesRead =
                        fis.read(buffer)) != -1) {

                    cos.write(
                            buffer,
                            0,
                            bytesRead);

                    processed += bytesRead;

                    callback.update(
                            processed,
                            totalSize);
                }
            }
        }
    }

    /**
     * Descifra un fichero generado por {@link #encryptFile}.
     *
     * <p>Primero se leen los 32 bytes de cabecera (salt + IV) y a partir de
     * ellos se deriva la misma clave que se usó para cifrar.</p>
     *
     * @param inputFile  fichero cifrado ({@code .aes}) que se va a descifrar
     * @param outputFile fichero de salida con el contenido original
     * @param password   contraseña con la que se cifró el fichero
     * @param callback   notificación de progreso; se invoca tras cada bloque
     *                   leído con los bytes procesados y el total
     * @throws Exception si el fichero de entrada no existe, la contraseña es
     *                   incorrecta o hay un error de E/S
     */
    public static void decryptFile(
            File inputFile,
            File outputFile,
            String password,
            ProgressCallback callback)
            throws Exception {

        try (
                FileInputStream fis =
                        new FileInputStream(inputFile)
        ) {

            byte[] salt =
                    new byte[SALT_LENGTH];

            byte[] iv =
                    new byte[IV_LENGTH];

            fis.read(salt);
            fis.read(iv);

            SecretKey key =
                    generateKey(password, salt);

            Cipher cipher =
                    Cipher.getInstance(
                            "AES/CBC/PKCS5Padding");

            cipher.init(
                    Cipher.DECRYPT_MODE,
                    key,
                    new IvParameterSpec(iv));

            long totalSize =
                    inputFile.length();

            long processed =
                    SALT_LENGTH + IV_LENGTH;

            try (
                    CipherInputStream cis =
                            new CipherInputStream(
                                    fis,
                                    cipher);

                    FileOutputStream fos =
                            new FileOutputStream(
                                    outputFile)
            ) {

                byte[] buffer =
                        new byte[BUFFER_SIZE];

                int bytesRead;

                while ((bytesRead =
                        cis.read(buffer)) != -1) {

                    fos.write(
                            buffer,
                            0,
                            bytesRead);

                    processed += bytesRead;

                    callback.update(
                            processed,
                            totalSize);
                }
            }
        }
    }

    /**
     * Deriva la clave AES a partir de la contraseña con PBKDF2-HMAC-SHA256.
     *
     * @param password contraseña introducida por el usuario
     * @param salt     salt aleatorio almacenado en la cabecera del fichero
     * @return clave AES de 256 bits lista para usar con el cifrado
     * @throws Exception si el algoritmo de derivación no está disponible
     */
    private static SecretKey generateKey(
            String password,
            byte[] salt) throws Exception {

        SecretKeyFactory factory =
                SecretKeyFactory.getInstance(
                        "PBKDF2WithHmacSHA256");

        PBEKeySpec spec =
                new PBEKeySpec(
                        password.toCharArray(),
                        salt,
                        ITERATIONS,
                        KEY_LENGTH);

        SecretKey temp =
                factory.generateSecret(spec);

        return new SecretKeySpec(
                temp.getEncoded(),
                "AES");
    }
}