// untuk upload history predict ke firebase
async function storeData(id, data) {
    const db = new Firestore({
        keyFilename: process.env.GOOGLE_APPLICATION_CREDENTIALS,
        projectId: process.env.GCLOUD_PROJECT,
    });
   
    const predictCollection = db.collection('wasteCollection');
    return predictCollection.doc(id).set(data);
    // .set diguanakn untuk menyimpan data ke firestore
}

async function getWastePrice(wasteType){
    const db = new Firestore({
        keyFilename: process.env.GOOGLE_APPLICATION_CREDENTIALS,
        projectId: process.env.GCLOUD_PROJECT,
    });

    const priceCollection = db.collection('wasteTypeCollection');
    const priceData = await priceCollection.doc(wasteType).get();
    return priceData.data();
}