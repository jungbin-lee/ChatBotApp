package com.h2square.chatbotapp.data.model

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.h2square.chatbotapp.MessageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatBotViewModel : ViewModel() {

    val userInptFlow = MutableStateFlow("")
    val isSendBtnEnableFlow = MutableStateFlow(false)

    val isLoading = MutableStateFlow(false)

    val messagesFlow =
        MutableStateFlow<List<Msg>>(emptyList())


    companion object {
        const val TAG = "CHATBOTVIEWMODEL"
    }

    init {

        viewModelScope.launch {
            userInptFlow.collectLatest {
                Log.d(TAG, "$it")
                isSendBtnEnableFlow.emit(it.isNotEmpty())
            }
        }
    }


    fun sendMessage() {
        viewModelScope.launch {
            callSendMessage()
        }
    }

    private suspend fun callSendMessage() {
        Log.d(TAG, "CHATBOTVIEWMODEL called")
        withContext(Dispatchers.IO) {
            kotlin.runCatching {
                messagesFlow.value += Msg(userInptFlow.value, MsgType.Me)
                isLoading.emit(true)
                MessageRepository.sendMessage(userInptFlow.value)

            }
                .onSuccess {
                    Log.d(TAG, "succsess")
                    messagesFlow.value += it
                    userInptFlow.value = ""
                    isLoading.emit(false)
                }
                .onFailure { Log.d(TAG, "fail: ${it.localizedMessage}")
                    isLoading.emit(false)
                }

        }

    }


}