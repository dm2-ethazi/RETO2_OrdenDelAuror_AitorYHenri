import json
from pathlib import Path

__author__ = "Henri Tejada"

class LanguageManager:
    def __init__(self, language="es"):
        self.language = language
        ruta = Path(__file__).parent/ ".." / "locales" / f"{language}.json"

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
