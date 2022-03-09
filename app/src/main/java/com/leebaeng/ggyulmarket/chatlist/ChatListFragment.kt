package com.leebaeng.ggyulmarket.chatlist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.leebaeng.ggyulmarket.R
import com.leebaeng.ggyulmarket.databinding.FragmentChatlistBinding

class ChatListFragment : Fragment(R.layout.fragment_chatlist) {
    private lateinit var binding: FragmentChatlistBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentChatlistBinding.bind(view)
    }
}