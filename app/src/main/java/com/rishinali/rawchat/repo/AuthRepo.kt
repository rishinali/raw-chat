package com.rishinali.rawchat.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.rishinali.rawchat.model.Result
import com.rishinali.rawchat.model.SignedResult
import com.rishinali.rawchat.model.User

private const val USERS = "users"
private const val TAG = "Auth"

class AuthRepo {

    private val auth = Firebase.auth
    private val usersRtDbRef = FirebaseDatabase.getInstance().reference.child(USERS)
    private var userId: String? = null

    private val _currentUserMutableLiveData = MutableLiveData<FirebaseUser>()
    private val _isUserLoggedInMutableLiveData = MutableLiveData<Result>()
    private val _isUserAccountCreatedMutableLiveData = MutableLiveData<Result>()

    val currentUserLiveData: LiveData<FirebaseUser> get() = _currentUserMutableLiveData
    val isUserLoggedInLiveData: LiveData<Result> get() = _isUserLoggedInMutableLiveData
    val isUserAccountCreatedLiveData: LiveData<Result> get() = _isUserAccountCreatedMutableLiveData

    init {
        Log.d(TAG, "init: authRepo")

        if (auth.currentUser != null) {
            Log.d(TAG, "authRepo init: currentUser = ${auth.currentUser!!.uid}")

            _currentUserMutableLiveData.postValue(auth.currentUser)
            _isUserLoggedInMutableLiveData.postValue(SignedResult.signedIn())
        } else {
            Log.d(TAG, "authRepo init: currentUser is null")

            _isUserLoggedInMutableLiveData.postValue(SignedResult.signedOut())
        }
    }

    fun signUp(user: User) {

        _isUserAccountCreatedMutableLiveData.postValue(SignedResult.signingIn())

        auth.createUserWithEmailAndPassword(user.email!!, user.password!!)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val currentUser = auth.currentUser
                    userId = currentUser!!.uid

                    Log.d(TAG, "signUp: successful with userId = $userId")

                    _currentUserMutableLiveData.postValue(auth.currentUser)
                    _isUserLoggedInMutableLiveData.postValue(SignedResult.signedIn())

                    createUserNode(user)

                } else {
                    _isUserAccountCreatedMutableLiveData.postValue(SignedResult.signingError(it.exception?.message))
                    Log.d(TAG, "signUp: failed with exception -> ${it.exception}")
                }
            }
    }

    private fun createUserNode(user: User) {

        if (userId != null) {
            Log.d(TAG, "createUserNode: usedId = $userId")

            val imageUrl = if(user.imageUrl != null) user.imageUrl else "default"
            val userMap = mapOf(
                "name" to user.name,
                "email" to user.email,
                "imageUrl" to imageUrl,
                "uid" to userId
            )

            usersRtDbRef.child(userId!!).updateChildren(userMap).addOnCompleteListener {
                if (it.isSuccessful) {
                    _isUserAccountCreatedMutableLiveData.postValue(SignedResult.signedIn())
                } else {
                    _isUserAccountCreatedMutableLiveData.postValue(SignedResult.signingError(null))
                }
            }
        }
    }

    fun signIn(user: User) {

        _isUserLoggedInMutableLiveData.postValue(SignedResult.signingIn())

        auth.signInWithEmailAndPassword(user.email!!, user.password!!)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val currentUser = auth.currentUser
                    userId = currentUser!!.uid

                    Log.d(TAG, "signIn: successful with userId = $userId")

                    _currentUserMutableLiveData.postValue(auth.currentUser)
                    _isUserLoggedInMutableLiveData.postValue(SignedResult.signedIn())

                } else {
                    _isUserLoggedInMutableLiveData.postValue(SignedResult.signingError(it.exception?.message))
                    Log.d(TAG, "signIn: failed with exception -> ${it.exception}")
                }
            }
    }

    fun signOut() {
        auth.signOut()
        _isUserLoggedInMutableLiveData.postValue(SignedResult.signedOut())
    }

}