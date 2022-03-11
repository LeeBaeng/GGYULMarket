package com.leebaeng.ggyulmarket.model

data class MarketModel(
    val sellerId: String = "",
    val title: String = "",
    val createdAt: Long = 0L,
    val price: Long = 0L,
    val description: String = "",
    val imgUrl: List<String>? = null,
    val talkCnt: Int? = null,
    val likeCnt: Int? = null,
    val id: String = "$sellerId-$createdAt"
)
