from database.init_setup import init_database
from doc_ocr_api.backend_api import start_api
from tools.api_macros import SERVER_PORT


if __name__ == '__main__':
    init_database()
    print("STARTING APP")
    start_api(SERVER_PORT)
