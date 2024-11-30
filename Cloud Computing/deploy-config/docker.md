# Docker Instructions
This document provides instructions on how to build and push the Docker image for the Berongsok backend service.

## Build Docker Image
To build the Docker image, run the following command in your terminal:
```sh
docker build berongsok-backend .
#                                                                project-id        repo-name        image-name
docker -t berongsok-backend asia-southeast2-docker.pkg.dev/berongsok-capstone/berongsok-capstone/berongsok-backend 
```

## Push Docker Image to Artifact Registry
After building the Docker image, you can push it to the Google Cloud Artifact Registry using the following command:
```sh
docker push asia-southeast2-docker.pkg.dev/berongsok-capstone/berongsok-capstone/berongsok-backend:latest
```

## Using Cloud Build
Alternatively, you can use Google Cloud Build to build and push the Docker image. Run the following command:
```sh
gcloud build submit -t asia-southeast2-docker.pkg.dev/berongsok-capstone/berongsok-capstone/berongsok-backend:latest
```

> Note: Ensure you have the necessary permissions and have authenticated with Google Cloud before running these commands.

## Deployment
Once the Docker image is pushed to the Artifact Registry, you can deploy it to your desired environment, such as Google Kubernetes Engine (GKE) or Cloud Run.

For example, to deploy to Cloud Run, you can use the following command:
```sh
gcloud run deploy berongsok-backend --image asia-southeast2-docker.pkg.dev/berongsok-capstone/berongsok-capstone/berongsok-backend:latest --region asia-southeast2
```

Make sure to replace `berongsok-backend` with your service name and adjust the region as needed.

## Additional Information

For more details on using Docker with Google Cloud, refer to the [Google Cloud documentation](https://cloud.google.com/artifact-registry/docs/docker/quickstart).

For troubleshooting and best practices, consult the [Docker documentation](https://docs.docker.com/get-started/).
