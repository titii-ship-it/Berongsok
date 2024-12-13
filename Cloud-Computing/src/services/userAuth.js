require('dotenv').config();

const crypto = require('crypto');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcrypt');
const EmailService = require('./emailServices');
const { FieldValue } = require('@google-cloud/firestore');

const db = require('./firestore'); 
const errorService = require('./error');

const defaultErrorMessage = "Server error, Please try again or contact support if the problem persists.";

const registerUser = async (username, email, password) => {
  try {
    const usersCollection = db.collection('userProfile');
    const userDoc = await usersCollection.doc(email).get();
    const pendingUsersCollection = db.collection('pendingUsers');
    const pendingUserDoc = await pendingUsersCollection.doc(email).get();

    if (userDoc.exists) {
      throw new errorService.BadRequestError('This email is already registered');
    }
    if (password.length < 8) {
      throw new errorService.BadRequestError('Password must be at least 8 characters');
    }
  
    const hashedPassword = await bcrypt.hash(password, 8);
    const tpsId = crypto.randomBytes(16).toString('hex');
    const createdAt = new Date().toISOString();

    const otp = Math.floor(100000 + Math.random() * 900000).toString();
    const otpExpiresAt = new Date(new Date().getTime() + 15 * 60000).toISOString();

    // check if user has requested an OTP in the last 5 minutes
    if (pendingUserDoc.exists) {
      const data = pendingUserDoc.data();
      const lastAttempt = new Date(data.createdAt);
      const now = new Date();
      const diff = Math.abs(now - lastAttempt) / 60000;
      if (diff < 5) {
        throw new errorService.BadRequestError('You have already requested an OTP. Please check your email or try again in 5 minutes');
      }
    }
    // save to collection pendingUsers
    await pendingUsersCollection.doc(email).set({
      username: username,
      email: email,
      password: hashedPassword,
      tpsId: tpsId,
      otp,
      otpExpiresAt,
      createdAt: createdAt,
    });
                                            // Header         // template       // data
    await EmailService.sendEmail(email, 'Registration OTP', 'registerOTPEmail', { email, otp });

  } catch (error) {
    if (error instanceof errorService.ApplicationError) {
      throw error;
    } else {
      console.error(`Error while registering user, ${error}`);
      throw new errorService.InternalServerError(defaultErrorMessage);
    }
  }
};

const verifyRegistration = async (normalizedEmail, otp) => {
  try {
    const pendingUsersCollection = db.collection('pendingUsers');
    const pendingUserDoc = await pendingUsersCollection.doc(normalizedEmail).get();
    if (!pendingUserDoc.exists) {
      throw new errorService.NotFoundError('No pending registration found for this email.');
    }

    const pendingUserData = pendingUserDoc.data();

    // Verify OTP
    if (!pendingUserData.otp || !pendingUserData.otpExpiresAt) {
      throw new errorService.BadRequestError('OTP not found or expired. Please register again.');
    }
    if (pendingUserData.otp !== otp) {
      throw new errorService.BadRequestError('Invalid OTP.');
    }
    if (new Date() > new Date(pendingUserData.otpExpiresAt)) {
      throw new errorService.BadRequestError('OTP has expired. Please register again.');
    }

    // Move user data to userProfile collection
    const usersCollection = db.collection('userProfile');
    await usersCollection.doc(normalizedEmail).set({
      username: pendingUserData.username,
      email: pendingUserData.email,
      password: pendingUserData.password,
      tpsId: crypto.randomBytes(16).toString('hex'),
      createdAt: new Date().toISOString(),
    });

    // Delete pendingUsers 
    await pendingUsersCollection.doc(normalizedEmail).delete();

  } catch (error) {
    if (error instanceof errorService.ApplicationError) {
      throw error;
    } else {
      console.error(`Error while verify registration user, ${error}`);
      throw new errorService.InternalServerError(defaultErrorMessage);
    }
  }
};

