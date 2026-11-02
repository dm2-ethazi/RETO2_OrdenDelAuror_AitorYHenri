
using System.Collections;
using UnityEngine;

public class MovimientoPaloma : MonoBehaviour
{
    [Header("Configuración del movimiento")]
    [SerializeField] public float velocidad = 500f;
    [SerializeField] public float incrementoVelocidad = 50f;
    [SerializeField] public float segundosEspera = 3f;
    [SerializeField] public float posicionY = 200f;

    private RectTransform rectTransform;
    private RectTransform canvasRect;

    private bool movimientoPausado;
    private bool esperando;
    private Coroutine cicloMovimiento;

    private float velocidadInicial;
    private bool velocidadInicialGuardada;

    private void Awake()
    {
        // Guardar los valores iniciales de la partida.
        velocidadInicial = velocidad;
        velocidadInicialGuardada = true;

        rectTransform = GetComponent<RectTransform>();

        Canvas canvas = GetComponentInParent<Canvas>();

        if (canvas != null)
        {
            canvasRect = canvas.GetComponent<RectTransform>();
        }
    }

    private void OnEnable()
    {
        if (rectTransform == null)
        {
            rectTransform = GetComponent<RectTransform>();
        }

        if (canvasRect == null)
        {
            Canvas canvas = GetComponentInParent<Canvas>();

            if (canvas != null)
            {
                canvasRect = canvas.GetComponent<RectTransform>();
            }
        }

        if (canvasRect == null)
        {
            Debug.LogError(
                "MovimientoPaloma: no se ha encontrado el Canvas."
            );
            return;
        }

        movimientoPausado = false;
        esperando = false;

        ColocarIzquierda();

        if (cicloMovimiento != null)
        {
            StopCoroutine(cicloMovimiento);
        }

        cicloMovimiento = StartCoroutine(CicloPaloma());
    }

    private void OnDisable()
    {
        if (cicloMovimiento != null)
        {
            StopCoroutine(cicloMovimiento);
            cicloMovimiento = null;
        }

        esperando = false;
        movimientoPausado = false;
    }

    private void Update()
    {
        if (movimientoPausado || esperando || canvasRect == null)
        {
            return;
        }

        Vector2 posicion = rectTransform.anchoredPosition;

        posicion.x += velocidad * Time.deltaTime;
        posicion.y = posicionY;

        rectTransform.anchoredPosition = posicion;
    }

    private IEnumerator CicloPaloma()
    {
        while (true)
        {
            // Esperar hasta que la paloma salga por la derecha.
            yield return new WaitUntil(() =>
                !movimientoPausado &&
                rectTransform.anchoredPosition.x >
                canvasRect.rect.width / 2f +
                rectTransform.rect.width / 2f
            );

            // Esperar antes de iniciar otra pasada.
            esperando = true;

            yield return new WaitForSeconds(segundosEspera);

            esperando = false;

            if (!movimientoPausado)
            {
                ColocarIzquierda();
            }
        }
    }

    private void ColocarIzquierda()
    {
        if (canvasRect == null || rectTransform == null)
        {
            return;
        }

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

    public void ReiniciarDesdeIzquierda()
    {
        movimientoPausado = false;
        esperando = false;

        ColocarIzquierda();
    }

    public void Acelerar(float incremento)
    {
        velocidad += incremento;
    }

    public void AcelerarConfigurado()
    {
        velocidad += incrementoVelocidad;
    }

    public void ReiniciarPartida()
    {
        // Recuperar la velocidad inicial.
        if (velocidadInicialGuardada)
        {
            velocidad = velocidadInicial;
        }

        // Colocar la paloma al principio del recorrido.
        ReiniciarDesdeIzquierda();
    }
}
