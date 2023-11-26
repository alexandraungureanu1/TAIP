# Stage 1: Compile and Build angular codebase

# Use official node image as the base image
FROM node:latest as build

# Set the working directory
WORKDIR /usr/local/app

# Add the source code to app
COPY . /usr/local/app/

WORKDIR /usr/local/app/ 
# Generate the build of the application
RUN npm install
RUN npm install -g @angular/cli
#RUN npm install @angular-devkit/build-angular --force

RUN sed -i '/const swal: SweetAlert;/d' ./node_modules/sweetalert/typings/sweetalert.d.ts

EXPOSE 4200

ENTRYPOINT ["ng","serve","--host", "0.0.0.0"]