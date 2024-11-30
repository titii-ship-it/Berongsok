const { Firestore, FieldValue } = require('@google-cloud/firestore');
const FirebaseService = require('../services/firestoreService');
const predictWaste = require('../services/predict');
const AuthService = require('../services/userAuth');
const EmailService = require('../services/emailServices');
const storeImage = require('../services/storeImage');
const crypto = require('crypto');
const jwt = require('jsonwebtoken');
const { create } = require('domain');

// File: src/server/handlers.js
const db = new Firestore({
  keyFilename: process.env.GOOGLE_APPLICATION_CREDENTIALS,
  projectId: process.env.GCLOUD_PROJECT,
});
const usersCollection = db.collection('userProfile');
const pendingUsersCollection = db.collection('pendingUsers');

const registerHandler = async (request, h) => {
  const { username, email, password } = request.payload;

  try {
    await AuthService.registerUser(username, email, password);
    const responese = h.response({
      error: false,
      message: 'OTP has been sent to your email. Please verify to complete registration.',
    })
    responese.code(200);
    return responese;

  } catch (error) {
    const responese = h.response({
      error: true,
      message: error.message,
    })
    responese.code(400);
    return responese;
  }
};

const verifyRegistrationHandler = async (request, h) => {
  const { email, otp } = request.payload;

  try {
    // Get user from pendingUsers collection
    const pendingUserDoc = await pendingUsersCollection.doc(email).get();
    if (!pendingUserDoc.exists) {
      throw new Error('No pending registration found for this email.');
    }

    const pendingUserData = pendingUserDoc.data();

    // Verify OTP
    if (!pendingUserData.otp || !pendingUserData.otpExpiresAt) {
      throw new Error('OTP not found or expired. Please register again.');
    }

    if (pendingUserData.otp !== otp) {
      throw new Error('Invalid OTP.');
    }

    if (new Date() > new Date(pendingUserData.otpExpiresAt)) {
      throw new Error('OTP has expired. Please register again.');
    }

    // Move user data to userProfile collection
    await usersCollection.doc(email).set({
      username: pendingUserData.username,
      email: pendingUserData.email,
      password: pendingUserData.password,
      tpsId: crypto.randomBytes(16).toString('hex'),
      createdAt: new Date().toISOString(),
    });

    // Delete pendingUsers 
    await pendingUsersCollection.doc(email).delete();

    const response = h.response({
      error: false,
      message: 'Your account has been successfully created.',
    });
    response.code(201);
    return response;

  } catch (error) {
    const response = h.response({
      error: true,
      message: error.message,
    });
    response.code(400);
    return response;
  }
};

const loginHandler = async (request, h) => {
  const { email, password } = request.payload;

  try {
    const loginResult = await AuthService.loginUser(email, password);
    const respone = h.response({
      error: false,
      message: 'success',
      loginResult,
    })
    respone.code(200);
    return respone;

  } catch (error) {
    const respone = h.response({
      error: true,
      message: `${error.message}`,
    })
    respone.code(400);
    return respone;
  }
};

// send OTP
const requestPasswordResetHandler = async (request, h) => {
  const { email } = request.payload;

  try {
    // Cek apakah user ada
    const db = new Firestore({
      keyFilename: process.env.GOOGLE_APPLICATION_CREDENTIALS,
      projectId: process.env.GCLOUD_PROJECT,
    });
    const userDoc = await usersCollection.doc(email).get();
    if (!userDoc.exists) {
      throw new Error("Email tidak terdaftar.");
    }

    // Generate OTP
    const otp = Math.floor(100000 + Math.random() * 900000).toString();

    // Simpan OTP ker Firebase -> 15 menit kadaluarsa
    await usersCollection.doc(email).update({
      otp,
      otpExpiresAt: new Date(new Date().getTime() + 15 * 60000).toISOString(),
    });

    // Kirim OTP ke email user
    await EmailService.sendEmail(email, 'Reset Password OTP', 'resetPasswordEmail', { email, otp});
    
    return h.response({
      status: 'success',
      message: 'OTP telah dikirim ke email Anda.',
    }).code(200);

  } catch (error) {
    return h.response({
      status: 'fail',
      message: error.message,
    }).code(400);
  }
};

// reset password
const resetPasswordHandler = async (request, h) => {
  const { email, otp, newPassword } = request.payload;

  try {
    const userDoc = await usersCollection.doc(email).get();
    if (!userDoc.exists) {
      throw new Error("Email tidak terdaftar.");
    }

    const userData = userDoc.data();

    // Verifikasi OTP
    if (!userData.otp || !userData.otpExpiresAt) {
      throw new Error('OTP tidak ditemukan. Silakan minta OTP baru.');
    }

    if (userData.otp !== otp) {
      throw new Error('OTP salah.');
    }

    if (new Date() > new Date(userData.otpExpiresAt)) {
      throw new Error('OTP telah kadaluarsa. Silakan minta OTP baru.');
    }

    // Update password
    if (newPassword.length < 8) {
      throw new Error('Password harus memiliki minimal 8 karakter.');
    }

    const hashedPassword = await bcrypt.hash(newPassword, 8);

    await usersCollection.doc(email).update({
      password: hashedPassword,
      otp: FieldValue.delete(),
      otpExpiresAt: FieldValue.delete(),
    });

    return h.response({
      status: 'success',
      message: 'Password Anda berhasil diubah.',
    }).code(201);

  } catch (error) {
    return h.response({
      status: 'fail',
      message: error.message,
    }).code(400);
  }
};

