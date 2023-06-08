@file:OptIn(ExperimentalMaterial3Api::class)

package com.h2square.chatbotapp

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator


import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Surface

import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager

import androidx.compose.ui.res.painterResource

import androidx.compose.ui.unit.dp
import com.h2square.chatbotapp.MainActivity.Companion.TAG
import com.h2square.chatbotapp.data.model.ChatBotViewModel
import com.h2square.chatbotapp.data.model.Msg
import com.h2square.chatbotapp.data.model.MsgType
import com.h2square.chatbotapp.ui.theme.BotArrowShapeForLeft
import com.h2square.chatbotapp.ui.theme.BotArrowShapeForRight
import com.h2square.chatbotapp.ui.theme.BotBubbleColor
import com.h2square.chatbotapp.ui.theme.ChatBotAppTheme
import com.h2square.chatbotapp.ui.theme.MeBubbleColor
import com.h2square.chatbotapp.ui.theme.Purple40
import com.h2square.chatbotapp.ui.theme.SendBtnColor
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    companion object {
        const val TAG = "chatbot"
    }
    private val chatBotViewModel:ChatBotViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setContent {
            ChatBotAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatBotView(chatBotViewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBotView(chatBotViewModel: ChatBotViewModel) {

    val userInput : State<String> = chatBotViewModel.userInptFlow.collectAsState()
    val isSendBtnEnable : State<Boolean> = chatBotViewModel.isSendBtnEnableFlow.collectAsState()
//    var userInput by remember {
//        mutableStateOf("사용자입력")
//    }
    val messages :State<List<Msg>> =chatBotViewModel.messagesFlow.collectAsState()
    val isLoading:State<Boolean> = chatBotViewModel.isLoading.collectAsState()
    val nubersRange=(1..100)
    val dummyMessages = nubersRange.mapIndexed{index, number ->

        val type =if (index%2==0)MsgType.Bot else MsgType.Me
        Msg("$number 번째 입니다",type)
    }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state=scrollState,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.Top),
            contentPadding = PaddingValues(10.dp)
        ) {
            items(messages.value) {
                 MsgRowItem(it)
//                Text(text = "이정빈입니다.$aNumber")


            }
        }
        Text(text = userInput.value, modifier = Modifier.padding(20.dp))

        Divider(thickness = 1.dp, color = Color.LightGray)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
                .background(Color.White),
            verticalAlignment = Alignment.Top
        ) {
            BasicTextField(
                modifier = Modifier
                    .background(Color.White)
                    .weight(1f)
                    .padding(12.dp),
                value = userInput.value,
                onValueChange = { justTypedUserInput ->
                    chatBotViewModel.userInptFlow.value = justTypedUserInput
                })
            Card(colors = if (isSendBtnEnable.value) CardDefaults.cardColors(SendBtnColor) else CardDefaults.cardColors(
                Purple40),
                onClick = {
                    Log.d(TAG, "정송버튼 클릭")
                    if (!isSendBtnEnable.value){
                        return@Card
                    }
                    if (userInput.value.isEmpty()){return@Card }
//                    messages.value +=Msg(userInput.value,MsgType.Me)
//                    chatBotViewModel.userInptFlow.value =""
                    chatBotViewModel.sendMessage()
                    focusManager.clearFocus()
                    coroutineScope.launch {    scrollState.animateScrollToItem(messages.value.size)}

                }) {

                if (isLoading.value){
                    CircularProgressIndicator()

                }else {
                    Image(
                        painter = painterResource(id = R.drawable.send),
                        contentDescription = "보내기버튼",
                        modifier = Modifier
                            .padding(10.dp)
                            .width(30.dp)

                    )
                }
            }
        }
    }

}

@Composable
fun MsgRowItem(data:Msg) {
    val itemArrangement: (MsgType) -> Arrangement.Horizontal = {
        if (data.type == MsgType.Me) Arrangement.End else Arrangement.Start
    }
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = itemArrangement(data.type)
    ) {
        when (data.type) {
            MsgType.Me -> MeMsgRowItem(message = data.content)
            MsgType.Bot -> BotMsgRowItem(message =  data.content)
        }
    }

}

@Composable
fun MeMsgRowItem(message: String) {


    Row(
        modifier = Modifier
            .height(IntrinsicSize.Max)
            .fillMaxWidth(0.7f)
            .padding(10.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = MeBubbleColor,
                    shape = RoundedCornerShape(12.dp, 12.dp, 0.dp, 12.dp)
                )
                .width(IntrinsicSize.Max)
        ) {
            Text(text = message, modifier = Modifier.padding(12.dp))
        }

        Box(
            modifier = Modifier
                .background(
                    color = MeBubbleColor,
                    shape = BotArrowShapeForRight(30)
                )
                .width(IntrinsicSize.Max)
                .fillMaxHeight()
        ) {

        }
    }


}

@Composable
fun BotMsgRowItem(message: String) {
    Row(
        modifier = Modifier
            .height(IntrinsicSize.Max)
            .fillMaxWidth(0.7f),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Start
    ) {


        Image(
            painter = painterResource(id = R.drawable.yujin),
            contentDescription = "yujin",
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .border(BorderStroke(2.dp, Color.LightGray), shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .background(
                    color = BotBubbleColor,
                    shape = BotArrowShapeForLeft(30)
                )
                .width(10.dp)
                .fillMaxHeight()
        ) {

        }

        Box(
            modifier = Modifier
                .background(
                    color = BotBubbleColor,
                    shape = RoundedCornerShape(12.dp, 12.dp, 12.dp, 0.dp)
                )
                .width(IntrinsicSize.Max)
        ) {
            Text(text = message, modifier = Modifier.padding(12.dp))
        }


    }


    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }


    @Composable
    fun GreetingPreview() {
        ChatBotAppTheme {
            Greeting("Android")
        }
    }
}