const { Firestore } = require('@google-cloud/firestore');
const AuthService = require('./userAuth');
const db = require(`./firestore`);

// untuk upload history predict ke firebase
async function storeData(id, data) {
    const predictCollection = db.collection('transactionHistory');
    return predictCollection.doc(id).set(data);
    // .set digunakan untuk menyimpan data ke firestore
}

async function getWastePrice(wasteType){
    const normalizeWasteType = wasteType.toLowerCase().replace(/\s+/g, "-");
    console.log(normalizeWasteType);
    const priceCollection = db.collection('wastePricing');
    const priceData = await priceCollection.doc(normalizeWasteType).get();

    if (!priceData.exists) {
        throw new Error(`Price data for waste type "${wasteType}" not found`);
    }

    const price = priceData.data();
    return price.wastePrice;
    
}

async function getDataDashboard(authorizationToken){
    const decoded = await AuthService.verifyToken(authorizationToken);

    const tpsId = decoded.tpsId;
    const email = decoded.email;
    
    if (!email || typeof email !== 'string' || email.trim() === '') {
      throw new Error('Invalid email address, please register or login again.');
    }

    // get username dari profil pengguna
    const usersCollection = db.collection('userProfile');
    const userDoc = await usersCollection.doc(email).get();
    if (!userDoc.exists) {
      throw new Error('User not found.');
    }
    const userData = userDoc.data();
    const username = userData.username;

    // get riwayat transaksi tpsId
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