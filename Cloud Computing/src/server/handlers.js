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


// const getHistoryHandler = async (request, h) => {
//     // Get History logic
//   };
  

// const getTransactionHandler = async (request, h) => {
//     // Get Specific Transaction logic
// };

module.exports = {
    registerHandler,
    loginHandler,
    // predictHandler,
    // getHistoryHandler,
    // getTransactionHandler,
};