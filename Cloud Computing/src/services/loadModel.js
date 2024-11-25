const tf = require('@tensorflow/tfjs-node');

async function loadModel() {
    const model = tf.loadGraphModel(process.env.MODEL_PATH);
    return model;
}

module.exports = loadModel;