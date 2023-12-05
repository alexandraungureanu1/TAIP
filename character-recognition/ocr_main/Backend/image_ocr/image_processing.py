import numpy as np
import time
import cv2
from sewar.full_ref import rase, ssim

from database.entities import Template, Page
from tools.ocr_macros import MIN_SSIM_SCORE, MIN_RASE_SCORE, BATCH_TEMPLATES_SIZE, IMAGE_ALIGNED_WIDTH
from tools.process_images_utils import local_image_to_cv2_image, cv2_resize_ratio


def reorder(my_points):
    my_points = my_points.reshape((4, 2))
    my_points_new = np.zeros((4, 1, 2), dtype=np.int32)
    add = my_points.sum(1)

    my_points_new[0] = my_points[np.argmin(add)]
    my_points_new[3] = my_points[np.argmax(add)]
    diff = np.diff(my_points, axis=1)

    my_points_new[1] = my_points[np.argmin(diff)]
    my_points_new[2] = my_points[np.argmax(diff)]
    return my_points_new


def max_contour(contours):
    biggest = np.array([])
    max_area = 0
    for i in contours:
        area = cv2.contourArea(i)

        if area > 5000:
            peri = cv2.arcLength(i, True)
            approx = cv2.approxPolyDP(i, 0.02 * peri, True)

            if area > max_area and len(approx) == 4:
                biggest = approx
                max_area = area
    return biggest, max_area


def copy_size(cv2_template, cv2_image):
    (h1, w1, _) = cv2_template.shape
    (h2, w2, _) = cv2_image.shape
    if h1 != h2 or w1 != w2:
        cv2_image = cv2.resize(cv2_image, (w1, h1))
    return cv2_image


def get_match_score(cv2_original, cv2_altered):
    try:
        if cv2_original is None or cv2_altered is None:
            return None

        cv2_image = copy_size(cv2_original, cv2_altered)
        t = time.time()

        result = -rase(cv2_original, cv2_image)
        print("RASE ", time.time() - t)
        return result
    except:
        return None


def check_ssim(cv2_original, cv2_altered):
    try:
        if cv2_original is None or cv2_altered is None:
            return False

        cv2_image = copy_size(cv2_original, cv2_altered)
        t = time.time()

        result = ssim(cv2_original, cv2_image)[0] >= MIN_SSIM_SCORE
        print("SSIM", time.time() - t)
        return result
    except:
        return False


def get_image_aligned(cv2_template, cv2_image, th_step=5, pixels_remove=0, apply_align=True):
    try:
        if not apply_align:
            cv2_image = copy_size(cv2_template, cv2_image)
            return cv2_image, get_match_score(cv2_template, cv2_image)

        # (height_img, width_img, _) = (600, 700, 3)
        # cv2_image = cv2.resize(cv2_image, (width_img, height_img))
        width_img, height_img, cv2_image = cv2_resize_ratio(
            cv2_image=cv2_image, width=IMAGE_ALIGNED_WIDTH, get_resolution=True
        )
        max_warp_score = None

        for threshold in range(0, 120, th_step):
            img_gray = cv2.cvtColor(cv2_image, cv2.COLOR_BGR2GRAY)
            img_blur = cv2.GaussianBlur(img_gray, (5, 5), 1)

            img_threshold = cv2.Canny(img_blur, threshold, 0)
            kernel = np.ones((5, 5))
            img_dial = cv2.dilate(img_threshold, kernel, iterations=2)
            img_threshold = cv2.erode(img_dial, kernel, iterations=1)

            contours, hierarchy = cv2.findContours(img_threshold, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
            biggest, maxArea = max_contour(contours)

            if biggest.size != 0:
                biggest = reorder(biggest)

                pts1 = np.float32(biggest)
                pts2 = np.float32([[0, 0], [width_img, 0], [0, height_img], [width_img, height_img]])
                matrix = cv2.getPerspectiveTransform(pts1, pts2)

                img_warped = cv2.warpPerspective(cv2_image, matrix, (width_img, height_img))
                img_warped = img_warped[pixels_remove:img_warped.shape[0] - pixels_remove,
                             pixels_remove:img_warped.shape[1] - pixels_remove]

                match_score = get_match_score(cv2_template, img_warped)
                if max_warp_score is None or (match_score is not None and max_warp_score[1] < match_score):
                    max_warp_score = (img_warped, match_score)

        if max_warp_score is None:
            max_warp_score = None, None
        return max_warp_score
    except Exception as e:
        return None, None


def get_document_match(image):
    try:
        max_match = None
        cv2_image = image
        if type(image) is str:
            cv2_image = cv2.imread(image)

        current_page = -1
        while True:
            current_page += 1
            q_template = Template.query.offset(current_page * BATCH_TEMPLATES_SIZE).limit(BATCH_TEMPLATES_SIZE)

            for template in q_template.all():
                pages = Page.query.filter(Page.template_id == template.id).all()

                for page in pages:
                    image_processed_low = local_image_to_cv2_image(page.image_processed_low)
                    image_processed_high = local_image_to_cv2_image(page.image_processed)

                    cv2_template = image_processed_low.copy()
                    cv2_image_aligned, match_score = get_image_aligned(
                        cv2_template, cv2_image, apply_align=True
                    )

                    if max_match is None or (match_score is not None and match_score > max_match["match_score"]):
                        cv2_template = image_processed_high

                        max_match = {
                            "document_type": page,
                            "image_aligned": cv2.resize(
                                cv2_image_aligned,
                                (cv2_template.shape[1], cv2_template.shape[0])
                            ),
                            "low": image_processed_low,
                            "high": image_processed_high,
                            "match_score": match_score
                        }

            if q_template.count() < BATCH_TEMPLATES_SIZE:
                break

        if max_match is None:
            return None
        if not check_ssim(max_match["low"], max_match["image_aligned"]):
            return None
        if max_match["match_score"] < MIN_RASE_SCORE:
            return None
        return {
            "document_type": max_match["document_type"],
            "image_aligned": max_match["image_aligned"]
        }
    except Exception as e:
        raise e
