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
        "name": "NAMA TPS",
        "token": "token_user"
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
    
- Content-Type: `multipart/form-data`

- Request body:
    - image as file

if successful :
```json
{
	"error": false,
	"message": "success",
	"data": {
		"transactionId": "id unik pas predict gambar",
		"result": "hasil_prediksi -> label",
		"confidenceScore": " " ,
		"price": "harga_berdasarkan_label (perKG)",
		"CreateAt": "" ,
	}
}
```

If it fails - due to the photo being larger than 5 MB : 
```json
{
	"status" : false,
	"message" : "Payload content length greater than maximum allowed: 5000000"
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
- Method
    - POST
    
Request body: 
- nasabahName  -> name of the person who brought the trash
- wasteType
- price  -> waste price per kg
- weight
- totalPrice  -> price * weight 

Data to save to the database
- transactionId -> id obtained when making an image prediction
- tpsId-> id TPS nya
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
	"status": true,
	"error": false, 
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
	"message": "error.message" 
}
```

if fail - User not logged in:
```json
{ 
	"status": false,   // jika gagal di backend
	"error": true,  // error karena security
	"message": "error.message" 
}
```

# 5. getHistory
- URL
    - `/predictHistory`

- Method
    - GET

- request body
	- tpsId

response : 
```json
{
   "status": "success",
   "data": [
       {
           "idTransaksi": "13e907b3-4213-42ad-b12b-b9b7e12eb90e", // id pas predict
           "data": {
				"tpsId": "" , // id tps dimana sampah di jual
				"tpsId": "",
				"nasabahName": "" ,
				"wasteType": "" ,
				"price": "" ,
				"weight": "" ,
				"totalPrice": "" ,
				"imgUrl": "",
				"createAt": "" ,
	        }
       },
       
       {
           "idTransaksi": "13e907b3-4232-42ad-b12b-b9b7e12eb90e",
           "data": {
				"tpsId": "",
				"nasabahName": "" ,
				"wasteType": "" ,
				"price": "" ,
				"weight": "" ,
				"totalPrice": "" ,
				"imgUrl": "",
				"createAt": "" ,
	        }
       },
   ]
}
```

if fail :
```json
{
   "status": "fail",
   "data": [],
   "message": error.message
}
```


## 6. get transaksi tertentu
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
				"nasabahName": "" ,
				"wasteType": "" ,
				"price": "" ,
				"weight": "" ,
				"totalPrice": "" ,
				"imgUrl": "",
				"createAt": "" ,
	        }
       },
   ]
}
```

if fail  :
```json
{
   "status": "fail",
   "data": [],
   "message": error.message
}
```


