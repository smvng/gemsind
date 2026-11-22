from pathlib import Path
from jsonc_parser.parser import JsoncParser

ROOT_PATH = Path(__file__).parents[1]
OUTPUT_PATH = ROOT_PATH.joinpath("util/logs/MISSING_LANG_KEYS.txt")
ASSETS_PATH = ROOT_PATH.joinpath("src/main/resources/assets/gemsind")
LANGUAGES_PATH = ASSETS_PATH.joinpath("lang")
EN_US_PATH = LANGUAGES_PATH.joinpath("en_us.json")

en_us_data = JsoncParser.parse_file(EN_US_PATH)

logtext = ""

for lang_file in LANGUAGES_PATH.glob("**/*"):
    if lang_file.is_file():
        lang_path = LANGUAGES_PATH.joinpath(lang_file)
        if lang_path != EN_US_PATH:
            lang_data = JsoncParser.parse_file(lang_path)
            for key in en_us_data.keys():
                if lang_data.get(key) == None:
                    warntext = f"[{lang_file.name}] missing [{key}]"
                    logtext = logtext + warntext + "\n"

OUTPUT_PATH.parent.mkdir(exist_ok=True, parents=True)

with open(OUTPUT_PATH, "w") as f:
    f.write(logtext)