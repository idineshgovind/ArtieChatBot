package com.example.artiethechatbot

import com.squareup.moshi.Json

data class OpenAIResponse(
    @Json(name="text")
    val text:String
)
