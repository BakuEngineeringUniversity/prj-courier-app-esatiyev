package com.iko.android.courier.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iko.android.courier.UserManager
import com.iko.android.courier.api.ApiService
import com.iko.android.courier.data.model.Package
import kotlinx.coroutines.launch

class CargoViewModel(private val apiService: ApiService) : ViewModel() {

    private val _customerPackages = MutableLiveData<List<Package>>()
    val customerPackages: LiveData<List<Package>> get() = _customerPackages

    fun fetchCustomerPackages() {
        viewModelScope.launch {
            try {
                // Check if the customer ID is available
                val customerId: Long? = UserManager.id
                if (customerId != null) {
                    // Fetch customer packages using the stored customer ID
                    val response = apiService.getPackagesByCustomerId(customerId)
                    if (response.isSuccessful) {
                        val packages : MutableList<Package> = response.body() as MutableList<Package>
                        packages?.let {
                            UserManager.packages = it.toMutableList()
                            _customerPackages.value = it
                        } ?: run {
                            // Handle the case where the response body is null
                            _customerPackages.value = emptyList()
                        }

//                        UserManager.packages = packages
//                        _customerPackages.value = packages
                    } else {
                        // Handle other API response codes if needed
                        _customerPackages.value = emptyList()
                    }
                } else {
                    // Handle the case where the customer ID is not available
                    _customerPackages.value = emptyList()
                }
            } catch (e: Exception) {
                // Handle error
                _customerPackages.value = emptyList()
                Log.e("CargoViewModel", "Error fetching customer packages: ${e.message}")
            }
        }
    }
}


