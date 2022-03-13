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
import com.leebaeng.ggyulmarket.common.Constants
import com.leebaeng.ggyulmarket.databinding.FragmentHomeBinding
import com.leebaeng.ggyulmarket.di.DBAdapter
import com.leebaeng.ggyulmarket.model.MarketModel
import com.leebaeng.ggyulmarket.ui.AddMarketItemActivity
import com.leebaeng.ggyulmarket.ui.BaseFragment
import com.leebaeng.ggyulmarket.ui.DetailActivity
import com.leebaeng.util.log.logD
import com.leebaeng.util.log.logEX
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.floor
import kotlin.random.Random

@AndroidEntryPoint
class HomeFragment : BaseFragment(R.layout.fragment_home) {
    @Inject lateinit var dbAdapter: DBAdapter
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

    fun getMarketList(startAt: DocumentSnapshot? = null, limit: Long = 5) {
        dbAdapter.marketDB
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(15)
            .get()
            .addOnSuccessListener { result ->
//                for (document in result)
//                    "db collection(${Const.FS_TABLE_MARKET}) get :  ${document.id} => ${document.data}".logD()

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
        val imgUrlList = listOf(
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

        val sellerIdPrefix = listOf(
            "샤방한", "서초구", "서래마을", "우리동네", "뀰은", "뀰마켓", "구글의", "대한민국", "일본", "중국",
            "샤프한", "도시의", "차가운", "따듯한", "뜨거운", "시원한", "예쁜", "멋진", "핸섬한", "너프한", "위험한", "아름다운", "매력쩌는", "섹시한",
            "심심한", "따분한", "고달픈", "행복한", "기쁜", "사랑스러운", "강한", "나만의"
        )

        val sellerIdSuffix = listOf(
            "마동석", "수호자", "계승자", "개그맨", "코미디언", "황재성", "유재석", "하하", "이미주", "정준하", "박명수", "노홍철", "신봉선",
            "노름꾼", "타짜", "조승우", "김혜수", "이광수", "전소민", "양말", "바가지", "정장", "손흥민", "헤리캐인", "요리스", "메시", "호날두"
        )

        val stateList = listOf("새삥", "신품급", "상급", "중급", "하급", "부품용", "쓰레기", "민트급")
        val titlePrefix = listOf(
            "매우 아끼던", "포장도 안뜯은", "사용감 있는", "애지중지 하던", "직거래로", "이번 주말에", "이번주 평일 중에", "조금밖에 안쓴", "사자마자 재뀰", "이거사면 개꿀", "뀰뀰한 인생", "뀰맛나는",
            "진짜 엄청 맛있는", "한번도 안쓴 사람은 있어도 한번만 쓴사람은 없다는", "초특가", "우리나라에 하나뿐인", "진짜 어렵게 구한", "잘 사용하던", "재밌는", "아기자기한", "한번 맛보면 잊을수 없는",
            "불면증에 좋은", "심심할때 최고", "한눈에 반한", "뀰잠 잘수 있는", "피곤한", "직수입한", "미국에서 사온", "일본에서 직구한", "단종된", "이제 찾을 수 없는", "잘쓰던", "뭐.....", "더울때 최고",
            "장터에서 구매한", "시장에서 사온", "하이마트에서 샀던", "삼성 정품", "애플 정품", "진짜 정품", "환불불가", "택배가능", "카드가능", "디지털 프라자 구매", "3개월 사용한", "엇그제 구매한", "사용기간 1주일", "3년된",
            "30년된", "타임세일", "먼저사는사람이 임자", "몸에 좋은", "건강에 좋은", "부모님 선물로 탁월한", "집들이 선물에 딱인", "아이들이 좋아하는", "남녀노소 누구나 잘먹는"
        )

        val titleProduct = listOf(
            "핸드폰", "컴퓨터", "노트북", "아이패드", "갤럭시 s22", "갤럭시 21", "갤럭시 20+", "갤노트", "Galaxy Note10", "아이폰 11", "아이폰12x", "페라리 F430", "BMW 320d", "BMW i8", "프라모델",
            "피규어", "깁슨 기타", "펜더 베이스", "스트라토 캐스터", "Pod Go", "Boss Me80", "헤드러쉬 mx5", "헤드러쉬 긱보드", "스케이트 보드", "야구방망이", "자전거", "로드 자전거 시마노", "듀라에이스 변속기",
            "크래프트 기타", "3D 프린터", "종이상자", "인테리어 공구", "전동 드릴", "해머", "휠셋", "BBS 휠", "로지텍 핸들", "게임기", "플스4", "XBox", "닌텐도", "아일랜드 식탁", "냉장고", "세탁기", "이사물품 처분",
            "가스레인지", "전자레인지", "밥솥", "60인치 TV", "쇼파", "침대", "톰브라운 티셔츠", "루이비똥 가방", "샤넬백", "불가리 시계", "펜더 앰프", "진공관 앰프", "JBL 스피커", "사운드 시스템", "콘덴서 마이크",
            "믹서", "아이들 장난감", "뽀로로 DVD 셋트", "뽀로로 캐릭터 인형", "레고", "카트라이더", "마우스", "키보드", "신발장", "구찌 신발", "명품 지갑", "썬글라스", "자갈", "테이블쏘", "밀워키 직쏘", "여행용 가방",
            "캐리어", "순금 24K 반지", "5돈 거북이", "킥보드", "BMW 19인치 M퍼포먼스 휠", "세차용품", "청소용품", "광택용품", "커튼", "유명한 작가의 그림", "마동석 선생님 작품", "QR레버", "공기 주입기", "마샬 앰프",
            "앞범퍼", "순살 치킨", "최고급 한우 꽃등심", "살치살", "국거리", "음료수", "보드카", "와인", "위스키"
        )

        for (i in 0 until 100) {
            val id = "${sellerIdPrefix.getRandomValue()}${sellerIdSuffix.getRandomValue()}"
            val price = random(1, 1000) * 1000
            val time = System.currentTimeMillis() - 2629800000L.random()
            val title =
                (if(10.random() < 7) "${titlePrefix.getRandomValue()} " else "") +
                        (if(Random.nextBoolean()) ("${stateList.getRandomValue()} ") else "") +
                        "${titleProduct.getRandomValue()}" +
                        if(Random.nextBoolean()) " 팝니다." else ""
            var imgList: MutableList<String>? = mutableListOf()

            if (20.random() < 20) {
                for (a in 0 until imgUrlList.size.random()) {
                    imgList!!.add(imgUrlList.getRandomValue() as String)
                }
            }
            if (imgList?.size == 0) imgList = null


            val model = MarketModel(
                id, title, time, price, "$id 님이 물건을 판매하려고 합니다. 상태는 ${stateList.getRandomValue()} 입니다.",
                Random.nextBoolean(), imgList
            )
            Firebase.firestore.collection(Constants.FS_TABLE_MARKET).add(model)
        }

        val id = "${sellerIdPrefix.getRandomValue()}${sellerIdSuffix.getRandomValue()}"
        val price = random(1, 1000) * 1000
        val time = System.currentTimeMillis()
        val title = "한번도 안쓴 사람은 있어도 한번만 쓴사람은 없다는 전세계 하나뿐인 Galaxy Note25 선착순 반품불가"
        var imgList: MutableList<String>? = mutableListOf()
        if (10.random() < 9) {
            for (a in 0 until imgUrlList.size.random()) {
                imgList!!.add(imgUrlList.getRandomValue() as String)
            }
        }
        val model = MarketModel(
            id, title, time, price, "$id 님이 물건을 판매하려고 합니다. 상태는 ${stateList.getRandomValue()} 입니다.",
            Random.nextBoolean(), imgList
        )
        Firebase.firestore.collection(Constants.FS_TABLE_MARKET).add(model)
    }

}

fun List<*>.getRandomValue(): Any? {
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