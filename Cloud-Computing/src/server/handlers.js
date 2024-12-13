const FirestoreService = require('../services/firestoreService');
const predictWaste = require('../services/predict');
const AuthService = require('../services/userAuth');
const storeImage = require('../services/storeImage');
const crypto = require('crypto');
const db = require('../services/firestore');
const errorService = require('../services/error');

function normalizeEmail(email) {
  // jika email tidak valid
  const emailRegex = /^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*\.[a-zA-Z]{2,}$/;
                  // /^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/;
  if (!emailRegex.test(email)) {
    throw new errorService.BadRequestError('Invalid email format');
  }
  return email.trim().toLowerCase();
}

function normalizeUsername(username) {
  return username.trim();
}

function normalizeWasteType(wasteType) {
  return wasteType
    .trim()
    .split(' ') 
    .map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase()) 
    .join(' ');
}


const registerHandler = async (request, h) => {
  const { username, email, password } = request.payload;

  try {
    // <-- validation>
    const requiredFields = { username, email, password };
    const missingFields = Object.keys(requiredFields).filter(field => !requiredFields[field]);

    if (missingFields.length > 0) {
      throw new errorService.BadRequestError(`Fields are required: ${missingFields.join(', ')}`);
    }
    // <-- end of validation>
    const normalizedEmail = normalizeEmail(email);
    const normalizedUsername = normalizeUsername(username);

    await AuthService.registerUser(normalizedUsername, normalizedEmail, password);
    
    const response = h.response({
      error: false,
      message: 'OTP has been sent to your email. Please verify to complete registration.',
    })
    response.code(200);
    return response;

  } catch (error) {
    const statusCode = error.statusCode || 500;
    const response = h.response({
      error: true,
      status: 'fail',
      message: error.message,
    })
    response.code(statusCode);
    return response;
  }
};

const verifyRegistrationHandler = async (request, h) => {
  const { email, otp } = request.payload;
  
  try {
    if (!email || !otp) {
      throw new errorService.BadRequestError('Email and OTP are required.');
    }

    // Get user from pendingUsers collection
    const normalizedEmail = normalizeEmail(email);
    await AuthService.verifyRegistration(normalizedEmail);
    
    const response = h.response({
      error: false,
      message: 'Your account has been successfully created.',
    });
    response.code(201);
    return response;

  } catch (error) {
    const statusCode = error.statusCode || 500;
    const response = h.response({
      error: true,
      message: error.message,
    });
    response.code(statusCode);
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
    const normalizedEmail = normalizeEmail(email);
    const loginResult = await AuthService.loginUser(normalizedEmail, password);
    
    const response = h.response({
      error: false,
      message: 'success',
      loginResult,
    })
    response.code(200);
    return response;

  } catch (error) {
    const statusCode = error.statusCode || 500;
    const response = h.response({
      error: true,
      message: error.message,
    })
    response.code(statusCode);
    return response;
  }
};

// send OTP
const requestPasswordResetHandler = async (request, h) => {
  const { email } = request.payload;

  try {
    if (!email) {
      throw new errorService.BadRequestError('Email is required.');
    }
    const normalizedEmail = normalizeEmail(email);
    await AuthService.requestPasswordReset(normalizedEmail);

    return h.response({
      status: 'success',
      message: 'OTP telah dikirim ke email Anda.',
    }).code(200);

  } catch (error) {
    const statusCode = error.statusCode || 500;
    return h.response({
      status: 'fail',
      message: error.message,
    }).code(statusCode);
  }
};

// reset password
const resetPasswordHandler = async (request, h) => {
  const { email, otp, newPassword } = request.payload;

  try {
    // <-- validation>
    const requiredFields = { email, otp, newPassword };
    const missingFields = Object.keys(requiredFields).filter(field => !requiredFields[field]);
    if (missingFields.length > 0) {
      throw new errorService.BadRequestError(`Fields are required: ${missingFields.join(', ')}`);
    }
    // <-- end of validation>

    const normalizedEmail = normalizeEmail(email);
    await AuthService.resetPassword(normalizedEmail, otp, newPassword);

    return h.response({
      status: 'success',
      message: 'Password Anda berhasil diubah.',
    }).code(201);

  } catch (error) {
    const statusCode = error.statusCode || 500;
    return h.response({
      status: 'fail',
      message: error.message,
    }).code(statusCode);
  }
};

const predictHandler = async (request, h) => {
  try {
    
    const authorizationToken = request.headers["authorization"];
    const decoded = await AuthService.verifyToken(authorizationToken);

    const { model } = request.server.app;  
    const { image } = request.payload; 
    if (!image || !image.hapi || !image._data || image._data.length === 0 || !image.hapi.headers["content-type"].startsWith("image/")) {
      return h.response({
          status: 'fail',  
          error : false,
          message: 'Image is required, please check the image format'
      }).code(400);
    }

    const { result, confidenceScore } = await predictWaste(model, image);
    
    const wasteType = result;
    const normalizedWasteType = normalizeWasteType(wasteType);
    
    const price = await FirestoreService.getWastePrice(wasteType);
    const createAt = new Date().toISOString();
    
    const data = {
        tpsId: decoded.tpsId, 
        result: normalizedWasteType,
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
    const statusCode = error.statusCode || 500;
    return h.response({
        status: 'fail',
        message: error.message
    }).code(statusCode);
  }
};

const saveTransaction = async (request, h) => {
  try { 
    const authorizationToken = request.headers["authorization"];
    const decoded = await AuthService.verifyToken(authorizationToken);

    const { image } = request.payload; 
    if (!image || !image.hapi || !image._data || image._data.length === 0 || !image.hapi.headers["content-type"].startsWith("image/")) {
      return h.response({
          status: 'fail',  
          error : false,
          message: 'Image is required, please check the image format'
      }).code(400);
    }
    const { nasabahName, wasteType, weight, price, totalPrice } = request.payload;
    
    // <-- validation>
    const requiredFields = { nasabahName, wasteType, weight, price, totalPrice };
    const missingFields = Object.keys(requiredFields).filter(field => !requiredFields[field]);

    if (missingFields.length > 0) {
      throw new errorService.BadRequestError(`Fields are required: ${missingFields.join(', ')}`);
    }
    // <-- end of validation>

    const normalizedWasteType = normalizeWasteType(wasteType);
    const imageUrl = await storeImage(image, wasteType); //get url
    const transactionId = crypto.randomUUID();
    const createAt = new Date().toISOString();

    if (isNaN(Number(weight)) || isNaN(Number(price))) {
      throw new errorService.BadRequestError('Weight and price must be a valid number');
    }
    
    const calculatedTotal = Number(weight) * Number(price);
    if (Number(totalPrice) !== calculatedTotal) {
      throw new errorService.BadRequestError('Total price calculation is incorrect');
    }

    await FirestoreService.storeData(transactionId, {
      transactionId,
      tpsId:decoded.tpsId,
      nasabahName,
      weight: Number(weight),
      price: Number(price),
      wasteType: normalizedWasteType,
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
    const statusCode = error.statusCode || 500;
    const response = h.response({
      status: 'fail',
      message: `An error occurred while saving to the database, ${error.message}`
    })
    response.code(statusCode);
    return response;
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
    const statusCode = error.statusCode || 500;
    return h.response({
      status: 'fail',
      message: error.message,
    }).code(statusCode);
  }
};

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
};