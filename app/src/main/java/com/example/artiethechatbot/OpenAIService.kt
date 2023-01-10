package com.example.artiethechatbot

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenAIService {

    @POST("v1/completions")
    fun generateText(
        @Header("Authorization") apiKey: String = "Bearer <YOUR API_KEY>",
        @Header("Content-Type") type : String = "application/json",
        @Body request: Map<String, String>
    ): Call<ResponseBody>
}
