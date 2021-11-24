package com.rishinali.rawchat.view

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.Navigation
import com.rishinali.rawchat.R
import com.rishinali.rawchat.databinding.FragmentSignUpBinding
import com.rishinali.rawchat.model.Status
import com.rishinali.rawchat.model.User
import com.rishinali.rawchat.viewmodel.AuthViewModel

private const val TAG = "Auth"

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private lateinit var navGraph: NavGraph
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.signUpToolbar.root)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = ""
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initialUIState()
        getAccountCreationStatus()

        binding.goToSignInTv.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.signUpBtn.setOnClickListener {

            prepareSignUp()
        }
    }


    private fun prepareSignUp() {

        val name = binding.signUpNameEt.text.toString()
        val email = binding.signUpEmailEt.text.toString()
        val password = binding.signUpPasswordEt.text.toString()
        val confirmPassword = binding.signUpConfirmPasswordEt.text.toString()

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                if (password.length >= 6) {
                    if (password == confirmPassword) {

                        val newUser = User(
                            null,
                            name = name,
                            email = email,
                            password = password,
                            confirmPassword = confirmPassword,
                            null
                        )

                        authViewModel.doSignUp(newUser)
                        signingUpUIState()
//                        getAccountCreationStatus()

                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Password and confirm password must be same!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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
                "All fields are required!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getAccountCreationStatus() {
        Log.d(TAG, "SignUp getAccountCreationStatus: observing to account creation status")

        authViewModel.getAccountCreationStatus().observe(viewLifecycleOwner, Observer { signedResult ->

            when(signedResult.status) {
                Status.SIGNED_OUT -> {
                    initialUIState()
                    Log.d(TAG, "SignUp getAccountCreationStatus: signed out")
                }

                Status.SIGNING_IN -> {
                    signingUpUIState()
                    Log.d(TAG, "SignUp getAccountCreationStatus: creating account")
                }

                Status.SIGNED_IN -> {
                    Log.d(TAG, "SignUp getAccountCreationStatus: account created")
                    goToChatListFragment()
                }

                Status.SIGNING_ERROR -> {
                    initialUIState()
                    Toast.makeText(
                        requireContext(),
                        signedResult.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(TAG, "SignUp getAccountCreationStatus: account not created ${signedResult.message}")
                }
            }
        })
    }

    private fun goToChatListFragment() {
        navController.navigate(R.id.action_global_landingFragment)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> requireActivity().onBackPressed()
        }
        return true
    }

    private fun initialUIState() {
        binding.signUpNameEt.isEnabled = true
        binding.signUpEmailEt.isEnabled = true
        binding.signUpPasswordEt.isEnabled = true
        binding.signUpConfirmPasswordEt.isEnabled = true
        binding.goToSignInTv.visibility = View.VISIBLE
        binding.signUpBtn.isEnabled = true
        binding.signUpBtn.visibility = View.VISIBLE
        binding.signUpPb.visibility = View.GONE
    }

    private fun signingUpUIState() {
        binding.signUpNameEt.isEnabled = false
        binding.signUpEmailEt.isEnabled = false
        binding.signUpPasswordEt.isEnabled = false
        binding.signUpConfirmPasswordEt.isEnabled = false
        binding.goToSignInTv.visibility = View.GONE
        binding.signUpBtn.isEnabled = false
        binding.signUpBtn.visibility = View.GONE
        binding.signUpPb.visibility = View.VISIBLE
    }

}