const { Storage } = require('@google-cloud/storage');

function normalizeInput(input, separator = '-') {
    return input.toLowerCase().replace(/\s+/g, separator);
}

const storage = new Storage({
    keyFilename: process.env.GOOGLE_APPLICATION_CREDENTIALS,
    projectId: process.env.GCLOUD_PROJECT,
});

async function storeImage(image,wasteType) {
    const bucketName = process.env.STORAGE_BUCKET;
    const bucket = storage.bucket(bucketName);
    console.log(`mencoba menyimpan gambar ke bucket: ${bucketName}`);
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
}



module.exports = storeImage;