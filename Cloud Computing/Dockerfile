FROM node:18

WORKDIR /usr/src/app

COPY package*.json ./

RUN npm install --legacy-peer-deps

COPY . .

ENV PORT=8080
ENV MODEL_PATH=./src/model/model.json
ENV GCLOUD_PROJECT=berongsok-capstone
ENV STORAGE_BUCKET=berongsok-bucket
ENV GOOGLE_APPLICATION_CREDENTIALS=./service_account.json

EXPOSE 8080

CMD ["npm", "start"]