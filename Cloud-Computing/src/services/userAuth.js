require('dotenv').config();

const crypto = require('crypto');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcrypt');
const EmailService = require('./emailServices');
const FirestoreService = require('./firestoreService');
const db = require('./firestore'); 

const usersCollection = db.collection('userProfile');

const registerUser = async (username, email, password) => {
    const usersCollection = db.collection('userProfile');
    const userDoc = await usersCollection.doc(email).get();
    const pendingUsersCollection = db.collection('pendingUsers');
    const pendingUserDoc = await pendingUsersCollection.doc(email).get();

    if (userDoc.exists) {
      throw new Error('This email is already registered');
    }

    if (password.length < 8) {
      throw new Error('Password must be at least 8 characters');
    }
  
    const hashedPassword = await bcrypt.hash(password, 8);
    const tpsId = crypto.randomBytes(16).toString('hex');
    const createdAt = new Date().toISOString();

    const otp = Math.floor(100000 + Math.random() * 900000).toString();
    const otpExpiresAt = new Date(new Date().getTime() + 15 * 60000).toISOString();

    // jika sudah pernah mencoba registrasi kurang dari 5 menit lalu
    if (pendingUserDoc.exists) {
      const data = pendingUserDoc.data();
      const lastAttempt = new Date(data.createdAt);
      const now = new Date();
      const diff = Math.abs(now - lastAttempt) / 60000;

      if (diff < 5) {
        throw new Error('You have already requested an OTP. Please check your email or try again in 5 minutes');
      }
    }
    // save ke collection pendingUsers
    await pendingUsersCollection.doc(email).set({
      username: username,
      email: email,
      password: hashedPassword,
      tpsId: tpsId,
      otp,
      otpExpiresAt,
      createdAt: createdAt,
    });
                                            // judul         // template       // data
    await EmailService.sendEmail(email, 'Registration OTP', 'registerOTPEmail', { email, otp });

};

const loginUser = async (email, password) => {
  console.log('db:', db);
  const usersCollection = db.collection('userProfile');
  const userDoc = await usersCollection.doc(email).get();

  if (!userDoc.exists) {
    throw new Error("We couldn't find an account with that information");
  }

  const userData = userDoc.data();
  const isValidPassword = await bcrypt.compare(password, userData.password);

  if (!isValidPassword) {
    throw new Error('The email or password you entered is incorrect.');
  }
  
  // token = jwt.sign(data, jwtSecretKey)
  const token = jwt.sign(
      { tpsId: userData.tpsId,
        username: userData.username,
        email: userData.email,}, 
        process.env.JWT_SECRET, 
      { expiresIn: '30d' }
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
      throw new Error('Authorization header is required, please login first');
  }

  const token = authorizationToken.split(' ')[1];

  if (!token) {
      throw new Error('Bearer token is required, your format token is invalid');
  }

  // // Check logout
  // const isBlacklisted = await redisClient.get(`blacklist_${token}`);
  // if (isBlacklisted) {
  //   throw new Error('You already logged out, please login again');
  // }

  try {
      const decoded = jwt.verify(token, process.env.JWT_SECRET);
      return decoded;
  } catch (error) {
      throw new Error('Youre not authenticated, please login first');
  }
};

module.exports = {
  registerUser,
  loginUser,
  verifyToken,
};