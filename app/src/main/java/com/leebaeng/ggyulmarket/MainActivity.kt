package com.leebaeng.ggyulmarket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.leebaeng.ggyulmarket.chatlist.ChatListFragment
import com.leebaeng.ggyulmarket.home.HomeFragment
import com.leebaeng.ggyulmarket.login.LoginFragment
import com.leebaeng.ggyulmarket.mypage.MyPageFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val homeFragment = HomeFragment()
        val chatListFragment = ChatListFragment()
        val myPageFragment = MyPageFragment()
        val loginFragment = LoginFragment()

        replaceFragment(loginFragment)

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

    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }
}