package com.leebaeng.ggyulmarket.ui

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentReference
import com.leebaeng.ggyulmarket.R
import com.leebaeng.ggyulmarket.common.ext.getPriceCommaFormat
import com.leebaeng.ggyulmarket.common.ext.getPriceCommaFormatWithWon
import com.leebaeng.ggyulmarket.common.ext.getTimeGapFormatString
import com.leebaeng.ggyulmarket.common.ext.showShortToast
import com.leebaeng.ggyulmarket.databinding.ActivityDetailBinding
import com.leebaeng.ggyulmarket.di.DBAdapter
import com.leebaeng.ggyulmarket.model.ChatRoomModel
import com.leebaeng.ggyulmarket.model.MarketModel
import com.leebaeng.ggyulmarket.model.UserModel
import com.leebaeng.util.log.logD
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject

// TODO : 좋아요 및 채팅시작 안되는 이슈 확인 필요
// TODO : DB작업 공통작업은 DBAdapter로 옮기기

@AndroidEntryPoint
class DetailActivity : BaseActivity() {
    @Inject
    lateinit var dbAdapter: DBAdapter

    lateinit var binding: ActivityDetailBinding

    private lateinit var docRefMarket: DocumentReference

    private lateinit var sellerModel: UserModel
    private lateinit var marketModel: MarketModel
    private var buyerModel: UserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        binding.atvDetail = this
        setContentView(binding.root)
        setTransparentSystemUI()

        binding.statusBar.setPadding(0, resources.getDimensionPixelSize(resources.getIdentifier("status_bar_height", "dimen", "android")), 0, 0)

        val marketId = intent.getStringExtra("modelId")
        if (marketId == null) {
            "잘못된 상품 정보 입니다.".showShortToast(this)
            finish()
            return
        }

