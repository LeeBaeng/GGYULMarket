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
    private lateinit var marketModel: MarketModel
    private lateinit var userModel: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTransparentSystemUI()

        binding.statusBar.setPadding(0, resources.getDimensionPixelSize(resources.getIdentifier("status_bar_height", "dimen", "android")), 0, 0)

        bindData(intent.getSerializableExtra("model") as MarketModel)
    }

    private fun bindData(model: MarketModel) {
        marketModel = model
        val newViewCnt = (model.viewCnt ?: 0) + 1

        val auth = Firebase.auth
        val collectionRef = Firebase.firestore.collection(DBKey.TABLE_MARKET_LIST)
        collectionRef.document(model.id).update("viewCnt", newViewCnt)

        binding.apply {
            if (!model.imgUrl.isNullOrEmpty()) {
                Glide.with(this@DetailActivity)
                    .load(model.imgUrl[0])
                    .into(imgDetailPhoto)
            }

            txtTitle.text = model.title
            txtCreatedAt.text = Date(model.createdAt).getTimeGapFormatString()
            txtDescription.text = model.description
            txtPrice.text = model.price.getPriceCommaFormatWithWon()
            txtReadCount.text = getString(R.string.atv_detail_view_cnt, newViewCnt.getPriceCommaFormat())
            txtLikeCnt.text = (model.likeCnt ?: 0).getPriceCommaFormat()
            txtPriceProposeAble.isVisible = model.priceProposeAble ?: false

            auth.currentUser?.let {
                if (it.uid == model.sellerId) return@let

                val likedUserList = (model.likedUserList ?: mutableListOf()) as MutableList
                if (likedUserList.contains(it.uid))
                    imgFavorite.setImageResource(R.drawable.ic_baseline_favorite_24)

                imgFavorite.setOnClickListener { _ ->
                    var newLikeCnt = (model.likeCnt ?: 0)
                    if (!likedUserList.contains(it.uid)) {
                        newLikeCnt++
                        likedUserList.add(auth.currentUser!!.uid)
                        imgFavorite.setImageResource(R.drawable.ic_baseline_favorite_24)
                    } else {
                        if (newLikeCnt > 0) newLikeCnt--
                        likedUserList.remove(auth.currentUser!!.uid)
                        imgFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                    }
                    txtLikeCnt.text = newLikeCnt.getPriceCommaFormat()
                    collectionRef.document(model.id).update("likeCnt", newLikeCnt)
                    collectionRef.document(model.id).update("likedUserList", likedUserList)
                }
            }
            if (auth.currentUser == null) {
                imgFavorite.setOnClickListener {
                    "비회원은 좋아요를 할 수 없습니다.".showShortToast(this@DetailActivity)
                }
            }
        }
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