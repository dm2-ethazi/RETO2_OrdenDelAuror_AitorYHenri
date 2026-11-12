
using System.Collections;
using UnityEngine;

public class MovimientoPaloma : MonoBehaviour
{
    // Parámetros que permiten configurar el movimiento de la paloma desde el Inspector.
    [Header("Configuración del movimiento")]
    [SerializeField] public float velocidad = 500f;
    [SerializeField] public float incrementoVelocidad = 50f;
    [SerializeField] public float segundosEspera = 3f;
    [SerializeField] public float posicionY = 200f;

    // Referencia al RectTransform de la paloma y al Canvas donde se encuentra.
    private RectTransform rectTransform;
    private RectTransform canvasRect;

    // Indica si el movimiento está pausado o si la paloma está esperando.
    private bool movimientoPausado;
    private bool esperando;

    // Corrutina que controla el ciclo de aparición y movimiento de la paloma.
    private Coroutine cicloMovimiento;

    // Variables para almacenar y recuperar la velocidad inicial.
    private float velocidadInicial;
    private bool velocidadInicialGuardada;

    private void Awake()
    {
        // Guardar la velocidad inicial para poder recuperarla al reiniciar la partida.
        velocidadInicial = velocidad;
        velocidadInicialGuardada = true;

        // Obtener el RectTransform del objeto que contiene este script.
        rectTransform = GetComponent<RectTransform>();

        // Buscar el Canvas al que pertenece la paloma.
        Canvas canvas = GetComponentInParent<Canvas>();

        // Obtener el RectTransform del Canvas si se ha encontrado.
        if (canvas != null)
        {
            canvasRect = canvas.GetComponent<RectTransform>();
        }
    }

    private void OnEnable()
    {
        // Obtener el RectTransform de la paloma si todavía no está asignado.
        if (rectTransform == null)
        {
            rectTransform = GetComponent<RectTransform>();
        }

        // Buscar el Canvas si todavía no se ha encontrado.
        if (canvasRect == null)
        {
            Canvas canvas = GetComponentInParent<Canvas>();

            if (canvas != null)
            {
                canvasRect = canvas.GetComponent<RectTransform>();
            }
        }

        // Comprobar que se ha encontrado el Canvas.
        // Sin él no se pueden calcular los límites del recorrido.
        if (canvasRect == null)
        {
            Debug.LogError(
                "MovimientoPaloma: no se ha encontrado el Canvas."
            );
            return;
        }

        // Activar el movimiento y desactivar el estado de espera.
        movimientoPausado = false;
        esperando = false;

        // Colocar la paloma en el extremo izquierdo del Canvas.
        ColocarIzquierda();

        // Detener una posible corrutina anterior para evitar duplicados.
        if (cicloMovimiento != null)
        {
            StopCoroutine(cicloMovimiento);
        }

        // Iniciar el ciclo que controla las apariciones de la paloma.
        cicloMovimiento = StartCoroutine(CicloPaloma());
    }

    private void OnDisable()
    {
        // Detener la corrutina cuando el objeto se desactiva.
        if (cicloMovimiento != null)
        {
            StopCoroutine(cicloMovimiento);
            cicloMovimiento = null;
        }

        // Restablecer los estados internos del movimiento.
        esperando = false;
        movimientoPausado = false;
    }

    private void Update()
    {
        // No mover la paloma si está pausada, esperando
        // o si no se ha encontrado el Canvas.
        if (movimientoPausado || esperando || canvasRect == null)
        {
            return;
        }

        // Obtener la posición actual de la paloma dentro del Canvas.
        Vector2 posicion = rectTransform.anchoredPosition;

        // Desplazar la paloma hacia la derecha según su velocidad
        // y el tiempo transcurrido desde el último fotograma.
        posicion.x += velocidad * Time.deltaTime;

        // Mantener la altura configurada en el Inspector.
        posicion.y = posicionY;

        // Aplicar la nueva posición al RectTransform.
        rectTransform.anchoredPosition = posicion;
    }

    // Controla el ciclo de movimiento de la paloma.
    private IEnumerator CicloPaloma()
    {
        // Mantener el ciclo activo mientras el objeto esté habilitado.
        while (true)
        {
            // Esperar hasta que la paloma salga completamente
            // por el extremo derecho del Canvas.
            yield return new WaitUntil(() =>
                !movimientoPausado &&
                rectTransform.anchoredPosition.x >
                canvasRect.rect.width / 2f +
                rectTransform.rect.width / 2f
            );

            // Indicar que la paloma está esperando antes de reaparecer.
            esperando = true;

            // Esperar los segundos configurados en el Inspector.
            yield return new WaitForSeconds(segundosEspera);

            // Finalizar el estado de espera.
            esperando = false;

            // Si el movimiento no está pausado, colocar la paloma
            // de nuevo en el extremo izquierdo para iniciar otro recorrido.
            if (!movimientoPausado)
            {
                ColocarIzquierda();
            }
        }
    }

    // Coloca la paloma fuera del Canvas, por su extremo izquierdo.
    private void ColocarIzquierda()
    {
        // Comprobar que existen las referencias necesarias.
        if (canvasRect == null || rectTransform == null)
        {
            return;
        }

        // Obtener la posición actual de la paloma.
        Vector2 posicion = rectTransform.anchoredPosition;

        // Situar la paloma fuera del límite izquierdo del Canvas,
        // teniendo en cuenta la mitad de su propio ancho.
        posicion.x = -canvasRect.rect.width / 2f
                     - rectTransform.rect.width / 2f;

        // Establecer la altura configurada.
        posicion.y = posicionY;

        // Aplicar la nueva posición.
        rectTransform.anchoredPosition = posicion;
    }

    // Pausa el movimiento de la paloma.
    public void PausarMovimiento()
    {
        movimientoPausado = true;
    }

    // Permite que la paloma vuelva a moverse.
    public void ReanudarMovimiento()
    {
        movimientoPausado = false;
    }

    // Reinicia la posición de la paloma y reactiva su movimiento.
    public void ReiniciarDesdeIzquierda()
    {
        // Eliminar los estados de pausa y espera.
        movimientoPausado = false;
        esperando = false;

        // Colocar la paloma al principio del recorrido.
        ColocarIzquierda();
    }

    // Aumenta la velocidad de la paloma según el incremento recibido.
    public void Acelerar(float incremento)
    {
        velocidad += incremento;
    }

    // Aumenta la velocidad utilizando el incremento configurado
    // en el Inspector de Unity.
    public void AcelerarConfigurado()
    {
        velocidad += incrementoVelocidad;
    }

    // Recupera el estado inicial de la paloma al reiniciar la partida.
    public void ReiniciarPartida()
    {
        // Restaurar la velocidad inicial si se había guardado correctamente.
        if (velocidadInicialGuardada)
        {
            velocidad = velocidadInicial;
        }

        // Colocar la paloma al principio del recorrido
        // y reactivar su movimiento.
        ReiniciarDesdeIzquierda();
    }
}
