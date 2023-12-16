package com.iko.android.courier.viewmodel

import com.iko.android.courier.api.ApiService
import com.iko.android.courier.data.model.Customer
import com.iko.android.courier.api.Result
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegistrationViewModel(private val apiService: ApiService) : ViewModel() {

    val registrationResult = MutableLiveData<Result<Customer?>>()

    fun registerCustomer(customer: Customer) {
        viewModelScope.launch {
            try {
                val response = apiService.createCustomer(customer)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        registrationResult.value = Result.Success(response.body())
                    } else {
//                        registrationResult.value = Result.Error(Exception("Registration failed2"))
                        val errorMessage = "Registration failed. HTTP error code: ${response.code()}, Message: ${response.message()}"
                        registrationResult.value = Result.Error(Exception(errorMessage))
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    registrationResult.value = Result.Error(e)
                }
            }
        }
    }
}

