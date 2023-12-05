import builtins

from database.entities import Field
from image_ocr.image_enhancement import init_image, ENHANCEMENT_LIST, cv2_to_pillow
from tools.regex_field_filters import SEARCH_MAX_FILTERS, CATEGORY_TO_FILTER, format_field_name

TESSERACT_API = builtins.TESSERACT_API


def apply_ocr_field(cv2_image, field_filter):
    try:
        # text = pytesseract.image_to_string(cv2_image, lang="ron")

        TESSERACT_API.SetImage(cv2_to_pillow(cv2_image))
        text = TESSERACT_API.GetUTF8Text()
        ocr_text = field_filter(text)
    except Exception as e:
        x = str(e)
        ocr_text = None

    if ocr_text is None or len(ocr_text) == 0:
        return None
    return ocr_text


def test(cv2_image, filter_ocr, enhancement_list):
    print("###########################")
    for enhancement_method in enhancement_list:
        ocr_text = apply_ocr_field(enhancement_method(cv2_image), filter_ocr)
        print("FILTER: ", filter_ocr, ", ENHANCE: ", enhancement_method, ", RESULT FILTERED TEXT: ", ocr_text)
    print("###########################")


def get_image_ocr(cv2_image, filter_ocr, enhancement_list):
    # test(cv2_image, filter_ocr, ENHANCEMENT_LIST)
    best_ocr_text = None
    ocr_text = apply_ocr_field(cv2_image, filter_ocr)
    if ocr_text is not None:
        if filter_ocr not in SEARCH_MAX_FILTERS:
            return ocr_text
        best_ocr_text = ocr_text

    cv2_image = init_image(cv2_image)
    best_enhancement = None
    for enhancement_method in enhancement_list:
        # cv2.imshow("cropped", enhancement_method(cv2_image))
        # cv2.waitKey()

        ocr_text = apply_ocr_field(enhancement_method(cv2_image), filter_ocr)
        if ocr_text is not None:
            if filter_ocr not in SEARCH_MAX_FILTERS:
                enhancement_list.remove(enhancement_method)
                enhancement_list[:] = [enhancement_method] + enhancement_list
                return ocr_text

            if best_ocr_text is None or len(best_ocr_text) < len(ocr_text):
                best_enhancement = enhancement_method
                best_ocr_text = ocr_text

    if best_enhancement is not None:
        enhancement_list.remove(best_enhancement)
        enhancement_list[:] = [best_enhancement] + enhancement_list
    return best_ocr_text


def get_document_ocr(document_match):
    try:
        cv2_image = document_match["image_aligned"]
        document_ocr = {"document_type": document_match["document_type"].name, "fields": {}}
        enhancement_list = ENHANCEMENT_LIST.copy()

        page = document_match["document_type"]
        fields = Field.query.filter(Field.page_id == page.id).all()

        for field_info in fields:
            field_image = cv2_image[field_info.p2:field_info.p4, field_info.p1:field_info.p3]
            if len(field_image) == 0:
                continue
            # cv2.imshow(field_info.category, field_image)
            # cv2.waitKey()
            # continue

            # a = time.time()
            filter_ocr = CATEGORY_TO_FILTER.get(field_info.category, None)
            if filter_ocr is None:
                ocr_text = None
            else:
                try:
                    ocr_text = get_image_ocr(field_image, filter_ocr, enhancement_list)
                except:
                    ocr_text = None
            # print("OCR FIELD: ", field_info.name, " time ", time.time() - a)

            if ocr_text is not None:
                document_ocr["fields"][format_field_name(field_info.name)] = {
                    "name": field_info.name,
                    "text": ocr_text,
                    "sensitive": field_info.sensitive
                }
                # print(ocr_text)
                # cv2.imshow("cropped", field_image)
                # cv2.waitKey()

        # print("##############################")

        fields_data = document_ocr.get("fields", {})  # Get the 'fields' sub-dictionary
        simplified_fields = {field_name: field_data["text"] for field_name, field_data in fields_data.items()}
        return simplified_fields
    except Exception as e:
        x = str(e)
        return {}