        val initDBJob = CoroutineScope(Dispatchers.IO).launch { initDB(marketId) }
        CoroutineScope(Dispatchers.Main).launch {
            initDBJob.join()
            updateUI()
        }
    }

    private suspend fun initDB(marketId: String) {
        docRefMarket = dbAdapter.marketDB.document(marketId)
        marketModel = dbAdapter.getMarketModel(marketId) ?: return

        // TODO : 임시 사용자용 SellerModel 테스트 코드 삭제
        sellerModel =
            if (this@DetailActivity.marketModel.sellerId.isNotEmpty())
                dbAdapter.getUserModel(this@DetailActivity.marketModel.sellerId)
                    ?: UserModel("", "", "", 36.5f)
            else return
        buyerModel = dbAdapter.getMyUserModel()

        "marketModel = $marketModel".logD()
        "buyerModel = $buyerModel / sellerModel = $sellerModel".logD()
    }

    private fun updateViewCnt() {
        val newViewCnt = (marketModel.viewCnt ?: 0) + 1
        docRefMarket.update("viewCnt", newViewCnt)
    }

    private fun updateUI() {
        updateSellerInfo()
        updateProductInfo()
        updateLikedInfo()
    }

    private fun updateProductInfo() {
        if (!marketModel.imgUrl.isNullOrEmpty()) {
            Glide.with(this@DetailActivity)
                .load(marketModel.imgUrl!![0])
                .into(binding.imgDetailPhoto)
        }

        binding.txtTitle.text = marketModel.title
        binding.txtCreatedAt.text = Date(marketModel.createdAt).getTimeGapFormatString()
        binding.txtDescription.text = marketModel.description
        binding.txtPrice.text = marketModel.price.getPriceCommaFormatWithWon()
        binding.txtReadCount.text = getString(R.string.atv_detail_view_cnt, marketModel.viewCnt?.getPriceCommaFormat() ?: 0)
        binding.txtLikeCnt.text = (marketModel.likedUserList?.size ?: 0).getPriceCommaFormat()
        binding.txtPriceProposeAble.isVisible = marketModel.priceProposeAble ?: false
    }

    private fun updateSellerInfo() {
        binding.txtUserNickName.text = sellerModel.nickName
        binding.txtUserLocation.text = sellerModel.location
        if (sellerModel.profileImgUrl != null)
            Glide.with(this@DetailActivity).load(sellerModel.profileImgUrl).circleCrop().into(binding.imgUserPicture)
    }

    private fun updateLikedInfo() {
        if (marketModel.likedUserList == null || buyerModel == null) {
            "pass init Liked Info = marketModel.linkedUserList? = ${marketModel.likedUserList} // buyterMode? = $buyerModel".logD()
            return
        }

        val myId = buyerModel!!.id
        val likedUserList = marketModel.likedUserList!!

        if (myId == marketModel.sellerId) return
        if (likedUserList.contains(myId))
            binding.imgFavorite.setImageResource(R.drawable.ic_baseline_favorite_24)
    }

    fun onFavoriteBtnClick() {
        if (buyerModel == null) {
            "비회원은 좋아요를 할 수 없습니다.\n회원 가입 후 이용해 주세요.".apply {
                showShortToast(this@DetailActivity)
                logD()
            }
            return
        } else if (buyerModel!!.id == sellerModel.id) {
            "내가 업로드한 게시물에는 좋아요를 할 수 없습니다.".apply {
                showShortToast(this@DetailActivity)
                logD()
            }
            return
        }

        val myId = buyerModel!!.id
        val likedUserList = (marketModel.likedUserList ?: mutableListOf()) as MutableList<String>
        val docRefMarket = dbAdapter.marketDB.document(marketModel.id)
        val isAdd: Boolean // 좋아요 설정 true, 해제 false

        if (!likedUserList.contains(myId)) {
            isAdd = true
            likedUserList.add(myId)
        } else {
            likedUserList.remove(myId)
            isAdd = false
        }
        // TODO : Check Multiple Update Available
        docRefMarket.update("likedUserList", likedUserList).addOnCompleteListener {
            "update LikeCnt ${marketModel.id} / ${marketModel.title} :: success? = ${it.isSuccessful}"
            "좋아요를 눌렀습니다.".showShortToast(this@DetailActivity)
            binding.txtLikeCnt.text = likedUserList.size.getPriceCommaFormat()
            binding.imgFavorite.setImageResource(if (isAdd) R.drawable.ic_baseline_favorite_24 else R.drawable.ic_baseline_favorite_border_24)
            CoroutineScope(Dispatchers.IO).launch {
                marketModel = dbAdapter.getMarketModel(marketModel.id) ?: return@launch
                updateUI()
            }
        }
    }


    fun onGoChatBtnClick() {
        if (buyerModel == null) {
            "비회원은 채팅을 할 수 없습니다.\n회원 가입 후 이용해 주세요.".showShortToast(this@DetailActivity)
            return
        } else if (buyerModel!!.id == sellerModel.id) {
            "내가 업로드한 게시물 입니다.".showShortToast(this@DetailActivity)
            return
        }

        val myId = buyerModel!!.id

        /** 유저 모델에 채팅룸 정보를 업데이트 한다 */
        fun updateChatList(um: UserModel) {
            val chatRoomIdList = um.chatRoomIdList ?: hashMapOf()
            val currentChatRoomId = ChatRoomModel.generateId(myId, sellerModel.id, marketModel.id)

            // 유저 모델내 채팅룸 리스트에 없다면 추가
            if (!chatRoomIdList.containsKey(currentChatRoomId)) {
                chatRoomIdList[currentChatRoomId] = currentChatRoomId
                ChatRoomModel(myId, marketModel.sellerId, marketModel.title, marketModel.id)
                dbAdapter.userDB.document(um.id).update("chatRoomIdList", chatRoomIdList)
            }
        }

        updateChatList(buyerModel!!)
        updateChatList(sellerModel)

        val chatRoomModel = ChatRoomModel(myId, marketModel.sellerId, marketModel.title, marketModel.id)
        dbAdapter.chatDB.push().setValue(chatRoomModel)
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