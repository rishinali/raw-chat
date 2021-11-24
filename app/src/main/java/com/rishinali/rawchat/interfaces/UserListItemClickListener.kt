package com.rishinali.rawchat.interfaces

import com.rishinali.rawchat.model.User

interface UserListItemClickListener {
    fun onUserItemClick(user: User)
}