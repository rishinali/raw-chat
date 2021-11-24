package com.rishinali.rawchat.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.rishinali.rawchat.R
import com.rishinali.rawchat.adapter.UserListAdapter
import com.rishinali.rawchat.databinding.FragmentUserListBinding
import com.rishinali.rawchat.interfaces.UserListItemClickListener
import com.rishinali.rawchat.model.Status
import com.rishinali.rawchat.model.User
import com.rishinali.rawchat.viewmodel.UserListViewModel


private const val TAG = "UserList"
private const val FRIEND_NAME = "FRIEND_NAME"
private const val FRIEND_ID = "FRIEND_ID"

class UserListFragment : Fragment(), UserListItemClickListener {

    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private lateinit var userListViewModel: UserListViewModel
    private lateinit var userListAdapter: UserListAdapter

    private val _isUserSignedInMutableLiveData = MutableLiveData<Boolean>()
    private val isUserSignedInLiveData get() = _isUserSignedInMutableLiveData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        userListViewModel = ViewModelProvider(this).get(UserListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUserListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.userListToolbar.root)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "RawChat"
//        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initViews()
    }

    private fun initViews() {
        binding.userListRv.apply {
            layoutManager = LinearLayoutManager(this@UserListFragment.context)
            userListAdapter = UserListAdapter(this@UserListFragment)
            adapter = userListAdapter
        }

        getDataForRv()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getDataForRv() {
        userListViewModel.userListLiveData.observe(viewLifecycleOwner, { userList ->
            if (userList != null) {
                Log.d(TAG, "getDataForRv: observed user list = ${userList.toString()}")

                userListAdapter.setUserList(userList)
                userListAdapter.notifyDataSetChanged()
            } else {
                Log.d(TAG, "getDataForRv: observed user list is null")
            }
        })
    }

    override fun onStart() {
        super.onStart()

        userListViewModel.getUserList()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return super.onOptionsItemSelected(item)
        when (item.itemId) {
            R.id.signOutMenuItem -> {
                userListViewModel.signOutUser()
                getSignInStatus()
            }
        }
        return true
    }

    private fun getSignInStatus() {
        userListViewModel.getSignedInStatus().observe(viewLifecycleOwner, { signedResult ->

            when(signedResult.status) {
                Status.SIGNED_OUT -> {
                    goToAuthGraph()
                    Log.d(TAG, "getSignInStatus: signed out")
                }
                else -> {
                    Log.d(TAG, "getSignInStatus: signed in")
                }
            }
        })
    }

    private fun goToAuthGraph() {
        navController.navigate(R.id.action_userListFragment_to_authGraph)
    }

    override fun onUserItemClick(user: User) {
        Log.d(TAG, "onUserItemClick: user list item clicked ${user.toString()}")

        val bundle = bundleOf(FRIEND_NAME to user.name)
        bundle.putString(FRIEND_ID, user.uid)

        navController.navigate(R.id.action_userListFragment_to_chatFragment, bundle)
    }

}