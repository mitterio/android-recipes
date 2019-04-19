package io.mitter.recipes.remote

import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/users/login")
    fun login(@Body loginRequest: LoginRequest): Single<LoginResponse>
}
