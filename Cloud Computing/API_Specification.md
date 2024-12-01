This document provides the specifications for the API endpoints of our application. Each endpoint is described with its URL, HTTP method, request parameters, and possible responses. The API is designed to be RESTful and follows standard conventions for request and response formats.

# 1. Register Process

## a. Register
- URL
  - `/register`
- Method
  - POST
- Request Body :
  - `username` as `string`
  - `email` as `string`, must be `unique`
  - `password` as `string`, must be at least 8 characters

if successful:
```json
{
  "error": false,
  "message": "OTP has been sent to your email. Please verify to complete registration."
}
```

if fail - email already taken:
```json
{
  "error": true,
  "message": "This email is already registered"
}
```

if fail - tried registering less than 5 minutes ago:
```json
{
  "error": true,
  "message": "You have already tried registering less than 5 minutes ago. Please wait before trying again."
}
```



## b. register verification
- URL
  - `/verify-registration`
- Method
  - POST
- Request Body :
  - `email` as `string`
  - `otp` as `int`
if successful:

```json
{
  "error": false,
  "message": "Your account has been successfully created."
}
```

if fail - no pending registration:
```json
{
  "error": true, 
  "message": "No pending registration found for this email."
}
```

if fail - no pending registration:
```json
{
  "error": true, 
  "message": "OTP has expired. Please register again."
}
```


# 2. Login

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
  - image as `file`, must be a valid image file, max size 5MB

if successful :

```json
{
	"error": false, // bool
	"message": "success", //string 
	"data": {
    "tpsId": "848d872c4c712741c1d86131a5925273", //string -> tempat dilakukan prediksi
    "result": "Plastic Cup", //string
    "confidenceScore": 89.78, //float
    "price": 5000, // int
    "createAt": "2024-11-30T04:42:41.094Z" //string
  }
} 
```

if it fails
```json
{
    "status": "fail",
    "message": "An error occurred during prediction: Expected image (BMP, JPEG, PNG, or GIF), but got unsupported image type"
}
```

If it fails - due to the photo being larger than 5 MB :
```json
{
  "status": "fail",
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

Request body:
  - `image` as `file`, must be a valid image file, max size 5MB

- Method
  - POST

Request body:
- nasabahName -> `(string)`, name of the person who brought the trash 
- wasteType -> `(string)`
- price -> `(int)`, waste price per kg 
- weight -> `(int)`
- totalPrice -> price \* weightFieldValue -> `(int)`

Data to save to the database
- transactionId as`string`, id generated during prediction 
- tpsId as`string`, id of the TPS where the waste was sold 
- nasabahName as `string`
- wasteType as `string`
- price as `int`
- weight as `int` or `float` (same in firestore)
- totalPrice as `int`
- imgUrl as `string`
- createAt as `string`

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
  "status": "fail", // string, Indicates the server failed to save the data
  "error": true, // Indicates the user is logged in
  "message": "User not logged in"
}
```

# 5. getHistory
Endpoint to retrieve prediction history
- URL
  - `/transactionhistory?tpsId=param`

- Method
  - GET 

- request params
  - tpsId as `string`

response (Status Code: 200):
```json
{
    "status": "success",
    "data": [
        {
            "id": "0MfHPiPGV4hQxCHFzPV7", //string
            "transactionId": "89f91a62-4d3b-4c76-b123-7fc298b0c752", // string 
            "weight": 2, // int
            "price": 15000, // int
            "imgUrl": "youtube.comfegew",// string
            "wasteType": "paper",// string
            "tpsId": "b8b9a25c870730804699c39069dc6924",// string
            "createAt": "2024-11-26T14:30:00Z",// string
            "nasabahName": "TPS Mutiara Ibu Siapa Ya",// string
            "totalPrice": 300000 //int
        },
        {
            "id": "3JRntWq0a2a9kJg1cfxd", // string
            "transactionId": "b8b9a25c870730804699c39069dc6924", // string
            "weight": 2, // int
            "price": 15000, // int
            "imgUrl": "youtube.comfegew", // string
            "wasteType": "paper", // string
            "tpsId": "b8b9a25c870730804699c39069dc6924", // string
            "createAt": "2024-11-26T14:30:00Z", // string
            "nasabahName": "TPS Mutiara Ibu Siapa", // string
            "totalPrice": 300000 // int
        }
    ]
}
```

if fail :
```json
{
  "status": "success", // string
  "data": [], // array
  "message": "No history found" // string
}
```

if fail - internal server error (Status code: 500):
```json
{
  "status": "error", // string
  "message": "Failed to retrieve history" // string
}
```


# 6. get transaksi tertentu
Endpoint to retrieve prediction history by transaction ID
- URL
  - `/transactionhistory/detail?tpsId=param&transactionId=param`

- Method
  - GET

- request parameter
  - tpsId -> `string`
  - transactionId -> `string`

