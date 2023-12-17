package com.iko.android.courier.viewmodel

import android.util.Log
import com.iko.android.courier.data.model.Package
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iko.android.courier.UserManager
import com.iko.android.courier.api.ApiService
import com.iko.android.courier.data.model.Courier
import com.iko.android.courier.data.model.Customer
import com.iko.android.courier.data.model.LoginRequest
import com.iko.android.courier.data.model.LoginResponse
import kotlinx.coroutines.launch
class LoginViewModel(private val apiService: ApiService) : ViewModel() {

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult

    fun loginUser(email: String, password: String) {
        val loginRequest = LoginRequest(email, password)

        println("Login Request Content: $loginRequest")

        viewModelScope.launch {
            try {
                val response = apiService.login(loginRequest)

                println("API Response: $response")

                if (response.isSuccessful) {
                    val loginResponse: LoginResponse? = response.body()
                    _loginResult.value = (loginResponse != null) && (loginResponse.customerId != null)

                    if (_loginResult.value == true) {
                        // Set the customer ID in UserManager
                        UserManager.id = loginResponse!!.customerId

                        val customer: Customer = apiService.getCustomerById(UserManager.id!!)
                        Log.d("LoginViewModel Customer Response", "Customer: $customer")
//                        val packages: MutableList<Package> = apiService.getPackagesByCustomerId(UserManager.id!!) as MutableList<Package>
//                        for (packet in packages) {
//                            Log.d("LoginViewModel Packages Response", "Package: $packet")
//                        }
//                            UserManager.packages = packages

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
//                        }

//                       val courier: Courier = apiService.getCourierById(loginResponse.customerId)
//                        if (courier != null) {
//                            UserManager.deliveriesPackages = courier.deliveriesPackages
//                            UserManager.reviews = courier.reviews
//                        }

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
}