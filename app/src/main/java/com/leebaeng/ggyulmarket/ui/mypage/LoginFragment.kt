package com.leebaeng.ggyulmarket.ui.mypage

import android.os.Bundle
import android.view.View
import com.leebaeng.ggyulmarket.R
import com.leebaeng.ggyulmarket.common.Constants
import com.leebaeng.ggyulmarket.common.ext.showShortToast
import com.leebaeng.ggyulmarket.databinding.FragmentLoginBinding
import com.leebaeng.ggyulmarket.di.DBAdapter
import com.leebaeng.ggyulmarket.ui.BaseFragment
import com.leebaeng.ggyulmarket.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    @Inject
    lateinit var dbAdapter: DBAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentLoginBinding.bind(view)
        binding.fragment = this
    }

    fun onLoginBtnClicked(view: View) {
        if (binding.edtId.text.isNullOrEmpty() || binding.edtPwd.text.isNullOrEmpty()) {
            "이메일과 비밀번호를 모두 입력해 주세요.".showShortToast(binding.root.context)
            return
        }

        if (dbAdapter.auth.currentUser == null) {
            // Login
            dbAdapter.auth.signInWithEmailAndPassword(binding.edtId.text.toString(), binding.edtPwd.text.toString()).addOnCompleteListener {
                if (!it.isSuccessful || dbAdapter.auth.currentUser == null){
                    "로그인에 실패 했습니다.\n이메일과 패스워드를 확인해 주세요".showShortToast(binding.root.context)
                    return@addOnCompleteListener
                }
                changeActivitiesFragment(Constants.FRAGMENT_ID_MY_PAGE)
            }
        }
//        else{
//            // logOut
//            dbAdapter.auth.signOut()
//        }
    }

    fun onJoinBtnClicked(view: View) {
        changeActivitiesFragment(Constants.FRAGMENT_ID_JOIN)
    }

    fun onFindPwdBtnClicked(view: View) {

    }

    fun onFaceBookLoginClicked(view: View) {

    }

    private fun changeActivitiesFragment(id: String){
        if(activity != null && activity is MainActivity)
            (activity as MainActivity).replaceFragment(id)
    }
}