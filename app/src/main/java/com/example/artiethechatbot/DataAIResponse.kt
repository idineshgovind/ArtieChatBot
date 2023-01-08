package com.example.artiethechatbot

import com.squareup.moshi.Json

data class DataAIResponse(
    @Json(name = "choices") val choices: List<OpenAIResponse>)
