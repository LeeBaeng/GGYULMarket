package com.leebaeng.ggyulmarket.model

data class UserModel(
    val id: String,
    val nickName: String,
    val location: String,
    val manner: Float = 36.5f,
    val profileImgUrl: String? = null,
    val sellItemIdList: HashMap<String, String>? = null, // SellItem ID Map. value is possible get data from DB and convert to <MarketModel> Object
    val chatRoomIdList: HashMap<String, String>? = null // ChatRoom ID Map. value is possible get data from DB and convert to <ChatRoomModel> Object
)