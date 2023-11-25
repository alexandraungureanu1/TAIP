import datetime
from functools import wraps
from http import HTTPStatus

import jwt
from flask import request

from database.entities import User
from tools.api_macros import API_JWT_SECRET_KEY, API_JWT_EXP_MIN, API_JWT_REFRESH_MIN, AccountRoles


def generate_jwt(payload_dict):
    if payload_dict is None or type(payload_dict) is not dict:
        payload_dict = {}

    try:
        return jwt.encode({
            **payload_dict,
            'exp': datetime.datetime.utcnow() + datetime.timedelta(minutes=API_JWT_EXP_MIN)
        }, API_JWT_SECRET_KEY)
    except:
        return None


def refresh_jwt(jwt_token):
    try:
        jwt_dict = jwt.decode(jwt_token, API_JWT_SECRET_KEY, algorithms=['HS256'])
    except:
        return None

    exp_timestamp = jwt_dict["exp"]
    now = datetime.datetime.now(datetime.timezone.utc)
    target_timestamp = datetime.datetime.timestamp(now + datetime.timedelta(minutes=API_JWT_REFRESH_MIN))

    if target_timestamp > exp_timestamp:
        jwt_dict.pop("exp")
        return generate_jwt(jwt_dict)
    return jwt_token


def jwt_token_required(wrapped_func):
    @wraps(wrapped_func)
    def decorated_func(*args, **kwargs):
        jwt_token = None
        if 'x-access-token' in request.headers:
            jwt_token = request.headers['x-access-token']
        if not jwt_token:
            return "Unauthorized. Missing token", HTTPStatus.UNAUTHORIZED

        try:
            jwt_dict = jwt.decode(jwt_token, API_JWT_SECRET_KEY, algorithms=['HS256'])
            user_id = jwt_dict["id"]
        except:
            return "Unauthorized. Expired token", HTTPStatus.UNAUTHORIZED
        return wrapped_func(user_id, *args, **kwargs)
    return decorated_func

def check_user_authorization(user_id, check_admin):
    try:
        user = User.query.filter(User.id == user_id).first()
    except:
        return None, "Failure. Try again later", HTTPStatus.INTERNAL_SERVER_ERROR

    if user is None:
        return None, "Unauthorized. User not found", HTTPStatus.UNAUTHORIZED

    if check_admin:
        if user.role != AccountRoles.ADMIN.value:
            return user, "Forbidden. Admin privileges are needed", HTTPStatus.FORBIDDEN
    return user, None, None
