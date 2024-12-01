const { Firestore } = require('@google-cloud/firestore');

const db = new Firestore({
    keyFilename: process.env.GOOGLE_APPLICATION_CREDENTIALS,
    projectId: process.env.GCLOUD_PROJECT,
});

module.exports = db;