const handlers = require('./handlers');

const routes = [
    {
      //menangani semua tipe request
      method: '*', 
      path: '/',
      handler: (request,h) => {
        const responese = h.response({
          status: 'success',
          message: 'Welcome to Berongsok Backend Server',
        })
        responese.code(200);
        return responese;
      },
    },
    {
      method: 'POST',
      path: '/register',
      handler: handlers.registerHandler,
    },
    {
      method: 'POST',
      path: '/login',
      handler: handlers.loginHandler,
    },
    {
      method: 'POST',
      path: '/predict',
      handler: handlers.testHandler,
    },
    {
      method: 'POST',
      path: `/saveTransaction`,
      handler: handlers.testSaveHandler,
      // handler: handlers.saveTransaction,
      options: {
        payload: {
          maxBytes: 5 * 1024 * 1024, // Batas ukuran payload 5 MB
          output: 'stream',
          parse: true,
          allow: 'multipart/form-data',
          multipart: true,
        }
      }
    },
    {
      method: 'POST',
      path: `/waste/transactions`,
      handler: handlers.testSaveHandler,
      // handler: handlers.saveTransaction,
      options: {
        payload: {
          maxBytes: 5 * 1024 * 1024, // Batas ukuran payload 5 MB
          output: 'stream',
          parse: true,
          allow: 'multipart/form-data',
          multipart: true,
        }
      }
    },
    {
      method: 'GET',
      path: '/transactionhistory',
      handler: handlers.getHistoryHandler,
    },
    {
      method: 'GET',
      path: '/transactionbyid',
      handler: handlers.getHistoryByIdHandler,
    }
]


module.exports = routes;