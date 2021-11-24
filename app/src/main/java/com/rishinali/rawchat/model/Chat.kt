package com.rishinali.rawchat.model


data class Chat(
    var messageId: String? = null,
    val message: String? = null,
    var imageUrl: String? = null,
    val messageType: String? = null,
    val senderId: String? = null,
    val recipientId: String? = null,
    val timestamp: Long? = null
)
