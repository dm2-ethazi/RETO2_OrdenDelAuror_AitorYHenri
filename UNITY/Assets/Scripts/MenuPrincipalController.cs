
using UnityEngine;
using TMPro;

public class MenuPrincipalController : MonoBehaviour
{
    // Referencias a los contenedores principales del juego.
    [Header("Contenedores principales")]
    [SerializeField] private GameObject contenedorMenuPrincipal;
    [SerializeField] private GameObject contenedorMinijuego;

    // Referencias a los elementos que aparecen durante el minijuego.
    [Header("Elementos del minijuego")]
    [SerializeField] private GameObject contenedorBotonesInferiores;
    [SerializeField] private GameObject contenedorBarraVida;
    [SerializeField] private GameObject contenedorPaloma;
    [SerializeField] private GameObject contenedorTroll;
    [SerializeField] private GameObject contenedorAcertijo;

    // Referencia al script que controla el movimiento del troll.
    [Header("Movimiento del troll")]
    [SerializeField] private MovimientoTroll movimientoTroll;

    // Referencia al script que detecta cuándo el troll llega a la puerta.
    [Header("Colision de la puerta")]
    [SerializeField] private CollisionPuerta collisionPuerta;

    // Referencias a los diferentes paneles que forman los menús del juego.
    [Header("Paneles del menu")]
    [SerializeField] private GameObject contenedorMenu;
    [SerializeField] private GameObject panelConfirmarSalida;
    [SerializeField] private GameObject panelSeleccionIdioma;
    [SerializeField] private GameObject panelInstrucciones;
    [SerializeField] private GameObject panelConfirmarVolver;
    [SerializeField] private GameObject panelConfirmarReiniciar;
    [SerializeField] private GameObject panelDerrota;
    [SerializeField] private GameObject panelVictoria;

    // Textos que aparecen en el menú principal.
    [Header("Textos del menu principal")]
    [SerializeField] private TMP_Text textoTitulo;
    [SerializeField] private TMP_Text textoJugar;
    [SerializeField] private TMP_Text textoInstrucciones;
    [SerializeField] private TMP_Text textoIdioma;
    [SerializeField] private TMP_Text textoSalir;

    // Textos del panel de confirmación de salida.
    [Header("Textos de confirmacion de salida")]
    [SerializeField] private TMP_Text textoMensajeConfirmarSalida;
    [SerializeField] private TMP_Text textoBotonConfirmarSalida;
    [SerializeField] private TMP_Text textoCancelar;

    // Textos del panel de selección de idioma.
    [Header("Textos de seleccion de idioma")]
    [SerializeField] private TMP_Text textoEspanol;
    [SerializeField] private TMP_Text textoEuskera;
    [SerializeField] private TMP_Text textoIngles;

    // Textos de los cuatro apartados del panel de instrucciones.
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

    // Textos del panel de confirmación para volver al menú principal.
    [Header("Textos de confirmacion de volver")]
    [SerializeField] private TMP_Text textoConfirmarVolver;
    [SerializeField] private TMP_Text textoSalirMenu;
    [SerializeField] private TMP_Text textoCancelarVolver;

    // Textos del panel de confirmación para reiniciar la partida.
    [Header("Textos de confirmacion de reinicio")]
    [SerializeField] private TMP_Text textoConfirmarReiniciar;
    [SerializeField] private TMP_Text textoBotonReiniciar;
    [SerializeField] private TMP_Text textoBotonCancelarReinicio;

    // Textos que aparecen en el menú cuando el jugador pierde.
    [Header("Textos del menu de derrota")]
    [SerializeField] private TMP_Text textoDerrota;
    [SerializeField] private TMP_Text textoBotonRevancha;
    [SerializeField] private TMP_Text textoBotonMenuPrincipal;

    // Textos que aparecen en el menú cuando el jugador gana.
    [Header("Textos del menu de victoria")]
    [SerializeField] private TMP_Text textoVictoria;
    [SerializeField] private TMP_Text textoBotonVolverAJugar;
    [SerializeField] private TMP_Text textoBotonMenuPrincipalVictoria;

