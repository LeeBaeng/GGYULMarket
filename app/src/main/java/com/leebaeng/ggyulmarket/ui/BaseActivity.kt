package com.leebaeng.ggyulmarket.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.leebaeng.util.log.logS

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        "onCreate($savedInstanceState)".logS(this)
    }

    override fun onStart() {
        super.onStart()
        "onStart".logS(this)
    }

    override fun onRestart() {
        super.onRestart()
        "onRestart".logS(this)
    }

    override fun onResume() {
        super.onResume()
        "onResume".logS(this)
    }

    override fun onPause() {
        super.onPause()
        "onPause".logS(this)
    }

    override fun onStop() {
        super.onStop()
        "onStop".logS(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        "onDestroy".logS(this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        "onAttachedToWindow".logS(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        "onDetachedFromWindow".logS(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        "onSaveInstanceState($outState)".logS(this)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        "onRestoreInstanceState($savedInstanceState)".logS(this)
    }

}