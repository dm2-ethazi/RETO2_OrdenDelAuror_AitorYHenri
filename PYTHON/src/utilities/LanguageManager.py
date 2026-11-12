import json
import sys
from pathlib import Path

__author__ = "Henri Tejada"

class LanguageManager:
    def __init__(self, language="es"):
        self.language = language

        if hasattr(sys, '_MEIPASS'):
            # En el .exe, todo se descomprime en la raíz de la carpeta temporal
            base_path = Path(sys._MEIPASS)
            ruta = base_path / "locales" / f"{language}.json"
        else:
            # En modo desarrollo, subes dos niveles desde utilites/ hasta src/
            base_path = Path(__file__).resolve().parent.parent
            ruta = base_path / "locales" / f"{language}.json"
        # 2. Construimos la ruta apuntando directamente a la carpeta locales
        ruta = base_path / "locales" / f"{language}.json"
        with open(ruta, "r", encoding="utf-8") as f:
            self.translations = json.load(f)

    def t(self, key):
        return self.translations.get(key, key)

    @property
    def language(self):
        return self._language

    @language.setter
    def language(self, language):
        self._language = language
