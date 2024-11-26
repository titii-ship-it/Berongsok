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

    const priceCollection = db.collection('wastePricing');
    const priceData = await priceCollection.doc(wasteType).get(); 
    return priceData.data();
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