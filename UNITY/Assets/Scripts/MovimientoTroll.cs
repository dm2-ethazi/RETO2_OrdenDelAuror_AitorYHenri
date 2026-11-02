
using UnityEngine;

public class MovimientoTroll : MonoBehaviour
{
    [Header("Movimiento")]
    [SerializeField] private float velocidad = 100f;

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

    public void ReiniciarMovimiento()
    {
        transform.position = posicionInicial;
        velocidad = velocidadInicial;

        // Dejar el troll preparado para volver a moverse.
        moviendose = false;
    }

    public void ReiniciarYEmpezarMovimiento()
    {
        ReiniciarMovimiento();
        IniciarMovimiento();
    }
}
