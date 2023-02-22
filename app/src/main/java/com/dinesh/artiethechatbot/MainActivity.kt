package com.dinesh.artiethechatbot

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.dinesh.artiethechatbot.databinding.ActivityMainBinding
import java.util.*


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val speechRequestCode = 100
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        //Text-to-speech
        initTextToSpeech()

        // Create an instance of the MainViewModel class
        val mainViewModel = MainViewModel()

        // references to the UI elements
        val questionEditText = binding.messageInput
        val answerTextView = binding.textAns
        val generateButton = binding.sendBtn
        val againButton = binding.againBtn2
        val share = binding.shareButton
        val speak = binding.speakBtn

        fun onAskButtonClicked() {
            if (binding.messageInput.text.isNotBlank()) {
                MainViewModel.onGenerateButtonClick(mainViewModel)
                answerTextView.text = getString(R.string.typing)
                againButton.visibility = View.VISIBLE
                generateButton.visibility = View.INVISIBLE
                share.visibility = View.VISIBLE
                hideKeyboard(this, generateButton)
            } else {
                Toast.makeText(this, "Enter any question to continue!", Toast.LENGTH_SHORT).show()
            }
        }

        fun onAgainBtnClicked() {
            questionEditText.text.clear()
            answerTextView.text = getString(R.string.ans_show)
            againButton.visibility = View.INVISIBLE
            generateButton.visibility = View.VISIBLE
            share.visibility = View.INVISIBLE
            tts.stop()
        }

        fun onShareBtnClicked() {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "*${mainViewModel.question.value}*\n\n${mainViewModel.answer.value}\n\nI found this answer by Artie the Chatbot\n*Download the app now\uD83D\uDC47*\nhttps://global.app.mi.com/details?lo=IN&la=en_US&id=com.dinesh.artiethechatbot"
            )
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, "Share To:"))
        }

        fun onSpeakPressed() {
            val text = mainViewModel.ans.value
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }


        // Pass the UI elements to the init method of the MainViewModel instance
        mainViewModel.init(questionEditText, answerTextView, generateButton)

        // Set up the generate button click listener
        questionEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onAskButtonClicked()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        binding.micBtn.setOnClickListener {
            micPressed()
        }

        speak.setOnClickListener {
            onSpeakPressed()
        }
        answerTextView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                when {
                    s?.length!! > 10 -> speak.performClick()
                }
            }
        })


        generateButton.setOnClickListener {
            onAskButtonClicked()
        }
        againButton.setOnClickListener {
            onAgainBtnClicked()
        }
        share.setOnClickListener {
            onShareBtnClicked()
        }

        // Observe the answerText LiveData object and update the answerTextView text accordingly
        mainViewModel.answerText.observe(this) {
            answerTextView.text = it
        }
    }
    private fun hideKeyboard(context: Context, view: View?) {
        val inputMethodManager =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view!!.windowToken, 0)
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == speechRequestCode && resultCode == RESULT_OK) {
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = results?.get(0)
            // use spokenText to update your editText
            binding.messageInput.setText(spokenText)
            binding.sendBtn.performClick()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        tts.stop()
        tts.shutdown()
        Log.i("onDestroyTag", "On Destroy Called")
    }
    private fun initTextToSpeech() {
        tts = TextToSpeech(this) {
            if (it == TextToSpeech.SUCCESS) {
                tts.language = Locale.US
            } else {
                val installTts = Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA)
                startActivity(installTts)
            }
        }
    }
    private fun micPressed() {
        startActivityForResult(
            Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now")
            }, speechRequestCode
        )
        tts.stop()
    }
}

