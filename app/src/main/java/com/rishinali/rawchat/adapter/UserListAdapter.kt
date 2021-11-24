package com.rishinali.rawchat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rishinali.rawchat.databinding.RvUserItemBinding
import com.rishinali.rawchat.interfaces.UserListItemClickListener
import com.rishinali.rawchat.model.User

class UserListAdapter(
    private val userListItemClickListener: UserListItemClickListener
): RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {

    private var _binding: RvUserItemBinding? = null
    private val binding get() = _binding

    private var userList: List<User>? = null

    fun setUserList(userList: List<User>) {
        this.userList = userList
    }

    class UserViewHolder(
        private val binding: RvUserItemBinding,
        private val userListItemClickListener: UserListItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.userItemNameTv.text = user.name
            binding.userItemEmailTv.text = user.email

            binding.rvUserItemCl.setOnClickListener {
                userListItemClickListener.onUserItemClick(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        _binding = RvUserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding!!, userListItemClickListener)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user: User? = userList?.get(position)
        if (user != null) {
            holder.bind(user)
        }
    }

    override fun getItemCount(): Int {
        return userList?.size ?: 0
    }
}