    // Referencias necesarias para restaurar el estado de la partida.
    [Header("Referencias para reiniciar la partida")]
    [SerializeField] private VidaTroll vidaTroll;
    [SerializeField] private MovimientoPaloma movimientoPaloma;
    [SerializeField] private GestorAcertijo gestorAcertijo;

    private void Start()
    {
        // Mostrar el menú principal al iniciar el juego.
        MostrarMenuInicial();

        // Cargar los textos del menú en el idioma seleccionado.
        ActualizarTextos();
    }

    // Configura el estado inicial de los menús y del minijuego.
    private void MostrarMenuInicial()
    {
        // Mostrar el contenedor del menú principal.
        if (contenedorMenuPrincipal != null)
            contenedorMenuPrincipal.SetActive(true);

        // Ocultar el contenedor del minijuego.
        if (contenedorMinijuego != null)
            contenedorMinijuego.SetActive(false);

        // Mostrar el menú principal.
        if (contenedorMenu != null)
            contenedorMenu.SetActive(true);

        // Ocultar los paneles secundarios.
        OcultarPanelesSecundarios();

        // Asegurarse de que el panel de derrota esté oculto.
        if (panelDerrota != null)
            panelDerrota.SetActive(false);

        // Asegurarse de que el panel de victoria esté oculto.
        if (panelVictoria != null)
            panelVictoria.SetActive(false);

        // Detener el movimiento del troll mientras estamos en el menú.
        if (movimientoTroll != null)
            movimientoTroll.DetenerMovimiento();

        // Desactivar la detección de colisiones con la puerta.
        if (collisionPuerta != null)
            collisionPuerta.enabled = false;

        // Ocultar todos los elementos del minijuego.
        OcultarElementosMinijuego();
    }

    // Oculta todos los paneles secundarios del menú.
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

    // Activa los elementos que deben estar visibles durante el minijuego.
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

