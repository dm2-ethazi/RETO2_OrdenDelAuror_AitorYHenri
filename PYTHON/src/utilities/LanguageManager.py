import json
from pathlib import Path

__author__ = "Henri Tejada"

class LanguageManager:
    def __init__(self, language="es"):
        ruta = Path(__file__).parent/ ".." / "locales" / f"{language}.json"

        with open(ruta, "r", encoding="utf-8") as f:
            self.translations = json.load(f)

    def t(self, key):
        return self.translations.get(key, key)