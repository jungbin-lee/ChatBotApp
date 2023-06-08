package com.h2square.chatbotapp.data.model

import android.app.DownloadManager.Request
import kotlinx.serialization.Serializable
import java.net.CacheRequest
@Serializable
data class MessageResponse(

    val status:Long,
    val statusMessage:String,
    val request: com.h2square.chatbotapp.data.model.Request,
    val atext:String,
    val lang:String

)
@Serializable
data class Request(

        val utext:String,
        val lang: String


)
fun MessageResponse.toMsg():Msg{
    return Msg(this.atext, type = MsgType.Bot)
}