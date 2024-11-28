const FirebaseService = require('../services/firestore');
const predictWaste = require('../services/predict');
const AuthService = require('../services/userAuth');
const { Firestore } = require('@google-cloud/firestore');
const storeImage = require('../services/storeImage');
const crypto = require('crypto');
// const { get } = require('https');
const jwt = require('jsonwebtoken');
// const { channel } = require('diagnostics_channel');
// const predictService = require('../services/newPredictService');

const registerHandler = async (request, h) => {
  const { username, email, password } = request.payload;

  try {
    await AuthService.registerUser(username, email, password);
    const respone = h.response({
      error: false,
      message: 'Your account has been successfully created.',
    })
    respone.code(200);
    return respone;

  } catch (error) {
    const respone = h.response({
      error: true,
      message: error.message,
    })
    respone.code(400);
    return respone;
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
      message: `invalid token : ${error.message}`,
    })
    respone.code(400);
    return respone;
  }
};

const predictHandler = async (request, h) => {
  try {
      // ambil model dan image dari request.payload
      const { model } = request.server.app;  
      const { image } = request.payload ;
        if (!image) {
          return h.response({
              status: 'fail',  
              error : false,
              message: 'Image is required'
          }).code(400);
        }
        
        // CEK user login 
        const authorizationToken = request.headers["authorization"];
        const decoded = await AuthService.verifyToken(authorizationToken);
        
        const { result, confidenceScore } = await predictWaste(model, image);
        const wasteType = result;
        const price = await FirebaseService.getWastePrice(wasteType);
        console.log('price:', price);
        const createAt = new Date().toISOString();

        const data = {
            tpsId: decoded.tpsId, 
            result,
            confidenceScore,
            price,
            createAt,
        };
        
        const response = h.response ({
            status: "success",
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

    const { image } = request.payload ;
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
      transactionId,
      tpsId:decoded.tpsId,
      nasabahName,
      price,
      wasteType,
      weight,
      totalPrice,
      imageUrl,
      createAt,
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
      const history = {
        id: doc.id,
        history: doc.data(),
      };
      data.push(history);
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
      const history = {
        id: doc.id,
        history: doc.data(),
      };
      data.push(history);
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
const testHandler = async (request, h) => {
  try {
      const authorizationToken = request.headers["authorization"];
      const decoded = await AuthService.verifyToken(authorizationToken);

      const userDoc = await usersCollection.doc(decoded.email).get();
      const userData = userDoc.data();

      const newRefreshToken = jwt.sign(
        
      )

      const response = h.response({
          status: 'success',
          message: 'Token is valid',
          decoded
      });
      response.code(200);
      return response;
      
  } catch (error) {  // ambil error dari verifyToken
      const response = h.response({
          status: 'fail',
          message: error.message
      });
      response.code(401);
      return response;
  }
}
// <---- Kumpulan test handler ---->

module.exports = {
    registerHandler,
    loginHandler,
    predictHandler,
    saveTransaction,
    getHistoryHandler,
    getTransactionDetailHandler,
    // logoutHandler,
    testHandler,
    // testSaveHandler,
};