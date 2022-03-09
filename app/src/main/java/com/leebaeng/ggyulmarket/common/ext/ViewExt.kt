package com.leebaeng.ggyulmarket.common.ext

import android.content.Context
import android.widget.Toast

fun String.showShortToast(context: Context){
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
}

fun String.showLongToast(context: Context){
    Toast.makeText(context, this, Toast.LENGTH_LONG).show()
}