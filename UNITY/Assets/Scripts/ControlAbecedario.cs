
using UnityEngine;

public class ControlAbecedario : MonoBehaviour
{
    // Referencia a la imagen del abecedario que se mostrará u ocultará.
    [Header("Referencia")]
    [SerializeField] private GameObject imagenAbecedario;

    private void Start()
    {
        // Ocultar el abecedario al iniciar el juego.
        if (imagenAbecedario != null)
            imagenAbecedario.SetActive(false);
    }

    // Alterna la visibilidad de la imagen del abecedario.
    public void AlternarAbecedario()
    {
        // Comprobar que la imagen está asignada en el Inspector.
        if (imagenAbecedario == null)
        {
            Debug.LogWarning("No se ha asignado la imagen del abecedario.");
            return;
        }

        // Cambiar el estado de la imagen:
        // si está visible, la oculta; si está oculta, la muestra.
        imagenAbecedario.SetActive(!imagenAbecedario.activeSelf);
    }
}
