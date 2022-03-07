package com.leebaeng.ggyulmarket.common.ext

import android.content.Context
import android.util.TypedValue
import android.util.DisplayMetrics




/**
 * DP 단위를 PX 단위로 변환
 * @param context
 * @param sizeInDP Float 형으로 입력 -> Int 형 반환
 */
fun Float.dpToPx(context : Context): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics).toInt()
}

/**
 * SP 단위를 PX 단위로 변환 Float 형으로 입력 -> Int 형 반환
 * @param context
 * @param sizeInSP Float 형으로 입력 -> Int 형 반환
 */
fun Float.spToPx(context : Context): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, context.resources.displayMetrics).toInt()
}

/**
 * Pixel을 DP 단위로 변환한다.
 * @param dpi : default = mdpi. ldpi(120), mdpi(160), hdpi(240), xhdpi(320), xxhdpi(480), xxxhdpi(640)
 */
fun Number.pxToDp(context: Context, dpi: Int = DisplayMetrics.DENSITY_DEFAULT): Float {
    return this.toInt() / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}