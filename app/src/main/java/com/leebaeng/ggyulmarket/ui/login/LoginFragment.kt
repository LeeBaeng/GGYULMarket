package com.leebaeng.ggyulmarket.ui.login

import android.os.Bundle
import android.view.View
import com.leebaeng.ggyulmarket.R
import com.leebaeng.ggyulmarket.databinding.FragmentLoginBinding
import com.leebaeng.ggyulmarket.ui.BaseFragment

class LoginFragment : BaseFragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentLoginBinding.bind(view)
    }
}