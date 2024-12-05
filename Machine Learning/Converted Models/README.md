# Models for Garbage Classification

This folder contains all the saved models used or generated during the development of Berongsok project. The models are saved in various formats to ensure compatibility with different platforms and applications.

## Contents

The folder may include the following types of model files:

1. **`.h5` Files**: Saved models in HDF5 format for use with Keras and TensorFlow.
2. **`.keras` Files**: Native Keras model format.
3. **TensorFlow.js (`tfjs`) Models**: Models converted for use in web-based applications.

### Usage Instructions

1. **Loading Models**:
   - For `.h5` or `.keras` models:
     ```python
     from tensorflow.keras.models import load_model
     model = load_model('path_to_model.h5')  # or .keras
     ```
   - For TensorFlow.js models, follow the [TensorFlow.js documentation](https://www.tensorflow.org/js) for integration.

2. **Model Formats**:
   - Use `.h5` or `.keras` formats for local development and deployment on Python-based platforms.
   - Use `tfjs` models for web applications that require client-side machine learning.

### Current Models

The following saved models are included in this folder:

1. `model_fix.keras`: Final trained model in `.keras` format, optimized for garbage classification.
2. `model_fix_tfjs/`: TensorFlow.js version of the model for use in web applications.
3. (Add more models here as needed.)

### Notes

- Ensure that the required TensorFlow/Keras version matches the version used to save the model for proper compatibility.
- If the model is missing, make sure to regenerate it by training the model using the project code.

If you encounter any issues with loading or using the models, please contact the project team.
