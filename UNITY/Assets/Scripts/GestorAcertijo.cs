
using System;
using System.Collections;
using UnityEngine;
using TMPro;

public class GestorAcertijo : MonoBehaviour
{
    // Referencias a los scripts que controlan las vidas y el movimiento de los personajes.
    [Header("Referencias")]
    [SerializeField] private VidaTroll vidaTroll;
    [SerializeField] private MovimientoPaloma movimientoPaloma;
    [SerializeField] private MovimientoTroll movimientoTroll;

    // Animator encargado de reproducir el efecto de explosión cuando se acierta.
    [Header("Efecto de explosion")]
    [SerializeField] private Animator animacionExplosion;

    // Contenedores de los paneles de la paloma y del acertijo.
    [Header("Contenedores")]
    [SerializeField] private GameObject contenedorAcertijo;
    [SerializeField] private GameObject contenedorPaloma;

    // Elementos de texto utilizados para mostrar el acertijo y recoger la respuesta.
    [Header("Textos del acertijo")]
    [SerializeField] private TMP_Text textoCifrado;
    [SerializeField] private TMP_Text textoDesplazamiento;
    [SerializeField] private TMP_InputField campoRespuesta;
    [SerializeField] private TMP_Text textoPlaceholder;
    [SerializeField] private TMP_Text textoBotonConfirmar;

    // Objetos que muestran si la respuesta introducida es correcta o incorrecta.
    [Header("Mensajes de resultado")]
    [SerializeField] private GameObject txtCorrecto;
    [SerializeField] private GameObject txtIncorrecto;
    [SerializeField] private float duracionMensaje = 2f;

    // Número de posiciones que se desplazan las letras mediante el cifrado César.
    [Header("Configuracion")]
    [SerializeField] private int desplazamiento;

    // Acertijos fáciles que se utilizan cuando el troll tiene 3 vidas o más.
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

    // Acertijos de dificultad media que se utilizan cuando el troll tiene 2 vidas.
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

    // Acertijos difíciles que se utilizan cuando al troll le queda 1 vida.
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

    // Guarda la frase original, antes de aplicar el cifrado César.
    private string fraseOriginal;

    // Corrutinas utilizadas para controlar los mensajes y la animación de explosión.
    private Coroutine corrutinaMensaje;
    private Coroutine corrutinaExplosion;

    // Indica si el script ya se ha inicializado.
    private bool inicializado;

    // Evita que se procese más de una respuesta para el mismo acertijo.
    private bool respuestaProcesada;

    private void Start()
    {
        // Marcar el script como inicializado.
        inicializado = true;

        // Ocultar los mensajes de resultado al comenzar.
        OcultarMensajes();

        // Desactivar la animación de explosión hasta que sea necesaria.
        if (animacionExplosion != null)
            animacionExplosion.gameObject.SetActive(false);

        // Generar el primer acertijo.
        GenerarAcertijo();
    }

    private void OnEnable()
    {
        // Al volver a activar el objeto, preparar un nuevo acertijo.
        // Se comprueba que Start ya se haya ejecutado para evitar
        // generar el acertijo dos veces al iniciar el juego.
        if (inicializado)
        {
            respuestaProcesada = false;
            OcultarMensajes();
            GenerarAcertijo();
        }
    }

