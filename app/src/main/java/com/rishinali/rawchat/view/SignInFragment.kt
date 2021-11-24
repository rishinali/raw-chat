package com.rishinali.rawchat.view


import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.rishinali.rawchat.R
import com.rishinali.rawchat.databinding.FragmentSignInBinding
import com.rishinali.rawchat.model.Status
import com.rishinali.rawchat.model.User
import com.rishinali.rawchat.viewmodel.AuthViewModel

private const val TAG = "Auth"

class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        binding.goToSignUpTv.setOnClickListener {
            navController.navigate(R.id.action_signInFragment_to_signUpFragment)
        }

        binding.signInBtn.setOnClickListener {

            prepareSignIn()
        }

        initialUIState()
        getSignInStatus()
    }

    private fun prepareSignIn() {

        val email = binding.signInEmailEt.text.toString()
        val password = binding.signInPasswordEt.text.toString()

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                if (password.length >= 6) {

                    val newUser = User(
                        null,
                        name = null,
                        email = email,
                        password = password,
                        confirmPassword = null,
                        null
                    )

                    authViewModel.doSignIn(newUser)
                    signingInUIState()

                } else {
                    Toast.makeText(
                        requireContext(),
                        "Password must be at least 6 characters!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please enter a valid email!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                requireContext(),
                "Email or password cannot be empty!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getSignInStatus() {
        Log.d(TAG, "SignIn getSignInStatus: observing sign in status")

        authViewModel.getSignInStatus().observe(viewLifecycleOwner, { signedResult ->

            when(signedResult.status) {
                Status.SIGNED_OUT -> {
                    initialUIState()
                    Log.d(TAG, "SignIn getSignInStatus: signed out")
                }
                Status.SIGNED_IN -> {
                    goToUserListFragment()
                    Log.d(TAG, "SignIn getSignInStatus: signed in")
                }
                Status.SIGNING_IN -> {
                    signingInUIState()
                    Log.d(TAG, "SignIn getSignInStatus: signing in")
                }
                Status.SIGNING_ERROR -> {
                    initialUIState()
                    Toast.makeText(
                        requireContext(),
                        signedResult.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(TAG, "SignIn getSignInStatus: error = ${signedResult.message}")
                }
            }
        })
    }

    private fun goToUserListFragment() {
        navController.navigate(R.id.action_global_userListFragment)
    }

    private fun initialUIState() {
        binding.signInEmailEt.isEnabled = true
        binding.signInPasswordEt.isEnabled = true
        binding.goToSignUpTv.visibility = View.VISIBLE
        binding.signInPb.visibility = View.GONE
        binding.signInBtn.isEnabled = true
        binding.signInBtn.visibility = View.VISIBLE
    }

    private fun signingInUIState() {
        binding.signInEmailEt.isEnabled = false
        binding.signInPasswordEt.isEnabled = false
        binding.goToSignUpTv.visibility = View.GONE
        binding.signInPb.visibility = View.VISIBLE
        binding.signInBtn.isEnabled = false
        binding.signInBtn.visibility = View.GONE
    }

}