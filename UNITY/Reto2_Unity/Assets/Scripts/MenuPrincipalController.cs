
using TMPro;
using UnityEngine;

public class MenuPrincipalController : MonoBehaviour
{
    [Header("Paneles")]
    [SerializeField] private GameObject contenedorMenu;
    [SerializeField] private GameObject panelConfirmarSalida;
    [SerializeField] private GameObject panelSeleccionIdioma;

    [Header("Textos del menu principal")]
    [SerializeField] private TMP_Text textoTitulo;
    [SerializeField] private TMP_Text textoJugar;
    [SerializeField] private TMP_Text textoInstrucciones;
    [SerializeField] private TMP_Text textoIdioma;
    [SerializeField] private TMP_Text textoSalir;

    [Header("Textos de confirmacion de salida")]
    [SerializeField] private TMP_Text textoConfirmacion;
    [SerializeField] private TMP_Text textoConfirmarSalir;
    [SerializeField] private TMP_Text textoCancelarSalir;

    [Header("Textos del panel de idiomas")]
    [SerializeField] private TMP_Text textoEspanol;
    [SerializeField] private TMP_Text textoEuskera;
    [SerializeField] private TMP_Text textoIngles;

    private void Start()
    {
        if (contenedorMenu != null)
            contenedorMenu.SetActive(true);

        if (panelConfirmarSalida != null)
            panelConfirmarSalida.SetActive(false);

        if (panelSeleccionIdioma != null)
            panelSeleccionIdioma.SetActive(false);

        ActualizarTextos();
    }

    // Mostrar el panel de confirmacion de salida
    public void MostrarConfirmacion()
    {
        if (contenedorMenu != null)
            contenedorMenu.SetActive(false);

        if (panelSeleccionIdioma != null)
            panelSeleccionIdioma.SetActive(false);

        if (panelConfirmarSalida != null)
            panelConfirmarSalida.SetActive(true);
    }

    // Confirmar la salida del juego
    public void ConfirmarSalir()
    {
        Debug.Log("Cerrando el juego...");

#if UNITY_EDITOR
        UnityEditor.EditorApplication.isPlaying = false;
#else
        Application.Quit();
#endif
    }

    // Cancelar la salida y volver al menu
    public void CancelarSalir()
    {
        if (panelConfirmarSalida != null)
            panelConfirmarSalida.SetActive(false);

        if (contenedorMenu != null)
            contenedorMenu.SetActive(true);
    }

    // Mostrar el panel para seleccionar idioma
    public void MostrarSeleccionIdioma()
    {
        if (contenedorMenu != null)
            contenedorMenu.SetActive(false);

        if (panelConfirmarSalida != null)
            panelConfirmarSalida.SetActive(false);

        if (panelSeleccionIdioma != null)
            panelSeleccionIdioma.SetActive(true);
    }

    // Seleccionar espanol
    public void SeleccionarEspanol()
    {
        SeleccionarIdioma("es");
    }

    // Seleccionar euskera
    public void SeleccionarEuskera()
    {
        SeleccionarIdioma("eu");
    }

    // Seleccionar ingles
    public void SeleccionarIngles()
    {
        SeleccionarIdioma("en");
    }

    private void SeleccionarIdioma(string idioma)
    {
        if (GestorIdiomas.Instancia == null)
        {
            Debug.LogError(
                "No existe un GestorIdiomas activo en la escena."
            );
            return;
        }

        GestorIdiomas.Instancia.CambiarIdioma(idioma);

        ActualizarTextos();

        if (panelSeleccionIdioma != null)
            panelSeleccionIdioma.SetActive(false);

        if (panelConfirmarSalida != null)
            panelConfirmarSalida.SetActive(false);

        if (contenedorMenu != null)
            contenedorMenu.SetActive(true);
    }

    // Actualizar todos los textos segun el idioma seleccionado
    private void ActualizarTextos()
    {
        if (GestorIdiomas.Instancia == null)
        {
            Debug.LogError(
                "No existe un GestorIdiomas activo. " +
                "Anadelo a la escena y asigna los JSON."
            );
            return;
        }

        AsignarTexto(textoTitulo, "titulo");
        AsignarTexto(textoJugar, "jugar");
        AsignarTexto(textoInstrucciones, "instrucciones");
        AsignarTexto(textoIdioma, "idioma");
        AsignarTexto(textoSalir, "salir");

        AsignarTexto(textoConfirmacion, "confirmar_salida");
        AsignarTexto(textoConfirmarSalir, "salir");
        AsignarTexto(textoCancelarSalir, "cancelar");

        AsignarTexto(textoEspanol, "espanol");
        AsignarTexto(textoEuskera, "euskera");
        AsignarTexto(textoIngles, "ingles");
    }

    private void AsignarTexto(TMP_Text componente, string clave)
    {
        if (componente != null)
        {
            componente.text =
                GestorIdiomas.Instancia.ObtenerTexto(clave);
        }
    }
}
