package com.leebaeng.ggyulmarket.ui.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.leebaeng.ggyulmarket.R
import com.leebaeng.ggyulmarket.common.Constants
import com.leebaeng.ggyulmarket.databinding.FragmentMypageBinding
import com.leebaeng.ggyulmarket.di.DBAdapter
import com.leebaeng.ggyulmarket.ui.BaseFragment
import com.leebaeng.ggyulmarket.ui.MainActivity
import com.leebaeng.util.log.logW
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyPageFragment : BaseFragment(R.layout.fragment_mypage) {
    @Inject lateinit var dbAdapter: DBAdapter
    private lateinit var binding: FragmentMypageBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if(dbAdapter.auth.currentUser == null && activity != null && activity is MainActivity){
            (activity as MainActivity).replaceFragment(Constants.FRAGMENT_ID_LOGIN)
            return null
        }else{
            dbAdapter.auth.logW("Auth is Not Null!! (signIn state) : ${dbAdapter.auth.uid}")
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentMypageBinding.bind(view)
    }
}