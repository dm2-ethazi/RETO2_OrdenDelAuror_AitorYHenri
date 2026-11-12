
using System;
using System.Collections.Generic;
using UnityEngine;

public class GestorIdiomas : MonoBehaviour
{
    // Instancia única del gestor de idiomas para poder acceder a él
    // desde cualquier otro script mediante GestorIdiomas.Instancia.
    public static GestorIdiomas Instancia { get; private set; }

    // Archivos JSON que contienen las traducciones de cada idioma.
    [Header("Archivos de traducciones")]
    [SerializeField] private TextAsset textosEspanol;
    [SerializeField] private TextAsset textosEuskera;
    [SerializeField] private TextAsset textosIngles;

    // Guarda el idioma seleccionado actualmente.
    // Por defecto, el idioma inicial es español.
    public string IdiomaActual { get; private set; } = "es";

    // Diccionario que almacena las traducciones utilizando
    // la clave como identificador y el texto como valor.
    private Dictionary<string, string> traducciones =
        new Dictionary<string, string>();

    // Clave utilizada para guardar y recuperar el idioma seleccionado
    // mediante PlayerPrefs.
    private const string CLAVE_IDIOMA = "IdiomaSeleccionado";

    // Clase que representa la estructura principal del archivo JSON.
    // Contiene un array con todas las traducciones.
    [Serializable]
    private class ArchivoTraducciones
    {
        public Traduccion[] traducciones;
    }

    // Clase que representa cada traducción del archivo JSON.
    // Cada elemento contiene una clave y su correspondiente texto.
    [Serializable]
    private class Traduccion
    {
        public string clave;
        public string valor;
    }

    private void Awake()
    {
        // Comprobar si ya existe otra instancia del gestor.
        // Si existe, destruir este objeto para evitar duplicados.
        if (Instancia != null && Instancia != this)
        {
            Destroy(gameObject);
            return;
        }

        // Asignar este objeto como instancia principal.
        Instancia = this;

        // Recuperar el idioma guardado anteriormente.
        // Si no existe ninguno guardado, utilizar español.
        string idiomaGuardado =
            PlayerPrefs.GetString(CLAVE_IDIOMA, "es");

        // Cargar las traducciones del idioma recuperado.
        CargarIdioma(idiomaGuardado);
    }

    // Cambia el idioma de la aplicación al idioma indicado.
    public void CambiarIdioma(string idioma)
    {
        // Comprobar que el idioma solicitado está disponible.
        if (idioma != "es" && idioma != "eu" && idioma != "en")
        {
            Debug.LogWarning(
                "Idioma no reconocido: " + idioma
            );
            return;
        }

        // Intentar cargar las traducciones del nuevo idioma.
        if (CargarIdioma(idioma))
        {
            // Guardar el idioma seleccionado para futuras ejecuciones.
            PlayerPrefs.SetString(CLAVE_IDIOMA, idioma);

            // Guardar los cambios en las preferencias del juego.
            PlayerPrefs.Save();
        }
    }

    // Carga el archivo JSON correspondiente al idioma seleccionado.
    // Devuelve true si la carga se realiza correctamente.
    private bool CargarIdioma(string idioma)
    {
        // Obtener el archivo JSON correspondiente al idioma.
        TextAsset archivo = ObtenerArchivoIdioma(idioma);

        // Comprobar que el archivo está asignado en el Inspector.
        if (archivo == null)
        {
            Debug.LogError(
                "No se ha asignado el JSON del idioma: " + idioma
            );
            return false;
        }

        try
        {
            // Convertir el contenido JSON en un objeto de C#.
            ArchivoTraducciones datos =
                JsonUtility.FromJson<ArchivoTraducciones>(
                    archivo.text
                );

            // Comprobar que el JSON tiene la estructura esperada
            // y contiene el array de traducciones.
            if (datos == null || datos.traducciones == null)
            {
                Debug.LogError(
                    "El JSON no tiene el formato esperado: " +
                    archivo.name
                );
                return false;
            }

            // Crear un nuevo diccionario para almacenar las traducciones.
            Dictionary<string, string> nuevasTraducciones =
                new Dictionary<string, string>();

            // Recorrer todas las traducciones obtenidas del JSON.
            foreach (Traduccion traduccion in datos.traducciones)
            {
                // Ignorar los elementos nulos o que no tengan una clave.
                if (traduccion == null ||
                    string.IsNullOrEmpty(traduccion.clave))
                {
                    continue;
                }

                // Añadir la traducción al diccionario.
                // Si el valor es nulo, guardar una cadena vacía.
                nuevasTraducciones[traduccion.clave] =
                    traduccion.valor ?? "";
            }

            // Sustituir las traducciones anteriores por las nuevas.
            traducciones = nuevasTraducciones;

            // Actualizar el idioma actual.
            IdiomaActual = idioma;

            // Informar de que el idioma se ha cargado correctamente.
            Debug.Log("Idioma cargado: " + idioma);

            return true;
        }
        catch (Exception e)
        {
            // Capturar posibles errores durante la lectura del JSON.
            Debug.LogError(
                "Error al cargar las traducciones: " + e.Message
            );

            return false;
        }
    }

    // Devuelve el archivo JSON asociado al código de idioma recibido.
    private TextAsset ObtenerArchivoIdioma(string idioma)
    {
        switch (idioma)
        {
            // Archivo de traducciones en español.
            case "es":
                return textosEspanol;

            // Archivo de traducciones en euskera.
            case "eu":
                return textosEuskera;

            // Archivo de traducciones en inglés.
            case "en":
                return textosIngles;

            // Si el idioma no coincide con ninguno, devolver español.
            default:
                return textosEspanol;
        }
    }

    // Busca una traducción en el diccionario a partir de su clave.
    public string ObtenerTexto(string clave)
    {
        // TryGetValue busca la clave y devuelve su valor si existe.
        if (traducciones.TryGetValue(clave, out string valor))
            return valor;

        // Avisar si no se encuentra la traducción solicitada.
        Debug.LogWarning(
            "No existe la traduccion para la clave: " + clave
        );

        // Si no existe la clave, devolverla como texto alternativo.
        return clave;
    }
}
