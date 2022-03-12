package com.leebaeng.ggyulmarket.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.leebaeng.util.log.logS

abstract class BaseFragment : Fragment {
    constructor(resId: Int) : super(resId)
    constructor() : super()

    override fun onInflate(context: Context, attrs: AttributeSet, savedInstanceState: Bundle?) {
        super.onInflate(context, attrs, savedInstanceState)
        "onInflate($context, $attrs, $savedInstanceState)".logS(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        "onCreate($savedInstanceState)".logS(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        "onCreateView($inflater, $container, $savedInstanceState)".logS(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        "onViewCreated($view, $savedInstanceState)".logS(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        "onAttach($context)".logS(this)
    }

    override fun onStart() {
        super.onStart()
        "onStart".logS(this)
    }

    override fun onResume() {
        super.onResume()
        "onResume".logS(this)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        "onHiddenChanged($hidden)".logS(this)
    }

    override fun onPause() {
        super.onPause()
        "onPause".logS(this)
    }

    override fun onStop() {
        super.onStop()
        "onStop".logS(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        "onSaveInstanceState($outState)".logS(this)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        "onViewStateRestored($savedInstanceState)".logS(this)
    }

    override fun onDetach() {
        super.onDetach()
        "onDetach".logS(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        "onDestroyView".logS(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        "onDestroy".logS(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        "onActivityCreated($savedInstanceState)".logS(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        "onActivityResult($requestCode, $requestCode, $data)".logS(this)
    }

}