package com.iko.android.courier.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.iko.android.courier.api.ApiService
import com.iko.android.courier.viewmodel.LoginViewModel
import android.content.Context

class LoginViewModelFactory(private val apiService: ApiService, private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(apiService, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
