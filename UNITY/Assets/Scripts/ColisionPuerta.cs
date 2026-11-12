
using UnityEngine;

public class CollisionPuerta : MonoBehaviour
{
    // Referencias a los elementos necesarios para controlar la colisión y la derrota.
    [Header("Referencias")]
    [SerializeField] private RectTransform troll;
    [SerializeField] private RectTransform puerta;
    [SerializeField] private GameObject panelDerrota;
    [SerializeField] private MovimientoTroll movimientoTroll;
    [SerializeField] private GameObject contenedorAcertijo;

    // Elementos de la interfaz que se ocultan cuando el jugador pierde.
    [Header("Elementos que se ocultan al perder")]
    [SerializeField] private GameObject[] elementosDurantePartida;

    // Variables para guardar la transformación inicial del troll.
    private Vector3 posicionInicialTroll;
    private Quaternion rotacionInicialTroll;
    private Vector3 escalaInicialTroll;

    // Indica si la partida ya ha terminado por derrota.
    private bool haPerdido = false;

    private void Awake()
    {
        // Guardar la posición, rotación y escala iniciales del troll al comenzar.
        if (troll != null)
        {
            posicionInicialTroll = troll.position;
            rotacionInicialTroll = troll.rotation;
            escalaInicialTroll = troll.localScale;
        }
    }

    private void Update()
    {
        // No comprobar la colisión si la partida ya ha terminado
        // o si falta alguna de las referencias necesarias.
        if (haPerdido || troll == null || puerta == null)
            return;

        // Comprobar si el troll se solapa con la puerta.
        // Si ocurre, se ejecuta el método de derrota.
        if (SeSolapan(troll, puerta))
            PerderPartida();
    }

    // Comprueba si dos elementos de tipo RectTransform se solapan.
    private bool SeSolapan(RectTransform a, RectTransform b)
    {
        // Crear arrays para almacenar las cuatro esquinas de cada elemento.
        Vector3[] esquinasA = new Vector3[4];
        Vector3[] esquinasB = new Vector3[4];

        // Obtener las coordenadas de las esquinas en el espacio del mundo.
        a.GetWorldCorners(esquinasA);
        b.GetWorldCorners(esquinasB);

        // Crear un rectángulo a partir de las esquinas del primer elemento.
        Rect rectA = Rect.MinMaxRect(
            esquinasA[0].x,
            esquinasA[0].y,
            esquinasA[2].x,
            esquinasA[2].y
        );

        // Crear un rectángulo a partir de las esquinas del segundo elemento.
        Rect rectB = Rect.MinMaxRect(
            esquinasB[0].x,
            esquinasB[0].y,
            esquinasB[2].x,
            esquinasB[2].y
        );

        // Devolver true si los dos rectángulos se solapan.
        return rectA.Overlaps(rectB);
    }

    // Gestiona todas las acciones que se realizan cuando el jugador pierde.
    private void PerderPartida()
    {
        // Evitar que el método se ejecute más de una vez.
        if (haPerdido)
            return;

        // Marcar la partida como perdida.
        haPerdido = true;

        // Detener el movimiento del troll.
        if (movimientoTroll != null)
            movimientoTroll.DetenerMovimiento();

        // Ocultar el panel del acertijo si estaba abierto.
        if (contenedorAcertijo != null)
            contenedorAcertijo.SetActive(false);

        // Recorrer todos los elementos de la interfaz
        // y ocultar aquellos que estén asignados.
        foreach (GameObject elemento in elementosDurantePartida)
        {
            if (elemento != null)
                elemento.SetActive(false);
        }

        // Mostrar el panel de derrota.
        if (panelDerrota != null)
            panelDerrota.SetActive(true);

        // Mostrar un mensaje informativo en la consola.
        Debug.Log("Derrota: el troll ha llegado a la puerta.");
    }

    // Reinicia la partida para permitir jugar otra vez.
    public void ReiniciarPartida()
    {
        // Bloquear temporalmente la detección de la puerta
        // para evitar que se produzca otra derrota durante el reinicio.
        haPerdido = true;

        // Detener el movimiento antes de restaurar la posición.
        if (movimientoTroll != null)
            movimientoTroll.DetenerMovimiento();

        // Restaurar la posición, rotación y escala iniciales del troll.
        if (troll != null)
        {
            troll.position = posicionInicialTroll;
            troll.rotation = rotacionInicialTroll;
            troll.localScale = escalaInicialTroll;
        }

        // Reiniciar las variables internas del movimiento
        // y asegurarse de que el componente esté activado.
        if (movimientoTroll != null)
        {
            movimientoTroll.ReiniciarMovimiento();
            movimientoTroll.enabled = true;
        }

        // Ocultar el panel de derrota.
        if (panelDerrota != null)
            panelDerrota.SetActive(false);

        // Volver a mostrar todos los elementos de la partida.
        foreach (GameObject elemento in elementosDurantePartida)
        {
            if (elemento != null)
                elemento.SetActive(true);
        }

        // Asegurarse de que el panel del acertijo permanezca oculto.
        if (contenedorAcertijo != null)
            contenedorAcertijo.SetActive(false);

        // Reactivar la detección de colisiones con la puerta.
        haPerdido = false;

        // Iniciar de nuevo el movimiento del troll.
        if (movimientoTroll != null)
            movimientoTroll.IniciarMovimiento();

        // Mostrar un mensaje informativo en la consola.
        Debug.Log("Revancha: partida reiniciada.");
    }
}
