import builtins
import json
import os
from http import HTTPStatus

import time
from flask import request
from flask_restful import Resource
from sqlalchemy.exc import IntegrityError

from database.entities import Template, Field, Page
from tools.api_macros import ALLOWED_EXTENSIONS, STATIC_TEMPLATE_PATH, IMAGE_HOST
from tools.common_utils import to_md5
from tools.jwt_utils import check_user_authorization, jwt_token_required
from tools.ocr_macros import PROCESSED_LOW_WIDTH
from tools.process_images_utils import add_local_file_system_image, base64_to_bytes, pillow_resize_from_bytes_ratio, \
    process_image, bytes_to_extension


class APITemplatesHandler(Resource):
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
            q = Template.query
            if params["filter"] is not None:
                q = q.filter(Template.name.like("%{}%".format(params["filter"])))
            q = q.offset(params["page"] * params["size"]).limit(params["size"])

            templates = []
            for t in q.all():
                page = Page.query.filter(Page.template_id == t.id).first()
                template = {
                    "id": str(t.id),
                    "name": t.name,
                }
                if page is not None:
                    template["preview"] = IMAGE_HOST + STATIC_TEMPLATE_PATH + page.preview
                templates.append(template)
        except:
            return "Failure. Try again later", HTTPStatus.INTERNAL_SERVER_ERROR

        return builtins.app.response_class(response=json.dumps({
            "templates": templates
        }), status=HTTPStatus.OK, mimetype='application/json')

    @jwt_token_required
    def post(requester_id, self):
        xx = time.time()
        _, error_msg, error_code = check_user_authorization(requester_id, True)
        if error_msg is not None and error_code is not None:
            return error_msg, error_code

        try:
            template_json = request.get_json()
        except:
            return "Only JSON is accepted", HTTPStatus.BAD_REQUEST

        for st in ["name"]:
            try:
                obj = template_json[st]
                assert type(obj) is str and 0 < len(obj) < Template.__dict__[st].type.length
            except:
                return "Invalid template %s" % st, HTTPStatus.BAD_REQUEST

        try:
            page_names = {}
            for page in template_json["pages"]:
                try:
                    assert type(page["name"]) is str and 0 < len(page["name"]) < Page.name.type.length
                except:
                    return "Invalid template page name", HTTPStatus.BAD_REQUEST

                assert page_names.get(page["name"], None) is None
                page_names[page["name"]] = True

                try:
                    page["image"], page["ext"] = base64_to_bytes(page["image"], False)
                    if page["ext"] is None:
                        page["ext"] = bytes_to_extension(page["image"])
                    assert page["ext"] in ALLOWED_EXTENSIONS
                except Exception as e:
                    x = str(e)
                    return "Invalid template page image", HTTPStatus.BAD_REQUEST

                try:
                    assert type(page["fields"]) is list
                    field_names = {}

                    for f in page["fields"]:
                        assert type(f) is dict
                        assert type(f["name"]) is str and 0 < len(f["name"]) < Field.name.type.length
                        assert field_names.get(f["name"], None) is None
                        field_names[f["name"]] = True

                        assert type(f["category"]) is str and 0 < len(f["category"]) < Field.category.type.length
                        assert type(f["sensitive"]) is bool
                        for p in ["p1", "p2", "p3", "p4"]:
                            assert type(f[p]) is int
                        assert (f["p1"] != f["p3"] or f["p2"] != f["p4"])

                except:
                    return "Invalid template page fields", HTTPStatus.BAD_REQUEST
        except:
            return "Invalid template pages", HTTPStatus.BAD_REQUEST

        template_object = Template(template_json)
        try:
            builtins.database.session.add(template_object)
            builtins.database.session.commit()

            images_list = []
            for page in template_json["pages"]:
                page["template_id"] = template_object.id
                page_object = Page(page)

                try:
                    builtins.database.session.add(page_object)
                    builtins.database.session.commit()

                    for field in page["fields"]:
                        field["page_id"] = page_object.id
                        field_object = Field(field)

                        try:
                            builtins.database.session.add(field_object)
                            builtins.database.session.commit()
                        except IntegrityError:
                            return "Template page field name already exists", HTTPStatus.CONFLICT
                        except:
                            return "Failure. Try again later", HTTPStatus.INTERNAL_SERVER_ERROR
                except IntegrityError:
                    return "Template page name already exists", HTTPStatus.CONFLICT
                except:
                    return "Failure. Try again later", HTTPStatus.INTERNAL_SERVER_ERROR

                page_object.image = "%s.%s" % (to_md5("page_%s" % page_object.id), page["ext"])
                page_object.image_processed = "%s.%s" % (to_md5("page_processed_%s" % page_object.id), page["ext"])
                page_object.image_processed_low = "%s.%s" % (
                    to_md5("page_processed_low_%s" % page_object.id), page["ext"]
                )

                page_object.preview = "%s.%s" % (to_md5("page_preview_%s" % page_object.id), page["ext"])
                images_list.append((
                    page["image"], page["ext"], page_object.image,
                    page_object.image_processed, page_object.image_processed_low,
                    page_object.preview, page["fields"]
                ))
                builtins.database.session.commit()
        except IntegrityError:
            return "Template name already exists", HTTPStatus.CONFLICT
        except:
            return "Failure. Try again later", HTTPStatus.INTERNAL_SERVER_ERROR

        for image_bytes, image_ext, image_path, image_processed_path, image_processed_low_path, image_preview_path, fields in images_list:
            try:
                processed_bytes = process_image(image_bytes, image_ext, fields)
                add_local_file_system_image(processed_bytes, image_processed_path)
                add_local_file_system_image(
                    pillow_resize_from_bytes_ratio(processed_bytes, width=PROCESSED_LOW_WIDTH),
                    image_processed_low_path
                )
            except Exception as e:
                print(e)
                pass
            try:
                add_local_file_system_image(image_bytes, image_path)
            except Exception as e:
                print(e)
                pass
            try:
                add_local_file_system_image(pillow_resize_from_bytes_ratio(image_bytes), image_preview_path)
            except Exception as e:
                print(e)
                pass

        print("TEMPLATE PROCESSING: ", time.time() - xx)
        return builtins.app.response_class(response=json.dumps({
            "template": {
                "id": str(template_object.id),
                "name": template_object.name
            }
        }), status=HTTPStatus.OK, mimetype='application/json')


