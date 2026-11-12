using UnityEngine;

// Controla el movimiento del troll y la aceleración que recibe
// cuando el jugador falla un acertijo.
public class MovimientoTroll : MonoBehaviour
{
    [Header("Movimiento")]
    // Velocidad inicial a la que se desplaza el troll.
    [SerializeField] private float velocidad = 100f;

    [Header("Aceleración por fallar acertijos")]
    // Porcentaje en el que aumenta la velocidad cada vez que falla un acertijo.
    [SerializeField] private float incrementoVelocidad = 0.20f;

    // Multiplicador máximo que puede alcanzar la velocidad respecto a la inicial.
    [SerializeField] private float multiplicadorMaximo = 2f;

    // Indica si el troll está actualmente en movimiento.
    private bool moviendose = false;

    // Almacena la velocidad original para poder restaurarla al reiniciar.
    private float velocidadInicial;

    // Guarda la posición inicial del troll.
    private Vector3 posicionInicial;

    // Se ejecuta al inicializar el objeto.
    private void Awake()
    {
        // Guarda la velocidad configurada inicialmente.
        velocidadInicial = velocidad;

        // Guarda la posición inicial del troll.
        posicionInicial = transform.position;
    }

    // Se ejecuta una vez por cada frame.
    private void Update()
    {
        // Si el troll no está en movimiento, no realiza ningún desplazamiento.
        if (!moviendose)
            return;

        // Desplaza el troll hacia la derecha utilizando su velocidad actual.
        // Time.deltaTime permite que el movimiento sea independiente de los FPS.
        transform.Translate(
            Vector3.right * velocidad * Time.deltaTime,
            Space.Self
        );
    }

    // Activa el movimiento del troll.
    public void IniciarMovimiento()
    {
        moviendose = true;
    }

    // Detiene el movimiento del troll.
    public void DetenerMovimiento()
    {
        moviendose = false;
    }

    // Aumenta la velocidad cuando el jugador falla un acertijo.
    public void Acelerar()
    {
        // Calcula la velocidad máxima permitida según la velocidad inicial
        // y el multiplicador máximo configurado.
        float velocidadMaxima = velocidadInicial * multiplicadorMaximo;

        // Incrementa la velocidad aplicando el porcentaje configurado.
        velocidad *= (1f + incrementoVelocidad);

        // Evita que la velocidad supere el límite máximo establecido.
        velocidad = Mathf.Min(velocidad, velocidadMaxima);

        // Muestra en la consola la velocidad actual del troll.
        Debug.Log("¡El troll ha acelerado! Velocidad actual: " + velocidad);
    }

    // Restaura el estado inicial del movimiento del troll.
    public void ReiniciarMovimiento()
    {
        // Devuelve el troll a su posición original.
        transform.position = posicionInicial;

        // Restaura la velocidad inicial.
        velocidad = velocidadInicial;

        // Deja el movimiento detenido.
        moviendose = false;
    }

    // Reinicia el movimiento y lo vuelve a iniciar inmediatamente.
    public void ReiniciarYEmpezarMovimiento()
    {
        // Restaura la posición, velocidad y estado iniciales.
        ReiniciarMovimiento();

        // Activa de nuevo el movimiento del troll.
        IniciarMovimiento();
    }
}