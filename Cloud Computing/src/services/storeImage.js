const { Storage } = require('@google-cloud/storage');


async function storeImage(image,wasteType) {
    const storage = new Storage({
        keyFilename: process.env.GOOGLE_APPLICATION_CREDENTIALS,
        projectId: process.env.GCLOUD_PROJECT,
    });

    const bucketName = process.env.STORAGE_BUCKET;
    const bucket = storage.bucket(bucketName);

    // const filename = `${wasteType}-${Date.now()}-${image.hapi.filename}`
    const formattedDate = new Date().toISOString().replace(/[:.]/g, '-');
    const filename = `${wasteType}-${formattedDate}-${image.hapi.filename}`;

    const destination = `predict_folder/${wasteType}/${filename}`;

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


    // await file.save(imageBuffer, {
    //     metadata: { contentType: 'image/jpeg' },
    //     public: true,
    //     validation: 'md5'
    // });

    // const imageUrl = `https://storage.googleapis.com/${bucketName}/${destination}`;
    // return imageUrl;
}



module.exports = storeImage;