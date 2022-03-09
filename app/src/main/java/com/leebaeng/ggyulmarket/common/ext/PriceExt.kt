package com.leebaeng.ggyulmarket.common.ext

import android.text.Selection
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import com.leebaeng.util.log.LLog
import com.leebaeng.util.log.logEX
import java.lang.Exception
import java.text.NumberFormat

/**
 * 금액에 점을 추가한 문자열로 반환한다. (ex 23000 -> 23,000)
 */
fun Number.getPriceCommaFormat(): String {
    try {
        val numberFormat = NumberFormat.getInstance()
        return numberFormat.format(this.toLong())
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ""
}

/**
 * 금액에 점을 추가하여 "원" 문자열을 포함하여 반환한다. (ex 23000 -> 23,000원)
 */
fun Number.getPriceCommaFormatWithWon(): String {
    val formatted = getPriceCommaFormat()
    return if (formatted.isEmpty()) "0원" else "$formatted 원"
}

fun EditText.setPriceCommaFormat() {
    var commaString = ""
    this.doAfterTextChanged { editable ->
        LLog.warn("doTextChange : $editable")
        if (!editable.isNullOrBlank() && commaString != editable.toString()) {
            try {
                val amount = editable.toString().toLongRemovedComma()
                commaString = amount.getPriceCommaFormat()
                this.setTextKeepState(commaString)
                Selection.setSelection(this.text, commaString.length)
            } catch (e: Exception) {
                e.logEX()
            }
        }
    }
}

fun String.toLongRemovedComma(): Long = this.replace(",", "").toLong()