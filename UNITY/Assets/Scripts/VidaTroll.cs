
using UnityEngine;

public class VidaTroll : MonoBehaviour
{
    [Header("Corazones")]
    [SerializeField] private GameObject[] corazones;

    private int vidaActual;

    // Permite consultar las vidas desde otros scripts.
    public int VidaActual => vidaActual;

    private void Start()
    {
        vidaActual = corazones.Length;
        ActualizarCorazones();
    }

    public void RecibirDano()
    {
        if (vidaActual <= 0)
            return;

        vidaActual--;

        ActualizarCorazones();

        if (vidaActual <= 0)
        {
            Debug.Log("¡El troll ha sido derrotado!");
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
        ActualizarCorazones();
    }
}
