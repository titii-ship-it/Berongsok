require('dotenv').config();

const { Firestore } = require('@google-cloud/firestore');
const crypto = require('crypto');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcrypt');

const db = new Firestore({
  keyFilename: process.env.GOOGLE_APPLICATION_CREDENTIALS,
  projectId: process.env.GCLOUD_PROJECT,
});

const usersCollection = db.collection('userProfile');

const registerUser = async (username, email, password) => {
  const userDoc = await usersCollection.doc(email).get();

  if (userDoc.exists) {
    throw new Error('This email is already registered');
  }

  if (password.length < 8) {
    throw new Error('Password must be at least 8 characters');
  }

  const hashedPassword = await bcrypt.hash(password, 8);
  const tpsId = crypto.randomBytes(16).toString('hex');
  const createdAt = new Date().toISOString();

  await usersCollection.doc(email).set({
    username,
    email,
    password: hashedPassword,
    tpsId,
    createdAt,
  });
};

const loginUser = async (email, password) => {
  const userDoc = await usersCollection.doc(email).get();

  if (!userDoc.exists) {
    throw new Error("We couldn't find an account with that information");
  }

  const userData = userDoc.data();
  const isValidPassword = await bcrypt.compare(password, userData.password);

  if (!isValidPassword) {
    throw new Error('The email or password you entered is incorrect.');
  }
  console.log('JWT_SECRET:', process.env.JWT_SECRET);
  const token = jwt.sign(
      { tpsId: userData.tpsId }, 
      process.env.JWT_SECRET, 
      { expiresIn: '1h' }
    );

  return {
    tpsId: userData.tpsId,
    email: userData.email,
    username: userData.username,
    token,
  };
};

const verifyToken = async (authorizationToken) => {
  // lempar error jika -> tidak ada header, tidak ada token, token tidak valid
  if (!authorizationToken) {
      throw new Error('Authorization header is required : please login first');
  }
  const token = authorizationToken.split(' ')[1];

  if (!token) {
      throw new Error('Bearer token is required');
  }
  try {
      const decoded = jwt.verify(token, process.env.JWT_SECRET);
      return decoded;
  } catch (error) {
      throw new Error('Invalid token : Youre not authenticated, please login first');
  }
};


module.exports = {
  registerUser,
  loginUser,
  verifyToken,
  // logoutUser
};