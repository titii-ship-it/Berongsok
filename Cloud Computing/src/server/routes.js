const handlers = require('./handlers');

const routes = [
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