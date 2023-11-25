import builtins

import pytesseract
from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import create_engine
from sqlalchemy.orm import declarative_base
from tesserocr._tesserocr import PyTessBaseAPI

from tools.api_macros import *

pytesseract.pytesseract.tesseract_cmd = r'C:\Program Files\Tesseract-OCR\tesseract.exe'
TESSERACT_PATH = 'C:/Program Files/Tesseract-OCR/tessdata/'
TESSERACT_API = PyTessBaseAPI(path=TESSERACT_PATH, lang='ron')
builtins.TESSERACT_API = TESSERACT_API

builtins.app = Flask(__name__)
builtins.app.config['SQLALCHEMY_DATABASE_URI'] = SQL_ALCHEMY_DATABASE_URI
builtins.app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

builtins.Base = declarative_base()
builtins.database = SQLAlchemy(builtins.app)
builtins.engine = create_engine(SQL_ALCHEMY_DATABASE_URI)

from database.entities import *

with builtins.app.app_context():
    builtins.database.create_all()

from database.entities import User


def init_database():
    admin = None
    try:
        with builtins.app.app_context():
            admin = User.query.filter(User.role == "admin").first()
    except Exception as e:
        print("Ini setup 38\n")
        print(e)
        pass

    if admin is None:
        admin = User(BASE_ADMIN)
        try:
            with builtins.app.app_context():
                builtins.database.session.add(admin)
                builtins.database.session.commit()
        except Exception as e:
            print("Ini setup 47\n")
            print(e)
            print("Couldn't set a base admin for the backend system... Aborting system startup.")
            exit(0)
