const handlers = require('./handlers');

const routes = [
    {
      method: '*', 
      path: '/',
      handler: (request,h) => {
        const response = h.response({
          status: 'success',
          message: 'Welcome to Berongsok Backend Server',
        })
        response.code(200);
        return response;
      },
    },
    {
      method: '*',
      path: '/{any*}',
      handler: (request,h) => {
        const response = h.response({
          status: 'fail',
          message: 'Please check your request path',
        })
        response.code(404);
        return response;
      }
    },
    {
      method: 'POST',
      path: '/register',
      handler: handlers.registerHandler,
    },
    {
      method: 'POST',
      path: '/verify-registration',
      handler: handlers.verifyRegistrationHandler,
    },
    {
      method: 'POST',
      path: '/login',
      handler: handlers.loginHandler,
    },
    {
      method: 'POST',
      path: '/predict',
      handler: handlers.predictHandler,
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
      handler: handlers.saveTransaction,
      options: {
        payload: {
          maxBytes: 5 * 1024 * 1024, // Batas ukuran payload 
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
      path: '/transactionhistory/detail',
      handler: handlers.getTransactionDetailHandler,
    },
    {
      method: 'POST',
      path: '/request-password-reset',
      handler: handlers.requestPasswordResetHandler,
    },
    {
      method: 'POST',
      path: '/reset-password',
      handler: handlers.resetPasswordHandler,
    },
    {
      method: 'GET',
      path: `/dashboard`,
      handler: handlers.getDashboardHandler,
    },
]


module.exports = routes;