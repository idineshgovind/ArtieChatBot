package com.example.artiethechatbot

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.artiethechatbot.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()


        // Create an instance of the com.example.artiethechatbot.MainViewModel class
        val mainViewModel = MainViewModel()

        // references to the UI elements
        val questionEditText = binding.messageInput
        val answerTextView = binding.textAns
        val generateButton = binding.sendBtn
        val againButton = binding.againBtn2
        val search = binding.imageView
        val share = binding.shareButton



        // Pass the UI elements to the init method of the com.example.artiethechatbot.MainViewModel instance
        mainViewModel.init(questionEditText, answerTextView, generateButton)

        // Set up the generate button click listener
        questionEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                // Perform the action when the "Go" button is clicked
                if (binding.messageInput.text.isNotBlank()) {
                    MainViewModel.onGenerateButtonClick(mainViewModel)
                    answerTextView.text = getString(R.string.typing)
                    againButton.visibility = View.VISIBLE
                    generateButton.visibility = View.INVISIBLE
                    search.visibility = View.VISIBLE
                    share.visibility = View.VISIBLE
                    hideKeyboard(this, generateButton)
                }else{
                    Toast.makeText(this, "Enter any question to continue!", Toast.LENGTH_SHORT).show()
                }
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        generateButton.setOnClickListener {
            if (binding.messageInput.text.isNotBlank()) {
                MainViewModel.onGenerateButtonClick(mainViewModel)
                answerTextView.text = getString(R.string.typing)
                againButton.visibility = View.VISIBLE
                generateButton.visibility = View.INVISIBLE
                search.visibility = View.VISIBLE
                share.visibility = View.VISIBLE
                hideKeyboard(this, it)
            }else{
                Toast.makeText(this, "Enter any question to continue!", Toast.LENGTH_SHORT).show()
            }
        }

        againButton.setOnClickListener {
            questionEditText.text.clear()
            answerTextView.text=getString(R.string.ans_show)
            againButton.visibility = View.INVISIBLE
            generateButton.visibility = View.VISIBLE
            search.visibility = View.INVISIBLE
            share.visibility = View.INVISIBLE
        }
        share.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "*${mainViewModel.question.value}*\n\n${mainViewModel.answer.value}\n\nI found this answer by Artie the Chatbot\n*Download the app now\uD83D\uDC47*\nhttps://www.bit.ly/ArtieAppBot"
            )
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, "Share To:"))
        }

        // Observe the answerText LiveData object and update the answerTextView text accordingly
        mainViewModel.answerText.observe(this) {
            answerTextView.text = it
        }
    }

    private fun hideKeyboard(context: Context, view: View?) {
        val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    override fun onStart() {
        super.onStart()
        Log.i("onStartTag", "OnStart Called")
    }

    override fun onResume() {
        super.onResume()
        Log.i("onResumeTag", "OnResume Called")
    }

    override fun onPause() {
        super.onPause()
        Log.i("onPauseTag", "OnPause Called")
    }

    override fun onStop() {
        super.onStop()
        Log.i("onStopTag", "OnStop Called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("onDestroyTag", "OnDestroy Called")
    }

}