    // Actualiza los textos de la interfaz según el idioma seleccionado.
    public void ActualizarTextos()
    {
        // Comprobar que existe el gestor de idiomas.
        if (GestorIdiomas.Instancia == null)
        {
            Debug.LogError("No se ha encontrado GestorIdiomas.");
            return;
        }

        // Traducir el texto de ayuda del campo de respuesta.
        if (textoPlaceholder != null)
        {
            textoPlaceholder.text =
                GestorIdiomas.Instancia.ObtenerTexto(
                    "acertijo.placeholder"
                );
        }

        // Traducir el texto del botón de confirmar.
        if (textoBotonConfirmar != null)
        {
            textoBotonConfirmar.text =
                GestorIdiomas.Instancia.ObtenerTexto(
                    "acertijo.confirmar"
                );
        }

        // Mostrar el desplazamiento del cifrado César.
        // Si es positivo, se añade el signo + delante del número.
        if (textoDesplazamiento != null)
        {
            textoDesplazamiento.text = desplazamiento > 0
                ? "+" + desplazamiento
                : desplazamiento.ToString();
        }

        // Traducir el mensaje que aparece cuando se acierta.
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

        // Traducir el mensaje que aparece cuando se falla.
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

    // Genera un nuevo acertijo teniendo en cuenta las vidas del troll y el idioma.
    public void GenerarAcertijo()
    {
        // Comprobar que existe el gestor de idiomas.
        if (GestorIdiomas.Instancia == null)
        {
            Debug.LogError("No se ha encontrado GestorIdiomas.");
            return;
        }

        // Comprobar que existe la referencia al sistema de vidas.
        if (vidaTroll == null)
        {
            Debug.LogError("No se ha asignado VidaTroll.");
            return;
        }

        // Generar un desplazamiento aleatorio entre -5 y 5.
        // Se repite el proceso para evitar valores entre -1 y 1.
        do
        {
            desplazamiento = UnityEngine.Random.Range(-5, 6);
        }
        while (desplazamiento >= -1 && desplazamiento <= 1);

        // Obtener el idioma seleccionado actualmente.
        string idioma = GestorIdiomas.Instancia.IdiomaActual;

        // Variable donde se guardará el conjunto de frases seleccionado.
        string[] frasesSeleccionadas;

        // Seleccionar la dificultad según las vidas restantes del troll.
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

        // Comprobar que existen frases disponibles.
        if (frasesSeleccionadas == null ||
            frasesSeleccionadas.Length == 0)
        {
            Debug.LogError("No hay frases disponibles.");
            return;
        }

        // Elegir aleatoriamente una frase del conjunto seleccionado.
        fraseOriginal = frasesSeleccionadas[
            UnityEngine.Random.Range(0, frasesSeleccionadas.Length)
        ];

        // Cifrar la frase y mostrarla en la interfaz.
        if (textoCifrado != null)
            textoCifrado.text = CifrarCesar(fraseOriginal);

        // Actualizar los textos según el idioma y el desplazamiento.
        ActualizarTextos();

        // Limpiar el campo de respuesta para el nuevo acertijo.
        if (campoRespuesta != null)
            campoRespuesta.text = "";

        // Permitir que se procese una nueva respuesta.
        respuestaProcesada = false;
    }

    // Devuelve las frases fáciles correspondientes al idioma seleccionado.
    private string[] ObtenerFrasesFaciles(string idioma)
    {
        switch (idioma)
        {
            case "eu":
                return frasesFacilesEU;

            case "en":
                return frasesFacilesEN;

            // Si no es euskera ni inglés, utilizar español.
            default:
                return frasesFacilesES;
        }
    }

    // Devuelve las frases de dificultad media según el idioma.
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

    // Devuelve las frases difíciles según el idioma seleccionado.
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

    // Aplica el cifrado César a una frase utilizando el desplazamiento generado.
    private string CifrarCesar(string texto)
    {
        // Variable donde se construirá la frase cifrada.
        string resultado = "";

        // Recorrer todos los caracteres de la frase.
        foreach (char caracter in texto)
        {
            // Comprobar si el carácter es una letra mayúscula.
            if (caracter >= 'A' && caracter <= 'Z')
            {
                // Obtener la posición de la letra dentro del alfabeto.
                int posicion = caracter - 'A';

                // Calcular la nueva posición aplicando el desplazamiento.
                // La operación permite volver al principio del alfabeto
                // cuando se supera la letra Z.
                int nuevaPosicion =
                    ((posicion + desplazamiento) % 26 + 26) % 26;

                // Añadir la nueva letra al resultado.
                resultado += (char)('A' + nuevaPosicion);
            }
            // Comprobar si el carácter es una letra minúscula.
            else if (caracter >= 'a' && caracter <= 'z')
            {
                int posicion = caracter - 'a';

                int nuevaPosicion =
                    ((posicion + desplazamiento) % 26 + 26) % 26;

                resultado += (char)('a' + nuevaPosicion);
            }
            else
            {
                // Mantener espacios y cualquier otro carácter sin modificar.
                resultado += caracter;
            }
        }

        // Devolver la frase cifrada.
        return resultado;
    }

    // Comprueba si la respuesta coincide con la frase original,
    // ignorando tildes, acentos y otras marcas diacríticas.
    public bool ComprobarRespuesta(string respuesta)
    {
        // Rechazar respuestas vacías o que solo contengan espacios.
        if (string.IsNullOrWhiteSpace(respuesta))
            return false;

        // Normalizar la respuesta introducida por el jugador.
        string respuestaNormalizada =
            QuitarMarcasDiacriticas(respuesta.Trim());

        // Normalizar también la frase original.
        string fraseNormalizada =
            QuitarMarcasDiacriticas(fraseOriginal.Trim());

        // Comparar ambas frases sin distinguir entre mayúsculas y minúsculas.
        return respuestaNormalizada.Equals(
            fraseNormalizada,
            StringComparison.OrdinalIgnoreCase
        );
    }

    // Elimina las marcas diacríticas Unicode de un texto.
    // Esto permite comparar letras con y sin tildes, diéresis y otros acentos.
    private string QuitarMarcasDiacriticas(string texto)
    {
        // Descomponer los caracteres para separar las letras de sus marcas.
        string normalizado = texto.Normalize(
            System.Text.NormalizationForm.FormD
        );

        // Crear un StringBuilder para construir el texto sin marcas.
        System.Text.StringBuilder resultado =
            new System.Text.StringBuilder();

        // Recorrer todos los caracteres del texto normalizado.
        foreach (char caracter in normalizado)
        {
            // Obtener la categoría Unicode del carácter.
            System.Globalization.UnicodeCategory categoria =
                System.Globalization.CharUnicodeInfo.GetUnicodeCategory(caracter);

            // Ignorar las marcas diacríticas y conservar el resto de caracteres.
            if (categoria != System.Globalization.UnicodeCategory.NonSpacingMark &&
                categoria != System.Globalization.UnicodeCategory.SpacingCombiningMark &&
                categoria != System.Globalization.UnicodeCategory.EnclosingMark)
            {
                resultado.Append(caracter);
            }
        }

        // Devolver el texto reconstruido y normalizado.
        return resultado.ToString().Normalize(
            System.Text.NormalizationForm.FormC
        );
    }

    // Procesa la respuesta introducida por el jugador.
    public void ConfirmarRespuesta()
    {
        // Evitar que se pueda confirmar varias veces el mismo acertijo.
        if (respuestaProcesada)
            return;

        // Comprobar que están asignadas las referencias necesarias.
        if (campoRespuesta == null || vidaTroll == null)
        {
            Debug.LogError(
                "Faltan referencias del campo de respuesta o de VidaTroll."
            );
            return;
        }

        // Si el troll ya no tiene vidas, cerrar el acertijo.
        if (vidaTroll.VidaActual <= 0)
        {
            CerrarAcertijo();
            return;
        }

        // No procesar respuestas vacías.
        if (string.IsNullOrWhiteSpace(campoRespuesta.text))
            return;

        // Marcar la respuesta como procesada para evitar duplicados.
        respuestaProcesada = true;

        // Comprobar si la respuesta introducida es correcta.
        bool respuestaCorrecta =
            ComprobarRespuesta(campoRespuesta.text);

        // Mostrar el mensaje correspondiente al resultado.
        MostrarMensajeResultado(respuestaCorrecta);

        if (respuestaCorrecta)
        {
            // Si acierta, el troll pierde una vida.
            vidaTroll.RecibirDano();

            // Reproducir el efecto visual de explosión.
            ReproducirExplosion();
        }
        else
        {
            // Si falla, aumentar la velocidad de movimiento del troll.
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

        // Acelerar también el movimiento de las palomas.
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

        // Si el troll todavía tiene vidas, preparar la siguiente paloma.
        if (vidaTroll.VidaActual > 0)
        {
            // Reiniciar la posición de la paloma para que vuelva a aparecer.
            if (movimientoPaloma != null)
                movimientoPaloma.ReiniciarDesdeIzquierda();

            // Volver a mostrar el contenedor de la paloma.
            if (contenedorPaloma != null)
                contenedorPaloma.SetActive(true);
        }

        // Cerrar el panel del acertijo después de procesar la respuesta.
        CerrarAcertijo();
    }

    // Activa y reproduce la animación de explosión.
    private void ReproducirExplosion()
    {
        // Comprobar que existe una referencia al Animator.
        if (animacionExplosion == null)
        {
            Debug.LogWarning(
                "No se ha asignado la animacion de explosion."
            );
            return;
        }

        // Detener una animación anterior si todavía está ejecutándose.
        if (corrutinaExplosion != null)
        {
            StopCoroutine(corrutinaExplosion);
            corrutinaExplosion = null;
        }

        // Activar el objeto que contiene la animación.
        animacionExplosion.gameObject.SetActive(true);

        // Reproducir la animación desde el primer fotograma.
        animacionExplosion.Play("ExplosionAnimation", 0, 0f);

        // Iniciar una corrutina para desactivar la explosión al terminar.
        corrutinaExplosion = StartCoroutine(
            DesactivarExplosionAlTerminar()
        );
    }

    // Espera a que termine la animación y después la desactiva.
    private IEnumerator DesactivarExplosionAlTerminar()
    {
        // Esperar un fotograma para que el Animator actualice su estado.
        yield return null;

        // Obtener la duración del estado de animación actual.
        float duracion = animacionExplosion
            .GetCurrentAnimatorStateInfo(0).length;

        // Si la duración no es válida, utilizar un segundo como valor por defecto.
        if (duracion <= 0f)
            duracion = 1f;

        // Esperar hasta que haya transcurrido la duración de la animación.
        yield return new WaitForSeconds(duracion);

        // Desactivar el objeto de la explosión.
        if (animacionExplosion != null)
            animacionExplosion.gameObject.SetActive(false);

        // Indicar que la corrutina ha terminado.
        corrutinaExplosion = null;
    }

    // Muestra el mensaje de respuesta correcta o incorrecta.
    private void MostrarMensajeResultado(bool respuestaCorrecta)
    {
        // Ocultar cualquier mensaje que estuviera mostrándose anteriormente.
        OcultarMensajes();

        // Seleccionar el mensaje correspondiente al resultado.
        GameObject mensaje = respuestaCorrecta
            ? txtCorrecto
            : txtIncorrecto;

        // Comprobar que el mensaje está asignado.
        if (mensaje == null)
        {
            Debug.LogWarning(
                "No se ha asignado el mensaje de resultado correspondiente."
            );
            return;
        }

        // Mostrar el mensaje seleccionado.
        mensaje.SetActive(true);

        // Iniciar una corrutina para ocultarlo después de unos segundos.
        corrutinaMensaje = StartCoroutine(
            OcultarMensajeDespuesDeTiempo(mensaje)
        );
    }

    // Oculta los mensajes de resultado y detiene su corrutina si está activa.
    private void OcultarMensajes()
    {
        // Detener la corrutina anterior para evitar que interfiera
        // con un nuevo mensaje.
        if (corrutinaMensaje != null)
        {
            StopCoroutine(corrutinaMensaje);
            corrutinaMensaje = null;
        }

        // Ocultar el mensaje de respuesta correcta.
        if (txtCorrecto != null)
            txtCorrecto.SetActive(false);

        // Ocultar el mensaje de respuesta incorrecta.
        if (txtIncorrecto != null)
            txtIncorrecto.SetActive(false);
    }

    // Espera el tiempo configurado y después oculta el mensaje.
    private IEnumerator OcultarMensajeDespuesDeTiempo(GameObject mensaje)
    {
        // Esperar la duración establecida en el Inspector.
        yield return new WaitForSeconds(duracionMensaje);

        // Ocultar el mensaje si todavía existe.
        if (mensaje != null)
            mensaje.SetActive(false);

        // Indicar que la corrutina ha terminado.
        corrutinaMensaje = null;
    }

    // Oculta el contenedor del acertijo.
    private void CerrarAcertijo()
    {
        if (contenedorAcertijo != null)
            contenedorAcertijo.SetActive(false);
    }

    // Prepara un acertijo nuevo cuando se abre el panel.
    public void PrepararNuevoAcertijo()
    {
        // Permitir procesar una nueva respuesta.
        respuestaProcesada = false;

        // Ocultar los mensajes del acertijo anterior.
        OcultarMensajes();

        // Limpiar el campo de respuesta.
        if (campoRespuesta != null)
            campoRespuesta.text = "";

        // Generar un nuevo acertijo.
        GenerarAcertijo();
    }

    // Reinicia el estado del acertijo y devuelve la interfaz a la fase de preparación.
    public void ReiniciarAcertijo()
    {
        // Permitir procesar una nueva respuesta.
        respuestaProcesada = false;

        // Ocultar los mensajes de resultado.
        OcultarMensajes();

        // Limpiar el campo de respuesta.
        if (campoRespuesta != null)
            campoRespuesta.text = "";

        // Ocultar el panel del acertijo.
        if (contenedorAcertijo != null)
            contenedorAcertijo.SetActive(false);

        // Volver a mostrar el contenedor de la paloma.
        if (contenedorPaloma != null)
            contenedorPaloma.SetActive(true);

        // Generar un nuevo acertijo.
        GenerarAcertijo();
    }
}
