# Importing all dependencies
import tensorflow as tf
import os

from tensorflow import keras
from tensorflow.keras import layers,models
from tensorflow.keras.preprocessing.image import ImageDataGenerator
from tensorflow.keras.applications import MobileNetV2
from tensorflow.keras.layers import Dense, Dropout, GlobalAveragePooling2D
from tensorflow.keras import Model
from tensorflow.keras.callbacks import EarlyStopping, LearningRateScheduler
from tensorflow.keras.regularizers import l2

from tensorflow.keras.preprocessing import image_dataset_from_directory
from tensorflow.keras.layers import RandomFlip, RandomRotation


# Dataset Directory
train_dir = './garbage_dataset/train'
val_dir = './garbage_dataset/validation'

# Checking the folder
print("Classes in the train folder:")
print(os.listdir(train_dir))

print("\nClasses in the validation folder:")
print(os.listdir(val_dir))

# Parameters Needed
BATCH_SIZE = 32
IMG_SIZE = (224, 224)
NUM_CLASSES = 6
IMG_SHAPE = IMG_SIZE + (3,)

# Splitting the Data into Train and Val
train_data = image_dataset_from_directory(
    train_dir,
    image_size=IMG_SIZE,
    batch_size=BATCH_SIZE,
    shuffle=True
)

val_data = image_dataset_from_directory(
    val_dir,
    image_size=IMG_SIZE,
    batch_size=BATCH_SIZE,
    shuffle=False
)

AUTOTUNE = tf.data.experimental.AUTOTUNE
train_data= train_data.prefetch(buffer_size=AUTOTUNE)

# Used For Training
early_stopping = EarlyStopping(monitor='val_loss', patience=5, restore_best_weights=True)
lr_schedule = LearningRateScheduler(lambda epoch: 0.001 * 0.1**(epoch // 5))

# Load MobileNetV3 Large as Base Model
base_model = MobileNetV2(
    weights="imagenet",
    include_top=False,
    input_shape=(224, 224, 3)
)

# Add Preprocessing Layer
preprocess_layer = tf.keras.applications.mobilenet_v2.preprocess_input

# Add Data Augmentation Layer
data_augmentation = tf.keras.Sequential([
  RandomFlip("horizontal"),
  RandomRotation(0.2),
])

# Do Fine-Tuning
for layer in base_model.layers[:-25]:
  layer.trainable = False

# This is MY MAGIC
inputs = tf.keras.Input(shape=IMG_SHAPE)

x = data_augmentation(inputs)
x = preprocess_layer(x)

x = base_model(x)

x = GlobalAveragePooling2D()(x)
x = Dense(128, activation='relu', kernel_regularizer=l2(0.01))(x)
x = Dropout(0.2)(x)

outputs = Dense(NUM_CLASSES, activation='softmax', kernel_regularizer=l2(0.01))(x)

model = Model(inputs=inputs, outputs=outputs)

# Compiling ALL
model.compile(
    optimizer=tf.keras.optimizers.Adam(1e-5),
    loss="sparse_categorical_crossentropy",
    metrics=["accuracy"],
)

# See My Model
model.summary()

# Do THE MAGIC
model.fit(
    train_data,
    validation_data=val_data,
    epochs=30,
    callbacks=[early_stopping, lr_schedule]
)

# Saved the Model into .keras format
model.save('model_fix.keras')

# See How Well the Model is
test_loss, test_accuracy = model.evaluate(test_data)
print(f"Test Loss: {test_loss:.4f}")
print(f"Test Accuracy: {test_accuracy:.4f}")