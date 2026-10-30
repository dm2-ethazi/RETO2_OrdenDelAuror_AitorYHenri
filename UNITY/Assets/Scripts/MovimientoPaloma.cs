
using System.Collections;
using UnityEngine;

public class MovimientoPaloma : MonoBehaviour
{
    public float velocidad = 500f;
    public float segundosEspera = 3f;
    public float posicionY = 200f;

    private RectTransform rectTransform;
    private RectTransform canvasRect;
    private bool movimientoPausado;
    private bool esperando;

    private void Awake()
    {
        rectTransform = GetComponent<RectTransform>();

        Canvas canvas = GetComponentInParent<Canvas>();

        if (canvas != null)
            canvasRect = canvas.GetComponent<RectTransform>();
    }

    private void Start()
    {
        if (canvasRect == null)
        {
            Debug.LogError("MovimientoPaloma: no se ha encontrado el Canvas.");
            enabled = false;
            return;
        }

        StartCoroutine(CicloPaloma());
    }

    private void Update()
    {
        if (movimientoPausado || esperando || canvasRect == null)
            return;

        Vector2 posicion = rectTransform.anchoredPosition;
        posicion.x += velocidad * Time.deltaTime;
        posicion.y = posicionY;

        rectTransform.anchoredPosition = posicion;
    }

    private IEnumerator CicloPaloma()
    {
        while (true)
        {
            // Aparece fuera de la pantalla por la izquierda
            ColocarIzquierda();

            // Espera hasta salir completamente por la derecha
            yield return new WaitUntil(() =>
                rectTransform.anchoredPosition.x >
                canvasRect.rect.width / 2f +
                rectTransform.rect.width / 2f);

            // Se queda fuera de pantalla mientras espera
            esperando = true;
            yield return new WaitForSeconds(segundosEspera);
            esperando = false;
        }
    }

    private void ColocarIzquierda()
    {
        Vector2 posicion = rectTransform.anchoredPosition;

        posicion.x = -canvasRect.rect.width / 2f
                     - rectTransform.rect.width / 2f;
        posicion.y = posicionY;

        rectTransform.anchoredPosition = posicion;
    }

    public void PausarMovimiento()
    {
        movimientoPausado = true;
    }

    public void ReanudarMovimiento()
    {
        movimientoPausado = false;
    }
}
