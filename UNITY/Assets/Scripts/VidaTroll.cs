
using UnityEngine;

public class VidaTroll : MonoBehaviour
{
    [Header("Corazones")]
    [SerializeField] private GameObject[] corazones;

    [Header("Controlador del menú")]
    [SerializeField] private MenuPrincipalController menuPrincipalController;

    private int vidaActual;
    private bool victoriaMostrada;

    public int VidaActual => vidaActual;

    private void Start()
    {
        vidaActual = corazones.Length;
        victoriaMostrada = false;
        ActualizarCorazones();
    }

    public void RecibirDano()
    {
        if (vidaActual <= 0)
            return;

        vidaActual--;

        ActualizarCorazones();

        if (vidaActual <= 0 && !victoriaMostrada)
        {
            victoriaMostrada = true;

            Debug.Log("¡El troll ha sido derrotado!");

            if (menuPrincipalController != null)
            {
                menuPrincipalController.MostrarVictoria();
            }
            else
            {
                Debug.LogError("No se ha asignado MenuPrincipalController en VidaTroll.");
            }
        }
    }

    private void ActualizarCorazones()
    {
        for (int i = 0; i < corazones.Length; i++)
        {
            if (corazones[i] != null)
            {
                corazones[i].SetActive(i < vidaActual);
            }
        }
    }

    public void ReiniciarVida()
    {
        vidaActual = corazones.Length;
        victoriaMostrada = false;
        ActualizarCorazones();
    }
}
