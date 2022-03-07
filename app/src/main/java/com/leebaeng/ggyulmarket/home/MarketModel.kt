package com.leebaeng.ggyulmarket.home

data class MarketModel(
    val sellerId: String,
    val title: String,
    val createdAt: Long,
    val price: Int,
    val imgUrl: String?,
    val talkCnt: Int?,
    val likeCnt: Int?
)
