
using UnityEngine;

public class VidaTroll : MonoBehaviour
{
    [Header("Corazones")]
    [SerializeField] private GameObject[] corazones;

    private int vidaActual;

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
            corazones[i].SetActive(i < vidaActual);
        }
    }

    public void ReiniciarVida()
    {
        vidaActual = corazones.Length;
        ActualizarCorazones();
    }
}
