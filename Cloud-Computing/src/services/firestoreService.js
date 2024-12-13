const AuthService = require('./userAuth');
const db = require(`./firestore`);
const errorService = require('./error');

const defaultErrorMessage = 'Server error, Please try again or contact support if the problem persists.';

// untuk upload history predict ke firebase
async function storeData(id, data) {
    const predictCollection = db.collection('transactionHistory');
    return predictCollection.doc(id).set(data);
    // .set digunakan untuk menyimpan data ke firestore
}

async function getWastePrice(wasteType){
  try {
    const normalizeWasteType = wasteType.toLowerCase().replace(/\s+/g, "-");
    const priceCollection = db.collection('wastePricing');
    const priceData = await priceCollection.doc(normalizeWasteType).get();

    if (!priceData.exists) {
      throw new errorService.NotFoundError(`Price data for waste type "${wasteType}" not found`);
    }

    const price = priceData.data();
    return price.wastePrice;

  } catch (error) {
    if (error instanceof errorService.ApplicationError) {
      throw error;
    } else {
      console.error(`Error while getting waste price data for "${wasteType}"`);
      throw new errorService.InternalServerError(defaultErrorMessage);
    }
  }
}

async function getDataDashboard(authorizationToken){
  try{
    const decoded = await AuthService.verifyToken(authorizationToken);
    const tpsId = decoded.tpsId;
    const email = decoded.email;

    // get username data
    const usersCollection = db.collection('userProfile');
    const userDoc = await usersCollection.doc(email).get();
    if (!userDoc.exists) {
      throw new errorService.NotFoundError('User not found.');
    }
    const userData = userDoc.data();
    const username = userData.username;

    // get transaction history data 
    const historyCollection = db.collection('transactionHistory')
      .where('tpsId', '==', tpsId);
    const historySnapshot = await historyCollection.get();

    let totalWeight = 0;
    let totalPrice = 0;
    const wasteTypeData = {};

    historySnapshot.forEach(doc => {
      const data = doc.data();
      const { wasteType, weight, totalPrice: itemTotalPrice } = data;

      totalWeight += weight;
      totalPrice += itemTotalPrice;

      if (!wasteTypeData[wasteType]) {
        wasteTypeData[wasteType] = {
          wasteType,
          totalWeight: 0,
          totalPrice: 0,
        };
      }
      wasteTypeData[wasteType].totalWeight += weight;
      wasteTypeData[wasteType].totalPrice += itemTotalPrice;
    });

    const mainData = Object.values(wasteTypeData);
    return { username, totalWeight, totalPrice, mainData };

  } catch (error) {
    if (error instanceof errorService.ApplicationError) {
      throw error;
    } else {
      console.error(`Error while get dashboard data, ${error}`);
      throw new errorService.InternalServerError(defaultErrorMessage);
    }
  }
}

async function getWasteHistory(tpsId){
    const historyCollection = db.collection('transactionHistory');
    const historyData = await historyCollection.where('tpsId', '==', tpsId).get();
    return historyData.docs.map(doc => doc.data());
}

module.exports = { 
    storeData, 
    getWastePrice, 
    getWasteHistory,
    getDataDashboard,
};