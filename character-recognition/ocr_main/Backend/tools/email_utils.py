import imaplib
import json
import smtplib
import time
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText

from tools.api_macros import EMAIL_ADDRESS, EMAIL_PASSWORD


def build_message(to_email, doc_ocr):
    message_dict = MIMEMultipart('mixed')
    message_dict['Subject'] = 'Document Identification Response'
    message_dict['From'] = EMAIL_ADDRESS
    message_dict['To'] = to_email

    send_html = False
    send_json = True
    if doc_ocr is None or type(doc_ocr) is not dict or doc_ocr.get("document_type", None) is None:
        send_json = False
        email_message = "The document's type couldn't be detected. Therefore, no text could be extracted from the uploaded document. Please try again."
    else:
        email_message = "Type of document uploaded: " + doc_ocr["document_type"] + "."
        fields = doc_ocr.get("fields", None)
        if fields is None or type(fields) is not dict or len(fields.keys()) == 0:
            doc_ocr = {"document_type": doc_ocr["document_type"]}
            email_message += " No text could be extracted from the uploaded document. Please try again."
        else:
            send_html = True

    html = None
    if send_html:
        triples = []
        fields = doc_ocr["fields"]
        for k in fields.keys():
            try:
                triples += [(fields[k]["name"], fields[k]["text"], fields[k]["sensitive"])]
            except:
                pass
        table_content = "".join(["""
                <tr>
                    <td style="border: 1px solid black; padding: 5px">{}</td>
                    <td style="border: 1px solid black; padding: 5px">{}</td>
                    <td style="border: 1px solid black; padding: 5px">{}</td>
                </tr>
                """.format(f, t, s) for f, t, s in triples])
        html = """
                <html>
                    <body>
                        <h2>Document OCR Extracted Text</h2>
                        <table style="width:100%; border: 1px solid black">
                            <tr>
                                <th style="border: 1px solid black; padding: 5px">Field</th>
                                <th style="border: 1px solid black; padding: 5px">Text</th>
                                <th style="border: 1px solid black; padding: 5px">Sensitive</th>
                            </tr>
                    """ + table_content + """
                        </table>
                    </body>
                </html>
            """

    text_part = MIMEText(email_message, 'plain')
    message_dict.attach(text_part)

    if send_html and html is not None:
        html_part = MIMEText(html, 'html')
        message_dict.attach(html_part)

    if send_json:
        try:
            json_part = MIMEText(json.dumps(doc_ocr, indent="\t"))
            json_part.add_header(
                'Content-Disposition', 'attachment',
                filename="document_ocr_%s.json" % str(time.time()).replace(".", "")
            )
            message_dict.attach(json_part)
        except Exception as e:
            print("Failed to attach JSON file: ", str(e))
    return message_dict


def permanently_delete_all_sent_emails():
    imap_mail = imaplib.IMAP4_SSL("imap.gmail.com", 993)
    imap_mail.login(EMAIL_ADDRESS, EMAIL_PASSWORD)

    imap_mail.select('"[Gmail]/Sent Mail"')

    imap_mail.store("1:*", '+X-GM-LABELS', '\\Trash')
    imap_mail.store("1:*", "+FLAGS", '\\Deleted')
    imap_mail.expunge()

    imap_mail.select(mailbox='"[Gmail]/Trash"', readonly=False)
    imap_mail.store("1:*", '+FLAGS', '\\Deleted')
    imap_mail.expunge()

    imap_mail.close()
    imap_mail.logout()


def send_email(to_email, doc_ocr):
    xx = time.time()
    message = build_message(to_email, doc_ocr)

    try:
        smtp_server = smtplib.SMTP('smtp.gmail.com', 587)
        smtp_server.ehlo()
        smtp_server.starttls()
        smtp_server.login(EMAIL_ADDRESS, EMAIL_PASSWORD)

        smtp_server.sendmail(EMAIL_ADDRESS, [to_email], message.as_string())
        smtp_server.close()
        permanently_delete_all_sent_emails()

        print("EMAIL TIME: ", time.time() - xx)
    except Exception as e:
        print("Failed to send mail: ", str(e))
