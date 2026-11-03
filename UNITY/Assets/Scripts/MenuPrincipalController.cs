
using UnityEngine;
using TMPro;

public class MenuPrincipalController : MonoBehaviour
{
    [Header("Contenedores principales")]
    [SerializeField] private GameObject contenedorMenuPrincipal;
    [SerializeField] private GameObject contenedorMinijuego;

    [Header("Elementos del minijuego")]
    [SerializeField] private GameObject contenedorBotonesInferiores;
    [SerializeField] private GameObject contenedorBarraVida;
    [SerializeField] private GameObject contenedorPaloma;
    [SerializeField] private GameObject contenedorTroll;
    [SerializeField] private GameObject contenedorAcertijo;

    [Header("Movimiento del troll")]
    [SerializeField] private MovimientoTroll movimientoTroll;

    [Header("Colision de la puerta")]
    [SerializeField] private CollisionPuerta collisionPuerta;

    [Header("Paneles del menu")]
    [SerializeField] private GameObject contenedorMenu;
    [SerializeField] private GameObject panelConfirmarSalida;
    [SerializeField] private GameObject panelSeleccionIdioma;
    [SerializeField] private GameObject panelInstrucciones;
    [SerializeField] private GameObject panelConfirmarVolver;
    [SerializeField] private GameObject panelConfirmarReiniciar;
    [SerializeField] private GameObject panelDerrota;
    [SerializeField] private GameObject panelVictoria;

    [Header("Textos del menu principal")]
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

    [Header("Textos de instrucciones")]
    [SerializeField] private TMP_Text textoTituloPreparacion;
    [SerializeField] private TMP_Text textoPreparacion;

    [SerializeField] private TMP_Text textoTituloEnigma;
    [SerializeField] private TMP_Text textoEnigma;

    [SerializeField] private TMP_Text textoTituloResultado;
    [SerializeField] private TMP_Text textoResultado;

    [SerializeField] private TMP_Text textoTituloUtilidades;
    [SerializeField] private TMP_Text textoUtilidades;

    [SerializeField] private TMP_Text textoVolver;

    [Header("Textos de confirmacion de volver")]
    [SerializeField] private TMP_Text textoConfirmarVolver;
    [SerializeField] private TMP_Text textoSalirMenu;
    [SerializeField] private TMP_Text textoCancelarVolver;

    [Header("Textos de confirmacion de reinicio")]
    [SerializeField] private TMP_Text textoConfirmarReiniciar;
    [SerializeField] private TMP_Text textoBotonReiniciar;
    [SerializeField] private TMP_Text textoBotonCancelarReinicio;

    [Header("Textos del menu de derrota")]
    [SerializeField] private TMP_Text textoDerrota;
    [SerializeField] private TMP_Text textoBotonRevancha;
    [SerializeField] private TMP_Text textoBotonMenuPrincipal;

    [Header("Textos del menu de victoria")]
    [SerializeField] private TMP_Text textoVictoria;
    [SerializeField] private TMP_Text textoBotonVolverAJugar;
    [SerializeField] private TMP_Text textoBotonMenuPrincipalVictoria;

    [Header("Referencias para reiniciar la partida")]
    [SerializeField] private VidaTroll vidaTroll;
    [SerializeField] private MovimientoPaloma movimientoPaloma;
    [SerializeField] private GestorAcertijo gestorAcertijo;

    private void Start()
    {
        MostrarMenuInicial();
        ActualizarTextos();
    }

    private void MostrarMenuInicial()
    {
        if (contenedorMenuPrincipal != null)
            contenedorMenuPrincipal.SetActive(true);

        if (contenedorMinijuego != null)
            contenedorMinijuego.SetActive(false);

        if (contenedorMenu != null)
            contenedorMenu.SetActive(true);

        OcultarPanelesSecundarios();

        if (panelDerrota != null)
            panelDerrota.SetActive(false);

        if (panelVictoria != null)
            panelVictoria.SetActive(false);

        if (movimientoTroll != null)
            movimientoTroll.DetenerMovimiento();

        if (collisionPuerta != null)
            collisionPuerta.enabled = false;

        OcultarElementosMinijuego();
    }

