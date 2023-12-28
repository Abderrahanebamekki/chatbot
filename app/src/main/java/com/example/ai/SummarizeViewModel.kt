package com.example.ai

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.asTextOrNull
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SummarizeViewModel(
) : ViewModel()  {
    private val generativeModelImage = GenerativeModel(
        modelName = "gemini-pro-vision",
        apiKey = BuildConfig.apiKey
    )
    private val generativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = BuildConfig.apiKey
    )

    private val _uiState: MutableStateFlow<SummarizeUiState> =
        MutableStateFlow(SummarizeUiState.Initial)
    val uiState: StateFlow<SummarizeUiState> =
        _uiState.asStateFlow()
//    val chatImage = generativeModelImage.startChat(
//    )
    val chat = generativeModel.startChat(
    )
    fun summarize(inputText: String) {
        _uiState.value = SummarizeUiState.Loading

        val prompt = "Summarize the following text for me: $inputText"

        viewModelScope.launch {
            try {
                val response = generativeModel.generateContent(prompt)
                response.text?.let { outputContent ->
                    _uiState.value = SummarizeUiState.Success(outputContent)
                    Log.d("summm", "summarize: $outputContent")
                }
            } catch (e: Exception) {
                _uiState.value = SummarizeUiState.Error(e.localizedMessage ?: "")
            }
        }
    }

    fun summarizeChat(inputText: String) {
        _uiState.value = SummarizeUiState.Loading

        val prompt = inputText

        viewModelScope.launch {
            try {
                val response = chat.sendMessage(prompt)
                Log.d("bef", "summarizeChat: no thing")
                response.text?.let { outputContent ->
                    _uiState.value = SummarizeUiState.Success(outputContent)
                    Log.d("summm", "summarize: $outputContent")
                }
            } catch (e: Exception) {
                _uiState.value = SummarizeUiState.Error(e.localizedMessage ?: "")
            }
        }
    }

//    fun summarizeChatImage(inputText: String , listBitmap : List<Bitmap>) {
//        _uiState.value = SummarizeUiState.Loading
//
//        val prompt = content(role = "user"){
//            text(inputText)
//            listBitmap.forEach{
//                image(it)
//            }
//        }
//
//        viewModelScope.launch {
//            Log.d("bef", "summarizeChat: here")
//            try {
//                val response = chatImage.sendMessageStream(prompt)
//                 response.collect{
//                     Log.d("aft", "summarizeChat: ${it.candidates.first().content.parts.first().asTextOrNull()}")
//                 }
//            } catch (e: Exception) {
//                _uiState.value = SummarizeUiState.Error(e.localizedMessage ?: "")
//                Log.d("aft1", "summarizeChat: ${e.localizedMessage.toString()}")
//            }
//        }
//    }
}