package com.leebaeng.ggyulmarket.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.leebaeng.ggyulmarket.R
import com.leebaeng.ggyulmarket.common.constants.DBKey
import com.leebaeng.ggyulmarket.databinding.FragmentHomeBinding
import com.leebaeng.ggyulmarket.model.MarketModel
import com.leebaeng.ggyulmarket.ui.AddMarketItemActivity
import com.leebaeng.ggyulmarket.ui.BaseFragment
import com.leebaeng.ggyulmarket.ui.DetailActivity
import com.leebaeng.util.log.logD
import com.leebaeng.util.log.logEX
import com.leebaeng.util.log.logS
import kotlin.math.floor
import kotlin.random.Random

class HomeFragment : BaseFragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var marketListAdapter: MarketListAdapter
    private lateinit var auth: FirebaseAuth

    private val marketList = mutableListOf<MarketModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        marketListAdapter = MarketListAdapter(onItemClickListener = { model: MarketModel ->
            val i = Intent(requireContext(), DetailActivity::class.java)
            i.putExtra("model", model)
            startActivity(i)
        })
        binding = FragmentHomeBinding.bind(view)
        binding.rcvMarketList.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvMarketList.adapter = marketListAdapter
        binding.btnAddNewItem.setOnClickListener {
            if (auth.currentUser != null) {
//                startActivity(Intent(requireContext(), AddMarketItemActivity::class.java))
                // TODO : startActivityForResult -> registerForActivityResult 로 변경
                startActivityForResult(Intent(requireContext(), AddMarketItemActivity::class.java), 3003)
            } else {
//                startActivity(Intent(requireContext(), AddMarketItemActivity::class.java))
                startActivityForResult(Intent(requireContext(), AddMarketItemActivity::class.java), 3003)
                Snackbar.make(view, "로그인이 필요 합니다.", Snackbar.LENGTH_SHORT).show()
                //TODO LoginActivity로 이동
            }
        }

        marketList.clear()
        getMarketList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 3003) {
            when (resultCode) {
                1000 -> {
                    // 새 글 등록후 복귀
                    binding.rcvMarketList.scrollToPosition(0)
                    marketList.clear()
                    getMarketList()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        "onResume".logD(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun initCloudFirebase() {
        getMarketList()
    }

    fun getMarketList(startAt: DocumentSnapshot? = null, limit: Long = 5) {
        val collectionRef = Firebase.firestore.collection(DBKey.TABLE_MARKET_LIST)
        collectionRef
            .orderBy("createdAt", Query.Direction.DESCENDING)
//            .limit(3)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    "db collection(${DBKey.TABLE_MARKET_LIST}) get :  ${document.id} => ${document.data}".logD()
                }

                val models = result.toObjects(MarketModel::class.java)
                for (m in models) marketList.add(m)
                submitList()
            }
            .addOnFailureListener { exception ->
                exception.logEX("Error getting documents.")
            }
    }

    private fun submitList() {
        marketListAdapter.submitList(marketList)
        marketListAdapter.notifyDataSetChanged()
    }

    private fun createTestData() {
        val imgUrlList = mutableListOf(
            "https://firebasestorage.googleapis.com/v0/b/ggyulmarket.appspot.com/o/market%2Fphoto%2F_1647078900320_0.png?alt=media&token=e92c7fb8-b919-4a69-bf29-ffda7a61dd67",
            "https://firebasestorage.googleapis.com/v0/b/ggyulmarket.appspot.com/o/market%2Fphoto%2F_1647079520136_0.png?alt=media&token=b86a5ca4-64b9-48e3-9f47-ba83369ffa87",
            "https://firebasestorage.googleapis.com/v0/b/ggyulmarket.appspot.com/o/market%2Fphoto%2F_1647079501112_0.png?alt=media&token=06129e71-9b90-45f1-9d5a-5bf15c3eb929",
            "https://firebasestorage.googleapis.com/v0/b/ggyulmarket.appspot.com/o/market%2Fphoto%2F_1647079461786_0.png?alt=media&token=cf115021-2b07-41c7-8858-59fb2f086370",
            "https://firebasestorage.googleapis.com/v0/b/ggyulmarket.appspot.com/o/market%2Fphoto%2F_1647078789787_1.png?alt=media&token=722d9059-aeee-45f4-9eae-aa7a1a7077ee",
            "https://firebasestorage.googleapis.com/v0/b/ggyulmarket.appspot.com/o/market%2Fphoto%2F_1647078789760_0.png?alt=media&token=1ade250b-041a-4aed-aac2-40b727acb5dd",
            "https://firebasestorage.googleapis.com/v0/b/ggyulmarket.appspot.com/o/market%2Fphoto%2F_1647078789790_3.png?alt=media&token=af7e0891-4015-4fab-a182-e15655f8c748",
            "https://firebasestorage.googleapis.com/v0/b/ggyulmarket.appspot.com/o/market%2Fphoto%2F_1647078789790_4.png?alt=media&token=a69340e6-651a-428e-a483-bfbbb1253f81",
            "https://firebasestorage.googleapis.com/v0/b/ggyulmarket.appspot.com/o/market%2Fphoto%2F_1647078789790_5.png?alt=media&token=fca2f033-214e-46d3-8287-24307cb7a62c",
            "https://firebasestorage.googleapis.com/v0/b/ggyulmarket.appspot.com/o/market%2Fphoto%2F_1647074166052_0.png?alt=media&token=5a7cc801-acc8-455f-97c0-8b7ac3371a76",
            "https://firebasestorage.googleapis.com/v0/b/ggyulmarket.appspot.com/o/market%2Fphoto%2F_1647074166073_1.png?alt=media&token=2b0141d4-b6ad-4c41-8c17-82bb7c2923d2",
            "https://firebasestorage.googleapis.com/v0/b/ggyulmarket.appspot.com/o/market%2Fphoto%2F_1647074166073_2.png?alt=media&token=8638a49e-a9fc-4052-b4da-ac0ba77491c1",
            "https://firebasestorage.googleapis.com/v0/b/ggyulmarket.appspot.com/o/market%2Fphoto%2F_1647074166075_3.png?alt=media&token=374c74c6-53a4-4e4e-a7de-313bbad13597",
            "https://firebasestorage.googleapis.com/v0/b/ggyulmarket.appspot.com/o/market%2Fphoto%2F_1647079026412_0.png?alt=media&token=a5ecc3d4-6484-492b-bbb7-6cfef5a05872",
            "https://firebasestorage.googleapis.com/v0/b/ggyulmarket.appspot.com/o/market%2Fphoto%2F_1647079664498_0.png?alt=media&token=526ec4dc-83db-499e-991f-c123e5c6bfe4"
        )

        val sellerIdPrefix = mutableListOf(
            "샤방한", "서초구", "서래마을", "우리동네", "뀰은", "뀰마켓", "구글의", "대한민국", "일본", "중국",
            "샤프한", "도시의", "차가운", "따듯한", "뜨거운", "시원한", "예쁜", "멋진", "핸섬한", "너프한", "위험한", "아름다운", "매력쩌는", "섹시한",
            "심심한", "따분한", "고달픈", "행복한", "기쁜", "사랑스러운", "강한", "나만의"
        )

        val sellerIdSuffix = mutableListOf(
            "마동석", "수호자", "계승자", "개그맨", "코미디언", "황재성", "유재석", "하하", "이미주", "정준하", "박명수", "노홍철", "신봉선",
            "노름꾼", "타짜", "조승우", "김혜수", "이광수", "전소민", "양말", "바가지", "정장", "손흥민", "헤리캐인", "요리스", "메시", "호날두"
        )

        val stateList = mutableListOf("매우 좋은 상태", "신품급", "포장도 안뜯은 새것", "상급", "중급", "하급", "그냥 쓸수 있는", "사용감 있는", "부품용", "쓰레기", "쓸수 없는", "민트급")

        for (i in 0 until 10) {
            val id = "${sellerIdPrefix.getRandomValue()}${sellerIdSuffix.getRandomValue()}"
            val price = (floor(Math.random() * (10000 - 10 + 1)).toInt() + 10) * 100
            val time = System.currentTimeMillis() - 800000L.random()
            var imgList: MutableList<String>? = mutableListOf()

            if (2.random() < 1) {
                for (a in 0 until imgUrlList.size.random()) {
                    imgList!!.add(imgUrlList.getRandomValue() as String)
                }
            }
            if (imgList?.size == 0) imgList = null


            val model = MarketModel(
                id, "$id 팝니다", time, price.toLong(), "$id 님이 물건을 판매하려고 합니다. 상태는 ${stateList.getRandomValue()} 입니다.",
                Random.nextBoolean(), imgList
            )
            Firebase.firestore.collection(DBKey.TABLE_MARKET_LIST).add(model)
        }
    }

}

fun MutableList<*>.getRandomValue(): Any? {
    if (!this.isNullOrEmpty())
        return this[(random(0L, (this.size - 1).toLong())).toInt()]
    return null
}

fun Number.random(): Long {
    return random(0, this.toLong())
}

fun random(min: Long, max: Long): Long {
    return floor(Math.random() * (max - min + 1)).toLong() + min
}