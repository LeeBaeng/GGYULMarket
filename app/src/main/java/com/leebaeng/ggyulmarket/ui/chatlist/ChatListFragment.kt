package com.leebaeng.ggyulmarket.ui.chatlist

import android.os.Bundle
import android.view.View
import com.leebaeng.ggyulmarket.R
import com.leebaeng.ggyulmarket.databinding.FragmentChatlistBinding
import com.leebaeng.ggyulmarket.ui.BaseFragment

class ChatListFragment : BaseFragment(R.layout.fragment_chatlist) {
    private lateinit var binding: FragmentChatlistBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentChatlistBinding.bind(view)
    }
}