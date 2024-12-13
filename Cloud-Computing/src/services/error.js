class ApplicationError extends Error {
  constructor(message, statusCode) {
    super(message);
    this.name = this.constructor.name;
    this.statusCode = statusCode || 500;
    Error.captureStackTrace(this, this.constructor);
  }
}

class BadRequestError extends ApplicationError {
  constructor(message) {
    super(message || 'Bad Request', 400);
  }
}

class UnauthorizedError extends ApplicationError {
  constructor(message) {
    super(message || 'Unauthorized', 401);
  }
}

class NotFoundError extends ApplicationError {
  constructor(message) {
    super(message || 'Not Found', 404);
  }
}

class InternalServerError extends ApplicationError {
  constructor(message) {
    super(message || 'Internal Server Error', 500);
  }
}

module.exports = {
  ApplicationError,
  BadRequestError,
  UnauthorizedError,
  NotFoundError,
  InternalServerError,
};