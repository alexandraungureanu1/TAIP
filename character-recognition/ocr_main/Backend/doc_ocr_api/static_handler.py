import builtins

from flask import send_from_directory
from flask_restful import Resource

from tools.jwt_utils import check_user_authorization, jwt_token_required


class StaticTemplateFilesHandler(Resource):
    def __init__(self):
        pass

    @jwt_token_required
    def get(requester_id, self, resource_path):
        _, error_msg, error_code = check_user_authorization(requester_id, True)
        if error_msg is not None and error_code is not None:
            return error_msg, error_code

        return send_from_directory('../static/templates', resource_path)


class StaticTestFilesHandler(Resource):
    def __init__(self):
        pass

    # @jwt_token_required
    def get(self, resource_path):
        # _, error_msg, error_code = check_user_authorization(requester_id, True)
        # if error_msg is not None and error_code is not None:
        # return error_msg, error_code

        return send_from_directory('../static/test', resource_path)
