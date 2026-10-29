
using UnityEngine;
using UnityEngine.UI;

public class BarraTiempo : MonoBehaviour
{
    [Header("Barra visual")]
    [SerializeField] private Image barra;

    [Header("Tiempo")]
    [SerializeField] private float tiempoMaximo = 30f;

    private float tiempoRestante;
    private bool activo;

    private void Start()
    {
        ReiniciarTiempo();
    }

    private void Update()
    {
        if (!activo)
            return;

        tiempoRestante -= Time.deltaTime;

        if (tiempoRestante <= 0f)
        {
            tiempoRestante = 0f;
            activo = false;
        }

        ActualizarBarra();
    }

    private void ActualizarBarra()
    {
        if (barra != null && tiempoMaximo > 0f)
        {
            barra.fillAmount = tiempoRestante / tiempoMaximo;
        }
    }

    public void ReiniciarTiempo()
    {
        tiempoRestante = tiempoMaximo;
        activo = true;
        ActualizarBarra();
    }

    public void PausarTiempo()
    {
        activo = false;
    }

    public void ReanudarTiempo()
    {
        if (tiempoRestante > 0f)
            activo = true;
    }
}
