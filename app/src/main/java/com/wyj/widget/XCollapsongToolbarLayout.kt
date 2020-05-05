package com.wyj.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import com.google.android.material.appbar.CollapsingToolbarLayout

/**
 *
 *@author abc
 *@time 2019/10/18 14:05
 */
class XCollapsongToolbarLayout : CollapsingToolbarLayout {

    private val TAG by lazy { XCollapsongToolbarLayout::class.java.simpleName }

    private var mIsScrimsShown: Boolean = false
    private var mListener: OnScrimsListener? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)


    override fun setScrimsShown(shown: Boolean, animate: Boolean) {
        super.setScrimsShown(shown, animate)
        if (mIsScrimsShown != shown) {
            mIsScrimsShown = shown
            mListener?.onScrimsStateChange(this, mIsScrimsShown)
        }
    }

    fun setOnScrimsListener(onScrimsListener: OnScrimsListener?) {
        this.mListener = onScrimsListener
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        Log.i(TAG,"scrollY[$scrollY]")
    }



    /**
     * CollapsingToolbarLayout渐变监听器
     */
    interface OnScrimsListener {
        fun onScrimsStateChange(layout: XCollapsongToolbarLayout, shown: Boolean)
    }


}