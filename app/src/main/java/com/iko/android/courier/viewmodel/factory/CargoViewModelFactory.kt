package com.iko.android.courier.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.iko.android.courier.api.ApiService
import com.iko.android.courier.viewmodel.CargoViewModel
import com.iko.android.courier.viewmodel.LoginViewModel

class CargoViewModelFactory(private val apiService: ApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CargoViewModel::class.java)) {
            return CargoViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
