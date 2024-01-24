package com.iko.android.courier.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.iko.android.courier.data.model.Package
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iko.android.courier.UserManager
import com.iko.android.courier.api.ApiService
import com.iko.android.courier.data.model.AuthenticationRequest
import com.iko.android.courier.data.model.AuthenticationResponse
import com.iko.android.courier.data.model.Courier
import com.iko.android.courier.data.model.Customer
import com.nimbusds.jwt.JWT
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.JWTParser

import kotlinx.coroutines.launch
class LoginViewModel(private val apiService: ApiService) : ViewModel() {

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult

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

                        val customer: Customer = apiService.getCustomerById(UserManager.id!!)
                        Log.d("LoginViewModel Customer Response", "Customer: $customer")
                        Log.d("Access token: ", UserManager.accessToken?:"")
                        Log.d("Refresh token: ", UserManager.refreshToken?:"")


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

    fun fetchUserInfo(id: Long): Boolean {
        var success = true
        viewModelScope.launch {
            try {
                // Make an API call to fetch additional user information based on email and password
                val customer: Customer = apiService.getCustomerById(id)
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