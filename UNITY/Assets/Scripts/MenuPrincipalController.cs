
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

    [Header("Colisión de la puerta")]
    [SerializeField] private CollisionPuerta collisionPuerta;

    [Header("Paneles del menú")]
    [SerializeField] private GameObject contenedorMenu;
    [SerializeField] private GameObject panelConfirmarSalida;
    [SerializeField] private GameObject panelSeleccionIdioma;
    [SerializeField] private GameObject panelInstrucciones;
    [SerializeField] private GameObject panelConfirmarVolver;
    [SerializeField] private GameObject panelConfirmarReiniciar;
    [SerializeField] private GameObject panelDerrota;

    [Header("Textos del menú principal")]
    [SerializeField] private TMP_Text textoTitulo;
    [SerializeField] private TMP_Text textoJugar;
    [SerializeField] private TMP_Text textoInstrucciones;
    [SerializeField] private TMP_Text textoIdioma;
    [SerializeField] private TMP_Text textoSalir;

    [Header("Textos de confirmación de salida")]
    [SerializeField] private TMP_Text textoMensajeConfirmarSalida;
    [SerializeField] private TMP_Text textoBotonConfirmarSalida;
    [SerializeField] private TMP_Text textoCancelar;

    [Header("Textos de selección de idioma")]
    [SerializeField] private TMP_Text textoEspanol;
    [SerializeField] private TMP_Text textoEuskera;
    [SerializeField] private TMP_Text textoIngles;

    [Header("Textos de instrucciones")]
    [SerializeField] private TMP_Text textoContenidoInstrucciones;
    [SerializeField] private TMP_Text textoVolver;

    [Header("Textos de confirmación de volver")]
    [SerializeField] private TMP_Text textoConfirmarVolver;
    [SerializeField] private TMP_Text textoSalirMenu;
    [SerializeField] private TMP_Text textoCancelarVolver;

    [Header("Textos de confirmación de reinicio")]
    [SerializeField] private TMP_Text textoConfirmarReiniciar;
    [SerializeField] private TMP_Text textoBotonReiniciar;
    [SerializeField] private TMP_Text textoBotonCancelarReinicio;

    [Header("Textos del menú de derrota")]
    [SerializeField] private TMP_Text textoDerrota;
    [SerializeField] private TMP_Text textoBotonRevancha;
    [SerializeField] private TMP_Text textoBotonMenuPrincipal;

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

    public void ActualizarTextos()
    {
        // Menú principal
        AsignarTexto(textoTitulo, "titulo");
        AsignarTexto(textoJugar, "jugar");
        AsignarTexto(textoInstrucciones, "instrucciones");
        AsignarTexto(textoIdioma, "idioma");
        AsignarTexto(textoSalir, "salir");

        // Confirmación de salida
        AsignarTexto(textoMensajeConfirmarSalida, "confirmar_salida");
        AsignarTexto(textoBotonConfirmarSalida, "salir");
        AsignarTexto(textoCancelar, "cancelar");

        // Selección de idioma
        AsignarTexto(textoEspanol, "espanol");
        AsignarTexto(textoEuskera, "euskera");
        AsignarTexto(textoIngles, "ingles");

        // Instrucciones
        AsignarTexto(textoContenidoInstrucciones, "texto_instrucciones");
        AsignarTexto(textoVolver, "volver");

        // Confirmación de volver al menú
        AsignarTexto(textoConfirmarVolver, "confirmar_volver");
        AsignarTexto(textoSalirMenu, "salir");
        AsignarTexto(textoCancelarVolver, "cancelar");

        // Confirmación de reinicio
        AsignarTexto(textoConfirmarReiniciar, "confirmar_reiniciar");
        AsignarTexto(textoBotonReiniciar, "boton_reiniciar");
        AsignarTexto(textoBotonCancelarReinicio, "boton_cancelar_reinicio");

        // Menú de derrota
        AsignarTexto(textoDerrota, "derrota");
        AsignarTexto(textoBotonRevancha, "revancha");
        AsignarTexto(textoBotonMenuPrincipal, "menu_principal");
    }

    private void AsignarTexto(TMP_Text texto, string clave)
    {
        if (texto == null || GestorIdiomas.Instancia == null)
            return;

        texto.text = GestorIdiomas.Instancia.ObtenerTexto(clave);
    }
}
