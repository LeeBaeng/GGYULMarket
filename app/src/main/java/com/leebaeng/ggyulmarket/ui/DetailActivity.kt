package com.leebaeng.ggyulmarket.ui

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.leebaeng.ggyulmarket.R
import com.leebaeng.ggyulmarket.common.constants.DBKey
import com.leebaeng.ggyulmarket.common.ext.getPriceCommaFormat
import com.leebaeng.ggyulmarket.common.ext.getPriceCommaFormatWithWon
import com.leebaeng.ggyulmarket.common.ext.getTimeGapFormatString
import com.leebaeng.ggyulmarket.common.ext.showShortToast
import com.leebaeng.ggyulmarket.databinding.ActivityDetailBinding
import com.leebaeng.ggyulmarket.model.MarketModel
import com.leebaeng.ggyulmarket.model.UserModel
import com.leebaeng.util.log.logD
import java.util.*

class DetailActivity : BaseActivity() {
    lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTransparentSystemUI()

        binding.statusBar.setPadding(0, resources.getDimensionPixelSize(resources.getIdentifier("status_bar_height", "dimen", "android")), 0, 0)
    }

    private fun setTransparentSystemUI() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        if (Build.VERSION.SDK_INT >= 30) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }

        resources.getIdentifier("status_bar_height", "dimen", "android").logD()
        resources.getIdentifier("navigation_bar_height", "dimen", "android").logD()

        binding.rootContainer.setPadding(
            0, 0, 0,
            resources.getDimensionPixelSize(resources.getIdentifier("navigation_bar_height", "dimen", "android"))

//        binding.scvPage.setPadding(0, 0, 0,
//            resources.getDimensionPixelSize(resources.getIdentifier("navigation_bar_height", "dimen", "android"))
        )
    }
}