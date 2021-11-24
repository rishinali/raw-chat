package com.rishinali.rawchat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rishinali.rawchat.model.Result
import com.rishinali.rawchat.model.User
import com.rishinali.rawchat.repo.AuthRepo
import com.rishinali.rawchat.repo.GetUserListRepo
import kotlinx.coroutines.launch

class UserListViewModel: ViewModel() {

    private val getUserListRepo = GetUserListRepo()
    private val authRepo = AuthRepo()

    private val _userListLiveData = MutableLiveData<List<User>> ()
    val userListLiveData: LiveData<List<User>> = _userListLiveData

    fun getUserList() {
        viewModelScope.launch {
            getUserListRepo.getUserList(_userListLiveData)
        }
    }

    fun signOutUser() {
        viewModelScope.launch {
            authRepo.signOut()
        }
    }

    fun getSignedInStatus(): LiveData<Result> {
        return authRepo.isUserLoggedInLiveData
    }
}