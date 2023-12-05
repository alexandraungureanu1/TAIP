import builtins
import json
import time
from http import HTTPStatus
from mimetypes import guess_type, guess_extension
from threading import Thread

import cv2
from flask import Flask, request
from flask_restful import Resource

from image_ocr.document_ocr import get_document_ocr
from image_ocr.image_processing import get_document_match
from tools.api_macros import ALLOWED_EXTENSIONS
from tools.common_utils import check_image_size_input, MAX_SIZE_MB_INPUT_IMG
from tools.email_utils import send_email
from tools.jwt_utils import jwt_token_required, check_user_authorization
from tools.process_images_utils import base64_to_cv2_image

app = Flask(__name__)


class DocumentOCRHandler(Resource):
    def __init__(self):
        pass

    # @jwt_token_required
    def post(self):
        xx = time.time()
        # user, error_msg, error_code = check_user_authorization(requester_id, True)
        # if error_msg is not None and error_code is not None:
        #     return error_msg, error_code

        try:
            img_json = request.get_json()
        except:
            return "Only JSON is accepted", HTTPStatus.BAD_REQUEST

        try:
            error_msg = "Invalid image."
            img_base_64 = img_json["image"]
            ext = guess_extension(guess_type(img_base_64)[0]).split(".")[-1]
            if ext not in ALLOWED_EXTENSIONS:
                error_msg += " The only allowed extensions are: " + str(ALLOWED_EXTENSIONS).replace(
                    "[", ""
                ).replace("]", "")

                raise Exception

            img_size, img_cv2 = base64_to_cv2_image(img_base_64)
            if img_size is None or img_cv2 is None:
                error_msg += "Could not read image"
                raise Exception

            if not check_image_size_input(img_size):
                error_msg += " Size can't be greater than " + str(MAX_SIZE_MB_INPUT_IMG) + " MB"
                raise Exception
        except:
            return error_msg, HTTPStatus.BAD_REQUEST

        aa = time.time()
        doc_ocr = get_document_match(img_cv2)
        cv2.imwrite("./output.jpg", doc_ocr["image_aligned"])
        # cv2.imshow('image', doc_ocr["image_aligned"])
        # cv2.waitKey(0)
        print("OCR PROCESS TIME: ", time.time() - aa)

        bb = time.time()
        doc_ocr = get_document_ocr(doc_ocr)
        if doc_ocr is not None and type(doc_ocr) is not dict:
            return "Failure. Try again later", HTTPStatus.INTERNAL_SERVER_ERROR
        print("OCR OCR TIME: ", time.time() - bb)

        # Thread(target=send_email, args=(user.mail, doc_ocr)).start()
        # send_email(user.mail, doc_ocr)

        if doc_ocr is None or len(doc_ocr.keys()) == 0:
            return "Couldn't extract any text. Try again", HTTPStatus.BAD_REQUEST
        print("TOTAL OCR TIME: ", time.time() - xx)
        return builtins.app.response_class(
            response=json.dumps(doc_ocr),
            status=200,
            mimetype='application/json'
        )
