# Use Node.js version 18
FROM node:18

# Set the working directory
WORKDIR /app
# WORKDIR /usr/src/app

# Copy package.json and package-lock.json from the Cloud-Computing directory
COPY Cloud-Computing/package.json Cloud-Computing/package-lock.json ./

# Install dependencies
RUN npm install --legacy-peer-deps

# Copy all files from the Cloud-Computing directory to the working directory
COPY Cloud-Computing/ ./

# Set environment variables
ENV PORT=8080
ENV MODEL_PATH=./src/model/model.json
ENV GCLOUD_PROJECT=berongsok-capstone-443115
ENV STORAGE_BUCKET=berongsok-bucket

# Expose the port
EXPOSE 8080

# Start the application
CMD ["npm", "start"]