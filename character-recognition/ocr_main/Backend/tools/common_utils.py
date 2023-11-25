import hashlib
import json
import re
from hashlib import sha256


REGEX_MAIL = r'\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,7}\b'


def to_sha256(string):
    if string is None or type(string) is not str:
        string = ""
    return sha256(string.encode('utf-8')).hexdigest()


def to_md5(string):
    if string is None or type(string) is not str:
        string = ""
    return hashlib.md5(string.encode('utf-8')).hexdigest()


def check_email(email):
    return re.fullmatch(REGEX_MAIL, email)


def load_json_file_to_dict(file_path):
    with open(file_path) as json_file:
        return json.load(json_file)


MAX_SIZE_MB_INPUT_IMG = 10


def check_image_size_input(img_size):
    return img_size < 1024 * 1024 * MAX_SIZE_MB_INPUT_IMG
