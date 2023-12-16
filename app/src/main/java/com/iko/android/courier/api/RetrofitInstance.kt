package com.iko.android.courier.api


import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
//    private const val BASE_URL = "http://192.168.31.80:8080/api/"
    private const val BASE_URL = "http://192.168.137.172:8080/api/"

    val apiService: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()



        retrofit.create(ApiService::class.java)
    }


//    private val retrofit by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }
//
//    val api: ApiService by lazy {
//        retrofit.create(ApiService::class.java)
//    }
}