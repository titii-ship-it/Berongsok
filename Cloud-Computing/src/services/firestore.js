const { Firestore } = require('@google-cloud/firestore');
const errorService = require('./error');

let db;
try {
  if (process.env.GOOGLE_APPLICATION_CREDENTIALS) {
    // For production with google secrets manager
    let credentials;
    try {
      credentials = JSON.parse(process.env.GOOGLE_APPLICATION_CREDENTIALS);
    } catch (jsonParseError) {
      // for local development -> GOOGLE_APPLICATION_CREDENTIALS is a file path
      console.log('-> Using GOOGLE_APPLICATION_CREDENTIALS as file path');
      db = new Firestore({
        keyFilename: process.env.GOOGLE_APPLICATION_CREDENTIALS,
        projectId: process.env.GCLOUD_PROJECT
      });
    }
    
    if (credentials) {
      // for production -> server using service account
      console.log('-> Using GOOGLE_APPLICATION_CREDENTIALS as JSON');
      db = new Firestore({
        credentials: credentials,
        projectId: process.env.GCLOUD_PROJECT
      });
    }
  } else {
    // for production -> server using IAM role
    console.log('-> Using IAM role');
    db = new Firestore({
      projectId: process.env.GCLOUD_PROJECT
    });
  }
} catch (error) {
  console.error('Error initializing Firestore:', error);
  throw new errorService.InternalServerError('Server error, Please try again or contact support if the problem persists.');
}

module.exports = db;