This document provides the specifications for the API endpoints of our application. Each endpoint is described with its URL, HTTP method, request parameters, and possible responses. The API is designed to be RESTful and follows standard conventions for request and response formats.

## 1. Register

- URL
  - `/register`
- Method
  - POST
- Request Body :
  - `username` as `string`
  - `email` as string, must be `unique`
  - `password` as string, must be at least 8 characters

if successful:

```json
{
  "error": false,
  "message": "Your account has been successfully created."
}
```

if fail - email already taken:

```json
{
  "error": true,
  "message": "This email is already registered"
}
```

## 2. Login

- URL
  - `/login`
- Method
  - POST
- Request Body
  - `email` as `string`
  - `password` as `string`

Respone:

```json
{
  "error": false,
  "message": "success",
  "loginResult": {
    "tpsId": "user-yj5pc_LARC_AgK61",
    "name": "TPS name",
    "token": "generated_user_token"
  }
}
```

if fail - email or password is incorrect.

```json
{
  "error": true,
  "message": "The email or password you entered is incorrect."
}
```

if fail - due to account was not found

```json
{
  "error": true,
  "message": "We couldn't find an account with that information"
}
```

## 3. Prediksi

- URL
  - `/predict`
- Method
  - POST
- Header

  - Content-Type: `multipart/form-data`
  - Authorization: Bearer \<token>

- Request body:
  - image as file

if successful :

```json
{
<<<<<<< HEAD
	"error": false,
	"message": "success",
	"data": {
		"result": "<Class Model> as string ",
		"confidenceScore": "<score> as float " ,
		"price": "harga_berdasarkan_label (perKG)",
		"CreateAt": "<timestamp> as datetime" ,
	}
=======
  "error": false,
  "message": "success",
  "data": {
    "transactionId": "<id> as string",
    "result": "<Class Model> as string ",
    "confidenceScore": "<score> as float ",
    "price": "harga_berdasarkan_label (perKG)",
    "CreateAt": "<timestamp> as datetime"
  }
>>>>>>> e3f2f72062c2e2d3fdb14c5ef7aa71a1ab6890b9
}
```

If it fails - due to the photo being larger than 5 MB :

```json
{
  "status": false,
  "message": "Payload content length greater than maximum allowed: 5000000"
}
```

If it fails - due to an error in the backend or the model :

```json
{
  "status": "fail",
  "message": "Terjadi kesalahan dalam melakukan prediksi"
}
```

## 4. addList

Endpoint to store data in the database

- URL

  - `/waste/transactions`

- Header

  - Authorization: Bearer \<token>

- Method
  - POST

Request body:

- nasabahName -> name of the person who brought the trash
- wasteType
- price -> waste price per kg
- weight
- totalPrice -> price \* weight

Data to save to the database

- transactionId -> id generated during prediction
- tpsId-> id of the TPS where the waste was sold
- nasabahName
- wasteType
- price
- weight
- totalPrice
- imgUrl
- createAt

if successful :

```json
{
  "status": true, // Indicates the server successfully saved the data
  "error": false, // Indicates the user is logged in
  "message": "success"
}
```

if fail :

- An error occurred while saving to the database
- Some required fields are empty

```json
{
  "status": false,
  "error": false,
  "message": "An error occurred while saving to the database"
}
```

if fail - User not logged in:

```json
{
  "status": false, // Indicates the server failed to save the data
  "error": true, // Indicates the user is logged in
  "message": "User not logged in"
}
```

# 5. getHistory

Endpoint to retrieve prediction history

- URL

  - `/predictHistory`

- Method

  - GET

- request body
  - tpsId

response (Status Code: 200):

```json
{
  "status": "success",
  "data": [
    {
      "idTransaksi": "13e907b3-4213-42ad-b12b-b9b7e12eb90e", // id generated during prediction type of waste
      "data": {
        "tpsId": "", //  id of the TPS where the waste was sold
        "nasabahName": "",
        "wasteType": "",
        "price": "",
        "weight": "",
        "totalPrice": "",
        "imgUrl": "",
        "createAt": "<timestamp> as datetime"
      }
    },

    {
      "idTransaksi": "13e907b3-4232-42ad-b12b-b9b7e12eb90e",
      "data": {
        "tpsId": "",
        "nasabahName": "",
        "wasteType": "",
        "price": "",
        "weight": "",
        "totalPrice": "",
        "imgUrl": "",
        "createAt": "<timestamp> as datetime"
      }
    }
  ]
}
```

if fail :

```json
{
  "status": "fail",
  "data": [],
  "message": "No history found"
}
```

## 6. get transaksi tertentu

Endpoint to retrieve prediction history by transaction ID

- URL

  - `/predictHistory/{transactionid}`

- Method

  - GET

- request parameter
  - tpsId

response :

```json
{
  "status": "success",
  "data": [
    {
      "idTransaksi": "13e907b3-4213-42ad-b12b-b9b7e12eb90e",
      "data": {
        "tpsId": "",
        "nasabahName": "",
        "wasteType": "",
        "price": "",
        "weight": "",
        "totalPrice": "",
        "imgUrl": "",
        "createAt": ""
      }
    }
  ]
}
```

if fail - no transaction (Status code : 404):

```json
{
  "status": "fail",
  "data": [],
  "message": "Transaction not found"
}
```

if fail - internal server error (Status code : 500) :

```json
{
  "status": "fail",
  "data": [],
  "message": "An internal server error occurred, please try again later"
}
```
