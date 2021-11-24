package com.rishinali.rawchat.repo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.rishinali.rawchat.model.User


private const val USERS = "users"
private const val TAG = "UserList"

class GetUserListRepo {

    private val usersRtDbRef = FirebaseDatabase.getInstance().reference.child(USERS)
    private val currentUserId = Firebase.auth.currentUser?.uid

    fun getUserList(userListLiveData: MutableLiveData<List<User>>) {
        Log.d(TAG, "getUserList: getting userList")

        usersRtDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val userList = mutableListOf<User>()
                for (userSnapshot: DataSnapshot in snapshot.children) {
                    userSnapshot.getValue(User::class.java)?.let { user ->
                        if (user.uid != currentUserId) {
                            userList.add(user)
                        }
                    }
                }

                Log.d(TAG, "onDataChange: posting the userList to livedata")
                userListLiveData.postValue(userList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Nothing to do
            }

        })
    }

}