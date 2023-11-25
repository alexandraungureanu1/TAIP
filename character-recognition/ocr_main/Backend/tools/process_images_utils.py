import base64
import io

import cv2
import magic
import numpy as np
import requests
from PIL import Image

from tools.api_macros import PREVIEW_WIDTH, STATIC_TEMPLATE_PATH, FACE_CASCADE


def base64_to_cv2_image(base64_image):
    return bytes_to_cv2_image(base64.b64decode(base64_image.split(';base64,')[-1]))


def bytes_to_cv2_image(bytes_image):
    return len(bytes_image), cv2.imdecode(
        np.fromstring(bytes_image, np.uint8),
        cv2.IMREAD_COLOR
    )


def detect_faces_open_cv(frame, zoom_height=300, zoom_width=None, debug=False):
    input_frame = frame.copy()
    frame_height = input_frame.shape[0]
    frame_width = input_frame.shape[1]
    if not zoom_width:
        zoom_width = int((frame_width / frame_height) * zoom_height)

    scale_height = frame_height / zoom_height
    scale_width = frame_width / zoom_width
    frame_opencv_small = cv2.resize(input_frame, (zoom_width, zoom_height))
    frame_gray = cv2.cvtColor(frame_opencv_small, cv2.COLOR_BGR2GRAY)

    faces = FACE_CASCADE.detectMultiScale(frame_gray)
    for (x, y, w, h) in faces:
        x1 = x - (1 // 3) * x
        y1 = y - (1 // 2) * y
        x2 = x + w + (1 // 3) * x
        y2 = y + h + (1 // 2) * y
        cv_rectangle = [
            int(x1 * scale_width), int(y1 * scale_height),
            int(x2 * scale_width), int(y2 * scale_height)
        ]

        if debug:
            cv2.rectangle(
                input_frame, (cv_rectangle[0], cv_rectangle[1]), (cv_rectangle[2], cv_rectangle[3]),
                (0, 255, 0),
                int(round(frame_height / 150)), 4
            )
        else:
            cv2.rectangle(
                input_frame,
                (cv_rectangle[0], cv_rectangle[1]), (cv_rectangle[2], cv_rectangle[3]),
                (255, 255, 255),
                -1
            )
    return input_frame


def crop_sections(frame, fields):
    input_frame = frame.copy()
    for field in fields:
        cv2.rectangle(
            input_frame,
            (field['p1'], field['p2']), (field['p3'], field['p4']),
            (255, 255, 255),
            -1
        )
    return input_frame


def process_image(image_bytes, ext, fields):
    if "." not in ext:
        ext = "." + ext.lower()

    image_cv2 = cv2.imdecode(np.frombuffer(image_bytes, np.uint8), -1)
    try:
        image_cv2 = detect_faces_open_cv(image_cv2)
    except:
        pass

    try:
        image_cv2 = crop_sections(image_cv2, fields)
    except Exception as e:
        x = str(e)
        pass
    return cv2.imencode(ext, image_cv2)[1]


def pillow_resize_from_bytes_ratio(image_bytes, width=PREVIEW_WIDTH):
    image_pillow = Image.open(io.BytesIO(image_bytes))
    width_percent = (width / float(image_pillow.size[0]))
    height_size = int((float(image_pillow.size[1]) * float(width_percent)))

    image_pillow = image_pillow.resize((width, height_size), Image.LANCZOS)
    image_bytes = io.BytesIO()
    image_pillow.save(image_bytes, format='PNG')
    return image_bytes.getvalue()


def cv2_resize_ratio(cv2_image, width=None, height=None, inter=cv2.INTER_AREA, get_resolution=False):
    (h, w) = cv2_image.shape[:2]

    if width is None and height is None:
        if get_resolution:
            return w, h, cv2_image
        return cv2_image

    if width is None:
        ratio = height / float(h)
        resolution = (int(w * ratio), height)
    else:
        ratio = width / float(w)
        resolution = (width, int(h * ratio))

    image_resize = cv2.resize(cv2_image, resolution, interpolation=inter)
    if get_resolution:
        image_shape = image_resize.shape[:2]
        return image_shape[1], image_shape[0], image_resize
    return image_resize


from mimetypes import guess_extension, guess_type


def base64_to_bytes(image_content, is_url=True):
    if is_url:
        base64_content = base64.b64encode(requests.get(image_content).content).decode('utf-8')
        ext = None
    else:
        base64_content = image_content
        ext = guess_extension(guess_type(base64_content)[0]).split(".")[-1]
        base64_content = base64_content.split(";base64,")[-1]
    bytes_content = base64.b64decode(base64_content.encode())
    return bytes_content, ext


def bytes_to_extension(image_bytes):
    mime_type = magic.from_buffer(image_bytes, mime=True)
    if "image/" not in mime_type:
        return None
    return mime_type.split("/")[-1].lower()


def add_local_file_system_image(image_bytes, image_path):
    with open(".%s%s" % (STATIC_TEMPLATE_PATH, image_path), "wb") as f:
        f.write(image_bytes)


def local_image_to_cv2_image(local_path):
    with open(".%s%s" % (STATIC_TEMPLATE_PATH, local_path), "rb") as f:
        return bytes_to_cv2_image(f.read())[1]
