package com.iko.android.courier.api


import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import com.iko.android.courier.UserManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://192.168.31.80:8080/api/"

    // Interceptor to add Authorization header with the access token
    private val tokenInterceptor = object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val accessToken = UserManager.accessToken

            // Add Authorization header if access token is available
            if (accessToken != null) {
                val newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $accessToken")
                    .build()
                return chain.proceed(newRequest)
            }

            return chain.proceed(originalRequest)
        }
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(tokenInterceptor)
        .build()


    val apiService: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(okHttpClient)
            .build()

        retrofit.create(ApiService::class.java)
    }

}