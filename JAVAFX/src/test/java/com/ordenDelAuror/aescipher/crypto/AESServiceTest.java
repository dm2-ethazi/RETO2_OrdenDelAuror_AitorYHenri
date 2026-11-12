package com.ordenDelAuror.aescipher.crypto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitarios del servicio de cifrado/descifrado AES ({@link AESService}).
 *
 * <p>Comprueba el round trip de cifrado y descifrado, la estructura del
 * fichero resultante, el comportamiento con contraseñas erróneas y ficheros
 * inexistentes, y las notificaciones del callback de progreso.</p>
 *
 * @author Henri Tejada
 */
class AESServiceTest {

    /** Contraseña usada en la mayoría de los tests. */
    private static final String PASSWORD = "super-secret-password-123";

    /** Cabecera esperada: 16 bytes de salt + 16 bytes de IV. */
    private static final int HEADER_SIZE = 32;

    /** Tamaño mayor que el buffer interno (8192) para forzar varias lecturas. */
    private static final int LARGE_FILE_SIZE = 50_000;

    /** Directorio temporal creado por JUnit 5 para cada test. */
    @TempDir
    Path tempDir;

    /**
     * Crea un fichero de prueba con el contenido indicado dentro del
     * directorio temporal.
     *
     * @param name    nombre del fichero a crear
     * @param content bytes que se escriben en el fichero
     * @return el fichero creado
     * @throws Exception si no se puede escribir el fichero
     */
    private File createFile(String name, byte[] content) throws Exception {
        Path path = tempDir.resolve(name);
        Files.write(path, content);
        return path.toFile();
    }

    /**
     * Genera una secuencia de bytes determinista a partir de una semilla.
     *
     * @param size número de bytes a generar
     * @param seed semilla que hace reproducible la secuencia
     * @return array con los bytes generados
     */
    private static byte[] randomBytes(int size, long seed) {
        byte[] data = new byte[size];
        new Random(seed).nextBytes(data);
        return data;
    }

    /** Callback de progreso que no hace nada, para centrarse en lo que se prueba. */
    private static final ProgressCallback NOOP_CALLBACK = (processed, total) -> { };

    // ---------------------------------------------------------------
    // Cifrado + descifrado (round trip)
    // ---------------------------------------------------------------

    /**
     * Un fichero grande (mayor que el buffer interno) cifrado y descifrado
     * debe devolver exactamente el contenido original.
     */
    @Test
    void cifrarYDescifrarArchivoGrande_devuelveElContenidoOriginal() throws Exception {
        byte[] original = randomBytes(LARGE_FILE_SIZE, 42);
        File input = createFile("grande.bin", original);
        File encrypted = tempDir.resolve("grande.bin.aes").toFile();
        File decrypted = tempDir.resolve("grande.bin.out").toFile();

        AESService.encryptFile(input, encrypted, PASSWORD, NOOP_CALLBACK);
        AESService.decryptFile(encrypted, decrypted, PASSWORD, NOOP_CALLBACK);

        assertArrayEquals(original, Files.readAllBytes(decrypted.toPath()));
    }

    /**
     * Un fichero pequeño (una sola lectura del buffer) también debe
     * recuperarse sin pérdidas.
     */
    @Test
    void cifrarYDescifrarArchivoPequeno_devuelveElContenidoOriginal() throws Exception {
        byte[] original = "Hola, mundo. Esto es una prueba.".getBytes("UTF-8");
        File input = createFile("pequeno.txt", original);
        File encrypted = tempDir.resolve("pequeno.txt.aes").toFile();
        File decrypted = tempDir.resolve("pequeno.txt.out").toFile();

        AESService.encryptFile(input, encrypted, PASSWORD, NOOP_CALLBACK);
        AESService.decryptFile(encrypted, decrypted, PASSWORD, NOOP_CALLBACK);

        assertArrayEquals(original, Files.readAllBytes(decrypted.toPath()));
    }

    /**
     * Cifrar un fichero vacío debe producir, al descifrarlo, un fichero
     * igualmente vacío.
     */
    @Test
    void cifrarYDescifrarArchivoVacio_devuelveUnArchivoVacio() throws Exception {
        File input = createFile("vacio.bin", new byte[0]);
        File encrypted = tempDir.resolve("vacio.bin.aes").toFile();
        File decrypted = tempDir.resolve("vacio.bin.out").toFile();

        AESService.encryptFile(input, encrypted, PASSWORD, NOOP_CALLBACK);
        AESService.decryptFile(encrypted, decrypted, PASSWORD, NOOP_CALLBACK);

        assertEquals(0, Files.size(decrypted.toPath()));
    }

    // ---------------------------------------------------------------
    // Propiedades del archivo cifrado
    // ---------------------------------------------------------------

