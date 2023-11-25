FROM python:3.11

ENV PYHTONUNBUFFERED=1
RUN apt-get update --fix-missing 

RUN apt-get -y install tesseract-ocr-ron
ADD ./Backend/requirements.txt /opt/app/
WORKDIR /opt/app/

RUN pip install -r requirements.txt
RUN pip install python-magic
RUN pip install Flask-SQLAlchemy
RUN pip install psycopg2-binary 

# ONLY ADD NEW STUFF BELOW!!! OTHERWISE NO CACHE
RUN pip install flask-restful 
RUN apt-get -y install ffmpeg libsm6 libxext6
RUN apt-get -y install libpq-dev

RUN find /usr/share -type d -name "tessdata"

COPY ./Backend/. /opt/app/
EXPOSE 5053

ENTRYPOINT ["python3", "main.py"]