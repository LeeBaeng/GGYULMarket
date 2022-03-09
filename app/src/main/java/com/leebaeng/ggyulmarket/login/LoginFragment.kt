package com.leebaeng.ggyulmarket.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.leebaeng.ggyulmarket.R
import com.leebaeng.ggyulmarket.databinding.FragmentLoginBinding

class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentLoginBinding.bind(view)
    }
}