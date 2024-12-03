const { Firestore } = require('@google-cloud/firestore');

let db;
try {
  if (process.env.GOOGLE_APPLICATION_CREDENTIALS) {
    // For production with google secrets manager
    let credentials;
    try {
      credentials = JSON.parse(process.env.GOOGLE_APPLICATION_CREDENTIALS);
    } catch (parseError) {
      // for local development -> GOOGLE_APPLICATION_CREDENTIALS is a file path
      db = new Firestore({
        keyFilename: process.env.GOOGLE_APPLICATION_CREDENTIALS,
        projectId: process.env.GCLOUD_PROJECT
      });
    }
    
    if (credentials) {
      db = new Firestore({
        credentials: credentials,
        projectId: process.env.GCLOUD_PROJECT
      });
    }
  } else {
    // for production -> server using IAM role
    db = new Firestore({
      projectId: process.env.GCLOUD_PROJECT
    });
  }
} catch (error) {
  console.error('Error initializing Firestore:', error);
  throw error;
}

module.exports = db;