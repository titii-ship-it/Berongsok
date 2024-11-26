const FirebaseService = require('../services/firebase');
const predictWaste = require('../services/predict');
const AuthService = require('../services/userAuth');
const { Firestore } = require('@google-cloud/firestore');
const storeImage = require('../services/storeImage');
const crypto = require('crypto');

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
      message: error.message,
    })
    respone.code(400);
    return respone;
  }
};


const predictHandler = async (request, h) => {
    
  // ambil model dan image dari request.payload
  const { image } = request.payload;
  const { model } = request.server.app;  
  
  try {
        // CEK user login 
        const authorizationToken = request.headers["authorization"];
        const decoded = await AuthService.verifyToken(authorizationToken);
        
        const { wasteType, confidenceScore } = await predictWaste(model, image);
        const price = await FirebaseService.getWastePrice(wasteType);
        const tpsId = decoded.tpsId;

        const data = {
            tpsId,
            result,
            confidenceScore,
            price,
            gambar,
        };
        
        // Simpan data prediksi sementara di server
        request.server.app.predictionData = data;
        
        const response = h.response ({
            status: "success",
            data
        });
        response.code(200);


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
    
    const { nasabahName, wasteType, weight, price, totalPrice } = request.payload;
    
    const transactionId = crypto.randomUUID();
    const createAt = new Date().toISOString();
  
    const predictionData = request.server.app.predictionData;

    if (!predictionData || !predictionData.image) {
        return h.response({
            status: 'fail',
            message: 'Prediction data or image not found'
        }).code(400);
    }
    const imageBuffer = Buffer.from(predictionData.image, 'base64');
    const imageUrl = await storeImage(imageBuffer, `${transactionId}.jpg`);

    await FirebaseService.storeData(transactionId, {
      createAt,
      imageUrl,
      nasabahName,
      price,
      totalPrice,
      tpsId,
      transactionId,
      wasteType,
      weight,
    });

  } catch (error) {
    const respone = h.response({
      status: 'fail',
      message: error.message
    })
    respone.code(401);
    return respone;
  }

};

const testHandler = async (request, h) => {
    try {
        const authorizationToken = request.headers["authorization"];
        const decoded = await AuthService.verifyToken(authorizationToken);

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

const getHistoryHandler = async (request, h) => { //by id tps
    
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
        const historyData = await FirebaseService.getWasteHistory(tpsId);

        // jika data kosong
        if (historyData.length === 0) {
            const response = h.response({
                status: 'success',
                data: historyData,
                message: 'No history found'
            });
            response.code(204);
            return response;
        }

        const response = h.response({
            status: 'success',
            data: historyData
        });
        response.code(200);
        return response;
    } catch (error) {
        const response = h.response({
            status: 'fail',
            message: error.message
        });
        response.code(500);
        return response;
    }
  };
  

// const getTransactionHandler = async (request, h) => {
//     // Get Specific Transaction logic
// };

module.exports = {
    registerHandler,
    loginHandler,
    predictHandler,
    saveTransaction,
    getHistoryHandler,
    testHandler
};