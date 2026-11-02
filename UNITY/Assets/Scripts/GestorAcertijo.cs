using System;
using System.Collections;
using UnityEngine;
using TMPro;

public class GestorAcertijo : MonoBehaviour
{
    [Header("Referencias")]
    [SerializeField] private VidaTroll vidaTroll;
    [SerializeField] private MovimientoPaloma movimientoPaloma;

    [Header("Contenedores")]
    [SerializeField] private GameObject contenedorAcertijo;
    [SerializeField] private GameObject contenedorPaloma;

    [Header("Textos del acertijo")]
    [SerializeField] private TMP_Text textoCifrado;
    [SerializeField] private TMP_Text textoDesplazamiento;
    [SerializeField] private TMP_InputField campoRespuesta;
    [SerializeField] private TMP_Text textoPlaceholder;
    [SerializeField] private TMP_Text textoBotonConfirmar;

    [Header("Mensajes de resultado")]
    [SerializeField] private GameObject txtCorrecto;
    [SerializeField] private GameObject txtIncorrecto;
    [SerializeField] private float duracionMensaje = 2f;

    [Header("Configuracion")]
    [SerializeField] private int desplazamiento;

    [Header("Frases faciles: 3 vidas")]
    [SerializeField] private string[] frasesFacilesES =
    {
        "TEME EL AGUA",
        "OIDOS SENSIBLES",
        "ODIA EL AJO",
        "SU ARMADURA ES VIEJA"
    };

    [SerializeField] private string[] frasesFacilesEU =
    {
        "URARI BELDURRA DIO",
        "BELARRI SENTIKORRAK DITU",
        "BARATXURIA GORROTO DU",
        "BERE ARMADURA ZAHARRA DA"
    };

    [SerializeField] private string[] frasesFacilesEN =
    {
        "HE FEARS WATER",
        "HE HAS SENSITIVE EARS",
        "HE HATES GARLIC",
        "HIS ARMOR IS OLD"
    };

    [Header("Frases medias: 2 vidas")]
    [SerializeField] private string[] frasesMediasES =
    {
        "LAS CAMPANAS LO ATURDEN",
        "LE DAN MIEDO LAS RATAS",
        "RESBALA SOBRE EL HIELO",
        "EL POLVO LE IMPIDE VER BIEN"
    };

    [SerializeField] private string[] frasesMediasEU =
    {
        "KANPAIEK NAHASTEN DUTE",
        "ARRATOIEI BELDURRA DIE",
        "IZOTZAREN IRRISTATZEN DA",
        "HAUTSAK EZ DIO ONDO IKUSTEN UZTEN"
    };

    [SerializeField] private string[] frasesMediasEN =
    {
        "BELLS STUN HIM",
        "HE IS AFRAID OF RATS",
        "SLIPS ON ICE",
        "DUST PREVENTS HIM FROM SEEING CLEARLY"
    };

    [Header("Frases dificiles: 1 vida")]
    [SerializeField] private string[] frasesDificilesES =
    {
        "LA PLATA LE PROVOCA QUEMADURAS SEVERAS",
        "LOS ESPEJOS LO CONFUNDEN Y LO PARALIZAN",
        "LAS SETAS VENENOSAS DEBILITAN SU CUERPO",
        "LE DA MIEDO VER SU PROPIA SOMBRA"
    };

    [SerializeField] private string[] frasesDificilesEU =
    {
        "ZILARRAK ERREDURA LARRIAK ERAGITEN DIZKIO",
        "ISPILUEK NAHASTEN ETA PARALIZATZEN DIOTE",
        "PERRETXIKO POZOITSUEK BERE GORPUTZA AHULTZEN DUTE",
        "BERE ITZALARI BEGIRATZEAK BELDURTZEN DIO"
    };

    [SerializeField] private string[] frasesDificilesEN =
    {
        "SILVER CAUSES HIM SEVERE BURNS",
        "MIRRORS CONFUSE AND PARALYZE HIM",
        "POISONOUS MUSHROOMS WEAKEN HIS BODY",
        "HE IS AFRAID OF HIS OWN SHADOW"
    };

