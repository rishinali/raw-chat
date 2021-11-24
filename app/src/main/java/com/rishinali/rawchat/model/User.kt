package com.rishinali.rawchat.model

data class User(
    var uid: String? = null,
    var name: String? = null,
    var email: String? = null,
    var password: String? = null,
    var confirmPassword: String? = null,
    var imageUrl: String? = "default",
)
