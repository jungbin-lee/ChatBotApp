package com.h2square.chatbotapp

import com.h2square.chatbotapp.data.model.MessageResponse
import com.h2square.chatbotapp.data.model.Msg
import com.h2square.chatbotapp.data.model.Request
import com.h2square.chatbotapp.data.model.toMsg
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

object MessageRepository {


    suspend fun sendMessage(userInput:String): Msg {
      val receivedMsg=  KtorClient.httpClient.post(KtorClient.BASE_URL){
            contentType(ContentType.Application.Json)
            setBody(Request(utext = userInput, lang = "ko"))
        }.body<MessageResponse>().toMsg()

        return receivedMsg

    }
}