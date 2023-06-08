package com.h2square.chatbotapp.data.model

enum class MsgType {
    Me,Bot
}


data class Msg(val content:String, val type :MsgType)