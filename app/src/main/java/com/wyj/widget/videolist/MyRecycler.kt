package com.wyj.widget.videolist

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

/**
 * 作者：王颜军 on 2020/8/18 15:40
 * 邮箱：3183424727@qq.com
 *
 */
class MyRecycler:RecyclerView {

    var isScroll = false

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return if(!isScroll) false else super.dispatchTouchEvent(ev)
//        return false
//        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(e: MotionEvent?): Boolean {
//        return false
//        return super.onTouchEvent(e)
        return if(!isScroll) false else super.onTouchEvent(e)
    }
}