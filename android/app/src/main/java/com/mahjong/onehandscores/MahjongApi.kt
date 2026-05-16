package com.mahjong.onehandscores

import retrofit2.http.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface MahjongApi {
    @POST("api/mahjong_scores/calculate_one_hand")
    suspend fun calculate(@Body request: CalculationRequest): CalculationResponse
}

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"  // замените на IP вашего сервера

    val api: MahjongApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MahjongApi::class.java)
    }
}