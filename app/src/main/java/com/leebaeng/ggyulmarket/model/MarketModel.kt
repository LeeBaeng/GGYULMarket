package com.leebaeng.ggyulmarket.model

import java.io.Serializable

data class MarketModel(
    val sellerId: String = "",
    val title: String = "",
    val createdAt: Long = 0L,
    val price: Long = 0L,
    val description: String = "",
    val priceProposeAble: Boolean? = false,
    val imgUrl: List<String>? = null,
    val talkCnt: Int? = null,
    val likeCnt: Int? = null,
    val viewCnt: Int? = null,
    val likedUserList: List<String>? = null,
    val talkedUserList: List<String>? = null,
    val id: String = "$sellerId-$createdAt"
): Serializable
