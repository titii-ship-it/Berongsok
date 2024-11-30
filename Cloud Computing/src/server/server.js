require('dotenv').config();
const Hapi = require('@hapi/hapi');
const routes = require('./routes');
const loadModel = require ('../services/loadModel');

const init = async () => {
  const server = Hapi.server({
    port: process.env.PORT || 8080,
    host: '0.0.0.0',
  });

  
  const model = await loadModel();
  server.app.model = model;

  console.log('JWT_SECRET:', process.env.JWT_SECRET);
  console.log('GOOGLE_APPLICATION_CREDENTIALS:', process.env.GOOGLE_APPLICATION_CREDENTIALS);
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