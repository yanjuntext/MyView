package com.wyj.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.view.NestedScrollingChild3
import androidx.core.view.NestedScrollingChildHelper
import androidx.viewpager.widget.ViewPager

/**
 *
 *@author abc
 *@time 2019/10/21 16:57
 */
class XViewPager : ViewPager, NestedScrollingChild3, View.OnTouchListener {

    private val mNestedScrollingChildHelper: NestedScrollingChildHelper by lazy {
        NestedScrollingChildHelper(this).also {
            it.isNestedScrollingEnabled = true
        }
    }

    private var canScroll = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    init {
        setOnTouchListener(this)
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?,
        type: Int,
        consumed: IntArray
    ) {
        mNestedScrollingChildHelper.dispatchNestedScroll(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            offsetInWindow,
            type,
            consumed
        )
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?,
        type: Int
    ): Boolean = mNestedScrollingChildHelper.dispatchNestedScroll(
        dxConsumed,
        dyConsumed,
        dxUnconsumed,
        dyUnconsumed,
        offsetInWindow,
        type
    )

    override fun startNestedScroll(axes: Int, type: Int): Boolean =
        mNestedScrollingChildHelper.startNestedScroll(axes, type)

    override fun dispatchNestedPreScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        offsetInWindow: IntArray?,
        type: Int
    ): Boolean = mNestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)

    override fun stopNestedScroll(type: Int) = mNestedScrollingChildHelper.stopNestedScroll(type)

    override fun hasNestedScrollingParent(type: Int): Boolean =
        mNestedScrollingChildHelper.hasNestedScrollingParent(type)

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                canScroll = !(currentItem == 0 && event.y > height * 16 / 9)
                canScroll
            }
            MotionEvent.ACTION_MOVE -> {
                canScroll
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                canScroll
            }
            else -> false
        }

    }

}