    private string fraseOriginal;
    private Coroutine corrutinaMensaje;
    private bool inicializado;
    private bool respuestaProcesada;

    private void Start()
    {
        inicializado = true;

        OcultarMensajes();
        GenerarAcertijo();
    }

    private void OnEnable()
    {
        if (inicializado)
        {
            respuestaProcesada = false;
            OcultarMensajes();
            GenerarAcertijo();
        }
    }

    public void ActualizarTextos()
    {
        if (GestorIdiomas.Instancia == null)
        {
            Debug.LogError("No se ha encontrado GestorIdiomas.");
            return;
        }

        if (textoPlaceholder != null)
        {
            textoPlaceholder.text =
                GestorIdiomas.Instancia.ObtenerTexto(
                    "acertijo.placeholder"
                );
        }

        if (textoBotonConfirmar != null)
        {
            textoBotonConfirmar.text =
                GestorIdiomas.Instancia.ObtenerTexto(
                    "acertijo.confirmar"
                );
        }

        if (textoDesplazamiento != null)
        {
            textoDesplazamiento.text = desplazamiento > 0
                ? "+" + desplazamiento
                : desplazamiento.ToString();
        }
    }

    public void GenerarAcertijo()
    {
        if (GestorIdiomas.Instancia == null)
        {
            Debug.LogError("No se ha encontrado GestorIdiomas.");
            return;
        }

        if (vidaTroll == null)
        {
            Debug.LogError("No se ha asignado VidaTroll.");
            return;
        }

        // Desplazamiento aleatorio entre -5 y +5,
        // excluyendo -1, 0 y +1.
        do
        {
            desplazamiento = UnityEngine.Random.Range(-5, 6);
        }
        while (desplazamiento >= -1 && desplazamiento <= 1);

        string idioma = GestorIdiomas.Instancia.IdiomaActual;
        string[] frasesSeleccionadas;

        if (vidaTroll.VidaActual >= 3)
        {
            frasesSeleccionadas = ObtenerFrasesFaciles(idioma);
        }
        else if (vidaTroll.VidaActual == 2)
        {
            frasesSeleccionadas = ObtenerFrasesMedias(idioma);
        }
        else
        {
            frasesSeleccionadas = ObtenerFrasesDificiles(idioma);
        }

        if (frasesSeleccionadas == null ||
            frasesSeleccionadas.Length == 0)
        {
            Debug.LogError("No hay frases disponibles.");
            return;
        }

        // Elegir una frase aleatoria.
        fraseOriginal = frasesSeleccionadas[
            UnityEngine.Random.Range(0, frasesSeleccionadas.Length)
        ];

        // Cifrar la frase.
        if (textoCifrado != null)
            textoCifrado.text = CifrarCesar(fraseOriginal);

        // Mostrar el desplazamiento actual.
        ActualizarTextos();

        // Limpiar la respuesta anterior.
        if (campoRespuesta != null)
            campoRespuesta.text = "";

        respuestaProcesada = false;
    }

    private string[] ObtenerFrasesFaciles(string idioma)
    {
        switch (idioma)
        {
            case "eu":
                return frasesFacilesEU;

            case "en":
                return frasesFacilesEN;

            default:
                return frasesFacilesES;
        }
    }

    private string[] ObtenerFrasesMedias(string idioma)
    {
        switch (idioma)
        {
            case "eu":
                return frasesMediasEU;

            case "en":
                return frasesMediasEN;

            default:
                return frasesMediasES;
        }
    }

    private string[] ObtenerFrasesDificiles(string idioma)
    {
        switch (idioma)
        {
            case "eu":
                return frasesDificilesEU;

            case "en":
                return frasesDificilesEN;

            default:
                return frasesDificilesES;
        }
    }

