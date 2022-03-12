package com.leebaeng.ggyulmarket.ui.mypage

import android.os.Bundle
import android.view.View
import com.leebaeng.ggyulmarket.R
import com.leebaeng.ggyulmarket.databinding.FragmentMypageBinding
import com.leebaeng.ggyulmarket.ui.BaseFragment

class MyPageFragment : BaseFragment(R.layout.fragment_mypage) {
    private lateinit var binding: FragmentMypageBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentMypageBinding.bind(view)
    }
}