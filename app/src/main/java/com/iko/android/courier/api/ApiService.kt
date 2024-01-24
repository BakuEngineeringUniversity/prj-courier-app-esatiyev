package com.iko.android.courier.api

import com.iko.android.courier.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

//    @POST("login")
//    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("auth")
    suspend fun authenticate(@Body authRequest: AuthenticationRequest): Response<AuthenticationResponse>
        // Courier Endpoints
    @GET("couriers")
    suspend fun getCouriers(): List<Courier>

    @GET("couriers/{courierId}")
    suspend fun getCourierById(@Path("courierId") courierId: Long): Courier

    @POST("couriers")
    suspend fun createCourier(@Body courier: Courier): Courier

    @PUT("couriers/{courierId}")
    suspend fun updateCourier(@Path("courierId") courierId: Long, @Body updatedCourier: Courier): Courier

    @DELETE("couriers/{courierId}")
    suspend fun deleteCourier(@Path("courierId") courierId: Long)

    // Customer Endpoints
    @GET("customers")
    suspend fun getCustomers(): List<Customer>

    @GET("customers/{customerId}")
    suspend fun getCustomerById(@Path("customerId") customerId: Long): Customer

    @POST("customers")
    suspend fun createCustomer(@Body customer: Customer): Response<Customer>

    @PUT("customers/{customerId}")
    suspend fun updateCustomer(@Path("customerId") customerId: Long, @Body updatedCustomer: Customer): Customer

    @DELETE("customers/{customerId}")
    suspend fun deleteCustomer(@Path("customerId") customerId: Long)

    // Package Endpoints
    @GET("packages")
    suspend fun getAllPackages(): List<Package>

    @GET("packages/{packageId}")
    suspend fun getPackageById(@Path("packageId") packageId: Long): Package

    @GET("packages/customers/{customerId}")
    suspend fun getPackagesByCustomerId(@Path("customerId") customerId: Long): Response<List<Package>>

    @GET("packages/couriers/{courierId}")
    suspend fun getPackagesByCourierId(@Path("courierId") courierId: Long): List<Package>

    @POST("packages/customers/{customerId}")
    suspend fun createPackage(@Path("customerId") customerId: Long, @Body packet: Package): Package

    @PUT("packages/{packageId}")
    suspend fun updatePackage(@Path("packageId") packageId: Long, @Body updatedPackage: Package): Package

    @PUT("packages/{packageId}/courier/{courierId}")
    suspend fun addPackageToCourier(@Path("packageId") packageId: Long, @Path("courierId") courierId: Long) : Response<Unit>

    @PUT("packages/couriers/{courierId}")
    suspend fun removePackageFromCourier(@Path("courierId") courierId: Long)

    @DELETE("packages/{packageId}")
    suspend fun deletePackage(@Path("packageId") packageId: Long): Response<Unit>

    // Review Endpoints
    @GET("reviews")
    suspend fun getAllReviews(): List<Review>

    @GET("reviews/{reviewId}")
    suspend fun getReviewById(@Path("reviewId") reviewId: Long): Review

    @GET("reviews/couriers/{courierId}")
    suspend fun getReviewsByCourierId(@Path("courierId") courierId: Long): List<Review>

    @POST("reviews/couriers/{courierEmail}")
    suspend fun addReview(@Path("courierEmail") courierEmail: String, @Body review: Review): Review

    @DELETE("reviews/couriers/{courierId}")
    suspend fun deleteReviewByCourierId(@Path("courierId") courierId: Long)
}