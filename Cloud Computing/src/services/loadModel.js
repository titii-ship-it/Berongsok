const tf = require('@tensorflow/tfjs-node');


async function loadModel() {
    try {
        const model = await tf.loadLayersModel(`file://${process.env.MODEL_PATH}`);
        console.log('model loaded');
        return model;
    } catch (error) {
        console.error(`error when loading model : ${error.message}!`);
    }
}

module.exports = loadModel;