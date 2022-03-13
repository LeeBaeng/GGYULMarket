package com.leebaeng.ggyulmarket.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.leebaeng.ggyulmarket.R
import com.leebaeng.ggyulmarket.common.Constants
import com.leebaeng.ggyulmarket.di.DBAdapter
import com.leebaeng.ggyulmarket.ui.chatlist.ChatListFragment
import com.leebaeng.ggyulmarket.ui.home.HomeFragment
import com.leebaeng.ggyulmarket.ui.mypage.JoinFragment
import com.leebaeng.ggyulmarket.ui.mypage.LoginFragment
import com.leebaeng.ggyulmarket.ui.mypage.MyPageFragment
import com.leebaeng.util.log.LLog
import com.leebaeng.util.log.logE
import com.leebaeng.util.log.logS
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    @Inject lateinit var dbAdapter: DBAdapter
    private val fragmentMap = hashMapOf<String, Fragment>(
        Constants.FRAGMENT_ID_HOME to HomeFragment(),
        Constants.FRAGMENT_ID_CHAT_LIST to ChatListFragment(),
        Constants.FRAGMENT_ID_LOGIN to LoginFragment(),
        Constants.FRAGMENT_ID_JOIN to JoinFragment(),
        Constants.FRAGMENT_ID_MY_PAGE to MyPageFragment()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LLog.init(this)

        setContentView(R.layout.activity_main)
        replaceFragment(fragmentMap[Constants.FRAGMENT_ID_HOME])

        val bottomNavView = findViewById<BottomNavigationView>(R.id.navViewBottom)
        bottomNavView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    replaceFragment(fragmentMap[Constants.FRAGMENT_ID_HOME])
                    return@setOnItemSelectedListener true
                }
                R.id.chatList -> {
                    replaceFragment(fragmentMap[Constants.FRAGMENT_ID_CHAT_LIST])
                    return@setOnItemSelectedListener true
                }
                R.id.myPage -> {
                    replaceFragment(fragmentMap[Constants.FRAGMENT_ID_MY_PAGE])
                    return@setOnItemSelectedListener true
                }
            }
            return@setOnItemSelectedListener false
        }

        findViewById<Button>(R.id.btnTest).setOnClickListener {
            "Start ItemDetailActivity!!".logS()
            startActivity(Intent(this, DetailActivity::class.java))
        }

        Firebase.database.reference.child("Users").child("ID")
    }

    fun replaceFragment(id: String) {
        replaceFragment(fragmentMap[id])
    }

    fun replaceFragment(fragment: Fragment?) {
        if(fragment != null){
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
        }else "ReplaceFragment Failed!! Fragment is null!".logE()
    }
}