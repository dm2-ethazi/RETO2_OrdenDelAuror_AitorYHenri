
using UnityEngine;

public class MovimientoTroll : MonoBehaviour
{
    [Header("Movimiento")]
    [SerializeField] private float velocidad = 100f;

    private bool moviendose = false;

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
        moviendose = false;
    }
}
