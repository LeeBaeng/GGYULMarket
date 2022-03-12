package com.leebaeng.ggyulmarket.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.leebaeng.ggyulmarket.R
import com.leebaeng.ggyulmarket.ui.chatlist.ChatListFragment
import com.leebaeng.ggyulmarket.ui.home.HomeFragment
import com.leebaeng.ggyulmarket.ui.login.LoginFragment
import com.leebaeng.ggyulmarket.ui.mypage.MyPageFragment
import com.leebaeng.util.log.LLog
import com.leebaeng.util.log.logS

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LLog.init(this)

        setContentView(R.layout.activity_main)

        val homeFragment = HomeFragment()
        val chatListFragment = ChatListFragment()
        val myPageFragment = MyPageFragment()
        val loginFragment = LoginFragment()

        replaceFragment(homeFragment)

        val bottomNavView = findViewById<BottomNavigationView>(R.id.navViewBottom)
        bottomNavView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    replaceFragment(homeFragment)
                    return@setOnItemSelectedListener true
                }
                R.id.chatList -> {
                    replaceFragment(chatListFragment)
                    return@setOnItemSelectedListener true
                }
                R.id.myPage -> {
                    replaceFragment(myPageFragment)
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

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }
}