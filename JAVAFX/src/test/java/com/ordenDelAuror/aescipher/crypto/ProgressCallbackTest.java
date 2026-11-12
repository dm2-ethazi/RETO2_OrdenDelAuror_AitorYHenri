package com.ordenDelAuror.aescipher.crypto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests del callback de progreso usado por {@link AESService}.
 *
 * <p>Comprueba que la interfaz funciona como lambda y que soporta tamaños de
 * archivo que no cabrían en un {@code int}.</p>
 *
 * @author Henri Tejada
 */
class ProgressCallbackTest {

    /** Una lambda debe poder registrar cada notificación con su progreso y total. */
    @Test
    void laInterfazPermiteUsarUnaLambdaQueRegistraLasLlamadas() {
        List<long[]> llamadas = new ArrayList<>();

        ProgressCallback callback =
                (processed, total) -> llamadas.add(new long[]{processed, total});

        callback.update(0, 100);
        callback.update(50, 100);
        callback.update(100, 100);

        assertEquals(3, llamadas.size());
        assertEquals(0, llamadas.get(0)[0]);
        assertEquals(50, llamadas.get(1)[0]);
        assertEquals(100, llamadas.get(2)[0]);
        assertTrue(
                llamadas.stream().allMatch(l -> l[1] == 100),
                "El total debe mantenerse en todas las notificaciones");
    }

    /** Los parámetros son long, por lo que no hay desbordamiento más allá de 2 GB. */
    @Test
    void laInterfazAdmiteProgresosDeArchivosGrandes() {
        List<long[]> llamadas = new ArrayList<>();

        ProgressCallback callback =
                (processed, total) -> llamadas.add(new long[]{processed, total});

        long total = 5_000_000_000L; // > 2 GB (int overflow si se usara int)
        callback.update(2_500_000_000L, total);
        callback.update(total, total);

        assertEquals(2, llamadas.size());
        assertEquals(total, llamadas.get(1)[0]);
        assertEquals(total, llamadas.get(1)[1]);
    }
}
