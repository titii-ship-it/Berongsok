const { Storage } = require('@google-cloud/storage');
const errorService = require('./error');
const defaultErrorMessage = "Server error, Please try again or contact support if the problem persists.";
let storage;

try {
  if (process.env.GOOGLE_APPLICATION_CREDENTIALS) {
    try {
      // For production with google secrets manager
      const credentials = JSON.parse(process.env.GOOGLE_APPLICATION_CREDENTIALS);
      storage = new Storage({
        credentials: credentials,
        projectId: process.env.GCLOUD_PROJECT
      });
    } catch (jsonParseError) {
      // for local development -> GOOGLE_APPLICATION_CREDENTIALS is a file path
      storage = new Storage({
        keyFilename: process.env.GOOGLE_APPLICATION_CREDENTIALS,
        projectId: process.env.GCLOUD_PROJECT
      });
    }
  } else {
    // for production -> server using IAM role
    storage = new Storage();
  }
} catch (error) {
  console.error('Error initializing Storage:', error);
  throw errorService.InternalServerError(defaultErrorMessage);
}

async function storeImage(image,wasteType) {
  try {
    const bucketName = process.env.STORAGE_BUCKET;
    const bucket = storage.bucket(bucketName);
    // const filename = `${wasteType}-${Date.now()}-${image.hapi.filename}`
    const formattedDate = new Date().toISOString().replace(/[:.]/g, '-');
    const normalizeWasteType = wasteType.trim().toLowerCase().replace(/\s+/g, "-");
    const filename = `${normalizeWasteType}-${formattedDate}-${image.hapi.filename}`;

    const destination = `predict_folder/${normalizeWasteType}/${filename}`;

    const imageBuffer = image._data;
    const blob = bucket.file(destination);
    const blobStream = blob.createWriteStream({
        resumable: false,
        metadata: {
            contentType: image.hapi.headers['content-type'],
        },
    });

    return new Promise((resolve, reject) => {
        blobStream.on('finish', () => {
            const imageUrl = `https://storage.googleapis.com/${bucketName}/${destination}`;
            resolve(imageUrl);
        });
        blobStream.on('error', (err) => {
            console.error(err);
            reject(err);
        });
        blobStream.end(imageBuffer);
    });
  } catch (error) {
    console.error(`Error while storing image: ${error}`);
    throw new errorService.InternalServerError(`An error occurred while saving to the database. Please try again later.`);
  } 
}



module.exports = storeImage;