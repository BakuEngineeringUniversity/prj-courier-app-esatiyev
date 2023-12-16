package com.iko.android.courier.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.iko.android.courier.api.ApiService
import com.iko.android.courier.viewmodel.RegistrationViewModel

class RegistrationViewModelFactory(private val apiService: ApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistrationViewModel::class.java)) {
            return RegistrationViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