    private void OcultarPanelesSecundarios()
    {
        if (panelConfirmarSalida != null)
            panelConfirmarSalida.SetActive(false);

        if (panelSeleccionIdioma != null)
            panelSeleccionIdioma.SetActive(false);

        if (panelInstrucciones != null)
            panelInstrucciones.SetActive(false);

        if (panelConfirmarVolver != null)
            panelConfirmarVolver.SetActive(false);

        if (panelConfirmarReiniciar != null)
            panelConfirmarReiniciar.SetActive(false);
    }

    private void ActivarElementosMinijuego()
    {
        if (contenedorBotonesInferiores != null)
            contenedorBotonesInferiores.SetActive(true);

        if (contenedorBarraVida != null)
            contenedorBarraVida.SetActive(true);

        if (contenedorPaloma != null)
            contenedorPaloma.SetActive(true);

        if (contenedorTroll != null)
            contenedorTroll.SetActive(true);

        if (contenedorAcertijo != null)
            contenedorAcertijo.SetActive(false);
    }

    private void OcultarElementosMinijuego()
    {
        if (contenedorBotonesInferiores != null)
            contenedorBotonesInferiores.SetActive(false);

        if (contenedorBarraVida != null)
            contenedorBarraVida.SetActive(false);

        if (contenedorPaloma != null)
            contenedorPaloma.SetActive(false);

        if (contenedorTroll != null)
            contenedorTroll.SetActive(false);

        if (contenedorAcertijo != null)
            contenedorAcertijo.SetActive(false);
    }

    public void Jugar()
    {
        OcultarPanelesSecundarios();

        if (panelDerrota != null)
            panelDerrota.SetActive(false);

        if (panelVictoria != null)
            panelVictoria.SetActive(false);

        if (contenedorMenuPrincipal != null)
            contenedorMenuPrincipal.SetActive(false);

        if (contenedorMinijuego != null)
            contenedorMinijuego.SetActive(true);

        ActivarElementosMinijuego();
        ReiniciarElementosPartida();

        if (collisionPuerta != null)
        {
            collisionPuerta.enabled = true;
            collisionPuerta.ReiniciarPartida();
        }
        else if (movimientoTroll != null)
        {
            movimientoTroll.IniciarMovimiento();
        }

        ActualizarTextos();
    }

    private void ReiniciarElementosPartida()
    {
        if (vidaTroll != null)
            vidaTroll.ReiniciarVida();

        if (movimientoPaloma != null)
            movimientoPaloma.ReiniciarPartida();

        if (gestorAcertijo != null)
            gestorAcertijo.ReiniciarAcertijo();

        if (contenedorAcertijo != null)
            contenedorAcertijo.SetActive(false);
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
        OcultarPanelesSecundarios();

        if (panelDerrota != null)
            panelDerrota.SetActive(false);

        if (panelVictoria != null)
            panelVictoria.SetActive(false);

        if (movimientoTroll != null)
            movimientoTroll.DetenerMovimiento();

        ReiniciarElementosPartida();

        if (collisionPuerta != null)
        {
            collisionPuerta.enabled = true;
            collisionPuerta.ReiniciarPartida();

            if (movimientoTroll != null)
                movimientoTroll.DetenerMovimiento();

            collisionPuerta.enabled = false;
        }
        else if (movimientoTroll != null)
        {
            movimientoTroll.ReiniciarMovimiento();
        }

        OcultarElementosMinijuego();

        if (contenedorMinijuego != null)
            contenedorMinijuego.SetActive(false);

        if (contenedorMenuPrincipal != null)
            contenedorMenuPrincipal.SetActive(true);

        if (contenedorMenu != null)
            contenedorMenu.SetActive(true);

        ActualizarTextos();

        Debug.Log("Partida reiniciada. Volviendo al menú principal.");
    }

