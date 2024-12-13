# Berongsok API 
<p align="center">
  <img src="https://github.com/titii-ship-it/Berongsok/blob/main/Logo/Logo%20Brongsok%20Light.png" width='250dp' alt="Logo">
</p>

## Overview
This is the backend API for the Berongsok project. It provides various endpoints for managing users, predictions, transactions, and more. The backend is built using Node.js with the Hapi framework and integrates with Google Cloud services for authentication, storage, and database management.
<p align="center">
  <img src="https://github.com/titii-ship-it/Berongsok/blob/main/Cloud-Computing/assets/Berongsok-Cloud_Architecture.png" alt="Cloud Architecture" width="700">
</p>

We successfully supported the requirements of our application by utilizing Google Cloud services:

1. Compute Services: To ensure scalability and smooth deployment, we used Google Cloud Run to host APIs and other web services.
2. Database Services: For high speed and real-time synchronization, we used Firestore as our database solution.
3. Storage Services: To manage data and file storage, including the Machine Learning models utilized in our application, we made use of Google Cloud Storage.

4. We used the Google Cloud Pricing Calculator to precisely anticipate costs in order to minimize operating expenditures, allowing for cost-effective deployment and preventing unexpected credit depletion.

5. In order to ensure that cost management and project security were closely monitored, access control was carefully managed, limiting project management to the Cloud Computing team.


## Technologies Used
- **Node.js**: JavaScript runtime environment.
- **Hapi.js**: A rich framework for building applications and services.
- **Google Cloud Platform**: Utilized for compute services, database services, and storage services.
  - **Google Cloud Run**: For deploying and running the backend containerized applications.
  - **Google Firestore**: For storing and managing user data and transaction history.
  - **Google Cloud Storage**: For storing uploaded images or model files.
  - **Google Secret Manager**:  For securely storing and managing sensitive configuration data.
  - **Google Artifact Registry**: For storing and managing Docker container images 
  - **Google Cloud Build**: For automating the building and deployment of container images using CI/CD pipelines.
- **TensorFlow.js**: Used for machine learning model predictions.
- **Nodemailer**: For sending emails to users (e.g., OTP for registration and password reset).
- **JSON Web Tokens (JWT)**: For handling user authentication and authorization.
- **Docker**: For containerizing the application.


## Features
- **User Registration and Authentication**: Secure user registration with email verification using OTP, and login functionalities using JWT.
- **Password Reset**: Allows users to reset passwords through email verification using OTP.
- **Waste Prediction**: Users can upload images of waste materials, and the system predicts the type and calculates the price.
- **Transaction Management**: Save and retrieve transaction histories related to waste processing.
- **Dashboard**: Provides an overview of total weight, total price, and other relevant data for users.

## Project Structure
- `src/server`: Contains the server setup and route handlers.
- `src/services`: Includes services for authentication, database operations, email services, and more.
- `src/models`: Holds the machine learning models used for predictions.
- `Cloud-Computing/deployment`: Detailed documentation and scripts related to deployment.
- `Cloud-Computing/assets`: Contains assets like the cloud architecture diagram.
```
Cloud-Computing/
├── src/
│   ├── server/
│   │   ├── server.js        
│   │   ├── routes.js        
│   │   └── handlers.js      
│   ├── services/            
│   ├── model/               
│   └── ...
├── deployment/
├── Dockerfile
├── .env      
├── assets/         
├── package.json
└── ...
```

## Getting Started
### Prerequisites
- Node.js (version 14 or higher)
- npm (Node Package Manager)
- Google Cloud Account: With necessary credentials and access to services.
- Docker (for deployment)


### Installation
#### 1. Clone the repository:
```sh
git clone https://github.com/titii-ship-it/Berongsok.git
```

#### 2. Navigate to the backend directory:
```sh
cd Berongsok/Cloud-Computing
```

#### 3. Install dependencies:
```sh
npm install
```

#### 4. Set up environment variables:
Create a .env file in the root directory with the following variables:
```.env
PORT=8080
JWT_SECRET=your_jwt_secret
GCLOUD_PROJECT=your_project_name_in_gcp
STORAGE_BUCKET=your_bucket_name
GOOGLE_APPLICATION_CREDENTIALS=path_to_your_google_service_account.json
EMAIL_USERNAME=your_email@example.com
EMAIL_PASSWORD=your_email_password
```
  - Replace `your_jwt_secret` with a secure secret key for JWT.
  - Ensure that `GOOGLE_APPLICATION_CREDENTIALS` points to the JSON file containing your service account key.
  - Store sensitive data securely, consider using **Google Secret Manager** for production environments.


#### 5. Running the Server
Start the server with the following command:
```sh
# For production
npm start

# For development mode
npm run dev
```
The server should now be running on http://localhost:8080.

## Deployment
Detailed deployment instructions and scripts are provided in the [Cloud-Computing/deployment](https://github.com/titii-ship-it/Berongsok/tree/main/Cloud-Computing/deployment) directory. This includes steps for deploying the backend on Google Cloud Platform services like Cloud Run, setting up Artifact Registry, and managing secrets with Secret Manager.

## Cloud Architecture
For a visual representation of the cloud architecture and how different services interact within the application, refer to the diagram in [Cloud-Computing/assets/Berongsok-Cloud_Architecture.png](https://github.com/titii-ship-it/Berongsok/blob/cloud_team/Cloud-Computing/assets/Berongsok-Cloud_Architecture.png).

## API Endpoints
The backend provides several API endpoints:
- **User Authentication**:
    - POST `/register`: Register a new user and request an OTP (OTP sent via email).
    - POST `/verify-registration`: Verify user registration using OTP.
    - POST `/login`: Log in a user and receive a JWT token.
    - POST `/request-password-reset`: Request a password reset (OTP sent via email).
    - POST `/reset-password`: Reset the password using OTP.

- **Waste Management**:
    - POST `/predict`: Upload an image for waste type prediction.
    - POST `/waste/transactions`: Save a transaction after prediction.

- **Data Retrieval**:
    - GET `/transactionhistory`: Retrieve transaction history.
    - GET `/transactionhistory/detail`: Get details of a specific transaction.
    - GET `/dashboard`: Get dashboard data for the user.


Please refer to the API Specification document for detailed information on request parameters, error responses, etc.

## Dependencies
- `@hapi/hapi`: Server framework.
- `@google-cloud/firestore`: Firestore database integration.
- `@google-cloud/storage`: For handling file storage.
- `@tensorflow/tfjs-node`: Machine learning model execution.
- `jsonwebtoken`: For handling JWT tokens.
- `bcrypt`: Password hashing.
- `nodemailer`: Sending emails.
- `dotenv`: Loading environment variables.
- `handlebars`: Templating engine for emails.
- `crypto`: For generating random values and hashing.
- `fs`: File system module for reading files.
- `path`: Utilities for working with file and directory paths.

---

# Contact
For any inquiries or issues, please open an issue on the [GitHub repository](https://github.com/titii-ship-it/Berongsok/issues).