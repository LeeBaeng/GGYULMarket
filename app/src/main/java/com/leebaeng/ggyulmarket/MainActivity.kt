package com.leebaeng.ggyulmarket

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.leebaeng.ggyulmarket.chatlist.ChatListFragment
import com.leebaeng.ggyulmarket.home.HomeFragment
import com.leebaeng.ggyulmarket.login.LoginFragment
import com.leebaeng.ggyulmarket.mypage.MyPageFragment
import com.leebaeng.util.log.LLog

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        Firebase.database.reference.child("Users").child("ID")
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }
}