        // El panel del acertijo comienza oculto.
        if (contenedorAcertijo != null)
            contenedorAcertijo.SetActive(false);
    }

    // Oculta todos los elementos que forman parte del minijuego.
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

    // Inicia una nueva partida desde el menú principal.
    public void Jugar()
    {
        // Ocultar cualquier panel secundario que estuviera abierto.
        OcultarPanelesSecundarios();

        // Ocultar los paneles de derrota y victoria.
        if (panelDerrota != null)
            panelDerrota.SetActive(false);

        if (panelVictoria != null)
            panelVictoria.SetActive(false);

        // Ocultar el menú principal.
        if (contenedorMenuPrincipal != null)
            contenedorMenuPrincipal.SetActive(false);

        // Mostrar el contenedor del minijuego.
        if (contenedorMinijuego != null)
            contenedorMinijuego.SetActive(true);

        // Mostrar los elementos necesarios para jugar.
        ActivarElementosMinijuego();

        // Reiniciar las vidas, las palomas y el acertijo.
        ReiniciarElementosPartida();

        // Activar la detección de la puerta y reiniciar el movimiento.
        if (collisionPuerta != null)
        {
            collisionPuerta.enabled = true;
            collisionPuerta.ReiniciarPartida();
        }
        else if (movimientoTroll != null)
        {
            // Si no existe el detector de la puerta, iniciar directamente el troll.
            movimientoTroll.IniciarMovimiento();
        }

        // Actualizar los textos según el idioma actual.
        ActualizarTextos();
    }

    // Restaura los elementos internos de la partida a su estado inicial.
    private void ReiniciarElementosPartida()
    {
        // Restaurar las vidas del troll.
        if (vidaTroll != null)
            vidaTroll.ReiniciarVida();

        // Reiniciar el movimiento y el estado de las palomas.
        if (movimientoPaloma != null)
            movimientoPaloma.ReiniciarPartida();

        // Reiniciar el acertijo.
        if (gestorAcertijo != null)
            gestorAcertijo.ReiniciarAcertijo();

        // Asegurarse de que el panel del acertijo esté oculto.
        if (contenedorAcertijo != null)
            contenedorAcertijo.SetActive(false);
    }

    // Abre el panel de instrucciones del juego.
    public void MostrarInstrucciones()
    {
        // Ocultar el menú principal.
        if (contenedorMenu != null)
            contenedorMenu.SetActive(false);

        // Ocultar los demás paneles secundarios.
        if (panelConfirmarSalida != null)
            panelConfirmarSalida.SetActive(false);

        if (panelSeleccionIdioma != null)
            panelSeleccionIdioma.SetActive(false);

        // Mostrar las instrucciones.
        if (panelInstrucciones != null)
            panelInstrucciones.SetActive(true);

        // Actualizar los textos de las instrucciones.
        ActualizarTextos();
    }

    // Cierra las instrucciones y vuelve al menú principal.
    public void VolverAlMenu()
    {
        if (panelInstrucciones != null)
            panelInstrucciones.SetActive(false);

        if (panelConfirmarSalida != null)
            panelConfirmarSalida.SetActive(false);

        if (panelSeleccionIdioma != null)
            panelSeleccionIdioma.SetActive(false);

        // Volver a mostrar el menú.
        if (contenedorMenu != null)
            contenedorMenu.SetActive(true);

        // Actualizar los textos.
        ActualizarTextos();
    }

    // Muestra el panel que solicita confirmación antes de salir del juego.
    public void MostrarConfirmacion()
    {
        // Ocultar el menú principal.
        if (contenedorMenu != null)
            contenedorMenu.SetActive(false);

        // Ocultar otros paneles secundarios.
        if (panelInstrucciones != null)
            panelInstrucciones.SetActive(false);

        if (panelSeleccionIdioma != null)
            panelSeleccionIdioma.SetActive(false);

        // Mostrar la confirmación de salida.
        if (panelConfirmarSalida != null)
            panelConfirmarSalida.SetActive(true);

        // Actualizar los textos del panel.
        ActualizarTextos();
    }

    // Cierra la aplicación cuando el usuario confirma la salida.
    public void ConfirmarSalir()
    {
#if UNITY_EDITOR
        // Si se ejecuta desde el editor, detener el modo Play.
        UnityEditor.EditorApplication.isPlaying = false;
#else
        // Si se ejecuta como aplicación, cerrarla.
        Application.Quit();
#endif
    }

    // Cancela la salida y vuelve a mostrar el menú principal.
    public void CancelarSalir()
    {
        if (panelConfirmarSalida != null)
            panelConfirmarSalida.SetActive(false);

        if (contenedorMenu != null)
            contenedorMenu.SetActive(true);

        ActualizarTextos();
    }

    // Abre el panel para seleccionar el idioma del juego.
    public void MostrarSeleccionIdioma()
    {
        // Ocultar el menú principal.
        if (contenedorMenu != null)
            contenedorMenu.SetActive(false);

        // Ocultar los demás paneles secundarios.
        if (panelConfirmarSalida != null)
            panelConfirmarSalida.SetActive(false);

        if (panelInstrucciones != null)
            panelInstrucciones.SetActive(false);

        // Mostrar el panel de selección de idioma.
        if (panelSeleccionIdioma != null)
            panelSeleccionIdioma.SetActive(true);

        ActualizarTextos();
    }

    // Selecciona el español como idioma del juego.
    public void SeleccionarEspanol()
    {
        CambiarIdioma("es");
    }

    // Selecciona el euskera como idioma del juego.
    public void SeleccionarEuskera()
    {
        CambiarIdioma("eu");
    }

    // Selecciona el inglés como idioma del juego.
    public void SeleccionarIngles()
    {
        CambiarIdioma("en");
    }

    // Cambia el idioma utilizando el gestor centralizado de traducciones.
    private void CambiarIdioma(string idioma)
    {
        // Comprobar que existe el gestor de idiomas.
        if (GestorIdiomas.Instancia == null)
        {
            Debug.LogError("No se encuentra GestorIdiomas.");
            return;
        }

        // Cargar el idioma seleccionado.
        GestorIdiomas.Instancia.CambiarIdioma(idioma);

        // Actualizar todos los textos de la interfaz.
        ActualizarTextos();

        // Cerrar el panel de selección de idioma.
        if (panelSeleccionIdioma != null)
            panelSeleccionIdioma.SetActive(false);

        // Volver a mostrar el menú principal.
        if (contenedorMenu != null)
            contenedorMenu.SetActive(true);
    }

    // Muestra el panel que confirma si el jugador quiere volver al menú.
    public void MostrarConfirmacionVolver()
    {
        // Actualizar los textos del panel.
        ActualizarTextos();

        // Mostrar la confirmación de volver.
        if (panelConfirmarVolver != null)
            panelConfirmarVolver.SetActive(true);
    }

    // Confirma la salida del minijuego y vuelve al menú principal.
    public void ConfirmarVolverAlMenu()
    {
        // Ocultar los paneles secundarios.
        OcultarPanelesSecundarios();

        // Ocultar los paneles de resultado.
        if (panelDerrota != null)
            panelDerrota.SetActive(false);

        if (panelVictoria != null)
            panelVictoria.SetActive(false);

        // Detener el movimiento del troll.
        if (movimientoTroll != null)
            movimientoTroll.DetenerMovimiento();

        // Reiniciar los elementos de la partida.
        ReiniciarElementosPartida();

        // Reiniciar también el detector de la puerta.
        if (collisionPuerta != null)
        {
            collisionPuerta.enabled = true;
            collisionPuerta.ReiniciarPartida();

            // Detener nuevamente al troll porque se va a abandonar el minijuego.
            if (movimientoTroll != null)
                movimientoTroll.DetenerMovimiento();

            // Desactivar el detector mientras estamos en el menú.
            collisionPuerta.enabled = false;
        }
        else if (movimientoTroll != null)
        {
            // Si no hay detector, restaurar directamente el movimiento del troll.
            movimientoTroll.ReiniciarMovimiento();
        }

        // Ocultar los elementos del minijuego.
        OcultarElementosMinijuego();

        // Ocultar el contenedor del minijuego.
        if (contenedorMinijuego != null)
            contenedorMinijuego.SetActive(false);

        // Mostrar el menú principal.
        if (contenedorMenuPrincipal != null)
            contenedorMenuPrincipal.SetActive(true);

        if (contenedorMenu != null)
            contenedorMenu.SetActive(true);

        // Actualizar los textos del menú.
        ActualizarTextos();

        Debug.Log("Partida reiniciada. Volviendo al menú principal.");
    }

    // Cancela la acción de volver al menú y cierra su panel de confirmación.
    public void CancelarVolverAlMenu()
    {
        if (panelConfirmarVolver != null)
            panelConfirmarVolver.SetActive(false);
    }

    // Muestra el panel de confirmación antes de reiniciar la partida.
    public void MostrarConfirmacionReiniciar()
    {
        ActualizarTextos();

        if (panelConfirmarReiniciar != null)
            panelConfirmarReiniciar.SetActive(true);
    }

    // Confirma el reinicio de la partida actual.
    public void ConfirmarReiniciar()
    {
        // Cerrar el panel de confirmación.
        if (panelConfirmarReiniciar != null)
            panelConfirmarReiniciar.SetActive(false);

        // Ocultar los paneles de derrota y victoria.
        if (panelDerrota != null)
            panelDerrota.SetActive(false);

        if (panelVictoria != null)
            panelVictoria.SetActive(false);

        // Asegurarse de que el minijuego esté visible.
        if (contenedorMinijuego != null)
            contenedorMinijuego.SetActive(true);

        // Reactivar los elementos y reiniciar el estado de la partida.
        ActivarElementosMinijuego();
        ReiniciarElementosPartida();

        // Reiniciar el detector de la puerta y el movimiento del troll.
        if (collisionPuerta != null)
        {
            collisionPuerta.enabled = true;
            collisionPuerta.ReiniciarPartida();
        }
        else if (movimientoTroll != null)
        {
            movimientoTroll.IniciarMovimiento();
        }

        // Actualizar los textos según el idioma seleccionado.
        ActualizarTextos();
    }

    // Cancela el reinicio y cierra el panel de confirmación.
    public void CancelarReiniciar()
    {
        if (panelConfirmarReiniciar != null)
            panelConfirmarReiniciar.SetActive(false);
    }

    // Muestra el panel de victoria cuando el troll pierde todas sus vidas.
    public void MostrarVictoria()
    {
        // Comprobar que el panel de victoria está asignado.
        if (panelVictoria == null)
        {
            Debug.LogError("No se ha asignado el PanelVictoria en el Inspector.");
            return;
        }

        // Detener el movimiento del troll.
        if (movimientoTroll != null)
            movimientoTroll.DetenerMovimiento();

        // Desactivar la detección de la puerta.
        if (collisionPuerta != null)
            collisionPuerta.enabled = false;

        // Ocultar los paneles secundarios.
        OcultarPanelesSecundarios();

        // Asegurarse de que el panel de derrota esté oculto.
        if (panelDerrota != null)
            panelDerrota.SetActive(false);

        // Ocultar los elementos del minijuego.
        OcultarElementosMinijuego();

        // Actualizar los textos antes de mostrar la victoria.
        ActualizarTextos();

        // Mostrar el panel de victoria.
        panelVictoria.SetActive(true);

        Debug.Log("¡Victoria! El troll se ha quedado sin vidas.");
    }

    // Actualiza todos los textos de la interfaz según el idioma actual.
    public void ActualizarTextos()
    {
        // Textos del menú principal.
        AsignarTexto(textoTitulo, "titulo");
        AsignarTexto(textoJugar, "jugar");
        AsignarTexto(textoInstrucciones, "instrucciones");
        AsignarTexto(textoIdioma, "idioma");
        AsignarTexto(textoSalir, "salir");

        // Textos del panel de confirmación de salida.
        AsignarTexto(textoMensajeConfirmarSalida, "confirmar_salida");
        AsignarTexto(textoBotonConfirmarSalida, "salir");
        AsignarTexto(textoCancelar, "cancelar");

        // Textos del panel de selección de idioma.
        AsignarTexto(textoEspanol, "espanol");
        AsignarTexto(textoEuskera, "euskera");
        AsignarTexto(textoIngles, "ingles");

        // Textos de los cuatro apartados del panel de instrucciones.
        AsignarTexto(textoTituloPreparacion, "instrucciones.preparacion.titulo");
        AsignarTexto(textoPreparacion, "instrucciones.preparacion.texto");

        AsignarTexto(textoTituloEnigma, "instrucciones.enigma.titulo");
        AsignarTexto(textoEnigma, "instrucciones.enigma.texto");

        AsignarTexto(textoTituloResultado, "instrucciones.resultado.titulo");
        AsignarTexto(textoResultado, "instrucciones.resultado.texto");

        AsignarTexto(textoTituloUtilidades, "instrucciones.utilidades.titulo");
        AsignarTexto(textoUtilidades, "instrucciones.utilidades.texto");

        AsignarTexto(textoVolver, "volver");

        // Textos del panel de confirmación para volver al menú.
        AsignarTexto(textoConfirmarVolver, "confirmar_volver");
        AsignarTexto(textoSalirMenu, "salir");
        AsignarTexto(textoCancelarVolver, "cancelar");

        // Textos del panel de confirmación de reinicio.
        AsignarTexto(textoConfirmarReiniciar, "confirmar_reiniciar");
        AsignarTexto(textoBotonReiniciar, "boton_reiniciar");
        AsignarTexto(textoBotonCancelarReinicio, "boton_cancelar_reinicio");

        // Textos del menú de derrota.
        AsignarTexto(textoDerrota, "derrota");
        AsignarTexto(textoBotonRevancha, "revancha");
        AsignarTexto(textoBotonMenuPrincipal, "menu_principal");

        // Textos del menú de victoria.
        AsignarTexto(textoVictoria, "victoria");
        AsignarTexto(textoBotonVolverAJugar, "volver_a_jugar");
        AsignarTexto(textoBotonMenuPrincipalVictoria, "menu_principal");
    }

    // Asigna a un componente de texto la traducción correspondiente a una clave.
    private void AsignarTexto(TMP_Text texto, string clave)
    {
        // Comprobar que el componente de texto está asignado.
        if (texto == null)
            return;

        // Comprobar que existe el gestor de idiomas.
        if (GestorIdiomas.Instancia == null)
            return;

        // Obtener la traducción y asignarla al componente TextMeshPro.
        texto.text = GestorIdiomas.Instancia.ObtenerTexto(clave);
    }
}
