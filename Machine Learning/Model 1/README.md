# Model 1 by Ummu Athiya

This model demonstrates a garbage classification system using a pre-trained MobileNetV2 model fine-tuned for six types of waste. The model is trained to classify images into the following categories:

- Glass Bottles
- Plastic Bottles
- Plastic Cups
- Paper
- Cardboard
- Cans

## Dataset

The dataset used for training and validation is structured into two directories:

- `./garbage_dataset/train`: Contains the training images, organized into subdirectories by class.
- `./garbage_dataset/validation`: Contains the validation images, organized similarly.

Ensure the dataset follows this structure for proper loading. You can download our dataset from here:

[Berongsok Dataset](https://drive.google.com/file/d/1o__AK8ILNQ1MIFdWJqUhVGBO7YbUZy5H/view?usp=sharing)

## Project Workflow

### 1. Dependencies

The project uses TensorFlow and Keras for deep learning. Dependencies for this project are listed in `requirements.txt`. Install them using:

```bash
pip install -r requirements.txt
```

### 2. Model Architecture

The model uses MobileNetV2 as a base with the following components:

- **Preprocessing Layer**: `mobilenet_v2.preprocess_input` for preprocessing input images.
- **Data Augmentation Layer**: Random horizontal flips and rotations for robust training.
- **Custom Classification Head**: Fully connected layers for classifying the six categories.

### 3. Training

The training is configured as follows:

- **Batch Size**: 32
- **Image Size**: 224x224
- **Optimizer**: Adam with an initial learning rate of 0.00001.
- **Loss Function**: Sparse Categorical Crossentropy.
- **Callbacks**:
  - EarlyStopping: Stops training if validation loss doesn't improve for 5 epochs.
  - LearningRateScheduler: Reduces learning rate every 5 epochs.

### 4. Fine-Tuning

The last 25 layers of the MobileNetV2 base are unfrozen for fine-tuning, allowing the model to learn task-specific features while retaining the benefits of pre-trained weights.

### 5. Saving the Model

The trained model is saved in the `.keras` format for reuse.

```python
model.save('model_fix.keras')
```

You can see the `.keras` model in here: [model_fix.keras ](https://github.com/titii-ship-it/Berongsok/blob/ml_team/Machine%20Learning/Converted%20Models/model_fix.keras)

### 6. Evaluation

The model is evaluated on a test dataset (not shown in the code snippet above, you can try it yourself). The performance metrics include:

- Test Loss
- Test Accuracy

### Running the Code

Ensure your dataset is placed in the specified directories. Then, execute the Python script to train the model:

```bash
python model.py
```

### Model Summary

The model architecture includes:

- MobileNetV2 base model (pre-trained on ImageNet)
- Data augmentation and preprocessing layers
- GlobalAveragePooling2D
- Dense layers with L2 regularization
- Dropout for regularization

### Key Results

- Achieved accuracy of \~97% and val_accuracy \~90% during training.
- Fine-tuned model optimized for garbage classification tasks.

## Author

This model was created as part of **Berongsok Project**. For further questions or collaboration, feel free to reach out at [Ummu Athiya ](https://www.linkedin.com/in/ummu-athiya-833b541b7/)