    /**
     * El fichero cifrado debe empezar por la cabecera de 32 bytes
     * (salt + IV) seguida del contenido con relleno PKCS5.
     */
    @Test
    void cifrarArchivo_uneSaltIvDelanteDelContenidoCifrado() throws Exception {
        byte[] original = randomBytes(1024, 7);
        File input = createFile("cabecera.bin", original);
        File encrypted = tempDir.resolve("cabecera.bin.aes").toFile();

        AESService.encryptFile(input, encrypted, PASSWORD, NOOP_CALLBACK);

        byte[] encryptedBytes = Files.readAllBytes(encrypted.toPath());

        // PKCS5 siempre añade al menos un bloque completo (16 bytes):
        // 1024 ya es múltiplo de 16, por lo que el padding son otros 16 bytes.
        int contenidoCifrado = ((original.length / 16) + 1) * 16;

        // Cabecera (salt + IV) + contenido cifrado con padding
        assertEquals(
                HEADER_SIZE + contenidoCifrado,
                encryptedBytes.length,
                "El archivo cifrado debe llevar 32 bytes de cabecera (salt + IV) más el contenido con padding PKCS5");
    }

    /**
     * Cifrar dos veces el mismo archivo con la misma contraseña debe dar dos
     * resultados distintos (salt e IV aleatorios), y ambos deben descifrarse.
     */
    @Test
    void cifrarElMismoArchivoDosVeces_generaCifradosDistintos() throws Exception {
        byte[] original = randomBytes(2048, 99);
        File input = createFile("aleatorio.bin", original);
        File encrypted1 = tempDir.resolve("aleatorio1.aes").toFile();
        File encrypted2 = tempDir.resolve("aleatorio2.aes").toFile();

        AESService.encryptFile(input, encrypted1, PASSWORD, NOOP_CALLBACK);
        AESService.encryptFile(input, encrypted2, PASSWORD, NOOP_CALLBACK);

        byte[] bytes1 = Files.readAllBytes(encrypted1.toPath());
        byte[] bytes2 = Files.readAllBytes(encrypted2.toPath());

        // Salt e IV aleatorios: dos cifrados del mismo texto nunca deben coincidir
        assertFalse(
                Arrays.equals(bytes1, bytes2),
                "Dos cifrados con la misma contraseña deben diferir (salt/IV aleatorios)");

        // Y ambos deben descifrarse correctamente
        File decrypted1 = tempDir.resolve("aleatorio1.out").toFile();
        File decrypted2 = tempDir.resolve("aleatorio2.out").toFile();
        AESService.decryptFile(encrypted1, decrypted1, PASSWORD, NOOP_CALLBACK);
        AESService.decryptFile(encrypted2, decrypted2, PASSWORD, NOOP_CALLBACK);

        assertArrayEquals(original, Files.readAllBytes(decrypted1.toPath()));
        assertArrayEquals(original, Files.readAllBytes(decrypted2.toPath()));
    }

    /**
     * El texto original no debe aparecer en claro dentro del fichero cifrado.
     */
    @Test
    void cifrarArchivo_elCifradoNoContieneElTextoOriginal() throws Exception {
        byte[] original = "mensaje secreto que no debe aparecer".getBytes("UTF-8");
        File input = createFile("secreto.txt", original);
        File encrypted = tempDir.resolve("secreto.txt.aes").toFile();

        AESService.encryptFile(input, encrypted, PASSWORD, NOOP_CALLBACK);

        String contenidoCifrado = new String(Files.readAllBytes(encrypted.toPath()), "ISO-8859-1");
        assertFalse(
                contenidoCifrado.contains("mensaje secreto"),
                "El texto original no debe aparecer en claro en el archivo cifrado");
    }

    // ---------------------------------------------------------------
    // Contraseña incorrecta
    // ---------------------------------------------------------------

    /**
     * Descifrar con la contraseña incorrecta nunca debe devolver el contenido
     * original: o lanza excepción (relleno inválido) o entrega otros bytes.
     */
    @Test
    void descifrarConContraIncorrecta_noRecuperaElContenidoOriginal() throws Exception {
        byte[] original = randomBytes(4096, 1234);
        File input = createFile("protegido.bin", original);
        File encrypted = tempDir.resolve("protegido.bin.aes").toFile();
        File decrypted = tempDir.resolve("protegido.bin.wrong").toFile();

        AESService.encryptFile(input, encrypted, PASSWORD, NOOP_CALLBACK);

        // Con la contraseña errónea debe fallar el padding o, como mínimo,
        // no devolver nunca el contenido original.
        boolean exceptionThrown = false;
        try {
            AESService.decryptFile(encrypted, decrypted, "wrong-password", NOOP_CALLBACK);
        } catch (Exception e) {
            exceptionThrown = true;
        }

        if (!exceptionThrown) {
            assertFalse(
                    Arrays.equals(original, Files.readAllBytes(decrypted.toPath())),
                    "Con la contraseña incorrecta jamás debe recuperarse el contenido original");
        }
    }

