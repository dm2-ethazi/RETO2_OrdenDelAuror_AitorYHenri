
using UnityEngine;

public class AbrirAcertijo : MonoBehaviour
{
    [Header("Contenedores")]
    [SerializeField] private GameObject contenedorPaloma;
    [SerializeField] private GameObject contenedorAcertijo;

    [Header("Referencias")]
    [SerializeField] private GestorAcertijo gestorAcertijo;
    [SerializeField] private MovimientoPaloma movimientoPaloma;

    public void AbrirPanel()
    {
        // Evitar abrir el panel si faltan referencias.
        if (contenedorAcertijo == null || contenedorPaloma == null)
        {
            Debug.LogError(
                "AbrirAcertijo: faltan referencias a los contenedores."
            );
            return;
        }

        // Pausar la paloma antes de ocultarla.
        if (movimientoPaloma != null)
        {
            movimientoPaloma.PausarMovimiento();
        }

        // Ocultar la paloma.
        contenedorPaloma.SetActive(false);

        // Mostrar el pergamino.
        contenedorAcertijo.SetActive(true);

        // Preparar un acertijo nuevo y limpiar el intento anterior.
        if (gestorAcertijo != null)
        {
            gestorAcertijo.PrepararNuevoAcertijo();
        }
        else
        {
            Debug.LogError(
                "AbrirAcertijo: no se ha asignado GestorAcertijo."
            );
        }
    }
}
