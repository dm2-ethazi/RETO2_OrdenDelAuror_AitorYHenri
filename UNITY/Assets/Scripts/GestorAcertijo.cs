
using System;
using System.Collections;
using UnityEngine;
using TMPro;

public class GestorAcertijo : MonoBehaviour
{
    [Header("Referencias")]
    [SerializeField] private VidaTroll vidaTroll;
    [SerializeField] private MovimientoPaloma movimientoPaloma;
    [SerializeField] private MovimientoTroll movimientoTroll;

    [Header("Efecto de explosion")]
    [SerializeField] private Animator animacionExplosion;

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
        "IZOTZEAN IRRISTATZEN DA",
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
    private Coroutine corrutinaExplosion;
    private bool inicializado;
    private bool respuestaProcesada;

    private void Start()
    {
        inicializado = true;

        OcultarMensajes();

        if (animacionExplosion != null)
            animacionExplosion.gameObject.SetActive(false);

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

        // Traducir mensaje de respuesta correcta.
        if (txtCorrecto != null)
        {
            TMP_Text textoCorrecto =
                txtCorrecto.GetComponentInChildren<TMP_Text>(true);

            if (textoCorrecto != null)
            {
                textoCorrecto.text =
                    GestorIdiomas.Instancia.ObtenerTexto(
                        "acertijo.correcto"
                    );
            }
        }

        // Traducir mensaje de respuesta incorrecta.
        if (txtIncorrecto != null)
        {
            TMP_Text textoIncorrecto =
                txtIncorrecto.GetComponentInChildren<TMP_Text>(true);

            if (textoIncorrecto != null)
            {
                textoIncorrecto.text =
                    GestorIdiomas.Instancia.ObtenerTexto(
                        "acertijo.incorrecto"
                    );
            }
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

        fraseOriginal = frasesSeleccionadas[
            UnityEngine.Random.Range(0, frasesSeleccionadas.Length)
        ];

        if (textoCifrado != null)
            textoCifrado.text = CifrarCesar(fraseOriginal);

        ActualizarTextos();

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
        {
            // Respuesta correcta: el troll pierde una vida.
            vidaTroll.RecibirDano();

            // Reproducir la explosion magica.
            ReproducirExplosion();
        }
        else
        {
            // Respuesta incorrecta: el troll acelera.
            if (movimientoTroll != null)
            {
                movimientoTroll.Acelerar();
            }
            else
            {
                Debug.LogError(
                    "No se ha asignado MovimientoTroll en GestorAcertijo."
                );
            }
        }

        // Se mantiene la aceleracion original de la paloma.
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

    private void ReproducirExplosion()
    {
        if (animacionExplosion == null)
        {
            Debug.LogWarning(
                "No se ha asignado la animacion de explosion."
            );
            return;
        }

        if (corrutinaExplosion != null)
        {
            StopCoroutine(corrutinaExplosion);
            corrutinaExplosion = null;
        }

        animacionExplosion.gameObject.SetActive(true);

        // Reiniciar la animacion desde el primer fotograma.
        animacionExplosion.Play("ExplosionAnimation", 0, 0f);

        corrutinaExplosion = StartCoroutine(
            DesactivarExplosionAlTerminar()
        );
    }

    private IEnumerator DesactivarExplosionAlTerminar()
    {
        yield return null;

        float duracion = animacionExplosion
            .GetCurrentAnimatorStateInfo(0).length;

        if (duracion <= 0f)
            duracion = 1f;

        yield return new WaitForSeconds(duracion);

        if (animacionExplosion != null)
            animacionExplosion.gameObject.SetActive(false);

        corrutinaExplosion = null;
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

        if (contenedorAcertijo != null)
            contenedorAcertijo.SetActive(false);

        if (contenedorPaloma != null)
            contenedorPaloma.SetActive(true);

        GenerarAcertijo();
    }
}
