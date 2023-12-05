import base64
import requests
import json


def encode_image():
    image_path = "./Backend/static/templates/c38898c6a1c572d1afb32bbf63697f22.jpg"
    with open(image_path, 'rb') as image_file:
        encoded_string = base64.b64encode(image_file.read()).decode('utf-8')
    return f"data:image/jpeg;base64,{encoded_string}"


def send_request():
    url = "http://localhost:5053/ocr"
    encoded_image = encode_image()
    payload = {
        "image": encoded_image
    }
    headers = {'Content-Type': 'application/json'}

    response = requests.post(url, data=json.dumps(payload), headers=headers)

    if response.status_code == 200:
        try:
            print(response.json())
        except json.JSONDecodeError:
            print(response.text)
    else:
        print(f"Error: {response.status_code}")


def send_request_to_app():
    url = "http://localhost:8082/api/verifier/nationality"
    encoded_image = encode_image()
    payload = {
        "firstName": "firstName",
        "lastName": "lastName",
        "countryCode": "countryCode",
        "email": "email",
        "personalIdentification": "personalIdentification",
        "documentIdentification": "documentIdentification",
        "encodedDocument": encoded_image
    }
    headers = {'Content-Type': 'application/json'}

    response = requests.post(url, data=json.dumps(payload), headers=headers)

    if response.status_code == 200:
        try:
            print(response.json())
        except json.JSONDecodeError:
            print(response.text)
    else:
        print(f"Error: {response.status_code}")


send_request_to_app()