class APITemplateHandler(Resource):
    def __init__(self):
        pass

    @jwt_token_required
    def get(requester_id, self, resource_id):
        _, error_msg, error_code = check_user_authorization(requester_id, True)
        if error_msg is not None and error_code is not None:
            return error_msg, error_code

        try:
            template = Template.query.filter(Template.id == resource_id).first()
        except Exception as e:
            if "InvalidTextRepresentation" in str(e):
                template = None
            else:
                return "Failure. Try again later", HTTPStatus.INTERNAL_SERVER_ERROR
        if template is None:
            return "Resource not found", HTTPStatus.NOT_FOUND

        try:
            pages = Page.query.filter(Page.template_id == template.id).all()
            pages_list = []

            for page in pages:
                fields = Field.query.filter(Field.page_id == page.id).all()
                fields_list = []
                for field in fields:
                    fields_list.append({
                        "id": str(field.id),
                        "page_id": str(field.page_id),
                        "name": field.name,
                        "sensitive": field.sensitive,
                        "category": field.category,

                        "p1": field.p1,
                        "p2": field.p2,
                        "p3": field.p3,
                        "p4": field.p4
                    })

                pages_list.append({
                    "id": str(page.id),
                    "template_id": str(page.template_id),
                    "image": IMAGE_HOST + STATIC_TEMPLATE_PATH + page.image,
                    "preview": IMAGE_HOST + STATIC_TEMPLATE_PATH + page.preview,
                    "name": page.name,
                    "fields": fields_list
                })

            template_json = {
                "id": str(template.id),
                "name": template.name,
                "pages": pages_list
            }
        except:
            return "Failure. Try again later", HTTPStatus.INTERNAL_SERVER_ERROR

        return builtins.app.response_class(response=json.dumps({
            "template": template_json
        }), status=HTTPStatus.OK, mimetype='application/json')

    @jwt_token_required
    def patch(requester_id, self, resource_id):  ##TODO PATCH TEMPLATE
        _, error_msg, error_code = check_user_authorization(requester_id, True)
        if error_msg is not None and error_code is not None:
            return error_msg, error_code

        try:
            template_json = request.get_json()
        except:
            return "Only JSON is accepted", HTTPStatus.BAD_REQUEST

        try:
            template = Template.query.filter(Template.id == resource_id).first()
        except:
            return "Failure. Try again later", HTTPStatus.INTERNAL_SERVER_ERROR
        if template is None:
            return "Resource not found", HTTPStatus.NOT_FOUND

        if template_json.get("name", None) is not None:
            try:
                obj = template_json["name"]
                assert type(obj) is str and 0 < len(obj) < Template.name.type.length

                template.name = template_json["name"]
            except:
                return "Invalid template name", HTTPStatus.BAD_REQUEST

        try:
            page_names = {}
            for page in template_json.get("pages", []):
                assert type(page) is dict

                if page.get("id", None) not in [None, ""]:
                    assert (type(page["id"]) is str and len(page["id"]) > 0)
                    is_page_patch = True
                else:
                    is_page_patch = False

                try:
                    assert type(page["name"]) is str and 0 < len(page["name"]) < Page.name.type.length
                except:
                    return "Invalid template page name", HTTPStatus.BAD_REQUEST

                assert page_names.get(page["name"], None) is None
                page_names[page["name"]] = True

                if page.get("image", None) not in [None, ""]:
                    page["image"], page["ext"] = base64_to_bytes(page["image"], False)
                    if page["ext"] is None:
                        page["ext"] = bytes_to_extension(page["image"])
                    assert page["ext"] in ALLOWED_EXTENSIONS
                elif not is_page_patch:
                    raise Exception

                try:
                    assert type(page["fields"]) is list
                    field_names = {}

                    for f in page.get("fields", []):
                        assert type(f) is dict
                        if f.get("id", None) not in ["", None]:
                            assert (type(f["id"]) is str and len(f["id"]) > 0)

                        assert type(f["name"]) is str and 0 < len(f["name"]) < Field.name.type.length
                        assert field_names.get(f["name"], None) is None
                        field_names[f["name"]] = True

                        assert type(f["sensitive"]) is bool
                        for p in ["p1", "p2", "p3", "p4"]:
                            assert type(f[p]) is int
                        assert (f["p1"] != f["p3"] or f["p2"] != f["p4"])

                except:
                    return "Invalid template page fields", HTTPStatus.BAD_REQUEST
        except:
            return "Invalid template pages", HTTPStatus.BAD_REQUEST

        images_list = []
        try:
            for page in template_json.get("pages", []):
                page["template_id"] = template.id
                if page.get("id", None) not in [None, '']:
                    is_page_patch = True
                    try:
                        old_field_ids = [str(f.id) for f in Field.query.filter(Field.page_id == page["id"])]
                        new_field_ids = [f["id"] for f in page.get("fields", []) if f.get("id", None) not in [None, '']]
                        for old_field_id in old_field_ids:
                            if old_field_id not in new_field_ids:
                                try:
                                    entity = Field.query.filter(Field.id == old_field_id).first()
                                    builtins.database.session.delete(entity)
                                except:
                                    pass
                    except Exception as e:
                        x = str(e)
                        pass
                    builtins.database.session.commit()

                    page_object = Page.query.filter(Page.id == page["id"]).first()
                    page_object.clone(page)
                else:
                    is_page_patch = False
                    page_object = Page(page)

                try:
                    if not is_page_patch:
                        builtins.database.session.add(page_object)
                    builtins.database.session.commit()

                    old_page_images = []
                    if page.get("image", None) not in ["", None] and page.get("ext") is not None:
                        if is_page_patch:
                            old_page_ext = page_object.image.split(".")[-1]
                            if page["ext"] != old_page_ext:
                                old_page_images = [page_object.image, page_object.preview, page_object.image_processed,
                                                   page_object.image_processed_low]
                    else:
                        page["ext"] = page_object.image.split(".")[-1]

                    page_object.image = "%s.%s" % (to_md5("page_%s" % page_object.id), page["ext"])
                    page_object.image_processed = "%s.%s" % (
                        to_md5("page_processed_%s" % page_object.id), page["ext"]
                    )
                    page_object.image_processed_low = "%s.%s" % (
                        to_md5("page_processed_low_%s" % page_object.id), page["ext"]
                    )
                    page_object.preview = "%s.%s" % (to_md5("page_preview_%s" % page_object.id), page["ext"])
                    images_list.append((
                        page["image"], page["ext"], old_page_images, page_object.image,
                        page_object.image_processed, page_object.image_processed_low,
                        page_object.preview, page.get("fields", [])
                    ))

                    for field in page.get("fields", []):
                        field["page_id"] = page_object.id
                        if field.get("id", None) not in [None, '']:
                            is_field_patch = True
                            field_object = Field.query.filter(Field.id == field["id"]).first()
                            field_object.clone(field)
                        else:
                            is_field_patch = False
                            field_object = Field(field)

                        try:
                            if not is_field_patch:
                                builtins.database.session.add(field_object)
                            builtins.database.session.commit()
                        except IntegrityError:
                            return "Template page field name already exists", HTTPStatus.CONFLICT
                        except:
                            return "Failure. Try again later", HTTPStatus.INTERNAL_SERVER_ERROR
                except IntegrityError:
                    return "Template page name already exists", HTTPStatus.CONFLICT
                except Exception as e:
                    x = str(e)
                    return "Failure. Try again later", HTTPStatus.INTERNAL_SERVER_ERROR

            try:
                old_page_ids = [str(p.id) for p in Page.query.filter(Page.template_id == template.id)]
                new_page_ids = [p["id"] for p in template_json.get("pages", []) if p.get("id", None) not in [None, '']]
                for old_page_id in old_page_ids:
                    if old_page_id not in new_page_ids:
                        try:
                            entity = Page.query.filter(Page.id == old_page_id).first()
                            builtins.database.session.delete(entity)
                        except:
                            pass
            except Exception as e:
                x = str(e)
                pass
            builtins.database.session.commit()
        except IntegrityError:
            return "Template name already exists", HTTPStatus.CONFLICT
        except:
            return "Failure. Try again later", HTTPStatus.INTERNAL_SERVER_ERROR

        for image_bytes, image_ext, old_images, image_path, image_processed_path, image_processed_low_path, image_preview_path, fields in images_list:
            try:
                skip_image = False
                if image_bytes in [None, ""]:
                    skip_image = True
                    with open("." + STATIC_TEMPLATE_PATH + image_path, "rb") as in_file:
                        image_bytes = in_file.read()

                for old_image in old_images:
                    old_path = ".%s%s" % (STATIC_TEMPLATE_PATH, old_image)
                    if os.path.exists(old_path):
                        os.remove(old_path)

                try:
                    processed_bytes = process_image(image_bytes, image_ext, fields)
                    add_local_file_system_image(processed_bytes, image_processed_path)
                    add_local_file_system_image(
                        pillow_resize_from_bytes_ratio(processed_bytes, width=PROCESSED_LOW_WIDTH),
                        image_processed_low_path
                    )
                except Exception as e:
                    print(e)
                    pass

                if skip_image:
                    continue
                try:
                    add_local_file_system_image(image_bytes, image_path)
                except Exception as e:
                    print(e)
                    pass
                try:
                    add_local_file_system_image(pillow_resize_from_bytes_ratio(image_bytes), image_preview_path)
                except Exception as e:
                    print(e)
                    pass

            except Exception as e:
                print(e)
                pass
        return "Success", 200

    @jwt_token_required
    def delete(requester_id, self, resource_id):
        _, error_msg, error_code = check_user_authorization(requester_id, True)
        if error_msg is not None and error_code is not None:
            return error_msg, error_code

        try:
            template = Template.query.filter(Template.id == resource_id).first()
        except:
            return "Failure. Try again later", HTTPStatus.INTERNAL_SERVER_ERROR
        if template is None:
            return "Resource not found", HTTPStatus.NOT_FOUND

        try:
            try:
                pages = Page.query.filter(Page.template_id == resource_id).all()
            except:
                return "Failure. Try again later", HTTPStatus.INTERNAL_SERVER_ERROR
            if pages is None:
                pages = []

            for page in pages:
                try:
                    os.remove(".%s%s" % (STATIC_TEMPLATE_PATH, page.image))
                    os.remove(".%s%s" % (STATIC_TEMPLATE_PATH, page.image_processed))
                    os.remove(".%s%s" % (STATIC_TEMPLATE_PATH, page.image_processed_low))
                    os.remove(".%s%s" % (STATIC_TEMPLATE_PATH, page.preview))
                except Exception as e:
                    print(e)
                    pass

            builtins.database.session.delete(template)
            builtins.database.session.commit()
        except:
            return "Failure. Try again later", HTTPStatus.INTERNAL_SERVER_ERROR
        return "Success", 200
