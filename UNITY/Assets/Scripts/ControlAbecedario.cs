using UnityEngine;

public class ControlAbecedario : MonoBehaviour
{
    [Header("Referencia")]
    [SerializeField] private GameObject imagenAbecedario;

    private void Start()
    {
        // El abecedario empieza oculto.
        if (imagenAbecedario != null)
            imagenAbecedario.SetActive(false);
    }

    public void AlternarAbecedario()
    {
        if (imagenAbecedario == null)
        {
            Debug.LogWarning("No se ha asignado la imagen del abecedario.");
            return;
        }

        imagenAbecedario.SetActive(!imagenAbecedario.activeSelf);
    }
}
