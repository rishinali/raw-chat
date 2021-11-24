package com.rishinali.rawchat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rishinali.rawchat.model.Result
import com.rishinali.rawchat.model.User
import com.rishinali.rawchat.repo.AuthRepo
import kotlinx.coroutines.launch

class AuthViewModel: ViewModel() {

    private val authRepo = AuthRepo()

    fun doSignIn(user: User) =
        viewModelScope.launch {
            authRepo.signIn(user)
        }

    fun getSignInStatus(): LiveData<Result> {
        return authRepo.isUserLoggedInLiveData
    }

    fun doSignUp(user: User) =
        viewModelScope.launch {
            authRepo.signUp(user)
        }

    fun getAccountCreationStatus(): LiveData<Result> {
        return authRepo.isUserAccountCreatedLiveData
    }
}