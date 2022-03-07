package com.leebaeng.ggyulmarket.common.ext

import java.lang.Exception
import java.text.NumberFormat

/**
 * 금액에 점을 추가한 문자열로 반환한다. (ex 23000 -> 23,000)
 */
fun Number?.getPriceDotFormat(): String {
    return if (this == null) {
        ""
    } else {
        try {
            val numberFormat = NumberFormat.getInstance()
            numberFormat.format(this.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}

/**
 * 금액에 점을 추가하여 "원" 문자열을 포함하여 반환한다. (ex 23000 -> 23,000원)
 */
fun Number?.getPriceDotFormatWithWon(): String {
    val formatted = getPriceDotFormat()
    return if(formatted.isEmpty()) "" else "$formatted 원"
}