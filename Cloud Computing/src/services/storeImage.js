const { Storage } = require('@google-cloud/storage');


async function storeImage(image) {
    const storage = new Storage({
        keyFilename: process.env.GOOGLE_SERVICE_ACCOUNT,
        projectId: process.env.PROJECT_NAME,
    });

    const bucketName = process.env.STORAGE_BUCKET;
    const bucket = storage.bucket(bucketName);

    const file = bucket.file(`images/${imageName}`);
    await file.save(imageBuffer, {
        metadata: { contentType: 'image/jpeg' },
        public: true,
        validation: 'md5'
    });

    const imageUrl = `https://storage.googleapis.com/${bucket.name}/predict_folder/${imageName}`;
    return imageUrl;
}

module.exports = storeImage;