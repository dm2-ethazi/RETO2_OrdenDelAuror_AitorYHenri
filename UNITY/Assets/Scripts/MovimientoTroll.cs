
using UnityEngine;

public class MovimientoTroll : MonoBehaviour
{
    [Header("Movimiento")]
    [SerializeField] private float velocidad = 100f;

    [Header("Aceleración por fallar acertijos")]
    [SerializeField] private float incrementoVelocidad = 0.20f;
    [SerializeField] private float multiplicadorMaximo = 2f;

    private bool moviendose = false;
    private float velocidadInicial;
    private Vector3 posicionInicial;

    private void Awake()
    {
        velocidadInicial = velocidad;
        posicionInicial = transform.position;
    }

    private void Update()
    {
        if (!moviendose)
            return;

        transform.Translate(
            Vector3.right * velocidad * Time.deltaTime,
            Space.Self
        );
    }

    public void IniciarMovimiento()
    {
        moviendose = true;
    }

    public void DetenerMovimiento()
    {
        moviendose = false;
    }

    // Aumenta la velocidad cuando el jugador falla un acertijo.
    public void Acelerar()
    {
        float velocidadMaxima = velocidadInicial * multiplicadorMaximo;

        velocidad *= (1f + incrementoVelocidad);

        velocidad = Mathf.Min(velocidad, velocidadMaxima);

        Debug.Log("¡El troll ha acelerado! Velocidad actual: " + velocidad);
    }

    public void ReiniciarMovimiento()
    {
        transform.position = posicionInicial;
        velocidad = velocidadInicial;
        moviendose = false;
    }

    public void ReiniciarYEmpezarMovimiento()
    {
        ReiniciarMovimiento();
        IniciarMovimiento();
    }
}
