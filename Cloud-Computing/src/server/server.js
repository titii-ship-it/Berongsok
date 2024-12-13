require('dotenv').config();
const Hapi = require('@hapi/hapi');
const routes = require('./routes');
const loadModel = require ('../services/loadModel');

const init = async () => {
  const server = Hapi.server({
    port: process.env.PORT || 8080,
    host: '0.0.0.0',
  });

  const requiredEnvVars = [
    'MODEL_PATH',
    'JWT_SECRET',
    'GCLOUD_PROJECT',
    'STORAGE_BUCKET',
    'EMAIL_USERNAME',
    'EMAIL_PASSWORD'
  ];
  
  const validateEnv = () => {
    const missing = requiredEnvVars.filter(key => !process.env[key]);
    if (missing.length > 0) {
      console.error(`Missing required environment variables: ${missing.join(', ')}`);
      process.exit(1);
    }
    console.log('-> Environment variables loaded successfully');
  };
  validateEnv();

  const model = await loadModel();
  server.app.model = model;

  server.route(routes);


  server.ext('onPreResponse', function (request, h) {
    const response = request.response;
    if (response.isBoom) {
      console.log(response);
        const newResponse = h.response({
            status: 'fail',
            message: response.message,
            message2: "Tolong cek kembali request method anda"
        })
        newResponse.code(response.output.statusCode)
        return newResponse;
    }
    return h.continue;
  });

  await server.start();
  console.log(`Server running on ${server.info.uri}`);
};  

init();