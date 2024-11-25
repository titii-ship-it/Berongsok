const FirebaseService = require('../services/firebase');
const predictWaste = require('../services/predict');
const AuthService = require('../services/userAuth');
const { Firestore } = require('@google-cloud/firestore');
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
    const { image } = request.payload;
    const { model } = request.server.app;

    // Jika user belum login/tidak mengirimkan token
    //if 

    try {
        const { wasteType, confidenceScore } = await predictWaste(model, image);
        const price = await FirebaseService.getWastePrice(wasteType);
        
        const transactionId = crypto.randomUUID();
        const createAt = new Date().toISOString();

        const data = {
            transactionId,
            createAt,
            result,
            confidenceScore,
            price
        };
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

const testHandler = async (request, h) => {
    try {
        const authorization = request.headers.authorization;
        const decoded = await AuthService.verifyToken(authorization);

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
    // predictHandler,
    getHistoryHandler,
    // getTransactionHandler,
    testHandler
};