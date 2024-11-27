async function predictWaste(model, image){
    try {
        console.log("mengubah gambar ke tensor...")
        const tensor =  tf.node
            .decodeImage(image, 3)
            .resizeNearestNeighbor([700, 700])
            .expandDims()
            .toFloat()
        console.log("mengubah gambar ke tensor")

        const wasteLabel = [ "Can", "Cardboard", "Glass Bottle", "Paper", "Plastic Bottle", "Plastic Cup"];
        console.log("mencoba prediksi gambar")
        const prediction = model.predict(tensor);
        const score = await prediction.data();
        const confidenceScore = Math.max(...score) * 100;

        const classResult = tf.argMax(prediction, 1).dataSync()[0];
        const result = wasteLabel[classResult];

        return {
            result,
            confidenceScore
        };

    } catch (error) {
        throw new Error('Terjadi kesalahan dalam melakukan prediksi'); //lempar ke handler
    }
}

module.exports = predictWaste;