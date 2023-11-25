import builtins
import json
from http import HTTPStatus

import waitress
from flask_restful import Api
from paste.translogger import TransLogger

from doc_ocr_api.document_ocr_handler import DocumentOCRHandler
from doc_ocr_api.static_handler import StaticTemplateFilesHandler, StaticTestFilesHandler
from doc_ocr_api.templates_handler import APITemplatesHandler, APITemplateHandler
from doc_ocr_api.templates_info_handler import APITemplatesInfoHandler
from doc_ocr_api.users_handler import APIUserRegisterHandler, APIUserLoginHandler, APIUsersViewHandler, APIUserAccountHandler
from tools.jwt_utils import generate_jwt, refresh_jwt

app = builtins.app


@app.after_request
def after_request(response):
    response.headers.add('Access-Control-Allow-Origin', '*')
    response.headers.add('Access-Control-Allow-Headers', 'Content-Type,Authorization,x-access-token')
    response.headers.add('Access-Control-Allow-Methods', 'GET,PUT,PATCH,POST,DELETE')
    response.headers.add('Access-Control-Expose-Headers', 'x-access-token')

    is_new_token = False
    try:
        data_json = response.get_json()
        if data_json.get("init_jwt", False) is True:
            data_json.pop("init_jwt")
            init_jwt = generate_jwt({
                "id": data_json["user"]["id"]
            })

            if init_jwt is None:
                return builtins.app.response_class(
                    response="Failed to generate a token",
                    status=HTTPStatus.OK,
                    mimetype='text'
                )

            response.headers['x-access-token'] = init_jwt
            response.data = json.dumps(data_json)
            is_new_token = True
    except:
        pass

    if not is_new_token:
        try:
            refreshed_jwt = refresh_jwt(response.headers['x-access-token'])
            if refreshed_jwt is not None:
                response.headers['x-access-token'] = refreshed_jwt
        except:
            pass
    return response


def set_api_resources():
    api = Api(app)
    api.add_resource(StaticTemplateFilesHandler, '/static/templates/<resource_path>')
    api.add_resource(StaticTestFilesHandler, '/static/test/<resource_path>')

    api.add_resource(APIUserRegisterHandler, '/register')
    api.add_resource(APIUserLoginHandler, '/login')
    api.add_resource(APIUsersViewHandler, '/users')
    api.add_resource(APIUserAccountHandler, '/users/<resource_id>')

    api.add_resource(APITemplatesHandler, '/templates')
    api.add_resource(APITemplatesInfoHandler, '/templates/info')
    api.add_resource(APITemplateHandler, '/templates/<resource_id>')

    api.add_resource(DocumentOCRHandler, '/ocr')


def start_api(use_port):
    set_api_resources()

    try:
        print("Server started")
        waitress.serve(TransLogger(app), port=use_port, threads=10)
    except:
        print("Failed to start server")
