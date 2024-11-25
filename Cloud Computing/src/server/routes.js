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
      path: '/predicthistory',
      handler: handlers.getHistoryHandler,
    }
]


module.exports = routes;