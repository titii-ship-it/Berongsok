
# QUEST for Cloud Computing
## Main Quest
- [ ] **Utilize Google Cloud services to support application needs**, such as:
    - Compute services for hosting APIs or other web services.
    - Database services for database applications.
    - Storage services for storing data, etc."


- [ ] **Utilize Google Cloud services for the machine learning workflow**, including analysis, training, and serving models. For example:
    - Use compute services for hosting machine learning deployments.
    - Utilize data services for data solutions, etc.

- [x] **Build cloud architecture** to illustrate all necessary components and technologies required by the applications and machine learning models.
    
    -> [Berongsok Cloud Architecure](https://lucid.app/lucidchart/57624f51-7130-4302-8591-c27b3622a741/edit?invitationId=inv_b3f07388-909b-475d-8922-da056751f702)

- [ ] **Calculate the costs** via Google Cloud Pricing Calculator to avoid sudden credit running out, and use the minimum costs.

- [ ] **Manage access to your Google** Cloud Project to ensure only the Cloud Computing team has access and can manage the costs.


## Side Quest 
- [ ] If you want to accelerate machine learning workflow into the next level, try utilize Vertex AI for your AI journey. Here are the details.
    - Vertex AI can only be used if you intend to build Generative AI.
    - Find out your best Generative AI model in Model Garden,
    - Utilize Vertex AI Workbench for running data analysis and training machine learning models, etc.
    - Vertex AI is prohibited for developing traditional machine learning such as deployment or other instant services to build and serve ML models.
    - Warning: Be mindful of the high cost of using Vertex AI, you would potentially run out of credit immediately. "

- [ ] Clean up your team's git repository and its documentation to make your project understandable by judges and audience.
- [ ] If necessary, use a custom Compute Engine VM on Colab via GCP Marketplace as Colab runtime.
- [ ] Implement Memorystore for caching method and Pub/Sub as message broker for your application.


# Run Server (development)
> You need to download `.env` and `service_account.json` [here](https://drive.google.com/drive/folders/1M5mHLb-UF2ShhJjfODTZ12g5V51w_MvO?usp=sharing).

1. Install dependencies:
    ```sh
    npm install
    ```

2. Start the server using `nodemon` :
    ```sh
    npm run dev
    ```