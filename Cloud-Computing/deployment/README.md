# Berongsok Backend Deployment Guide
This section outlines the steps to deploy the Berongsok backend API on Google Cloud Platform (GCP). 
<p align="center">
  <img src="https://github.com/titii-ship-it/Berongsok/blob/cloud_team/Cloud-Computing/assets/Berongsok-Cloud_Architecture.png" alt="Cloud Architecture" width="700">
</p>

## Prerequisites
- **Google Cloud Account**: Ensure you have a GCP account with the necessary permissions.
- **Google Cloud SDK**: Install the Google Cloud SDK (includes gcloud CLI) on your local machine or use Google Cloud Shell.
- **Docker**: Install Docker to build and manage container images.

## Steps
#### 1. Set Up Google Cloud Project
Create a new project in GCP or use an existing one:
```sh
gcloud projects create your-project-id
gcloud config set project your-project-id
```

#### 2. Enable Required APIs
Enable the necessary APIs for your project:
```sh
gcloud services enable run.googleapis.com
gcloud services enable artifactregistry.googleapis.com
gcloud services enable cloudbuild.googleapis.com
```

#### 3. Set Up Artifact Registry
Create a Docker repository in Artifact Registry:
```sh
gcloud artifacts repositories create berongsok-backend-repo \
  --repository-format=docker \
  --location=asia-southeast2 \
  --async
  --description="Docker repository for Berongsok backend"

gcloud auth configure-docker asia-southeast2-docker.pkg.dev
```

#### 4. Build the Docker Image
Build the Docker image using the provided Dockerfile:
```sh
docker build -t asia-southeast2-docker.pkg.dev/your-project-id/berongsok-backend-repo/berongsok-backend:latest \
  -f Cloud-Computing/Dockerfile .
```

#### 5. Push the Docker Image to Artifact Registry
Authenticate Docker with Google Cloud and push the image:
```sh
docker push asia-southeast2-docker.pkg.dev/your-project-id/berongsok-backend-repo/berongsok-backend:latest
```

Alternatively for no 4 and 5, you can use Google Cloud Build to build and push the Docker image. Run the following command:
```sh
gcloud build submit -t asia-southeast2-docker.pkg.dev/your-project-id/berongsok-capstone-repo/berongsok-backend:latest
```
> Note: Ensure you have the necessary permissions and have authenticated with Google Cloud before running these commands.


#### 6. Deployment
Once the Docker image is pushed to the Artifact Registry, you can deploy it to your desired environment, such as Google Kubernetes Engine (GKE) or Cloud Run.

For example, to deploy to Cloud Run, you can use the following command:
```sh
gcloud run deploy berongsok-backend-service \
  --image asia-southeast2-docker.pkg.dev/your-project-id/berongsok-backend-repo/berongsok-backend:latest \
  --platform managed \
  --region asia-southeast2 \
  --allow-unauthenticated \
  --set-env-vars PORT=8080,GCLOUD_PROJECT=your-project-id,STORAGE_BUCKET=your-bucket-name,JWT_SECRET=your_jwt_secret,GOOGLE_APPLICATION_CREDENTIALS=/path/to/service_account.json,EMAIL_USERNAME=your_email@example.com,EMAIL_PASSWORD=your_email_password
```
Replace environment variable values with your actual configurations.

#### 7. Configure Environment Variables
Ensure the following environment variables are properly set:
  - `PORT`: The port on which the server will run (default is 8080).
  - `JWT_SECRET`: Your secret key for JWT token generation.
  - `GOOGLE_APPLICATION_CREDENTIALS`: Path to your Google Cloud service account key JSON file.
  - `EMAIL_USERNAME` & `EMAIL_PASSWORD`: Credentials for the email service used by Nodemailer.

#### 8. Set Up Firestore and Cloud Storage
  - **Firestore**: Initialize Firestore (native mode) in your project and create the necessary collections 
    - userProfile 
    - transactionHistory
    - wastePricing
    - pendingUsers
  - **Cloud Storage**: Create a storage bucket to store uploaded images and model files.

#### 9. Update Service Account Permissions
Ensure the service account associated with your Cloud Run service has permissions for:
  - Firestore access 
  - Cloud Storage access 

#### 10. Verify Deployment
After deployment, verify that your service is running:
```sh
gcloud run services describe berongsok-backend-servic --region=asia-southeast2
```
- Access the service URL provided by Cloud Run.
- Test API endpoints using tools like Postman or curl.
- Check logs in the GCP console for any errors.

## Additional Information
 - **Continuous Integration/Continuous Deploymen**t: You can set up CI/CD pipelines using Cloud Build. Refer to the `cloudbuild.yaml` file for configuration.
 - **Scaling and Monitoring** : Cloud Run automatically scales based on incoming traffic. Use GCP's monitoring tools to keep track of service performance.
 - **Security**: Consider using Google Secret Manager to store `JWT_SECRET`, `EMAIL_PASSWORD`, or `GOOGLE_APPLICATION_CREDENTIALS`


## References
For more details on using Docker with Google Cloud, refer to the [Google Cloud documentation](https://cloud.google.com/artifact-registry/docs/docker/quickstart).

For troubleshooting and best practices, consult the [Docker documentation](https://docs.docker.com/get-started/).