package com.leebaeng.ggyulmarket.ui.mypage

import android.net.Uri
import android.os.Bundle
import android.view.View
import com.leebaeng.ggyulmarket.R
import com.leebaeng.ggyulmarket.common.Constants
import com.leebaeng.ggyulmarket.common.ext.showShortToast
import com.leebaeng.ggyulmarket.databinding.FragmentJoinBinding
import com.leebaeng.ggyulmarket.di.DBAdapter
import com.leebaeng.ggyulmarket.model.UserModel
import com.leebaeng.ggyulmarket.ui.AddMarketItemActivity
import com.leebaeng.ggyulmarket.ui.BaseFragment
import com.leebaeng.ggyulmarket.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class JoinFragment : BaseFragment(R.layout.fragment_join) {
    private lateinit var binding: FragmentJoinBinding

    @Inject
    lateinit var dbAdapter: DBAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentJoinBinding.bind(view)
        binding.fragment = this
    }

    fun onJoinBtnClicked(view: View) {
        val email = binding.edtId.text.toString()
        val pwd = binding.edtPwd.text.toString()
        val pwdChk = binding.edtPwdChk.text.toString()
        val nickName = binding.edtNickname.text.toString()
        val address = binding.edtAddress.text.toString()

        if (email.isEmpty() || pwd.isEmpty() || pwdChk.isEmpty() || nickName.isEmpty() || address.isEmpty()) {
            "모든 항목을 입력해 주세요.".showShortToast(binding.root.context)
            return
        } else if (pwd != pwdChk) {
            "입력한 비밀번호가 다릅니다.".showShortToast(binding.root.context)
            return
        } else if (pwd.length < 6) {
            "6자리 이상의 비밀번호를 입력해야 합니다.".showShortToast(binding.root.context)
            return
        }

        if (dbAdapter.auth.currentUser == null) {
            // createUser
            dbAdapter.auth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener { joinTask ->
                if (joinTask.isSuccessful) {
                    dbAdapter.auth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener { loginTask ->
                        if (loginTask.isSuccessful && dbAdapter.auth.currentUser != null) {
                            dbAdapter.myUserModel = UserModel(
                                id = dbAdapter.auth.currentUser!!.uid,
                                nickName = nickName,
                                location = address,
                                profileImgUrl = null, // TODO : Image 업로드 및 URL 설정
                            )
                            dbAdapter.userDB.document(dbAdapter.auth.currentUser!!.uid).set(dbAdapter.myUserModel!!).addOnCompleteListener {
                                if (it.isSuccessful && activity != null && activity is MainActivity) (activity as MainActivity).replaceFragment(Constants.FRAGMENT_ID_MY_PAGE)
                                else "회원 가입 실패 : ${it.exception}".showShortToast(binding.root.context)
                            }
                        } else {
                            "회원 가입에 실패 했습니다.\n이메일과 패스워드를 확인해 주세요 (E-2)".showShortToast(binding.root.context)
                        }
                    }
                } else {
                    "회원 가입에 실패 했습니다.\n이메일과 패스워드를 확인해 주세요 (E-1)".showShortToast(binding.root.context)
                }
            }
        }
    }

    fun onChangePictureBtnClicked(view: View) {

    }

    fun uploadPhoto(fileName: String, localUri: Uri, callback: (AddMarketItemActivity.UploadPhotoResult) -> Unit) {
        val storageRef = dbAdapter.marketPhotoRef.child(fileName)
        storageRef
            .putFile(localUri)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    storageRef.downloadUrl.addOnSuccessListener { remoteUri ->
                        callback(AddMarketItemActivity.UploadPhotoResult(true, remoteUri.toString()))
                    }.addOnFailureListener { e ->
                        callback(AddMarketItemActivity.UploadPhotoResult(false, exception = e))
                    }
                } else {
                    callback(AddMarketItemActivity.UploadPhotoResult(false, exception = it.exception))
                }
            }
    }
}