const loginUser = async (email, password) => {
  try {
    const usersCollection = db.collection('userProfile');
    const userDoc = await usersCollection.doc(email).get();
    
    if (!userDoc.exists) {
      throw new errorService.NotFoundError("We couldn't find an account with that information");
    }
    const userData = userDoc.data();
    const isValidPassword = await bcrypt.compare(password, userData.password);

    if (!isValidPassword) {
      throw new errorService.UnauthorizedError('The email or password you entered is incorrect.');
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

  } catch (error) {
    if (error instanceof errorService.ApplicationError) {
      throw error;
    } else {
      console.error(`Error while logging in user, ${error}`);
      throw new errorService.InternalServerError(defaultErrorMessage);
    }
  }
};

const requestPasswordReset = async (normalizedEmail) => {
  try{
    const usersCollection = db.collection('userProfile');
    const userDoc = await usersCollection.doc(normalizedEmail).get();
    if (!userDoc.exists) {
      throw new errorService.NotFoundError("We couldn't find an account with that information");
    }

    // Generate OTP
    const otp = Math.floor(100000 + Math.random() * 900000).toString();

    // save otp to firestore -> 15 minutes
    await usersCollection.doc(normalizedEmail).update({
      otp,
      otpExpiresAt: new Date(new Date().getTime() + 15 * 60000).toISOString(),
    });

    // send otp via email
    await EmailService.sendEmail(normalizedEmail, 'Reset Password OTP', 'resetPasswordEmail', { normalizedEmail, otp});
  } catch (error) {
    if (error instanceof errorService.ApplicationError) {
      throw error;
    } else {
      console.error(`Error while request reset password, ${error}`);
      throw new errorService.InternalServerError(defaultErrorMessage);
    }
  }
};

const resetPassword = async (normalizedEmail, otp, newPassword) => {
  try { 
    const usersCollection = db.collection('userProfile');
    const userDoc = await usersCollection.doc(normalizedEmail).get();
    if (!userDoc.exists) {
      throw new errorService.NotFoundError("We couldn't find an account with that information");
    }
    
    const userData = userDoc.data();

    // verify OTP
    if (!userData.otp || !userData.otpExpiresAt) {
      throw new errorService.BadRequestError('OTP not found or expired. Please request a new OTP.');
    }
    if (userData.otp !== otp) {
      throw new errorService.BadRequestError('Invalid OTP.');
    }
    if (new Date() > new Date(userData.otpExpiresAt)) {
      throw new errorService.BadRequestError('OTP expired. Please request a new OTP.');
    }
    
    // Update password
    if (newPassword.length < 8) {
      throw new errorService.BadRequestError('Password must be at least 8 characters.');
    }

    const hashedPassword = await bcrypt.hash(newPassword, 8);

    await usersCollection.doc(normalizedEmail).update({
      password: hashedPassword,
      otp: FieldValue.delete(),
      otpExpiresAt: FieldValue.delete(),
    });
  } catch (error) {
    if (error instanceof errorService.ApplicationError) {
      throw error;
    } else {
      console.error(`Error while resetting password, ${error}`);
      throw new errorService.InternalServerError(defaultErrorMessage);
    }
  }
}

const verifyToken = async (authorizationToken) => {
  try {
    if (!authorizationToken) {
      throw new errorService.UnauthorizedError('Authorization header is required, please login first');
    }
    const token = authorizationToken.split(' ')[1];
    if (!token) {
      throw new errorService.UnauthorizedError('Bearer token is required, your format token is invalid');
    }

    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    // if token is valid, but the payload is not as expected
    if (!decoded.tpsId || !decoded.email || !decoded.username) {
      throw new errorService.UnauthorizedError('Invalid token, please login again');
    }
    return decoded;

  } catch (error) {
    if (error instanceof errorService.ApplicationError) {
      throw error;
    } else {
      if (error.name === 'TokenExpiredError') {
        throw new errorService.UnauthorizedError('Youre not authenticated, please login first');
      }
      console.error(`Error while verifying token, ${error}`);
      throw new errorService.InternalServerError(defaultErrorMessage);
    }
  }
};

module.exports = {
  registerUser,
  loginUser,
  verifyToken,
  verifyRegistration,
  requestPasswordReset,
  resetPassword,
};