const predictHandler = async (request, h) => {
  try {
      // CEK user login 
      const authorizationToken = request.headers["authorization"];
      const decoded = await AuthService.verifyToken(authorizationToken);

      // ambil model dan image dari request.payload
      const { model } = request.server.app;  
      const { image } = request.payload;
        if (!image) {
          return h.response({
              status: 'fail',  
              error : false,
              message: 'Image is required'
          }).code(400);
        }
        
        const { result, confidenceScore } = await predictWaste(model, image);
        const wasteType = result;
        const price = await FirebaseService.getWastePrice(wasteType);
        console.log('price:', price);
        const createAt = new Date().toISOString();

        const data = {
            tpsId: decoded.tpsId, 
            result: wasteType,
            confidenceScore: confidenceScore,
            price: Number(price),
            createAt: createAt,
        };
        
        const response = h.response ({
            status: "success",
            error: false,
            data
        });
        response.code(200);
        return response;  


    }catch (error) {
        return h.response({
            status: 'fail',
            message: error.message //message dari predict service
        }).code(500);
    }
};

const saveTransaction = async (request, h) => {
  try { 
    const authorizationToken = request.headers["authorization"];
    const decoded = await AuthService.verifyToken(authorizationToken);

    const { image } = request.payload;
    if (!image) {
      return h.response({
          status: 'fail',  
          error : false,
          message: 'Image is required'
      }).code(400);
    }
    
    const { nasabahName, wasteType, weight, price, totalPrice } = request.payload;
    
    // <-- validation>
    const requiredFields = { nasabahName, wasteType, weight, price, totalPrice };
    const missingFields = Object.keys(requiredFields).filter(field => !requiredFields[field]);

    if (missingFields.length > 0) {
      return h.response({
        status: 'fail',
        message: `Fields are required: ${missingFields.join(', ')}`
      }).code(400);
    }
    // <-- end of validation>

    const imageUrl = await storeImage(image, wasteType); //get url
    const transactionId = crypto.randomUUID();
    const createAt = new Date().toISOString();

    await FirebaseService.storeData(transactionId, {
      imageUrl,
      createAt,
      totalPrice: Number(totalPrice),
      weight: Number(weight),
      price: Number(price),
      wasteType,
      tpsId:decoded.tpsId,
      nasabahName,
      transactionId,
    });
    
    const responese = h.response({
      status: 'success',
      error: false,
      message: 'Data has been saved successfully',
      url: imageUrl,
    })
    responese.code(201);
    return responese;


  } catch (error) {
    const respone = h.response({
      status: 'fail',
      message: `An error occurred while saving to the database, ${error.message}`
    })
    respone.code(401);
    return respone;
  }

};

const getHistoryHandler = async (request, h) => {
  const { tpsId } = request.query;

  if (!tpsId) {
    const response = h.response({
      status: 'fail',
      message: 'tpsId is required'
    });
    response.code(400);
    return response;
  }

  try {
    const db = new Firestore();
    const historyCollection = db.collection("transactionHistory").where("tpsId", "==", tpsId);
    const historySnapshot = await historyCollection.get();

    const data = [];

    historySnapshot.forEach((doc) => {
      const history = doc.data();
      data.push({
        id: doc.id,
        createAt: history.createAt,
        totalPrice: history.totalPrice,
        imgUrl: history.imgUrl,
        weight: history.weight,
        nasabahName: history.nasabahName,
        wasteType: history.wasteType,
        price: history.price,
        transactionId: history.transactionId,
        tpsId: history.tpsId
      });
    });

    if (data.length === 0) {
      const response = h.response({
        status: 'success',
        data: data,
        message: 'No history found'
      });
      response.code(204);
      return response;
    }

    const response = h.response({
      status: 'success',
      data: data,
    });
    response.code(200);
    return response;


  } catch (error) {
    const response = h.response({
      status: 'error',
      message: 'Failed to retrieve history',
    });
    response.code(500);
    return response;
  }
};

