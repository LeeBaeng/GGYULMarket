package com.leebaeng.ggyulmarket.ui

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

// TODO : 조회수 안올라가는 이슈, 좋아요 및 채팅시작 안되는 이슈 확인 필요

@AndroidEntryPoint
class DetailActivity : BaseActivity() {
    @Inject
    lateinit var dbAdapter: DBAdapter

    lateinit var binding: ActivityDetailBinding
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

        marketModel = intent.getSerializableExtra("model") as MarketModel
        bindData()
    }

    private fun bindData() {
        CoroutineScope(Dispatchers.IO).launch {
            val auth = dbAdapter.auth
            val docRefMarket = dbAdapter.marketDB.document(marketModel.id)
            val docRefMy = dbAdapter.userDB.document()
            val docRefSeller = dbAdapter.userDB.document()


            sellerModel =
                if (!this@DetailActivity.marketModel.sellerId.isNullOrEmpty()) dbAdapter.getUserModel(this@DetailActivity.marketModel.sellerId) ?: UserModel("", "", "", 36.5f) else UserModel(
                    "",
                    "",
                    "",
                    36.5f
                )
            buyerModel = dbAdapter.getMyUserModel()

            val newViewCnt = (marketModel.viewCnt ?: 0) + 1
            docRefMarket.update("viewCnt", newViewCnt)

            CoroutineScope(Dispatchers.Main).launch {
                initSellerInfo()
                initProductInfo(newViewCnt)
                initLikedInfo()
            }
        }
    }

    private fun initProductInfo(viewCnt: Int) {
        if (!marketModel.imgUrl.isNullOrEmpty()) {
            Glide.with(this@DetailActivity)
                .load(marketModel.imgUrl!![0])
                .into(binding.imgDetailPhoto)
        }

        binding.txtTitle.text = marketModel.title
        binding.txtCreatedAt.text = Date(marketModel.createdAt).getTimeGapFormatString()
        binding.txtDescription.text = marketModel.description
        binding.txtPrice.text = marketModel.price.getPriceCommaFormatWithWon()
        binding.txtReadCount.text = getString(R.string.atv_detail_view_cnt, viewCnt.getPriceCommaFormat())
        binding.txtLikeCnt.text = (marketModel.likeCnt ?: 0).getPriceCommaFormat()
        binding.txtPriceProposeAble.isVisible = marketModel.priceProposeAble ?: false
    }

    private fun initSellerInfo() {
        binding.txtUserNickName.text = sellerModel.nickName
        binding.txtUserLocation.text = sellerModel.location
        if (sellerModel.profileImgUrl != null)
            Glide.with(this@DetailActivity).load(sellerModel.profileImgUrl).circleCrop().into(binding.imgUserPicture)
    }

    private fun initLikedInfo() {
        if (marketModel.likedUserList == null || buyerModel == null) return

        val myId = buyerModel!!.id
        val likedUserList = marketModel.likedUserList!!

        if (myId == marketModel.sellerId) return
        if (likedUserList.contains(myId))
            binding.imgFavorite.setImageResource(R.drawable.ic_baseline_favorite_24)
    }

    fun onFavoriteBtnClick() {
        if (buyerModel == null) {
            "비회원은 좋아요를 할 수 없습니다.\n회원 가입 후 이용해 주세요.".showShortToast(this@DetailActivity)
            return
        } else if (buyerModel!!.id == sellerModel.id) {
            "내가 업로드한 게시물에는 좋아요를 할 수 없습니다.".showShortToast(this@DetailActivity)
            return
        }
        if (marketModel.likedUserList == null) return

        val myId = buyerModel!!.id
        var newLikeCnt = (marketModel.likeCnt ?: 0)
        val likedUserList = marketModel.likedUserList as MutableList
        val docRefMarket = dbAdapter.marketDB.document(marketModel.id)

        if (!likedUserList.contains(myId)) {
            newLikeCnt++
            likedUserList.add(myId)
            binding.imgFavorite.setImageResource(R.drawable.ic_baseline_favorite_24)
        } else {
            if (newLikeCnt > 0) newLikeCnt--
            likedUserList.remove(myId)
            binding.imgFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        }
        binding.txtLikeCnt.text = newLikeCnt.getPriceCommaFormat()

        // TODO : Check Multiple Update Available
        docRefMarket.update("likeCnt", newLikeCnt, "likedUserList", likedUserList)
//            collectionRef.document(marketModel.id).update("likeCnt", newLikeCnt)
//            collectionRef.document(marketModel.id).update("likedUserList", likedUserList)
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