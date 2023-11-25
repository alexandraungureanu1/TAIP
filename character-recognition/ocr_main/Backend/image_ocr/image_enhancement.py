import cv2
import numpy
from PIL import Image, ImageOps, ImageEnhance

from tools.ocr_macros import FIELD_CROP_MIN_DIMENSION
from tools.process_images_utils import cv2_resize_ratio


def init_image(cv2_image):
    (h, w) = cv2_image.shape[:2]

    if FIELD_CROP_MIN_DIMENSION is not None:
        if h < FIELD_CROP_MIN_DIMENSION or w < FIELD_CROP_MIN_DIMENSION:
            if h < w:
                return cv2_resize_ratio(cv2_image, height=FIELD_CROP_MIN_DIMENSION)
            else:
                return cv2_resize_ratio(cv2_image, width=FIELD_CROP_MIN_DIMENSION)
    return cv2_image


def cv2_to_pillow(cv2_image):
    return Image.fromarray(cv2.cvtColor(cv2_image, cv2.COLOR_BGR2RGB))


def pillow_to_cv2(pillow_image):
    return cv2.cvtColor(numpy.array(pillow_image), cv2.COLOR_RGB2BGR)


def make_grayscale(cv2_image):
    pillow_image = cv2_to_pillow(cv2_image)
    return pillow_to_cv2(ImageOps.grayscale(pillow_image))


def brightness_enhance(cv2_image):
    pillow_image = cv2_to_pillow(cv2_image)
    enhancer = ImageEnhance.Brightness(pillow_image)
    return pillow_to_cv2(enhancer.enhance(1.1))


def sharpness_enhance(cv2_image):
    pillow_image = cv2_to_pillow(cv2_image)
    enhancer = ImageEnhance.Sharpness(pillow_image)
    return pillow_to_cv2(enhancer.enhance(4))


def contrast_enhance(cv2_image):
    pillow_image = cv2_to_pillow(cv2_image)
    enhancer = ImageEnhance.Contrast(pillow_image)
    return pillow_to_cv2(enhancer.enhance(3.0))


def color_enhance(cv2_image):
    pillow_image = cv2_to_pillow(cv2_image)
    enhancer = ImageEnhance.Color(pillow_image)
    return pillow_to_cv2(enhancer.enhance(1.5))


def binarization(cv2_image):
    _, thresh = cv2.threshold(cv2_image, 127, 255, cv2.THRESH_BINARY)
    return cv2.cvtColor(thresh, cv2.COLOR_BGR2RGB)


def noise_removal(cv2_image):
    se = cv2.getStructuringElement(cv2.MORPH_RECT, (8, 8))
    bg = cv2.morphologyEx(cv2_image, cv2.MORPH_DILATE, se)
    out_gray = cv2.divide(cv2_image, bg, scale=255)
    return out_gray


def dilatation(cv2_image):
    kernel = numpy.ones((5, 5), numpy.uint8)
    img_dilatation = cv2.dilate(cv2_image, kernel, iterations=1)
    return img_dilatation


def erosion(cv2_image):
    kernel = numpy.ones((5, 5), numpy.uint8)
    img_erosion = cv2.erode(cv2_image, kernel, iterations=1)
    return img_erosion


ENHANCEMENT_LIST = [
    binarization, erosion, make_grayscale, noise_removal, sharpness_enhance
    # ,color_enhance, contrast_enhance, brightness_enhance, dilatation
]

XCCCCCC = [
    make_grayscale, brightness_enhance, sharpness_enhance, contrast_enhance,
    color_enhance, binarization, noise_removal, dilatation, erosion
]
