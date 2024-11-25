async function predictWaste(model, image){
    try {
        const tensor =  tf.node
            .decodeImage(image, 3)
            .expandDims()
            .toFloat()

        const wasteLabel = [ "Can", "Cardboard", "Glass Bottle", "Paper", "Plastic Bottle", "Plastic_cup"];

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