package com.leebaeng.ggyulmarket.model

data class ChatRoomModel(
    val buyerId: String = "",
    val sellerId: String = "",
    val title: String = "",
    val marketItemId : String ="",
    val id: String = generateId(buyerId, sellerId, marketItemId)
){
    companion object{
        fun generateId(buyerId: String, sellerId: String, marketItemId: String): String{
            return "${marketItemId}-${buyerId}_vs_$sellerId}"
        }
    }
}