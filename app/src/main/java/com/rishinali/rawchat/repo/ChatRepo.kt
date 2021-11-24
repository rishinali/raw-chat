package com.rishinali.rawchat.repo

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.rishinali.rawchat.model.Chat

private const val TAG = "Chat"
private const val CHATS = "chats"
private const val CHAT_IMAGES_DIR = "chat-images"
private const val DEFAULT_IMG_URL = "default"

class ChatRepo {

    private val chatsRtDbRef = FirebaseDatabase.getInstance().reference.child(CHATS)
    private val imagesStorageRef = FirebaseStorage.getInstance().reference.child(CHAT_IMAGES_DIR)

    private val _chatMessageListMutableLiveData = MutableLiveData<List<Chat>> ()
    val chatMessageListLiveData: LiveData<List<Chat>> = _chatMessageListMutableLiveData

    fun getChatMessageList(chatNode: String) {

        val messageList = mutableListOf<Chat>()

        chatsRtDbRef.child(chatNode).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                Log.d(TAG, "onDataChange: snapshot = ${snapshot.value.toString()}")

                messageList.clear()
                for (messageSnapshot: DataSnapshot in snapshot.children) {
                    messageSnapshot.getValue(Chat::class.java)?.let { messageList.add(it) }
                }

//                Log.d(TAG, "onDataChange: messageList = ${messageList.toString()}")
                _chatMessageListMutableLiveData.postValue(messageList)
            }

            override fun onCancelled(error: DatabaseError) {
                // nothing to do
            }

        })
    }

    fun uploadImageToDb(chatNode: String, chat: Chat, imageUri: Uri) {

        val messageId = chatsRtDbRef.child(chatNode).push().key
        chat.messageId = messageId
        chat.imageUrl = DEFAULT_IMG_URL
        addChatMessage(chatNode, chat, messageId)

        Log.d(TAG, "uploadImageToDb: messageIdRef = $messageId")
        val filepath = imagesStorageRef.child(chatNode).child("${messageId}.jpg")

        val uploadTask = filepath.putFile(imageUri)
        uploadTask.addOnCompleteListener{ task ->
            if (task.isSuccessful) {
                filepath.downloadUrl.addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(TAG, "uploadImageToDb: uploaded with download url = ${it.result.toString()}")

                        chat.imageUrl = it.result.toString()

                        addChatMessage(chatNode, chat, messageId)
                    }
                }
            }
        }
    }

    fun addChatMessage(chatNode: String, chat: Chat, messageId: String?) {

        var chatMessageId = messageId

        Log.d(TAG, "addChatMessage: messageId before pushing = $chatMessageId")

        if (chatMessageId == null) {
            chatMessageId = chatsRtDbRef.child(chatNode).push().key
        }

        Log.d(TAG, "addChatMessage: messageId after pushing = $chatMessageId")

        val chatMap = mapOf(
            "messageId" to chatMessageId,
            "message" to chat.message,
            "imageUrl" to chat.imageUrl,
            "messageType" to chat.messageType,
            "senderId" to chat.senderId,
            "recipientId" to chat.recipientId,
            "timestamp" to ServerValue.TIMESTAMP
        )

        chatMessageId?.let { chatKey ->
            chatsRtDbRef.child(chatNode).child(chatKey).updateChildren(chatMap).addOnCompleteListener {
                getChatMessageList(chatNode)
            }
        }
    }
}