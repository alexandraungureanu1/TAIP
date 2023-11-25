import builtins
import json
from http import HTTPStatus

from flask_restful import Resource

from tools.api_macros import FIELD_CATEGORIES_INFO
from tools.jwt_utils import check_user_authorization, jwt_token_required


class APITemplatesInfoHandler(Resource):
    def __init__(self):
        pass

    @jwt_token_required
    def get(requester_id, self):
        _, error_msg, error_code = check_user_authorization(requester_id, True)
        if error_msg is not None and error_code is not None:
            return error_msg, error_code

        return builtins.app.response_class(response=json.dumps(
            FIELD_CATEGORIES_INFO
        ), status=HTTPStatus.OK, mimetype='application/json')
