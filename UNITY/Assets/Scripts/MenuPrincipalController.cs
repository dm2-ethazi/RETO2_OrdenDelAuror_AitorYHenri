
using UnityEngine;
using TMPro;

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
    [SerializeField] private GameObject panelConfirmarVolver;

    [Header("Textos del menu")]
    [SerializeField] private TMP_Text textoTitulo;
    [SerializeField] private TMP_Text textoJugar;
    [SerializeField] private TMP_Text textoInstrucciones;
    [SerializeField] private TMP_Text textoIdioma;
    [SerializeField] private TMP_Text textoSalir;

    [Header("Textos de confirmacion de salida")]
    [SerializeField] private TMP_Text textoMensajeConfirmarSalida;
    [SerializeField] private TMP_Text textoBotonConfirmarSalida;
    [SerializeField] private TMP_Text textoCancelar;

    [Header("Textos de seleccion de idioma")]
    [SerializeField] private TMP_Text textoEspanol;
    [SerializeField] private TMP_Text textoEuskera;
    [SerializeField] private TMP_Text textoIngles;

    [Header("Textos del panel de instrucciones")]
    [SerializeField] private TMP_Text textoContenidoInstrucciones;
    [SerializeField] private TMP_Text textoVolver;

    [Header("Textos de confirmacion de volver")]
    [SerializeField] private TMP_Text textoConfirmarVolver;
    [SerializeField] private TMP_Text textoSalirMenu;
    [SerializeField] private TMP_Text textoCancelarVolver;

    private void Start()
    {
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

        if (panelConfirmarVolver != null)
            panelConfirmarVolver.SetActive(false);

        ActualizarTextos();
    }

    public void Jugar()
    {
        if (panelConfirmarVolver != null)
            panelConfirmarVolver.SetActive(false);

        if (contenedorMenuPrincipal != null)
            contenedorMenuPrincipal.SetActive(false);

        if (contenedorMinijuego != null)
            contenedorMinijuego.SetActive(true);
    }

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

        ActualizarTextos();
    }

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

        ActualizarTextos();
    }

    public void MostrarConfirmacion()
    {
        if (contenedorMenu != null)
            contenedorMenu.SetActive(false);

        if (panelInstrucciones != null)
            panelInstrucciones.SetActive(false);

        if (panelSeleccionIdioma != null)
            panelSeleccionIdioma.SetActive(false);

        if (panelConfirmarSalida != null)
            panelConfirmarSalida.SetActive(true);

        ActualizarTextos();
    }

    public void ConfirmarSalir()
    {
#if UNITY_EDITOR
        UnityEditor.EditorApplication.isPlaying = false;
#else
        Application.Quit();
#endif
    }

    public void CancelarSalir()
    {
        if (panelConfirmarSalida != null)
            panelConfirmarSalida.SetActive(false);

        if (contenedorMenu != null)
            contenedorMenu.SetActive(true);

        ActualizarTextos();
    }

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

        ActualizarTextos();
    }

    public void SeleccionarEspanol()
    {
        CambiarIdioma("es");
    }

    public void SeleccionarEuskera()
    {
        CambiarIdioma("eu");
    }

    public void SeleccionarIngles()
    {
        CambiarIdioma("en");
    }

    private void CambiarIdioma(string idioma)
    {
        if (GestorIdiomas.Instancia == null)
        {
            Debug.LogError("No se encuentra GestorIdiomas.");
            return;
        }

        GestorIdiomas.Instancia.CambiarIdioma(idioma);
        ActualizarTextos();

        if (panelSeleccionIdioma != null)
            panelSeleccionIdioma.SetActive(false);

        if (contenedorMenu != null)
            contenedorMenu.SetActive(true);
    }

    public void MostrarConfirmacionVolver()
    {
        ActualizarTextos();

        if (panelConfirmarVolver != null)
            panelConfirmarVolver.SetActive(true);
    }

    public void ConfirmarVolverAlMenu()
    {
        if (panelConfirmarVolver != null)
            panelConfirmarVolver.SetActive(false);

        if (contenedorMinijuego != null)
            contenedorMinijuego.SetActive(false);

        if (contenedorMenuPrincipal != null)
            contenedorMenuPrincipal.SetActive(true);

        if (contenedorMenu != null)
            contenedorMenu.SetActive(true);

        ActualizarTextos();
    }

    public void CancelarVolverAlMenu()
    {
        if (panelConfirmarVolver != null)
            panelConfirmarVolver.SetActive(false);
    }

    public void ActualizarTextos()
    {
        // Menu principal
        AsignarTexto(textoTitulo, "titulo");
        AsignarTexto(textoJugar, "jugar");
        AsignarTexto(textoInstrucciones, "instrucciones");
        AsignarTexto(textoIdioma, "idioma");
        AsignarTexto(textoSalir, "salir");

        // Confirmacion de salida
        AsignarTexto(textoMensajeConfirmarSalida, "confirmar_salida");
        AsignarTexto(textoBotonConfirmarSalida, "salir");
        AsignarTexto(textoCancelar, "cancelar");

        // Seleccion de idioma
        AsignarTexto(textoEspanol, "espanol");
        AsignarTexto(textoEuskera, "euskera");
        AsignarTexto(textoIngles, "ingles");

        // Instrucciones
        AsignarTexto(
            textoContenidoInstrucciones,
            "texto_instrucciones"
        );
        AsignarTexto(textoVolver, "volver");

        // Confirmacion para volver desde el minijuego
        AsignarTexto(textoConfirmarVolver, "confirmar_volver");
        AsignarTexto(textoSalirMenu, "salir");
        AsignarTexto(textoCancelarVolver, "cancelar");
    }

    private void AsignarTexto(TMP_Text texto, string clave)
    {
        if (texto == null)
            return;

        if (GestorIdiomas.Instancia == null)
            return;

        texto.text = GestorIdiomas.Instancia.ObtenerTexto(clave);
    }
}
