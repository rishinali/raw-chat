package com.rishinali.rawchat.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseUser
import com.rishinali.rawchat.R
import com.rishinali.rawchat.adapter.MessageListAdapter
import com.rishinali.rawchat.databinding.FragmentChatBinding
import com.rishinali.rawchat.model.Chat
import com.rishinali.rawchat.viewmodel.ChatViewModel

private const val TAG = "Chat"
private const val FRIEND_NAME = "FRIEND_NAME"
private const val FRIEND_ID = "FRIEND_ID"
private const val MSG_TYPE_TEXT = "text"
private const val MSG_TYPE_IMAGE = "image"

class ChatFragment : Fragment() {

    private var mFriendName: String? = null
    private var mFriendId: String? = null

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    private lateinit var chatViewModel: ChatViewModel

    private val _currentUserMutableLiveData = MutableLiveData<FirebaseUser> ()
    private val currentUserLiveData get() = _currentUserMutableLiveData

    private var compositeChatNodeKey: String? = null

    private lateinit var chatAdapter: MessageListAdapter

    private lateinit var selectImageLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        arguments?.let {
            mFriendName = it.getString(FRIEND_NAME)
            mFriendId = it.getString(FRIEND_ID)
        }

        chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.chatToolbar.root)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = mFriendName
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

        initView()
        getCurrentUser()
    }

    private fun initView() {

        selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                Log.d(TAG, "initView: image pick result url = ${result.data?.data?.toString()}")
                val imageUrl = result.data?.data?.toString()

                if (imageUrl != null) {
                    val imageUri = Uri.parse(imageUrl)
                    uploadImageToDb(imageUri)
                }
            }
        }

        binding.chatMessageSend.setOnClickListener {
            validateMessage()
        }

        binding.chatMessageAttach.setOnClickListener {
            selectImageFromStorage()
        }
    }

    private fun uploadImageToDb(imageUri: Uri) {

        val chat = Chat(
            messageType = MSG_TYPE_IMAGE,
            senderId = currentUserLiveData.value!!.uid,
            recipientId = mFriendId!!,
        )

        if (compositeChatNodeKey != null) {
            chatViewModel.uploadImageToDb(compositeChatNodeKey!!, chat, imageUri)
        }

    }

    private fun selectImageFromStorage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"

        selectImageLauncher.launch(intent)
    }

    private fun initRv(currentUserId: String) {

        binding.chatRv.apply {
            layoutManager = LinearLayoutManager(this@ChatFragment.context)
            chatAdapter = MessageListAdapter(currentUserId)
            adapter = chatAdapter
        }
    }

    private fun validateMessage() {

        if (
            !TextUtils.isEmpty(binding.chatMessageEt.text.toString()) &&
            compositeChatNodeKey != null &&
            currentUserLiveData.value?.uid != null
        ) {
            val chat = Chat(
                messageId = null,
                message = binding.chatMessageEt.text.toString(),
                imageUrl = null,
                messageType = MSG_TYPE_TEXT,
                senderId = currentUserLiveData.value!!.uid,
                recipientId = mFriendId!!,
                timestamp = null,
            )

            chatViewModel.addChatMessage(compositeChatNodeKey!!, chat)
            binding.chatMessageEt.text.clear()

        } else {
            Log.d(TAG, "validateMessage: empty message error")

            Toast.makeText(
                requireContext(),
                "Message cannot be empty",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getCurrentUser() {
        chatViewModel.currentUserLiveData.observe(viewLifecycleOwner, { firebaseUser ->
            if (firebaseUser != null) {
                Log.d(TAG, "getCurrentUser: email = ${firebaseUser.email}, uid = ${firebaseUser.uid}")

                _currentUserMutableLiveData.postValue(firebaseUser)

                initRv(firebaseUser.uid)
                prepareCompositeChatNodeKey(firebaseUser.uid)
            }
        })
    }

    private fun prepareCompositeChatNodeKey(currentUserId: String) {

        compositeChatNodeKey = if (currentUserId > mFriendId!!) {
            "${mFriendId}_${currentUserId}"
        } else {
            "${currentUserId}_${mFriendId}"
        }

        Log.d(TAG, "prepareCompositeChatNodeKey: compositeChatNodeKey = $compositeChatNodeKey")

        getChatMessageList(compositeChatNodeKey!!)
    }

    private fun getChatMessageList(chatNodeKey: String) {
        chatViewModel.getChatMessageList(chatNodeKey)

        getChatList()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getChatList() {
        chatViewModel.chatListLiveData.observe(viewLifecycleOwner, { chatList ->
            if (chatList != null) {
//                Log.d(TAG, "getChatList: observed chat list = ${chatList.toString()}")

                chatAdapter.setMessageList(chatList)
                chatAdapter.notifyDataSetChanged()
                binding.chatRv.smoothScrollToPosition(chatList.size)
            }
        })
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                requireActivity().onBackPressed()
            }
        }
        return true
    }
}