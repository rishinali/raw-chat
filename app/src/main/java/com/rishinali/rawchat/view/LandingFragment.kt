package com.rishinali.rawchat.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import com.rishinali.rawchat.R
import com.rishinali.rawchat.model.Status
import com.rishinali.rawchat.viewmodel.AuthViewModel

private const val TAG = "Auth"

class LandingFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_landing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController(view)
    }

    override fun onStart() {
        super.onStart()

        Log.d(TAG, "onStart: checking sign in status")
        getSignedInStatus()
    }

    private fun getSignedInStatus() {
        Log.d(TAG, "getSignedInStatus: inside")
        
        authViewModel.getSignInStatus().observe(this, { signedResult ->

            Log.d(TAG, "getSignedInStatus: sign in status = ${signedResult.status}")

            when(signedResult.status) {
                Status.SIGNED_IN -> {
                    goToUserListFragment()
                    Log.d(TAG, "SignIn getSignInStatus: signed in")
                }
                else -> {
                    goToAuthGraph()
                    Log.d(TAG, "SignIn getSignInStatus: unknown result")
                }
            }
        })
    }

    private fun goToUserListFragment() {
        navController.navigate(R.id.action_landingFragment_to_userListFragment)
    }

    private fun goToAuthGraph() {
        navController.navigate(R.id.action_landingFragment_to_authGraph)
    }

}