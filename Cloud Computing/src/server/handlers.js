const { Firestore, FieldValue } = require('@google-cloud/firestore');
const FirestoreService = require('../services/firestoreService');
const predictWaste = require('../services/predict');
const AuthService = require('../services/userAuth');
const EmailService = require('../services/emailServices');
const storeImage = require('../services/storeImage');
const crypto = require('crypto');
const db = require('../services/firestore');


const usersCollection = db.collection('userProfile');

const registerHandler = async (request, h) => {
  const { username, email, password } = request.payload;

  try {
    // <-- validation>
    const requiredFields = { username, email, password };
    const missingFields = Object.keys(requiredFields).filter(field => !requiredFields[field]);

    if (missingFields.length > 0) {
      return h.response({
        status: 'fail',
        message: `Fields are required: ${missingFields.join(', ')}`
      }).code(400);
    }
    // <-- end of validation>
    await AuthService.registerUser(username, email, password);
    const response = h.response({
      error: false,
      message: 'OTP has been sent to your email. Please verify to complete registration.',
    })
    response.code(200);
    return response;

  } catch (error) {
    const response = h.response({
      error: true,
      message: error.message,
    })
    response.code(400);
    return response;
  }
};

const verifyRegistrationHandler = async (request, h) => {
  const { email, otp } = request.payload;

  try {
    if (!email || !otp) {
      return h.response({
        error: true,
        message: 'Email and OTP are required.',
      }).code(400);
    }
    // Get user from pendingUsers collection
    const pendingUsersCollection = db.collection('pendingUsers');
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
  try {
    const { email, password } = request.payload;
    if (!email || !password) {
      return h.response({
        status: 'fail',
        message: 'Email and password are required'
      }).code(400);
    }
    
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
    const usersCollection = db.collection('userProfile');
    const userDoc = await usersCollection.doc(email).get();
    if (!userDoc.exists) {
      throw new Error("Email tidak terdaftar.");
    }

    // Generate OTP
    const otp = Math.floor(100000 + Math.random() * 900000).toString();

    // Simpan OTP ker firestore -> 15 menit kadaluarsa
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
    const usersCollection = db.collection('userProfile');
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
      const { image } = request.payload; //jangan di ganti
        if (!image) {
          return h.response({
              status: 'fail',  
              error : false,
              message: 'Image is required'
          }).code(400);
        }
        
        const { result, confidenceScore } = await predictWaste(model, image);
        const wasteType = result;
        const price = await FirestoreService.getWastePrice(wasteType);
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

    const { image } = request.payload.image; //jangan di ganti
    if (!image ) {
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

    await FirestoreService.storeData(transactionId, {
      transactionId,
      tpsId:decoded.tpsId,
      nasabahName,
      weight: Number(weight),
      price: Number(price),
      wasteType,
      totalPrice: Number(totalPrice),
      createAt,
      imageUrl,
    });
    
    const response = h.response({
      status: 'success',
      error: false,
      message: 'Data has been saved successfully',
      url: imageUrl,
    })
    response.code(201);
    return response;


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
    const historyCollection = db.collection("transactionHistory").where("tpsId", "==", tpsId);
    const historySnapshot = await historyCollection.get();

    const data = [];

    historySnapshot.forEach((doc) => {
      const history = doc.data();
      data.push({
        id: doc.id,
        createAt: history.createAt,
        totalPrice: history.totalPrice,
        imgUrl: history.imageUrl,
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
    const historyCollection = db.collection("transactionHistory").where("transactionId", "==", transactionId);
    const historySnapshot = await historyCollection.get();

    const data = [];

    historySnapshot.forEach((doc) => {
      const history = doc.data();
      data.push({
        id: doc.id,
        createAt: history.createAt,
        totalPrice: history.totalPrice,
        imgUrl: history.imageUrl,
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

const getDashboardHandler = async (request, h) => {
  try {
    const authorizationToken = request.headers["authorization"];
    const data = await FirestoreService.getDataDashboard(authorizationToken);

    const response = h.response({
      status: 'success',
      username: data.username,
      tpsId: data.tpsId,
      totalWeight: data.totalWeight,
      totalPrice: data.totalPrice,
      data: data.mainData,
    });
    response.code(200);
    return response;
  } catch (error) {
    console.log(error.message)
    return h.response({
      status: 'fail',
      message: "Failed to get dashboard data",
    }).code(500);
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

//     const wastePrice = await FirestoreService.getWastePrice(wasteType);
//     // const totalPrice = weight * wastePrice;


//     await FirestoreService.storeData(transactionId, {
//       imageUrl,
//       nasabahName,
//       totalPrice,
//       tpsId:decoded.tpsId,
//       transactionId,
//       wasteType,
//       wastePrice,
//     });

//     const response = h.response({
//       status: 'success',
//       error: false,
//       message: 'Data has been saved successfully',
//       url: imageUrl,
//     })
//     response.code(201);
//     return response;

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
    getDashboardHandler,
    // testHandler,
    // testSaveHandler,
};