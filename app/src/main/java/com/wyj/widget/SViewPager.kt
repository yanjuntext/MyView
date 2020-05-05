package com.wyj.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.ViewCompat
import androidx.viewpager.widget.ViewPager
import com.base.utils.DisplayHelper

/**
 *
 *@author abc
 *@time 2019/10/22 9:01
 */
class SViewPager : ViewPager {

    private var mScrollY: Int = 0
    private val MAX_SCROLL_Y = (DisplayHelper.screenWidth() * 9.0 / 16.0 + 0.5).toInt()

    private var startX = 0f
    private var startY = 0f

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

}