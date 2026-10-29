
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

    private const string CLAVE_IDIOMA = "IdiomaSeleccionado";

    private Dictionary<string, string> traducciones =
        new Dictionary<string, string>();

    public string IdiomaActual { get; private set; } = "es";

    [Serializable]
    private class ArchivoTraducciones
    {
        public EntradaTraduccion[] traducciones;
    }

    [Serializable]
    private class EntradaTraduccion
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
        DontDestroyOnLoad(gameObject);

        IdiomaActual = PlayerPrefs.GetString(CLAVE_IDIOMA, "es");

        CargarIdioma(IdiomaActual);
    }

    public void CambiarIdioma(string idioma)
    {
        if (idioma != "es" && idioma != "eu" && idioma != "en")
        {
            Debug.LogWarning("Idioma no válido: " + idioma);
            return;
        }

        CargarIdioma(idioma);

        IdiomaActual = idioma;
        PlayerPrefs.SetString(CLAVE_IDIOMA, idioma);
        PlayerPrefs.Save();
    }

    public string ObtenerTexto(string clave)
    {
        if (traducciones.TryGetValue(clave, out string valor))
        {
            return valor;
        }

        Debug.LogWarning(
            "No se ha encontrado la traducción para la clave: " + clave
        );

        return "[" + clave + "]";
    }

    private void CargarIdioma(string idioma)
    {
        TextAsset archivo;

        switch (idioma)
        {
            case "eu":
                archivo = textosEuskera;
                break;

            case "en":
                archivo = textosIngles;
                break;

            default:
                archivo = textosEspanol;
                break;
        }

        if (archivo == null)
        {
            Debug.LogError(
                "No se ha asignado el archivo JSON del idioma: " + idioma
            );
            return;
        }

        ArchivoTraducciones datos =
            JsonUtility.FromJson<ArchivoTraducciones>(archivo.text);

        if (datos == null || datos.traducciones == null)
        {
            Debug.LogError(
                "El formato JSON no es válido: " + archivo.name
            );
            return;
        }

        traducciones.Clear();

        foreach (EntradaTraduccion entrada in datos.traducciones)
        {
            if (entrada != null &&
                !string.IsNullOrWhiteSpace(entrada.clave))
            {
                traducciones[entrada.clave] = entrada.valor ?? "";
            }
        }

        Debug.Log("Idioma cargado correctamente: " + idioma);
    }
}