    public void CancelarVolverAlMenu()
    {
        if (panelConfirmarVolver != null)
            panelConfirmarVolver.SetActive(false);
    }

    public void MostrarConfirmacionReiniciar()
    {
        ActualizarTextos();

        if (panelConfirmarReiniciar != null)
            panelConfirmarReiniciar.SetActive(true);
    }

    public void ConfirmarReiniciar()
    {
        if (panelConfirmarReiniciar != null)
            panelConfirmarReiniciar.SetActive(false);

        if (panelDerrota != null)
            panelDerrota.SetActive(false);

        if (panelVictoria != null)
            panelVictoria.SetActive(false);

        if (contenedorMinijuego != null)
            contenedorMinijuego.SetActive(true);

        ActivarElementosMinijuego();
        ReiniciarElementosPartida();

        if (collisionPuerta != null)
        {
            collisionPuerta.enabled = true;
            collisionPuerta.ReiniciarPartida();
        }
        else if (movimientoTroll != null)
        {
            movimientoTroll.IniciarMovimiento();
        }

        ActualizarTextos();
    }

    public void CancelarReiniciar()
    {
        if (panelConfirmarReiniciar != null)
            panelConfirmarReiniciar.SetActive(false);
    }

    public void MostrarVictoria()
    {
        if (panelVictoria == null)
        {
            Debug.LogError("No se ha asignado el PanelVictoria en el Inspector.");
            return;
        }

        if (movimientoTroll != null)
            movimientoTroll.DetenerMovimiento();

        if (collisionPuerta != null)
            collisionPuerta.enabled = false;

        OcultarPanelesSecundarios();

        if (panelDerrota != null)
            panelDerrota.SetActive(false);

        OcultarElementosMinijuego();

        ActualizarTextos();

        panelVictoria.SetActive(true);

        Debug.Log("¡Victoria! El troll se ha quedado sin vidas.");
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

        // Instrucciones: cuatro pergaminos
        AsignarTexto(textoTituloPreparacion, "instrucciones.preparacion.titulo");
        AsignarTexto(textoPreparacion, "instrucciones.preparacion.texto");

        AsignarTexto(textoTituloEnigma, "instrucciones.enigma.titulo");
        AsignarTexto(textoEnigma, "instrucciones.enigma.texto");

        AsignarTexto(textoTituloResultado, "instrucciones.resultado.titulo");
        AsignarTexto(textoResultado, "instrucciones.resultado.texto");

        AsignarTexto(textoTituloUtilidades, "instrucciones.utilidades.titulo");
        AsignarTexto(textoUtilidades, "instrucciones.utilidades.texto");

        AsignarTexto(textoVolver, "volver");

        // Confirmacion de volver
        AsignarTexto(textoConfirmarVolver, "confirmar_volver");
        AsignarTexto(textoSalirMenu, "salir");
        AsignarTexto(textoCancelarVolver, "cancelar");

        // Confirmacion de reinicio
        AsignarTexto(textoConfirmarReiniciar, "confirmar_reiniciar");
        AsignarTexto(textoBotonReiniciar, "boton_reiniciar");
        AsignarTexto(textoBotonCancelarReinicio, "boton_cancelar_reinicio");

        // Menu de derrota
        AsignarTexto(textoDerrota, "derrota");
        AsignarTexto(textoBotonRevancha, "revancha");
        AsignarTexto(textoBotonMenuPrincipal, "menu_principal");

        // Menu de victoria
        AsignarTexto(textoVictoria, "victoria");
        AsignarTexto(textoBotonVolverAJugar, "volver_a_jugar");
        AsignarTexto(textoBotonMenuPrincipalVictoria, "menu_principal");
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
