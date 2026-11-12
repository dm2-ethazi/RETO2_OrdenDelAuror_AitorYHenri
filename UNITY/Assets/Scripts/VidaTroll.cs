using UnityEngine;

// Gestiona la vida del troll mediante un sistema de corazones.
public class VidaTroll : MonoBehaviour
{
    [Header("Corazones")]
    // Array con las imágenes u objetos que representan los corazones de vida.
    [SerializeField] private GameObject[] corazones;

    [Header("Controlador del menú")]
    // Referencia al controlador del menú principal para mostrar la pantalla de victoria.
    [SerializeField] private MenuPrincipalController menuPrincipalController;

    // Almacena la cantidad de vida que tiene actualmente el troll.
    private int vidaActual;

    // Evita que la pantalla de victoria se muestre más de una vez.
    private bool victoriaMostrada;

    // Propiedad pública que permite consultar la vida actual del troll
    // sin permitir modificarla directamente desde otros scripts.
    public int VidaActual => vidaActual;

    // Se ejecuta al inicio del juego.
    private void Start()
    {
        // Establece la vida inicial según la cantidad de corazones asignados.
        vidaActual = corazones.Length;

        // Indica que todavía no se ha mostrado la pantalla de victoria.
        victoriaMostrada = false;

        // Actualiza la representación visual de los corazones.
        ActualizarCorazones();
    }

    // Reduce la vida del troll cuando recibe daño.
    public void RecibirDano()
    {
        // Si el troll ya no tiene vida, no permite seguir reduciéndola.
        if (vidaActual <= 0)
            return;

        // Resta un punto de vida.
        vidaActual--;

        // Actualiza los corazones para reflejar el daño recibido.
        ActualizarCorazones();

        // Comprueba si el troll ha perdido toda su vida
        // y si todavía no se ha mostrado la victoria.
        if (vidaActual <= 0 && !victoriaMostrada)
        {
            // Marca la victoria como mostrada para evitar repeticiones.
            victoriaMostrada = true;

            // Muestra un mensaje en la consola indicando que el troll ha sido derrotado.
            Debug.Log("¡El troll ha sido derrotado!");

            // Comprueba que exista una referencia al controlador del menú.
            if (menuPrincipalController != null)
            {
                // Muestra la pantalla de victoria.
                menuPrincipalController.MostrarVictoria();
            }
            else
            {
                // Muestra un error si no se ha asignado el controlador en el Inspector.
                Debug.LogError("No se ha asignado MenuPrincipalController en VidaTroll.");
            }
        }
    }

    // Actualiza visualmente los corazones según la vida actual del troll.
    private void ActualizarCorazones()
    {
        // Recorre todos los corazones del array.
        for (int i = 0; i < corazones.Length; i++)
        {
            // Comprueba que el corazón actual tenga una referencia válida.
            if (corazones[i] != null)
            {
                // Mantiene activo el corazón si su posición es inferior
                // a la vida actual del troll; en caso contrario, lo oculta.
                corazones[i].SetActive(i < vidaActual);
            }
        }
    }

    // Restaura la vida del troll al valor inicial.
    public void ReiniciarVida()
    {
        // Recupera toda la vida según la cantidad de corazones configurados.
        vidaActual = corazones.Length;

        // Permite que la pantalla de victoria pueda mostrarse de nuevo.
        victoriaMostrada = false;

        // Vuelve a mostrar todos los corazones.
        ActualizarCorazones();
    }
}