    // ---------------------------------------------------------------
    // Ficheros inexistentes
    // ---------------------------------------------------------------

    /**
     * Cifrar un fichero que no existe debe lanzar
     * {@link java.io.FileNotFoundException}.
     */
    @Test
    void cifrarArchivoInexistente_lanzaFileNotFoundException() {
        File input = new File(tempDir.toFile(), "no-existe.bin");
        File encrypted = tempDir.resolve("no-existe.aes").toFile();

        assertThrows(
                FileNotFoundException.class,
                () -> AESService.encryptFile(input, encrypted, PASSWORD, NOOP_CALLBACK));
    }

    /**
     * Descifrar un fichero que no existe debe lanzar
     * {@link java.io.FileNotFoundException}.
     */
    @Test
    void descifrarArchivoInexistente_lanzaFileNotFoundException() {
        File input = new File(tempDir.toFile(), "no-existe.aes");
        File decrypted = tempDir.resolve("no-existe.out").toFile();

        assertThrows(
                FileNotFoundException.class,
                () -> AESService.decryptFile(input, decrypted, PASSWORD, NOOP_CALLBACK));
    }

    // ---------------------------------------------------------------
    // Callback de progreso
    // ---------------------------------------------------------------

    /**
     * Al cifrar, el callback debe notificar un total constante, un progreso
     * monótono no decreciente y terminar exactamente en el total.
     */
    @Test
    void cifrarArchivo_notificaElProgresoHastaElTotal() throws Exception {
        byte[] original = randomBytes(LARGE_FILE_SIZE, 5);
        File input = createFile("progreso.bin", original);
        File encrypted = tempDir.resolve("progreso.bin.aes").toFile();

        List<long[]> llamadas = new ArrayList<>();
        AESService.encryptFile(
                input,
                encrypted,
                PASSWORD,
                (processed, total) -> llamadas.add(new long[]{processed, total}));

        assertFalse(llamadas.isEmpty(), "El callback debe notificar al menos una vez");

        long total = input.length();
        long ultimoProcesado = -1;

        for (long[] llamada : llamadas) {
            assertEquals(total, llamada[1], "El total notificado debe ser el tamaño del archivo");
            assertTrue(
                    llamada[0] >= ultimoProcesado,
                    "El progreso no debe retroceder");
            ultimoProcesado = llamada[0];
        }

        assertEquals(
                total,
                ultimoProcesado,
                "La última notificación debe corresponder al 100% del archivo");
    }

    /**
     * Al descifrar, el progreso tampoco debe retroceder ni superar el total
     * y debe llegar como muy lejos al bloque de relleno final.
     */
    @Test
    void descifrarArchivo_notificaElProgresoHastaElTotal() throws Exception {
        byte[] original = randomBytes(LARGE_FILE_SIZE, 6);
        File input = createFile("progreso2.bin", original);
        File encrypted = tempDir.resolve("progreso2.bin.aes").toFile();
        AESService.encryptFile(input, encrypted, PASSWORD, NOOP_CALLBACK);

        List<long[]> llamadas = new ArrayList<>();
        AESService.decryptFile(
                encrypted,
                tempDir.resolve("progreso2.bin.out").toFile(),
                PASSWORD,
                (processed, total) -> llamadas.add(new long[]{processed, total}));

        assertFalse(llamadas.isEmpty(), "El callback debe notificar al menos una vez");

        long total = encrypted.length();
        long ultimoProcesado = -1;

        for (long[] llamada : llamadas) {
            assertEquals(total, llamada[1], "El total notificado debe ser el tamaño del archivo .aes");
            assertTrue(
                    llamada[0] >= ultimoProcesado,
                    "El progreso no debe retroceder");
            assertTrue(
                    llamada[0] <= total,
                    "El procesado nunca puede superar el total");
            ultimoProcesado = llamada[0];
        }

        // CipherInputStream no entrega el bloque de padding final como datos,
        // así que el progreso llega hasta el total menos ese bloque (16 bytes).
        assertTrue(
                total - ultimoProcesado <= 16,
                "El progreso debe quedar a menos de un bloque del total, pero fue: "
                        + ultimoProcesado + "/" + total);
    }

    /**
     * El cifrado debe crear el fichero de salida y no dejarlo vacío.
     */
    @Test
    void cifrarArchivo_losArchivosDeSalidaSeCrean() throws Exception {
        byte[] original = "contenido".getBytes("UTF-8");
        File input = createFile("existe.txt", original);
        File encrypted = tempDir.resolve("existe.txt.aes").toFile();

        AESService.encryptFile(input, encrypted, PASSWORD, NOOP_CALLBACK);

        assertNotNull(encrypted);
        assertTrue(encrypted.exists(), "Debe crearse el archivo cifrado");
        assertTrue(encrypted.length() > 0, "El archivo cifrado no puede estar vacío");
    }
}
