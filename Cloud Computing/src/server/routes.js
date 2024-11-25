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
]


module.exports = routes;