const tf = require('@tensorflow/tfjs-node');
const sharp = require('sharp');

async function predictWaste(model, imageStream) {
    try {
        // Baca stream gambar menjadi buffer
        const chunks = [];
        for await (const chunk of imageStream) {
            chunks.push(chunk);
        }
        const imageBuffer = Buffer.concat(chunks);

        // Konversi gambar ke format PNG
        const pngBuffer = await sharp(imageBuffer)
            .png()
            .toBuffer();

        // Decode buffer gambar menjadi tensor
        const tensor = tf.node
            // .decodeImage(pngBuffer, 3)
            // .resizeNearestNeighbor([224, 224])
            // .expandDims()
            // .toFloat();
            .decodeJpeg(pngBuffer)
            .resizeNearestNeighbor([224, 224])
            .expandDims()
            .toFloat()
        console.log("mengubah gambar ke tensor")

<<<<<<< HEAD
        // Melakukan prediksi
=======
        const wasteLabel = [ "Can", "Cardboard", "Glass Bottle", "Paper", "Plastic Bottle", "Plastic Cup"];
        console.log("mencoba prediksi gambar")
>>>>>>> c9ca095c2b0168d27baa65a8a45fee55c6bc4e16
        const prediction = model.predict(tensor);
        const score = await prediction.data();
        const confidenceScore = Math.max(...score) * 100;

        const classResult = tf.argMax(prediction, 1).dataSync()[0];
        const wasteLabel = ['Can', 'Cardboard', 'Glass Bottle', 'Paper', 'Plastic Bottle', 'Plastic Cup'];
        // const wasteLabel = ['Plastic Cup', 'Cardboard', 'Can', 'Glass Bottle', 'Plastic Bottle', 'Paper'];
        const result = wasteLabel[classResult];
        console.log('result:', result);

        return {
            result,
            confidenceScore
        };

    } catch (error) {
        throw new Error(`Terjadi kesalahan dalam melakukan prediksi: ${error.message}`);
    }
}

module.exports = predictWaste;