
using UnityEngine;

public class ColisionPuerta : MonoBehaviour
{
    [Header("Referencias")]
    [SerializeField] private RectTransform troll;
    [SerializeField] private RectTransform puerta;
    [SerializeField] private GameObject panelDerrota;
    [SerializeField] private MonoBehaviour movimientoTroll;

    private bool haPerdido = false;

    private void Update()
    {
        if (haPerdido || troll == null || puerta == null)
            return;

        if (SeSolapan(troll, puerta))
        {
            PerderPartida();
        }
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
        haPerdido = true;

        if (movimientoTroll != null)
            movimientoTroll.enabled = false;

        if (panelDerrota != null)
            panelDerrota.SetActive(true);

        Debug.Log("¡El troll ha llegado a la puerta! Derrota.");
    }
}
