# Stage 1: Compile and Build angular codebase

# Use official node image as the base image
FROM node:latest as build

# Set the working directory
WORKDIR /usr/local/app

# Add the source code to app
COPY ./Frontend/ /usr/local/app/

# Generate the build of the application
RUN npm install -g @angular/cli

EXPOSE 4200

ENTRYPOINT ["ng","serve"]