response :
```json
{
    "status": "success",
    "data": [
        {
            "id": "KRvbohCtv7oQsCTyJnEd", //string
            "createAt": "2024-11-26T14:30:00Z",// string
            "totalPrice": 300000, // int
            "imgUrl": "youtube.comfegew",// string
            "weight": 2, // int
            "nasabahName": "TPS Mutiara Ibu Siapa",// string
            "wasteType": "paper",// string
            "price": 15000, // int
            "transactionId": "30877736-3219-4cac-a0b1-346cd8f03524", // string
            "tpsId": "b8b9a25c870730804699c39069dc6924" // string
        }
    ]
}
```

if fail - no transaction (Status code : 404):
```json
{
  "status": "fail", // string
  "data": [], // array
  "message": "Transaction not found" // string
}
```

if fail - internal server error (Status code : 500) :
```json
{
  "status": "error", // string
  "message": "Failed to retrieve history" // string
}
```

# 7. Request OTP
- URL
  - `/request-password-reset`

- Method
  - POST

- request body
  - email as `string`

## response :

### Success
Success  (Status code 200) :
```json
{
    "status": "success", //string
    "message": "OTP telah dikirim ke email Anda." //string
}
```

### Fail
Email not found (Status code : 404):
```json
{
    "status": "fail", //string
    "message": "Email tidak terdaftar." //string
}
```

Server error (Status code : 404):
```json
{
    "status": "fail", //string
    "message": "Terjadi kesalahan pada server saat mengirim email." //string
}
```

# 8. Reset Pssword

- URL
  - `/reset-password`

- Method
  - POST

- request body
  - email -> `string`
  - otp -> `int`
  - newPassword -> `string` min 8 character


## response :

### Success
Success  (Status code 201) :
```json
{
    "status": "success", //string
    "message": "Password Anda berhasil diubah." //string
}
```

### Fail 
Email not found (Status code : 404):
```json
{
    "status": "fail", //string
    "message": "Email tidak terdaftar." //string
}
```


```json
{
    "status": "fail", //string
    "message": "OTP tidak ditemukan. Silakan minta OTP baru." //string
}
```

Server Error (Status code : 404):
```json
{
    "status": "fail", //string
    "message": "Terjadi kesalahan pada server saat mengirim email." //string
}
```
OTP code not found or not generated
```json
{
    "status": "fail", //string
    "message": "OTP tidak ditemukan. Silakan minta OTP baru." //string
}
```
Invalid OTP code
```json
{
    "status": "fail", //string
    "message": "OTP salah." //string
}
```

OTP code has expired
```json
{
    "status": "fail", //string
    "message": "OTP telah kadaluarsa. Silakan minta OTP baru." //string
}
```
Password less than 8 character
```json
{
    "status": "fail", //string
    "message": "Password harus memiliki minimal 8 karakter." //string
}
```


# 9. Dashboard
- URL
  - `/dashboard`

- Method
  - POST

- header
  - Authorization: Bearer \<token>

respone 
```json
{
  "status": "success",      // string
  "username" : "username", // string
  "tpsId": "tpsId", // string
  "totalWeight": 200,  //int
  "totalPrice": 200000,  //int
  "data": [
    {
      "wasteType": "Can", // string
      "totalWeight": 100, // int
      "totalPrice": 100000 //int
    },
    {
      "wasteType": "cardboard", // string
      "totalWeight": 50, //int
      "totalPrice": 50000 //int
    },
    {
      "wasteType": "Plastic Bottle", // string
      "totalWeight": 20, //int
      "totalPrice": 20000 //int
    },
    {
      "wasteType": "Plastic Cup", // string
      "totalWeight": 10, //int
      "totalPrice": 10000 //int
    },
    {
      "wasteType": "Glass Bottle", // string
      "totalWeight": 5, //int
      "totalPrice": 5000 //int
    },
    {
      "wasteType": "Paper", // string
      "totalWeight": 15, //int
      "totalPrice": 15000 //int
    }
  ]
}
```

if fail
```json
{
  "status": "fail",
  "message": "Failed to get dashboard data."
}
```

### About Normalize Waste Type
1. Input: `"plastic bottle"`
    - Output: `"Plastic Bottle"`

2. Input: `" cardboard box "`
    - Output: `"Cardboard Box"`

3. Input: `"GLASS jar"`
Output: `"Glass Jar"`

4. Input: `"metal can"`
    - Output: `"Metal Can"`

5. Input: `" paper "`
    - Output: `"Paper"`

6. Input: `"plastic bag"`
    - Output: `"Plastic Bag"`

7. Input: `" aluminum foil "`
    - Output: `"Aluminum Foil"`

8. Input: `"styrofoam cup"`
    - Output: `"Styrofoam Cup"`

9. Input: `" glass bottle "`
    - Output: `"Glass Bottle"`

10. Input: `"tin can"`
    - Output: `"Tin Can"`