    private string CifrarCesar(string texto)
    {
        string resultado = "";

        foreach (char caracter in texto)
        {
            if (caracter >= 'A' && caracter <= 'Z')
            {
                int posicion = caracter - 'A';

                int nuevaPosicion =
                    ((posicion + desplazamiento) % 26 + 26) % 26;

                resultado += (char)('A' + nuevaPosicion);
            }
            else if (caracter >= 'a' && caracter <= 'z')
            {
                int posicion = caracter - 'a';

                int nuevaPosicion =
                    ((posicion + desplazamiento) % 26 + 26) % 26;

                resultado += (char)('a' + nuevaPosicion);
            }
            else
            {
                resultado += caracter;
            }
        }

        return resultado;
    }

    public bool ComprobarRespuesta(string respuesta)
    {
        return !string.IsNullOrWhiteSpace(respuesta)
            && respuesta.Trim().Equals(
                fraseOriginal,
                StringComparison.OrdinalIgnoreCase
            );
    }

    public void ConfirmarRespuesta()
    {
        if (respuestaProcesada)
            return;

        if (campoRespuesta == null || vidaTroll == null)
        {
            Debug.LogError(
                "Faltan referencias del campo de respuesta o de VidaTroll."
            );
            return;
        }

        if (vidaTroll.VidaActual <= 0)
        {
            CerrarAcertijo();
            return;
        }

        if (string.IsNullOrWhiteSpace(campoRespuesta.text))
            return;

        respuestaProcesada = true;

        bool respuestaCorrecta =
            ComprobarRespuesta(campoRespuesta.text);

        MostrarMensajeResultado(respuestaCorrecta);

        if (respuestaCorrecta)
            vidaTroll.RecibirDano();

        if (movimientoPaloma != null)
        {
            movimientoPaloma.AcelerarConfigurado();
        }
        else
        {
            Debug.LogWarning(
                "No se ha asignado MovimientoPaloma en GestorAcertijo."
            );
        }

        if (vidaTroll.VidaActual > 0)
        {
            if (movimientoPaloma != null)
                movimientoPaloma.ReiniciarDesdeIzquierda();

            if (contenedorPaloma != null)
                contenedorPaloma.SetActive(true);
        }

        CerrarAcertijo();
    }

    private void MostrarMensajeResultado(bool respuestaCorrecta)
    {
        OcultarMensajes();

        GameObject mensaje = respuestaCorrecta
            ? txtCorrecto
            : txtIncorrecto;

        if (mensaje == null)
        {
            Debug.LogWarning(
                "No se ha asignado el mensaje de resultado correspondiente."
            );
            return;
        }

        mensaje.SetActive(true);

        corrutinaMensaje = StartCoroutine(
            OcultarMensajeDespuesDeTiempo(mensaje)
        );
    }

    private void OcultarMensajes()
    {
        if (corrutinaMensaje != null)
        {
            StopCoroutine(corrutinaMensaje);
            corrutinaMensaje = null;
        }

        if (txtCorrecto != null)
            txtCorrecto.SetActive(false);

        if (txtIncorrecto != null)
            txtIncorrecto.SetActive(false);
    }

    private IEnumerator OcultarMensajeDespuesDeTiempo(GameObject mensaje)
    {
        yield return new WaitForSeconds(duracionMensaje);

        if (mensaje != null)
            mensaje.SetActive(false);

        corrutinaMensaje = null;
    }

    private void CerrarAcertijo()
    {
        if (contenedorAcertijo != null)
            contenedorAcertijo.SetActive(false);
    }

    public void PrepararNuevoAcertijo()
    {
        respuestaProcesada = false;
        OcultarMensajes();

        if (campoRespuesta != null)
            campoRespuesta.text = "";

        GenerarAcertijo();
    }

    public void ReiniciarAcertijo()
{
    respuestaProcesada = false;
    OcultarMensajes();

    if (campoRespuesta != null)
        campoRespuesta.text = "";

    // Mantener cerrado el panel del acertijo tras reiniciar.
    if (contenedorAcertijo != null)
        contenedorAcertijo.SetActive(false);

    // Restaurar la paloma.
    if (contenedorPaloma != null)
        contenedorPaloma.SetActive(true);

    GenerarAcertijo();
}
}