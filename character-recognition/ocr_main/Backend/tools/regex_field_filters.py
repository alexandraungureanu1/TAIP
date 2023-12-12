import re

from tools.api_macros import FieldCategories
from tools.regex_macros import CNP_SERIES_REGEX, CITIES_REGEX, DISTRICTS_FULL_REGEX, DISTRICTS_SHORT_REGEX, \
    MUNICIPALITIES_REGEX, DISTRICTS_UPPER_NO_DIACRITICS_REGEX, CITIES_UPPER_NO_DIACRITICS_REGEX, AUTO_CATEGORIES_REGEX
from tools.regex_romanian_names import ROMANIAN_NAMES_REGEX, ROMANIAN_NAMES


def format_field_name(field_name):
    return sanitize_space(field_name).lower().replace(" ", "_")


def clear_space(text):
    return text.replace("\n", "").replace("\t", "").replace(" ", "")


def sanitize_space(text):
    return re.sub(r"(\s+)", " ", text.replace("\n", " ").replace("\t", " ")).strip()


def extract_cnp_series(text):
    return re.search(r'%s' % CNP_SERIES_REGEX, clear_space(text)).group(0)


def extract_cnp_nr(text):
    return re.search(r'(\d{6})', clear_space(text)).group(0)


def extract_first_name(text):
    text = re.sub('(-+)', '-', sanitize_space(text))
    text = re.sub('- ', '-', re.sub(' -', '-', text))

    for match in re.findall(r'[A-ZĂÎÂȘȚ]+(?:[-, ]?[A-ZĂÎÂȘȚ]+)+', text):
        new_match = ''
        for name in re.split(r'([\s|-])', match):
            if name in [' ', '-']:
                new_match += name
                continue
            if len(name) <= 2:
                continue
            new_match += name

        new_match = sanitize_space(new_match)
        if len(new_match) > 0:
            return new_match
    return None


def extract_last_name(text):
    return re.search(r'%s' % ROMANIAN_NAMES_REGEX, text).group(0)


def extract_cnp(text):
    for match in re.findall(r'([1,2,5,6]\d{12})', clear_space(text)):
        month = int(match[3:5])
        if month < 1 or month > 12:
            continue

        day = int(match[5:7])
        if day < 1 or day > 31:
            continue
        return match
    return None


def extract_nationality(text):
    text = re.sub(r'([\\/|]+)', '/', clear_space(text))
    text = re.search(r'([A-ZĂÎÂȘȚ]([a-zăîâșț]+/)[A-ZĂÎÂȘȚ]{3})', text).group(0)
    text = text.replace("/", " / ")
    return text


def extract_birth_place_id_card(text):
    text = re.sub(r"\.", ". ", text)
    text = sanitize_space(text)
    regexes = [
        r'Sat\. [A-ZĂÎÂȘȚ][a-zăîâșț]+',
        r'Com\. [A-ZĂÎÂȘȚ][a-zăîâșț]+',
        r'Orş\. (%s)' % CITIES_REGEX,
        r'Jud\. (%s)' % DISTRICTS_FULL_REGEX,
        r'Jud\. (%s)' % DISTRICTS_SHORT_REGEX,
        r'Mun\. (%s)' % MUNICIPALITIES_REGEX
    ]

    birthplace = ""
    for regex in regexes:
        match = re.search(regex, text)
        if match is None:
            continue
        birthplace += match.group(0) + " "

    birthplace = birthplace.strip()
    if len(birthplace) > 0:
        return birthplace
    return None


def extract_address_id_card(text):
    text = re.sub(r"\.", ". ", text)
    text = text.replace(",", ".")
    text = sanitize_space(text)
    regexes = [
        r'(Str|str)\. [A-ZĂÎÂȘȚ][a-zăîâșț]+',
        r'(Bl|bl)\. [A-ZĂÎÂȘȚ]\d',
        r'(Et|et)\. \d',
        r'(Ap}ap)\. \d',
        r'(Sat|sat)\. [A-ZĂÎÂȘȚ][a-zăîâșț]+',
        r'(Com|com)\. [A-ZĂÎÂȘȚ][a-zăîâșț]+',
        r'(Orş|Orș|orș|orş)\. (%s)' % CITIES_REGEX,
        r'(Jud|jud)\. (%s)' % DISTRICTS_FULL_REGEX,
        r'(Jud|jud)\. (%s)' % DISTRICTS_SHORT_REGEX,
        r'(Şos|șos|şos)\. [A-ZĂÎÂȘȚ][a-zăîâșț]+',
        r'(Mun|mun)\. (%s)' % MUNICIPALITIES_REGEX,
        r'(Nr|nr)\. \d'
    ]

    matches = []
    for regex in regexes:
        match = re.search(regex, text)
        if match is None:
            continue

        new_str = match.group(0).strip()
        if new_str not in matches:
            matches += [new_str]

    address = ", ".join(matches)
    address = address.strip()
    if len(address) > 0:
        return address
    return None


