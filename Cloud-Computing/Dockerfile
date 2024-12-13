FROM node:18

WORKDIR /app
# WORKDIR /usr/src/app

COPY Cloud-Computing/package.json Cloud-Computing/package-lock.json ./
# COPY ./package.json ./package-lock.json ./

RUN npm install --legacy-peer-deps
# RUN npm install

COPY Cloud-Computing/ ./
# COPY . ./

# Set environment variables
ENV PORT=8080
ENV MODEL_PATH=./src/model/model.json
ENV GCLOUD_PROJECT=berongsok-capstone-443115
ENV STORAGE_BUCKET=berongsok-bucket

EXPOSE 8080

CMD ["npm", "start"]