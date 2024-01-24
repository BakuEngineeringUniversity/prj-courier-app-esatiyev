package com.iko.android.courier.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iko.android.courier.UserManager
import com.iko.android.courier.api.ApiService
import com.iko.android.courier.data.model.AuthenticationRequest
import com.iko.android.courier.data.model.AuthenticationResponse
import com.iko.android.courier.data.model.Customer
import com.iko.android.courier.databases.AppDatabase
import com.nimbusds.jwt.JWT
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.JWTParser
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.iko.android.courier.databases.entities.User
import kotlinx.coroutines.launch

import kotlinx.coroutines.launch
class LoginViewModel(private val apiService: ApiService,
                     private val context: Context) : ViewModel() {

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult

    private val userDao = AppDatabase.getDatabase(context).userDao()


    fun loginUser(email: String, password: String) {
        val authenticationRequest = AuthenticationRequest(email, password)

        println("Login Request Content: $authenticationRequest")

        viewModelScope.launch {
            try {
                val response = apiService.authenticate(authenticationRequest)

                println("API Response: $response")

                if (response.isSuccessful) {
                    val authenticationResponse: AuthenticationResponse? = response.body()
                    // Set the customer ID in UserManager
                    UserManager.accessToken = authenticationResponse?.accessToken
                    UserManager.refreshToken = authenticationResponse?.refreshToken
                    _loginResult.value = (authenticationResponse != null) && (authenticationResponse.accessToken != null)

                    if (_loginResult.value == true) {
                        UserManager.id = extractSubjectFromJwt(authenticationResponse?.accessToken)

                        val customerResponse = apiService.getCustomerById(UserManager.id!!)
                        Log.d("LoginViewModel Customer Response", "Customer: $customerResponse")
                        Log.d("Access token: ", UserManager.accessToken?:"")
                        Log.d("Refresh token: ", UserManager.refreshToken?:"")
                        if(customerResponse.isSuccessful && customerResponse.body() != null) {
                            val customer: Customer = customerResponse.body()!!

                            userDao.deleteAllUsers()
                            // Save user data to the database
                            saveUserToDatabase(
                                UserManager.id!!,
                                customer.firstname,
                                customer.lastname,
                                customer.username,
                                customer.email,
                                customer.password,
                                customer.fin,
                                customer.serialNo,
                                customer.age,
                                customer.gender,
                                customer.phone,
                                customer.address,
                                customer.ordersNumber,
                                customer.expenses,
                                customer.isCourier,
                                UserManager.accessToken,
                                UserManager.refreshToken
                            )

//
//                            UserManager.firstname = customer.firstname
//                            UserManager.lastname = customer.lastname
//                            UserManager.username = customer.username
//                            UserManager.fin = customer.fin
//                            UserManager.serialNo = customer.serialNo
//                            UserManager.age = customer.age
//                            UserManager.gender = customer.gender
//                            UserManager.phone = customer.phone
//                            UserManager.address = customer.address
//                            UserManager.email = customer.email
//                            UserManager.password = customer.password
//                            UserManager.ordersNumber = customer.ordersNumber
//                            UserManager.expenses = customer.expenses
//                            UserManager.isCourier = customer.isCourier

                        }
                    }

                } else {
                    // Handle other API response codes if needed
                    _loginResult.value = false
                }
            } catch (e: Exception) {
                _loginResult.value = false
            }
        }
    }

    private suspend fun saveUserToDatabase(id: Long, firstname: String?, lastname: String?, username: String?,
                        email: String?, password: String?, fin: String?, serialNo: String?, age: Int?,
                        gender: String?, phone: String?, address: String?, ordersNumber: Int?, expenses: Float?,
                        isCourier: Boolean?, accessToken: String?, refreshToken: String?) {
        userDao.insertUser(User(userId = id, firstname =  firstname, lastname =  lastname, username =  username,
            email =  email, password =  password, fin = fin, serialNo = serialNo, age = age, gender = gender,
            phone = phone, address = address, ordersNumber = ordersNumber, expenses = expenses, isCourier = isCourier,
            deliversNumber = 0, rating = null, accessToken = accessToken, refreshToken = refreshToken))

//        val user = userDao.getFirstUser()
//        if(user.isPresent) {
//
//        }
    }

    fun fetchUserInfo(id: Long): Boolean {
        var success = true
        viewModelScope.launch {
            try {
                // Make an API call to fetch additional user information based on email and password
                val customerResponse = apiService.getCustomerById(id)
                if (customerResponse.isSuccessful && customerResponse.body() != null) {
                    val customer: Customer = customerResponse.body()!!
                    // Set up the user information in UserManager
                    UserManager.id = customer.id

                    UserManager.firstname = customer.firstname
                    UserManager.lastname = customer.lastname
                    UserManager.username = customer.username
                    UserManager.fin = customer.fin
                    UserManager.serialNo = customer.serialNo
                    UserManager.age = customer.age
                    UserManager.gender = customer.gender
                    UserManager.phone = customer.phone
                    UserManager.address = customer.address
                    UserManager.email = customer.email
                    UserManager.password = customer.password
                    UserManager.ordersNumber = customer.ordersNumber
                    UserManager.expenses = customer.expenses
                    UserManager.isCourier = customer.isCourier
                    // ... (set up other user information)

                    // You can also update the UI or perform other actions based on the fetched information
                }
            } catch (e: Exception) {
                // Handle the exception
                Log.e("LoginViewModel", "Failed to fetch user information", e)
                success = false
            }
        }

        return success
    }

    private fun extractSubjectFromJwt(jwtToken: String?): Long? {
        try {
            // Parse the JWT token
            val jwt: JWT = JWTParser.parse(jwtToken)

            // Get the JWT claims set
            val jwtClaimsSet: JWTClaimsSet = jwt.jwtClaimsSet

            // Extract subject (sub) claim
            return jwtClaimsSet.subject.toLong()
        } catch (e: Exception) {
            // Handle exceptions, e.g., log the error
            println("Error extracting subject from JWT: ${e.message}")
        }
        return null
    }
}