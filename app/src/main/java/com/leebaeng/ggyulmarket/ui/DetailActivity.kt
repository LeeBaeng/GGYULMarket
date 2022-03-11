package com.leebaeng.ggyulmarket.ui

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.leebaeng.ggyulmarket.databinding.ActivityDetailBinding
import com.leebaeng.util.log.logD
import com.leebaeng.util.log.logS

class DetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        "DetailActivity onCreated!!".logS()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTransparentSystemUI()

        binding.statusBar.setPadding(0, resources.getDimensionPixelSize(resources.getIdentifier("status_bar_height", "dimen", "android")), 0, 0)
    }

    private fun setTransparentSystemUI(){
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        if(Build.VERSION.SDK_INT >= 30){
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }

        resources.getIdentifier("status_bar_height", "dimen", "android").logD()
        resources.getIdentifier("navigation_bar_height", "dimen", "android").logD()

        binding.rootContainer.setPadding(0, 0, 0,
            resources.getDimensionPixelSize(resources.getIdentifier("navigation_bar_height", "dimen", "android"))

//        binding.scvPage.setPadding(0, 0, 0,
//            resources.getDimensionPixelSize(resources.getIdentifier("navigation_bar_height", "dimen", "android"))
        )
    }
}