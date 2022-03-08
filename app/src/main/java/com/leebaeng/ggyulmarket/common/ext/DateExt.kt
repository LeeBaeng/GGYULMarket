package com.leebaeng.ggyulmarket.common.ext

import java.text.SimpleDateFormat
import java.util.*

/**
 * 현재시간과 Date 객체의 날짜를 비교하여 표시할 **분전, **시간 형식의 String을 반환 한다.
 * 24시간 이상 지났거나, 과거가 아닌 미래의 경우 ****년 **월 **일 형식의 String을 반환한다.
 */
fun Date.getTimeGapFormatString(): String {
    val currentMills = System.currentTimeMillis()
    val mills = currentMills - this.time
    val minutes = mills / 1_000 / 60
    val hours = minutes / 60

    if (mills > 0 && hours < 1) return String.format("%d분 전", minutes)
    if (mills > 0 && hours < 24) return String.format("%d시간 전", hours)

    return SimpleDateFormat("yyyy년 MM월 dd일").format(this)
}