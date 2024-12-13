const tf = require('@tensorflow/tfjs-node');
const errorService = require('./error');

async function predictWaste(model, imageStream) {
    try {
        if (!model || typeof model.predict !== 'function') {
            throw new errorService.InternalServerError('Model is not loaded properly');
        }
        // Read image stream into buffer
        const chunks = [];
        for await (const chunk of imageStream) {
            chunks.push(chunk);
        }
        const imageBuffer = Buffer.concat(chunks);

        // Decode image buffer into tensor
        const tensor = tf.node.decodeImage(imageBuffer, 3)
            .resizeBilinear([224, 224])
            .expandDims()
            .toFloat()
            .div(tf.scalar(255.0));

        // Make prediction
        const prediction = model.predict(tensor);
        const score = await prediction.array();
        const confidenceScore = Math.max(...score[0]) * 100;
        const classResult = score[0].indexOf(Math.max(...score[0]));
        const wasteLabel = ['Can', 'Cardboard', 'Glass Bottle', 'Paper', 'Plastic Bottle', 'Plastic Cup'];
        const result = wasteLabel[classResult];

        return {
            result,
            confidenceScore
        };

    } catch (error) {
        console.error(`An error occurred during prediction: ${error.message}`);
        throw new errorService.InternalServerError(`An error occurred. Please try again or contact support if the problem persists.`);
    }
}

module.exports = predictWaste;