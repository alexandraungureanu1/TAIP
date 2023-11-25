import builtins
import json
from http import HTTPStatus

from flask import request
from flask_restful import Resource
from sqlalchemy.exc import IntegrityError

from database.entities import User
from tools.api_macros import AccountRoles
from tools.common_utils import to_sha256, check_email
from tools.jwt_utils import jwt_token_required, check_user_authorization


def check_user_account(user_dict, check_stage):
    if user_dict is None or type(user_dict) is not dict:
        return False, "Invalid JSON"

    for st in ["name", "password"]:
        try:
            obj = user_dict[st]
            assert type(obj) is str and 0 < len(obj) < User.__dict__[st].type.length
        except:
            return False, "Invalid user %s" % st

    if check_stage == "register":
        try:
            if not check_email(user_dict["mail"]):
                raise Exception
        except:
            return False, "Invalid user email"
    return True, None


class APIUserRegisterHandler(Resource):
    def __init__(self):
        pass

    def post(self):
        try:
            user_json = request.get_json()
        except:
            return "Only JSON is accepted", HTTPStatus.BAD_REQUEST

        is_valid, error_msg = check_user_account(user_json, check_stage="register")
        if not is_valid:
            return error_msg, HTTPStatus.BAD_REQUEST

        user = User(user_json)
        user.role = AccountRoles.USER.value
        try:
            builtins.database.session.add(user)
            builtins.database.session.commit()
        except IntegrityError:
            return "User name or email already exists", HTTPStatus.CONFLICT
        except:
            return "Failure. Try again later", HTTPStatus.INTERNAL_SERVER_ERROR

        return builtins.app.response_class(response=json.dumps({
            "user": {
                "id": str(user.id),
                "name": user.name,
                "mail": user.mail,
                "role": user.role
            },
            "init_jwt": True
        }), status=HTTPStatus.OK, mimetype='application/json')


class APIUserLoginHandler(Resource):
    def __init__(self):
        pass

    def post(self):
        try:
            user_json = request.get_json()
        except:
            return "Only JSON is accepted", HTTPStatus.BAD_REQUEST

        is_valid, error_msg = check_user_account(user_json, check_stage="login")
        if not is_valid:
            return error_msg, HTTPStatus.BAD_REQUEST

        user_temp = User(user_json)
        try:
            user = User.query.filter(User.name == user_temp.name, User.password == user_temp.password).first()
        except:
            return "Failure. Try again later", HTTPStatus.INTERNAL_SERVER_ERROR

        if user is None:
            return "Invalid user credentials", HTTPStatus.UNAUTHORIZED
        return builtins.app.response_class(response=json.dumps({
            "user": {
                "id": str(user.id),
                "name": user.name,
                "mail": user.mail,
                "role": user.role
            },
            "init_jwt": True
        }), status=HTTPStatus.OK, mimetype='application/json')


class APIUsersViewHandler(Resource):
    def __init__(self):
        pass

    @jwt_token_required
    def get(requester_id, self):
        _, error_msg, error_code = check_user_authorization(requester_id, True)
        if error_msg is not None and error_code is not None:
            return error_msg, error_code

        try:
            params = {
                "page": int(request.args.get("page", 0)),
                "size": int(request.args.get("size", None)),
                "filter": request.args.get("filter", None)
            }

            if params["filter"] is not None:
                params["filter"] = str(params["filter"])

            assert (params["page"] >= 0)
            assert (params["size"] > 0)
            assert (params["filter"] is None or type(params["filter"]) is str)
        except:
            return "Invalid search parameters", 400

        try:
            q = User.query
            if params["filter"] is not None:
                q = q.filter(User.name.like("%{}%".format(params["filter"])))
            q = q.offset(params["page"] * params["size"]).limit(params["size"])

            users = [{
                "id": str(u.id),
                "name": u.name,
                "mail": u.mail,
                "role": u.role
            } for u in q.all()]
        except:
            return "Failure. Try again later", HTTPStatus.INTERNAL_SERVER_ERROR

        return builtins.app.response_class(response=json.dumps({
            "users": users
        }), status=HTTPStatus.OK, mimetype='application/json')


