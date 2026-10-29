
using System;
using System.Collections.Generic;
using UnityEngine;

public class GestorIdiomas : MonoBehaviour
{
    public static GestorIdiomas Instancia { get; private set; }

    [Header("Archivos de traducciones")]
    [SerializeField] private TextAsset textosEspanol;
    [SerializeField] private TextAsset textosEuskera;
    [SerializeField] private TextAsset textosIngles;

    public string IdiomaActual { get; private set; } = "es";

    private Dictionary<string, string> traducciones =
        new Dictionary<string, string>();

    private const string CLAVE_IDIOMA = "IdiomaSeleccionado";

    [Serializable]
    private class ArchivoTraducciones
    {
        public Traduccion[] traducciones;
    }

    [Serializable]
    private class Traduccion
    {
        public string clave;
        public string valor;
    }

    private void Awake()
    {
        if (Instancia != null && Instancia != this)
        {
            Destroy(gameObject);
            return;
        }

        Instancia = this;

        string idiomaGuardado =
            PlayerPrefs.GetString(CLAVE_IDIOMA, "es");

        CargarIdioma(idiomaGuardado);
    }

    public void CambiarIdioma(string idioma)
    {
        if (idioma != "es" && idioma != "eu" && idioma != "en")
        {
            Debug.LogWarning(
                "Idioma no reconocido: " + idioma
            );
            return;
        }

        if (CargarIdioma(idioma))
        {
            PlayerPrefs.SetString(CLAVE_IDIOMA, idioma);
            PlayerPrefs.Save();
        }
    }

    private bool CargarIdioma(string idioma)
    {
        TextAsset archivo = ObtenerArchivoIdioma(idioma);

        if (archivo == null)
        {
            Debug.LogError(
                "No se ha asignado el JSON del idioma: " + idioma
            );
            return false;
        }

        try
        {
            ArchivoTraducciones datos =
                JsonUtility.FromJson<ArchivoTraducciones>(
                    archivo.text
                );

            if (datos == null || datos.traducciones == null)
            {
                Debug.LogError(
                    "El JSON no tiene el formato esperado: " +
                    archivo.name
                );
                return false;
            }

            Dictionary<string, string> nuevasTraducciones =
                new Dictionary<string, string>();

            foreach (Traduccion traduccion in datos.traducciones)
            {
                if (traduccion == null ||
                    string.IsNullOrEmpty(traduccion.clave))
                {
                    continue;
                }

                nuevasTraducciones[traduccion.clave] =
                    traduccion.valor ?? "";
            }

            traducciones = nuevasTraducciones;
            IdiomaActual = idioma;

            Debug.Log("Idioma cargado: " + idioma);
            return true;
        }
        catch (Exception e)
        {
            Debug.LogError(
                "Error al cargar las traducciones: " + e.Message
            );
            return false;
        }
    }

    private TextAsset ObtenerArchivoIdioma(string idioma)
    {
        switch (idioma)
        {
            case "es":
                return textosEspanol;

            case "eu":
                return textosEuskera;

            case "en":
                return textosIngles;

            default:
                return textosEspanol;
        }
    }

    public string ObtenerTexto(string clave)
    {
        if (traducciones.TryGetValue(clave, out string valor))
            return valor;

        Debug.LogWarning(
            "No existe la traduccion para la clave: " + clave
        );

        return clave;
    }
}
