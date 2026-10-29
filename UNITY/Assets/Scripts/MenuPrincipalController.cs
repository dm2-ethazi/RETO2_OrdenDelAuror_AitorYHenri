
using TMPro;
using UnityEngine;

public class MenuPrincipalController : MonoBehaviour
{
    [Header("Contenedores principales")]
    [SerializeField] private GameObject contenedorMenuPrincipal;
    [SerializeField] private GameObject contenedorMinijuego;

    [Header("Paneles del menu")]
    [SerializeField] private GameObject contenedorMenu;
    [SerializeField] private GameObject panelConfirmarSalida;
    [SerializeField] private GameObject panelSeleccionIdioma;
    [SerializeField] private GameObject panelInstrucciones;

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

    [Header("Textos del panel de instrucciones")]
    [SerializeField] private TMP_Text textoContenidoInstrucciones;
    [SerializeField] private TMP_Text textoVolver;

    private void Start()
    {
        // Mostrar el menu principal al iniciar
        if (contenedorMenuPrincipal != null)
            contenedorMenuPrincipal.SetActive(true);

        if (contenedorMinijuego != null)
            contenedorMinijuego.SetActive(false);

        if (contenedorMenu != null)
            contenedorMenu.SetActive(true);

        if (panelConfirmarSalida != null)
            panelConfirmarSalida.SetActive(false);

        if (panelSeleccionIdioma != null)
            panelSeleccionIdioma.SetActive(false);

        if (panelInstrucciones != null)
            panelInstrucciones.SetActive(false);

        ActualizarTextos();
    }

    // BOTON JUGAR
    public void Jugar()
    {
        if (contenedorMenuPrincipal != null)
            contenedorMenuPrincipal.SetActive(false);

        if (contenedorMinijuego != null)
            contenedorMinijuego.SetActive(true);

        Debug.Log("Entrando al minijuego");
    }

    // BOTON INSTRUCCIONES
    public void MostrarInstrucciones()
    {
        if (contenedorMenu != null)
            contenedorMenu.SetActive(false);

        if (panelConfirmarSalida != null)
            panelConfirmarSalida.SetActive(false);

        if (panelSeleccionIdioma != null)
            panelSeleccionIdioma.SetActive(false);

        if (panelInstrucciones != null)
            panelInstrucciones.SetActive(true);
        else
            Debug.LogError("No se ha asignado PanelInstrucciones.");
    }

    // BOTON VOLVER DE INSTRUCCIONES
    public void VolverAlMenu()
    {
        if (panelInstrucciones != null)
            panelInstrucciones.SetActive(false);

        if (panelConfirmarSalida != null)
            panelConfirmarSalida.SetActive(false);

        if (panelSeleccionIdioma != null)
            panelSeleccionIdioma.SetActive(false);

        if (contenedorMenu != null)
            contenedorMenu.SetActive(true);
    }

    // BOTON SALIR: MOSTRAR CONFIRMACION
    public void MostrarConfirmacion()
    {
        if (contenedorMenu != null)
            contenedorMenu.SetActive(false);

        if (panelSeleccionIdioma != null)
            panelSeleccionIdioma.SetActive(false);

        if (panelInstrucciones != null)
            panelInstrucciones.SetActive(false);

        if (panelConfirmarSalida != null)
            panelConfirmarSalida.SetActive(true);
    }

    // CONFIRMAR SALIDA
    public void ConfirmarSalir()
    {
        Debug.Log("Cerrando el juego...");

#if UNITY_EDITOR
        UnityEditor.EditorApplication.isPlaying = false;
#else
        Application.Quit();
#endif
    }

    // CANCELAR SALIDA
    public void CancelarSalir()
    {
        if (panelConfirmarSalida != null)
            panelConfirmarSalida.SetActive(false);

        if (contenedorMenu != null)
            contenedorMenu.SetActive(true);
    }

    // BOTON IDIOMA
    public void MostrarSeleccionIdioma()
    {
        if (contenedorMenu != null)
            contenedorMenu.SetActive(false);

        if (panelConfirmarSalida != null)
            panelConfirmarSalida.SetActive(false);

        if (panelInstrucciones != null)
            panelInstrucciones.SetActive(false);

        if (panelSeleccionIdioma != null)
            panelSeleccionIdioma.SetActive(true);
        else
            Debug.LogError("No se ha asignado PanelSeleccionIdioma.");
    }

    // SELECCION DE IDIOMA
    public void SeleccionarEspanol()
    {
        SeleccionarIdioma("es");
    }

    public void SeleccionarEuskera()
    {
        SeleccionarIdioma("eu");
    }

    public void SeleccionarIngles()
    {
        SeleccionarIdioma("en");
    }

    private void SeleccionarIdioma(string idioma)
    {
        if (GestorIdiomas.Instancia == null)
        {
            Debug.LogError("No existe un GestorIdiomas activo en la escena.");
            return;
        }

        GestorIdiomas.Instancia.CambiarIdioma(idioma);
        ActualizarTextos();

        if (panelSeleccionIdioma != null)
            panelSeleccionIdioma.SetActive(false);

        if (contenedorMenu != null)
            contenedorMenu.SetActive(true);
    }

    // ACTUALIZAR TEXTOS SEGUN EL IDIOMA
    private void ActualizarTextos()
    {
        if (GestorIdiomas.Instancia == null)
        {
            Debug.LogWarning("No existe un GestorIdiomas activo.");
            return;
        }

        // Menu principal
        AsignarTexto(textoTitulo, "titulo");
        AsignarTexto(textoJugar, "jugar");
        AsignarTexto(textoInstrucciones, "instrucciones");
        AsignarTexto(textoIdioma, "idioma");
        AsignarTexto(textoSalir, "salir");

        // Confirmacion de salida
        AsignarTexto(textoConfirmacion, "confirmar_salida");
        AsignarTexto(textoConfirmarSalir, "salir");
        AsignarTexto(textoCancelarSalir, "cancelar");

        // Panel de idiomas
        AsignarTexto(textoEspanol, "espanol");
        AsignarTexto(textoEuskera, "euskera");
        AsignarTexto(textoIngles, "ingles");

        // Panel de instrucciones
        AsignarTexto(
            textoContenidoInstrucciones,
            "texto_instrucciones"
        );

        AsignarTexto(textoVolver, "volver");
    }

    private void AsignarTexto(TMP_Text componente, string clave)
    {
        if (componente != null && GestorIdiomas.Instancia != null)
        {
            componente.text =
                GestorIdiomas.Instancia.ObtenerTexto(clave);
        }
    }
}
