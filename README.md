[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]

<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://github.com/titii-ship-it/Berongsok">
    <img src="https://github.com/titii-ship-it/Berongsok/blob/main/Logo/Logo%20Brongsok%20Light.png" width='250dp' alt="Logo">
  </a>

  <h3 align="center">Berongsok</h3>

  <p align="center">
    An application designed to assist Trash Banks with AI-powered waste classification and data management. 
    <br />
    <a href="https://github.com/titii-ship-it/Berongsok"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/titii-ship-it/Berongsok">View Demo</a>
    ·
    <a href="https://github.com/titii-ship-it/Berongsok/issues">Report Bug</a>
    ·
    <a href="https://github.com/titii-ship-it/Berongsok/issues">Request Feature</a>
  </p>
</p>


<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#features">Features</a></li>
        <li><a href="#tech-stack">Tech Stack</a></li>
      </ul>
    </li>
    <li><a href="#step-by-step-guide">Step-by-Step Guide</a></li>
    <li><a href="#future-enhancements">Future Enhancements</a></li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>


<!-- ABOUT THE PROJECT -->
## About The Project

Berongsok is an AI-powered waste classification app designed to assist Trash Banks in streamlining waste sorting and data collection processes. This application integrates machine learning to improve waste management efficiency and supports sustainable practices in Indonesia.


## Features
- AI-based waste classification using MobileNetV2.
- Automatic price calculation for waste transactions.
- Automatic financial reporting for Trash Banks.
- Dashboard for waste and financial data visualization.


## Tech Stack
- **Machine Learning**: Python, TensorFlow, MobileNetV2
- **Frontend**: Android (Java/Kotlin)
- **Backend**: Node.js, Express
- **Database**: Firebase Realtime Database
- **Deployment**: TensorFlow.js for the ML model


## Step-by-Step Guide

### 1. Dataset Preparation
1. **Gather Data**:
   - Scrape waste images from the internet and collect your own photos for six waste categories (e.g., glass bottles, plastic bottles, paper, cardboard, plastic cups, and cans).
   - Organize the data into `train` and `validation` folders.

2. **Label Data**:
   - Ensure each category has a corresponding folder name.
   - Check that the data distribution is balanced.

3. **Preprocess Data**:
   - Resize images to 224x224 pixels.
   - Use `tf.keras.applications.mobilenet_v2.preprocess_input` for preprocessing.


### 2. Machine Learning Model Development
1. **Setup Environment**:
   - Install TensorFlow and required libraries.

2. **Model Architecture**:
   - Use the MobileNetV2 pre-trained model.
   - Perform fine-tuning to adapt the model to the waste classification dataset.

3. **Train Model**:
   ```python
   model.compile(optimizer='adam', loss='categorical_crossentropy', metrics=['accuracy'])
   model.fit(train_dataset, validation_data=val_dataset, epochs=10)
   ```

4. **Evaluate and Save Model**:
   - Save the model in TensorFlow.js format for integration with the backend.
   ```bash
   tensorflowjs_converter --input_format=tf_saved_model /path/to/saved_model /path/to/output
   ```


### 3. Backend Development
1. **Setup Backend**:
   - Initialize a Node.js project.
   - Install Express.js and TensorFlow.js libraries.

2. **API Development**:
   - Create APIs for image upload and model inference.

3. **Integrate Model**:
   - Load the TensorFlow.js model and use it for waste classification requests.


### 4. Frontend Development
1. **Design UI**:
   - Create an intuitive user interface for Trash Bank users.

2. **Android Development**:
   - Develop an Android app with features for:
     - Capturing/uploading waste images.
     - Displaying classification results.
     - Recording data into Firebase.


### 5. Integration
1. **Connect Backend to Frontend**:
   - Use APIs for image classification and database operations.

2. **Test Application**:
   - Ensure all features work seamlessly.
   - Fix bugs and optimize performance.


### 6. Deployment
1. **Backend Deployment**:
   - Deploy the Node.js backend on a cloud platform like AWS or Heroku.

2. **Frontend Deployment**:
   - Package the Android app as an APK for distribution.


## Future Enhancements
- Integration with IoT for automatic weight data recording.
- Adding e-wallet functionality for Trash Bank transactions.
- Expanding user roles to include Trash Bank customers.
- Generating automated reports for Trash Bank clients.


## Usage
1. Download the APK from [here](https://drive.google.com/file/d/14xWhQk_C0Zi_e7KRtWUQkMjRsBNxaVw9/view?usp=drive_link).
2. Install the app on your Android device.
3. Follow the app instructions to capture or upload waste images and manage waste data efficiently.


## Contributing

Contributions are what make the open-source community an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request


## License

Distributed under the MIT License. See `LICENSE` for more information.


## Contact

<li><a href='https://www.linkedin.com/in/aimar-maulana/'>Haffidz Aimar Maulana</a></li>
<li><a href='https://www.linkedin.com/in/hilmiyatulasna/'>Hilmiyatul Asna</a></li>
<li><a href='https://www.linkedin.com/in/ummu-athiya-833b541b7/'>Ummu Athiya</a></li>
<li><a href='https://www.linkedin.com/in/cornelius-yuli/'>Cornelius Yuli Rosdianto</a></li>
<li><a href='https://www.linkedin.com/in/abian-pratama/'>M. Abian Abdi Pratama</a></li>
<li><a href='https://www.linkedin.com/in/rayhan-safar-putra-dwiliano-62497324b/'>Rayhan Safar Putra D</a></li>
<li><a href='https://www.linkedin.com/in/sep-sarip-hidayattuloh/'>Sep Sarip Hidayattuloh</a></li>


<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/titii-ship-it/Berongsok.svg?style=for-the-badge
[contributors-url]: https://github.com/titii-ship-it/Berongsok/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/titii-ship-it/Berongsok.svg?style=for-the-badge
[forks-url]: https://github.com/titii-ship-it/Berongsok/network/members
[stars-shield]: https://img.shields.io/github/stars/titii-ship-it/Berongsok.svg?style=for-the-badge
[stars-url]: https://github.com/titii-ship-it/Berongsok/stargazers
[issues-shield]: https://img.shields.io/github/issues/titii-ship-it/Berongsok.svg?style=for-the-badge
[issues-url]: https://github.com/titii-ship-it/Berongsok/issues
[license-shield]: https://img.shields.io/github/license/titii-ship-it/Berongsok.svg?style=for-the-badge
[license-url]: https://github.com/titii-ship-it/Berongsok/blob/master/LICENSE