def extract_issued_by_id_card(text):
    return re.search(r'SPCLEP (%s)' % CITIES_REGEX, sanitize_space(text)).group(0)


def extract_issued_by_driving_license(text):
    return re.search(r'SRPCIV (%s)' % DISTRICTS_UPPER_NO_DIACRITICS_REGEX, sanitize_space(text)).group(0)


def extract_driving_license_number(text):
    max_str = ""
    for l in [9, 10]:
        try:
            new_str = re.search(r'([A-Z0-9]){%s}' % str(l), sanitize_space(text)).group(0)
            if new_str is None:
                continue
        except:
            continue
        if len(new_str) > len(max_str):
            max_str = new_str
    if len(max_str) == 0:
        return None
    return max_str


def extract_date(text):
    text = clear_space(text)
    max_str = ""
    for l in [2, 4]:
        try:
            for match in re.findall(r'\d{2}\.\d{2}\.\d{%s}' % str(l), text):
                day = int(match[0:2])
                if day < 1 or day > 31:
                    continue

                month = int(match[3:5])
                if month < 1 or month > 12:
                    continue

                new_str = match[0: 6 + l]
                if len(new_str) > len(max_str):
                    max_str = new_str
        except:
            pass
    if len(max_str) == 0:
        return None
    return max_str


def extract_birth_place_driving_license(text):
    text = re.sub(',', " ", text)
    text = re.sub('\.', " ", text)
    text = sanitize_space(text)

    regexes = [
        r'mun (%s) (%s)' % (CITIES_UPPER_NO_DIACRITICS_REGEX, DISTRICTS_SHORT_REGEX),
        r'(%s) (%s)' % (CITIES_UPPER_NO_DIACRITICS_REGEX, DISTRICTS_SHORT_REGEX)
    ]

    birthplace = ""
    for regex in regexes:
        match = re.search(regex, text)
        if match is None:
            continue
        new_str = match.group(0)
        if new_str is None:
            continue

        if len(new_str) > len(birthplace):
            birthplace = new_str

    birthplace = birthplace.strip()
    birthplace = birthplace.replace(" ", ", ")
    birthplace = birthplace.replace("mun, ", "mun. ")

    if len(birthplace) > 0:
        return birthplace
    return None


def extract_categories(text):
    text = sanitize_space(text)
    if len(text) > 34:
        return None
    categories = []

    for s in text.split(" "):
        if s in AUTO_CATEGORIES_REGEX.split("|") and s not in categories:
            categories += [s]
    if len(categories) > 0:
        return ", ".join(categories)
    else:
        for match in re.findall(r"%s" % AUTO_CATEGORIES_REGEX, text):
            if match not in categories:
                categories += [match]

        if len(categories) > 0:
            return ", ".join(categories)
    return None


def get_max_regex_from_list(text, regex_list):
    max_str = ""
    for reg in regex_list:
        try:
            new_str = reg(text)
            if new_str is None:
                continue
        except:
            continue
        if len(new_str) > len(max_str):
            max_str = new_str
    if len(max_str) == 0:
        return None
    return max_str


def extract_issued_by_all(text):
    return get_max_regex_from_list(text, [extract_issued_by_id_card, extract_issued_by_driving_license])


def extract_address_by_all(text):
    return get_max_regex_from_list(text, [
        extract_birth_place_driving_license, extract_address_id_card
    ])


def extract_name_by_all(text):
    return get_max_regex_from_list(text, [extract_last_name, extract_first_name])


SEARCH_MAX_FILTERS = [extract_categories, extract_address_by_all]
extract_last_name(ROMANIAN_NAMES[0])

CATEGORY_TO_FILTER = {
    FieldCategories.ID.value: extract_cnp,
    FieldCategories.DRIVING_LICENSE_NR.value: extract_driving_license_number,
    FieldCategories.DATE.value: extract_date,
    FieldCategories.ISSUED_BY.value: extract_issued_by_all,
    FieldCategories.ADDRESS.value: extract_address_by_all,
    FieldCategories.NAME.value: extract_name_by_all,
    FieldCategories.DRIVING_CATEGORIES.value: extract_categories,
    FieldCategories.ID_SERIES.value: extract_cnp_series,
    FieldCategories.ID_NR.value: extract_cnp_nr,
    FieldCategories.NATIONALITY.value: extract_nationality
}
