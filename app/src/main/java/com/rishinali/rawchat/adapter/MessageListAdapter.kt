package com.rishinali.rawchat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rishinali.rawchat.R
import com.rishinali.rawchat.databinding.RvMessageImageItemLeftBinding
import com.rishinali.rawchat.databinding.RvMessageImageItemRightBinding
import com.rishinali.rawchat.databinding.RvMessageItemLeftBinding
import com.rishinali.rawchat.databinding.RvMessageItemRightBinding
import com.rishinali.rawchat.model.Chat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

private const val DEFAULT_IMG_URL = "default"

class MessageListAdapter(
    private val currentUserId: String
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var _bindingSent: RvMessageItemRightBinding? = null
    private var _bindingReceived: RvMessageItemLeftBinding? = null
    private var _bindingImageSent: RvMessageImageItemRightBinding? = null
    private var _bindingImageReceived: RvMessageImageItemLeftBinding? = null

    private val bindingSent get() = _bindingSent
    private val bindingReceived get() = _bindingReceived
    private val bindingImageSent get() =  _bindingImageSent
    private val bindingImageReceived get() = _bindingImageReceived

    private var messageList: List<Chat>? = null

    fun setMessageList(messageList: List<Chat>) {
        this.messageList = messageList
    }

    class SentMessageViewHolder(
        private val bindingSent: RvMessageItemRightBinding
    ) : RecyclerView.ViewHolder(bindingSent.root) {
        fun bind(chat: Chat) {

            bindingSent.messageTv.text = chat.message
            bindingSent.messageTimeTv.text = chat.timestamp?.let { getTimeInHHMMAA(it) }
        }
    }

    class ReceivedMessageViewHolder(
        private val bindingReceived: RvMessageItemLeftBinding
    ) : RecyclerView.ViewHolder(bindingReceived.root) {
        fun bind(chat: Chat) {
            bindingReceived.messageTv.text = chat.message
            bindingReceived.messageTimeTv.text = chat.timestamp?.let { getTimeInHHMMAA(it) }
        }
    }

    class SentImageMessageViewHolder(
        private val bindingImageSent: RvMessageImageItemRightBinding
    ) : RecyclerView.ViewHolder(bindingImageSent.root) {
        fun bind(chat: Chat) {

            if (chat.imageUrl.equals(DEFAULT_IMG_URL)) {
                chatImageUploadingUIState(chat)
            } else {
                chatImageUploadedUIState(chat)
            }
        }

        private fun chatImageUploadedUIState(chat: Chat) {
            bindingImageSent.messageIv.visibility = View.VISIBLE
            bindingImageSent.messagePb.visibility = View.GONE

            CoroutineScope(Dispatchers.Main).launch {
                Glide.with(bindingImageSent.root.context)
                    .load(chat.imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(bindingImageSent.messageIv)
            }
            bindingImageSent.messageTimeTv.text = chat.timestamp?.let { getTimeInHHMMAA(it) }
        }

        private fun chatImageUploadingUIState(chat: Chat) {
            bindingImageSent.messageIv.visibility = View.GONE
            bindingImageSent.messagePb.visibility = View.VISIBLE
            bindingImageSent.messageTimeTv.text = chat.timestamp?.let { getTimeInHHMMAA(it) }
        }
    }

    class ReceivedImageMessageViewHolder(
        private val bindingImageReceived: RvMessageImageItemLeftBinding
    ) : RecyclerView.ViewHolder(bindingImageReceived.root) {
        fun bind(chat: Chat) {

            if (chat.imageUrl.equals("default")) {
                chatImageUploadingUIState()
            } else {
                chatImageUploadedUIState(chat)
            }
        }

        private fun chatImageUploadedUIState(chat: Chat) {
            bindingImageReceived.root.visibility = View.VISIBLE
            bindingImageReceived.root.layoutParams = RecyclerView.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.WRAP_CONTENT)
            bindingImageReceived.messageIv.visibility = View.VISIBLE
            bindingImageReceived.messagePb.visibility = View.GONE

            CoroutineScope(Dispatchers.Main).launch {
                Glide.with(bindingImageReceived.root.context)
                    .load(chat.imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(bindingImageReceived.messageIv)
            }
            bindingImageReceived.messageTimeTv.text = chat.timestamp?.let { getTimeInHHMMAA(it) }
        }

        private fun chatImageUploadingUIState() {
            bindingImageReceived.root.visibility = View.GONE
            bindingImageReceived.root.layoutParams = RecyclerView.LayoutParams(0,0)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when (viewType) {
            SENT_TEXT_MSG -> {
                _bindingSent = RvMessageItemRightBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return SentMessageViewHolder(bindingSent!!)
            }

            RECEIVED_TEXT_MSG -> {
                _bindingReceived = RvMessageItemLeftBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ReceivedMessageViewHolder(bindingReceived!!)
            }

            SENT_IMAGE_MSG -> {
                _bindingImageSent = RvMessageImageItemRightBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return SentImageMessageViewHolder(bindingImageSent!!)
            }

            else -> {
                _bindingImageReceived = RvMessageImageItemLeftBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ReceivedImageMessageViewHolder(bindingImageReceived!!)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (messageList?.get(position)?.senderId == currentUserId) {
            if (messageList?.get(position)?.messageType == MSG_TYPE_TEXT) {
                (holder as SentMessageViewHolder).bind(messageList?.get(position)!!)
            } else {
                (holder as SentImageMessageViewHolder).bind(messageList?.get(position)!!)
            }
        } else {
            if (messageList?.get(position)?.messageType == MSG_TYPE_TEXT) {
                (holder as ReceivedMessageViewHolder).bind(messageList?.get(position)!!)
            } else {
                (holder as ReceivedImageMessageViewHolder).bind(messageList?.get(position)!!)
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList?.size ?: 0
    }

    override fun getItemViewType(position: Int): Int {
//        return super.getItemViewType(position)
        if (messageList?.get(position)?.senderId == currentUserId) {
            if (messageList?.get(position)?.messageType == MSG_TYPE_TEXT) {
                return SENT_TEXT_MSG
            }
            return SENT_IMAGE_MSG
        }
        if (messageList?.get(position)?.messageType == MSG_TYPE_TEXT) {
            return RECEIVED_TEXT_MSG
        }
        return RECEIVED_IMAGE_MSG
    }

    companion object {
        private const val MSG_TYPE_TEXT = "text"
        private const val MSG_TYPE_IMAGE = "image"

        private const val SENT_TEXT_MSG = 1
        private const val RECEIVED_TEXT_MSG = 2
        private const val SENT_IMAGE_MSG = 3
        private const val RECEIVED_IMAGE_MSG = 4

        fun getTimeInHHMMAA(timeInMillis: Long): String {
            val date = Date(timeInMillis)
            return SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(date)
        }
    }

}