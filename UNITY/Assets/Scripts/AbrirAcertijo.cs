
using UnityEngine;

public class AbrirAcertijo : MonoBehaviour
{
    public GameObject contenedorPaloma;
    public GameObject contenedorAcertijo;

    public void AbrirPanel()
    {
        contenedorPaloma.SetActive(false);
        contenedorAcertijo.SetActive(true);
    }
}
