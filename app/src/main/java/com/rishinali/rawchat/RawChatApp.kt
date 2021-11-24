package com.rishinali.rawchat

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.rishinali.rawchat.repo.AuthRepo

class RawChatApp: Application() {

    override fun onCreate() {
        super.onCreate()

        //For enabling offline capabilities for RtDb
        if (FirebaseApp.getApps(this).isNotEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        }
    }
}