const getTransactionDetailHandler = async (request, h) => {
  const { transactionId } = request.query;

  if (!transactionId) {
    const response = h.response({
      status: 'fail',
      message: 'transactionId is required',
    });
    response.code(400);
    return response;
  }

  try {
    const db = new Firestore();
    const historyCollection = db.collection("transactionHistory").where("transactionId", "==", transactionId);
    const historySnapshot = await historyCollection.get();

    const data = [];

    historySnapshot.forEach((doc) => {
      const history = doc.data();
      data.push({
        id: doc.id,
        createAt: history.createAt,
        totalPrice: history.totalPrice,
        imgUrl: history.imgUrl,
        weight: history.weight,
        nasabahName: history.nasabahName,
        wasteType: history.wasteType,
        price: history.price,
        transactionId: history.transactionId,
        tpsId: history.tpsId
      });
    });

    if (data.length === 0) {
      const response = h.response({
        status: 'success',
        data: data,
        message: 'No history found',
      });
      response.code(204);
      return response;
    }

    const response = h.response({
      status: 'success',
      data: data,
    });
    response.code(200);
    return response;
  } catch (error) {
    const response = h.response({
      status: 'error',
      message: 'Failed to retrieve history',
    });
    response.code(500);
    return response;
  }
};

// <---- Kumpulan test handler ---->

// const predict2Handler = async (request, h) => {
//   try {
//       const { image } = request.payload;
//       if (!image) {
//           return h.response({
//               status: 'fail',
//               error: false,
//               message: 'Image is required'
//           }).code(400);
//       }

//       // Call the Python backend for prediction
//       const prediction = await predictService(image);

//       const response = h.response({
//           status: 'success',
//           data: prediction
//       });
//       response.code(200);
//       return response;
//   } catch (error) {
//       return h.response({
//           status: 'fail',
//           message: error.message
//       }).code(500);
//   }
// };

// const logoutHandler = async (request, h) => {
//   try {
//     const authorizationToken = request.headers['authorization'];
//     if (!authorizationToken) {
//       return h.response({
//         status: 'fail',
//         message: 'Authorization header is required',
//       }).code(400);
//     }

//     const token = authorizationToken.split(' ')[1];
//     if (!token) {
//       return h.response({
//         status: 'fail',
//         message: 'Bearer token is required',
//       }).code(400);
//     }

//     await AuthService.logoutUser(token);

//     return h.response({
//       status: 'success',
//       message: 'Successfully logged out',
//     }).code(200);
//   } catch (error) {
//     return h.response({
//       status: 'fail',
//       message: error.message,
//     }).code(500);
//   }
// };

// const testSaveHandler = async (request, h) => {
//   try { 
//     const authorizationToken = request.headers["authorization"];
//     const decoded = await AuthService.verifyToken(authorizationToken);

//     const { image } = request.payload ;
//     if (!image) {
//       return h.response({
//           status: 'fail',  
//           error : false,
//           message: 'Image is required'
//       }).code(400);
//     }

//     const { nasabahName, wasteType, totalPrice } = request.payload;

//     // <--- validation>
//     const requiredFields = { nasabahName, wasteType, totalPrice, image };
//     const missingFields = Object.keys(requiredFields).filter(field => !requiredFields[field]);
  
//     if (missingFields.length > 0) {
//       return h.response({
//         status: 'fail',
//         message: `Fields are required: ${missingFields.join(', ')}`
//       }).code(400);
//     }
//     // <--- end of validation>

//     const imageUrl = await storeImage(image, wasteType);
//     const transactionId = crypto.randomUUID();

//     const wastePrice = await FirebaseService.getWastePrice(wasteType);
//     // const totalPrice = weight * wastePrice;


//     await FirebaseService.storeData(transactionId, {
//       imageUrl,
//       nasabahName,
//       totalPrice,
//       tpsId:decoded.tpsId,
//       transactionId,
//       wasteType,
//       wastePrice,
//     });

//     const responese = h.response({
//       status: 'success',
//       error: false,
//       message: 'Data has been saved successfully',
//       url: imageUrl,
//     })
//     responese.code(201);
//     return responese;

//   } catch (error) {
//     const respone = h.response({
//       status: 'fail',
//       message: `Terjadi kesalahan saat menyimpan data, ${error.message}`
//     })
//     respone.code(401);
//     return respone;
//   }
// };

// test handler for token verification
// const testHandler = async (request, h) => {
//   try {
//       const authorizationToken = request.headers["authorization"];
//       const decoded = await AuthService.verifyToken(authorizationToken);

//       const userDoc = await usersCollection.doc(decoded.email).get();
//       const userData = userDoc.data();

//       const newRefreshToken = jwt.sign(
        
//       )

//       const response = h.response({
//           status: 'success',
//           message: 'Token is valid',
//           decoded
//       });
//       response.code(200);
//       return response;
      
//   } catch (error) {  // ambil error dari verifyToken
//       const response = h.response({
//           status: 'fail',
//           message: error.message
//       });
//       response.code(401);
//       return response;
//   }
// }
// <---- Kumpulan test handler ---->

module.exports = {
    registerHandler,
    verifyRegistrationHandler,
    loginHandler,
    predictHandler,
    saveTransaction,
    getHistoryHandler,
    getTransactionDetailHandler,
    requestPasswordResetHandler,
    resetPasswordHandler,
    // testHandler,
    // testSaveHandler,
};