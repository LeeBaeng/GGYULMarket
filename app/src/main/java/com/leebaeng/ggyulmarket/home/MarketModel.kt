package com.leebaeng.ggyulmarket.home

data class MarketModel(
    val sellerId: String = "",
    val title: String = "",
    val createdAt: Long = 0L,
    val price: Int = 0,
    val imgUrl: String? = null,
    val talkCnt: Int? = null,
    val likeCnt: Int? = null,
    val id: String = "$sellerId-$createdAt"
)
