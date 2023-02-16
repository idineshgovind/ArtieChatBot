package com.dinesh.artiethechatbot

import android.annotation.SuppressLint
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dinesh.artiethechatbot.network.OpenAIService
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory


@SuppressLint("StaticFieldLeak")
class MainViewModel : ViewModel() {
    private lateinit var questionEditText: EditText
    private lateinit var answerTextView: TextView
    private lateinit var generateButton: TextView
    val answerText = MutableLiveData<String>()
    val question = MutableLiveData<String>()
    val answer = MutableLiveData<String>()
    val ans = MutableLiveData<String>()

    fun init(questionEditText: EditText, answerTextView: TextView, generateButton: TextView) {
        this.questionEditText = questionEditText
        this.answerTextView = answerTextView
        this.generateButton = generateButton
    }

    companion object {
        fun onGenerateButtonClick(mainViewModel: MainViewModel) {
            // Get the user's question from the EditText
            val question = mainViewModel.questionEditText.text.toString()
            // Send the request to the API
            mainViewModel.generateText(question)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun generateText(prompt: String) {
        // Create a logger for debugging purposes
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }


        val client = OkHttpClient.Builder().addInterceptor(logger).build()

        // Configure the Retrofit object
        val retrofit = Retrofit.Builder().baseUrl("https://api.openai.com/").client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()

        // Create an instance of the OpenAIService
        val service = retrofit.create(OpenAIService::class.java)

        // Set the request parameters
        val request = mapOf(
            "model" to "text-davinci-003",
            "prompt" to prompt,
            "temperature" to 0,
            "max_tokens" to 182,
            "top_p" to 1,
            "frequency_penalty" to 0,
            "presence_penalty" to 0,
            "stop" to "###"
        )


        // Make the API request
        service.generateText(request = request as Map<String, String>)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>, response: Response<ResponseBody>
                ) {
                    if (prompt == "what is your name".trim() || prompt == "what is your name".trim()
                            .uppercase() || prompt == "what is your name".trim().lowercase()
                    ) {
                        answerText.value = "I am Artie, your personal assistant."
                        ans.value = "I am Artie, your personal assistant."
                    } else if (response.isSuccessful) {
                        val responseBody = response.body()
                        val responseString = responseBody!!.string()
                        val jsonResponse = JSONObject(responseString)

                        val choices = jsonResponse.getJSONArray("choices").getJSONObject(0)
                        val text = (choices.getString("text"))
                        question.value = "Question : ${prompt.trim()}"
                        answer.value = "Answer :\n${text.trim()}"
                        ans.value = text.trim()


                        answerText.value = "Question : $prompt\n\n${text.trim()}"
                    } else {
                        answerText.value = response.errorBody()?.string()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    answerText.value = "There is something problem.Please retry!"
                    ans.value = "There is something problem.\nPlease retry!"
                }
            })
    }

}