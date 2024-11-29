const { Firestore } = require('@google-cloud/firestore');

// untuk upload history predict ke firebase
async function storeData(id, data) {
    const db = new Firestore({
        keyFilename: process.env.GOOGLE_APPLICATION_CREDENTIALS,
        projectId: process.env.GCLOUD_PROJECT,
    });
   
    const predictCollection = db.collection('transactionHistory');
    return predictCollection.doc(id).set(data);
    // .set digunakan untuk menyimpan data ke firestore
}

async function getWastePrice(wasteType){
    const db = new Firestore({
        keyFilename: process.env.GOOGLE_APPLICATION_CREDENTIALS,
        projectId: process.env.GCLOUD_PROJECT,
    });
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

async function getWasteHistory(tpsId){
    const db = new Firestore({
        keyFilename: process.env.GOOGLE_APPLICATION_CREDENTIALS,
        projectId: process.env.GCLOUD_PROJECT,
    });

    const historyCollection = db.collection('transactionHistory');
    const historyData = await historyCollection.where('tpsId', '==', tpsId).get();
    return historyData.docs.map(doc => doc.data());
}

module.exports = { 
    storeData, 
    getWastePrice, 
    getWasteHistory 
};