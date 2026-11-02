
using UnityEngine;

public class CollisionPuerta : MonoBehaviour
{
    [Header("Referencias")]
    [SerializeField] private RectTransform troll;
    [SerializeField] private RectTransform puerta;
    [SerializeField] private GameObject panelDerrota;
    [SerializeField] private MovimientoTroll movimientoTroll;
    [SerializeField] private GameObject contenedorAcertijo;

    [Header("Elementos que se ocultan al perder")]
    [SerializeField] private GameObject[] elementosDurantePartida;

    private Vector3 posicionInicialTroll;
    private Quaternion rotacionInicialTroll;
    private Vector3 escalaInicialTroll;

    private bool haPerdido = false;

    private void Awake()
    {
        if (troll != null)
        {
            posicionInicialTroll = troll.position;
            rotacionInicialTroll = troll.rotation;
            escalaInicialTroll = troll.localScale;
        }
    }

    private void Update()
    {
        if (haPerdido || troll == null || puerta == null)
            return;

        if (SeSolapan(troll, puerta))
            PerderPartida();
    }

    private bool SeSolapan(RectTransform a, RectTransform b)
    {
        Vector3[] esquinasA = new Vector3[4];
        Vector3[] esquinasB = new Vector3[4];

        a.GetWorldCorners(esquinasA);
        b.GetWorldCorners(esquinasB);

        Rect rectA = Rect.MinMaxRect(
            esquinasA[0].x,
            esquinasA[0].y,
            esquinasA[2].x,
            esquinasA[2].y
        );

        Rect rectB = Rect.MinMaxRect(
            esquinasB[0].x,
            esquinasB[0].y,
            esquinasB[2].x,
            esquinasB[2].y
        );

        return rectA.Overlaps(rectB);
    }

    private void PerderPartida()
    {
        if (haPerdido)
            return;

        haPerdido = true;

        if (movimientoTroll != null)
            movimientoTroll.DetenerMovimiento();

        if (contenedorAcertijo != null)
            contenedorAcertijo.SetActive(false);

        foreach (GameObject elemento in elementosDurantePartida)
        {
            if (elemento != null)
                elemento.SetActive(false);
        }

        if (panelDerrota != null)
            panelDerrota.SetActive(true);

        Debug.Log("Derrota: el troll ha llegado a la puerta.");
    }

    public void ReiniciarPartida()
    {
        // Bloquear temporalmente la detección de la puerta.
        haPerdido = true;

        // Detener el movimiento antes de restaurar la posición.
        if (movimientoTroll != null)
            movimientoTroll.DetenerMovimiento();

        // Restaurar la posición inicial del troll.
        if (troll != null)
        {
            troll.position = posicionInicialTroll;
            troll.rotation = rotacionInicialTroll;
            troll.localScale = escalaInicialTroll;
        }

        // Reiniciar las variables internas del movimiento.
        if (movimientoTroll != null)
        {
            movimientoTroll.ReiniciarMovimiento();
            movimientoTroll.enabled = true;
        }

        // Ocultar el panel de derrota.
        if (panelDerrota != null)
            panelDerrota.SetActive(false);

        // Reactivar los elementos de la partida.
        foreach (GameObject elemento in elementosDurantePartida)
        {
            if (elemento != null)
                elemento.SetActive(true);
        }

        // El acertijo debe empezar oculto.
        if (contenedorAcertijo != null)
            contenedorAcertijo.SetActive(false);

        // Permitir detectar de nuevo la puerta.
        haPerdido = false;

        // Iniciar el movimiento de nuevo.
        if (movimientoTroll != null)
            movimientoTroll.IniciarMovimiento();

        Debug.Log("Revancha: partida reiniciada.");
    }
}
