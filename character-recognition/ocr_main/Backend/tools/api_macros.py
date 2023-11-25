from enum import Enum

import cv2

from tools.common_utils import to_sha256, load_json_file_to_dict

try:
    CONFIG_DICT = load_json_file_to_dict("tools/backend_api_config.json")
    assert type(CONFIG_DICT) is dict
except:
    CONFIG_DICT = {}


class AccountRoles(Enum):
    ADMIN = 'admin'
    USER = 'user'


ALLOWED_EXTENSIONS = ['png', 'jpg', 'jpeg']
PREVIEW_WIDTH = 200
STATIC_TEMPLATE_PATH = "/static/templates/"
FACE_CASCADE = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')

SERVER_PORT = '5053'
IMAGE_HOST = 'http://localhost:' + SERVER_PORT
SQL_ALCHEMY_DATABASE_URI = CONFIG_DICT.get("SQL_ALCHEMY_DATABASE_URI", None)
BASE_ADMIN = {
    "name": CONFIG_DICT.get("BASE_ADMIN_NAME", None),
    "password": to_sha256(CONFIG_DICT.get("BASE_ADMIN_PASSWORD", None)),
    "mail": CONFIG_DICT.get("BASE_ADMIN_MAIL", None),
    "role": AccountRoles.ADMIN.value
}

API_JWT_SECRET_KEY = CONFIG_DICT.get("API_JWT_SECRET_KEY", None)
API_JWT_EXP_MIN = 24 * 60 * 30
API_JWT_REFRESH_MIN = API_JWT_EXP_MIN // 2

EMAIL_ADDRESS = CONFIG_DICT.get("EMAIL_ADDRESS", None)
EMAIL_PASSWORD = CONFIG_DICT.get("EMAIL_APP_PASSWORD", None)


class FieldSets(Enum):
    ALL = 'All existing fields'
    COMMON = 'Most common fields'
    ID_CARD = "ID Card fields"
    DRIVING_LICENSE = "Driving License fields"


class FieldCategories(Enum):
    NAME = "Name"
    ADDRESS = "Address"
    ID = "Personal ID"
    ID_SERIES = "Personal ID Series"
    ID_NR = "Personal ID Number"
    NATIONALITY = "Nationality"
    ISSUED_BY = "Issuer Authority"
    ################################################
    DATE = "Date"
    DRIVING_LICENSE_NR = "Driving License Number"
    DRIVING_CATEGORIES = "Driving Categories"


FIELD_CATEGORIES_INFO = {
    FieldSets.ALL.value: [c.value for c in FieldCategories],
    FieldSets.COMMON.value: [
        FieldCategories.NAME.value, FieldCategories.ADDRESS.value, FieldCategories.ID.value,
        FieldCategories.DATE.value, FieldCategories.ISSUED_BY.value
    ],
    FieldSets.ID_CARD.value: [
        FieldCategories.NAME.value, FieldCategories.ADDRESS.value, FieldCategories.ID.value,
        FieldCategories.ID_SERIES.value, FieldCategories.ID_NR.value, FieldCategories.NATIONALITY.value,
        FieldCategories.ISSUED_BY.value
    ],
    FieldSets.DRIVING_LICENSE.value: [
        FieldCategories.NAME.value, FieldCategories.DATE.value, FieldCategories.ADDRESS.value,
        FieldCategories.ISSUED_BY.value, FieldCategories.ID.value, FieldCategories.DRIVING_LICENSE_NR.value,
        FieldCategories.DRIVING_CATEGORIES.value
    ],
    "default": FieldSets.ALL.value
}