class APIUserAccountHandler(Resource):
    def __init__(self):
        pass

    @jwt_token_required
    def get(requester_id, self, resource_id):
        user, error_msg, error_code = check_user_authorization(requester_id, False)
        if error_msg is not None and error_code is not None:
            return error_msg, error_code

        try:
            if user.role != AccountRoles.ADMIN.value:
                assert (str(requester_id) == str(resource_id))
            else:
                user, _, _ = check_user_authorization(str(resource_id), False)
                if user is None:
                    return "Resource not found", HTTPStatus.NOT_FOUND
        except:
            return "Forbidden. Can't view this resource", HTTPStatus.FORBIDDEN

        try:
            user_object = User.query.filter(User.id == resource_id).first()
        except:
            return "Failure. Try again later", HTTPStatus.INTERNAL_SERVER_ERROR
        if user_object is None:
            return "Resource not found", HTTPStatus.NOT_FOUND

        return builtins.app.response_class(response=json.dumps({
            "user": {
                "id": str(user_object.id),
                "name": user_object.name,
                "mail": user_object.mail,
                "role": user_object.role
            }
        }), status=HTTPStatus.OK, mimetype='application/json')

    @jwt_token_required
    def patch(requester_id, self, resource_id):
        try:
            user_json_temp = request.get_json()
            user_json = {}

            for f in ["name", "password", "mail", "role"]:
                if user_json_temp.get(f, None) is not None:
                    user_json[f] = user_json_temp[f]
        except:
            return "Only JSON is accepted", HTTPStatus.BAD_REQUEST
        user, error_msg, error_code = check_user_authorization(requester_id, user_json.get("role", None) is not None)
        if error_msg is not None and error_code is not None:
            return error_msg, error_code

        try:
            if user.role != AccountRoles.ADMIN.value:
                assert (str(requester_id) == str(resource_id))
            else:
                user, _, _ = check_user_authorization(str(resource_id), False)
                if user is None:
                    return "Resource not found", HTTPStatus.NOT_FOUND
        except:
            return "Forbidden. Can't change this resource", HTTPStatus.FORBIDDEN
        if len(user_json.keys()) == 0:
            return "No relevant data received in JSON", HTTPStatus.BAD_REQUEST

        for st in ["name", "password"]:
            if user_json.get(st, None) is not None:
                try:
                    obj = user_json[st]
                    assert type(obj) is str and 0 < len(obj) < User.__dict__[st].type.length

                    if st == "password":
                        user_json[st] = to_sha256(user_json[st])
                except:
                    return "Invalid user %s" % st, HTTPStatus.BAD_REQUEST

        if user_json.get("role", None) is not None:
            try:
                role = user_json["role"]
                assert type(role) is str and role in [r.value for r in AccountRoles] and 0 < len(
                    role) < User.role.type.length
            except:
                return "Invalid user assigned role", HTTPStatus.BAD_REQUEST

        if user_json.get("mail", None) is not None:
            try:
                if not check_email(user_json["mail"]):
                    raise Exception
            except:
                return "Invalid user email", HTTPStatus.BAD_REQUEST

        try:
            for st in ["name", "password", "mail", "role"]:
                if user_json.get(st, None) is not None:
                    user.__setattr__(st, user_json[st])

            builtins.database.session.commit()
        except IntegrityError:
            return "User name or email already exists", HTTPStatus.CONFLICT
        except:
            return "Failure. Try again later", HTTPStatus.INTERNAL_SERVER_ERROR
        return "Success", 200

    @jwt_token_required
    def delete(requester_id, self, resource_id):
        user, error_msg, error_code = check_user_authorization(requester_id, False)
        if error_msg is not None and error_code is not None:
            return error_msg, error_code
        try:
            if user.role != AccountRoles.ADMIN.value:
                assert (str(requester_id) == str(resource_id))
            else:
                user, _, _ = check_user_authorization(str(resource_id), False)
                if user is None:
                    return "Resource not found", HTTPStatus.NOT_FOUND
        except:
            return "Forbidden. Can't change this resource", HTTPStatus.FORBIDDEN

        try:
            builtins.database.session.delete(user)
            builtins.database.session.commit()
        except:
            return "Failure. Try again later", HTTPStatus.INTERNAL_SERVER_ERROR
        return "Success", 200
