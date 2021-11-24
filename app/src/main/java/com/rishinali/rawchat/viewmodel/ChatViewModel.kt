package com.rishinali.rawchat.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.rishinali.rawchat.model.Chat
import com.rishinali.rawchat.repo.AuthRepo
import com.rishinali.rawchat.repo.ChatRepo
import kotlinx.coroutines.launch

class ChatViewModel: ViewModel() {

    private val authRepo = AuthRepo()
    private val chatRepo = ChatRepo()

    val chatListLiveData = chatRepo.chatMessageListLiveData

    val currentUserLiveData: LiveData<FirebaseUser> = authRepo.currentUserLiveData

    fun addChatMessage(chatNode: String, chat: Chat) {
        viewModelScope.launch {
            chatRepo.addChatMessage(chatNode, chat, null)
        }
    }

    fun getChatMessageList(chatNode: String) {
        viewModelScope.launch {
            chatRepo.getChatMessageList(chatNode)
        }
    }

    fun uploadImageToDb(chatNode: String, chat: Chat, imageUri: Uri) {
        viewModelScope.launch {
            chatRepo.uploadImageToDb(chatNode, chat, imageUri)
        }
    }

}