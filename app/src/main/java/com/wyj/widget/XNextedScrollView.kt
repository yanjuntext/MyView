package com.wyj.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import androidx.core.view.NestedScrollingParent2
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.base.utils.DisplayHelper
import com.base.utils.GlobalStatusBarUtil
import com.base.utils.MLog
import kotlin.math.abs

/**
 *嵌套滑动
 *@author abc
 *@time 2019/10/22 10:02
 */
class XNextedScrollView : RelativeLayout, NestedScrollingParent2 {


    private val TAG = XNextedScrollView::class.java.simpleName
    //兼容低版本
    private val mNestedScrollingParentHelper = NestedScrollingParentHelper(this)

    var onScrollChangeListener: OnScrollChangeListener? = null

    //滑动留白View
    private var mTopView: View? = null
    //滑动折叠View
    private var mStickyNavView: View? = null

    private var mViewPager: ViewPager2? = null

    private var MAX_SCROLL_DIS: Int = 0
    private var mScrollY: Int = 0

    private var isFirst = true

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        MLog.e(
            TAG,
            "MAX_SCROLL_DIS[$MAX_SCROLL_DIS],height[$measuredHeight],topView[${mTopView?.measuredHeight}],stickyView[${mStickyNavView?.measuredHeight}]"
        )
        MLog.e(
            TAG,
            "screenHeight[${DisplayHelper.screenHeight()}],bottom[${DisplayHelper.dp2px(45f)}],status[${GlobalStatusBarUtil.getStatusbarHeight(
                context
            )}]"
        )
        MLog.i(TAG, "mViewPager[${mViewPager == null}]")
        if (isFirst) {
            isFirst = false
            val params = this.layoutParams
            params.height =
                measuredHeight + (mStickyNavView?.measuredHeight ?: 0) - (mTopView?.measuredHeight
                    ?: 0)
            this.layoutParams = params
            super.onMeasure(
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(params.height, MeasureSpec.EXACTLY)
            )
        }
    }

    override fun onFinishInflate() {
        MLog.e(TAG, "onFinishInflate")
        super.onFinishInflate()
        for (i in 0..childCount) {
            if (getChildAt(i) != null) {
                if (getChildAt(i).id == R.id.top_view) {
                    mTopView = getChildAt(i)
                } else if (getChildAt(i).id == R.id.sticky_nav) {
                    mStickyNavView = getChildAt(i)
                } else if (getChildAt(i).id == R.id.view_pager && getChildAt(i) is ViewPager2) {
                    mViewPager = getChildAt(i) as ViewPager2
                }
            }

        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        MLog.e(TAG, "onSizeChanged")
        super.onSizeChanged(w, h, oldw, oldh)
        //滑动距离
        MAX_SCROLL_DIS = (mStickyNavView?.measuredHeight ?: 0) - (mTopView?.measuredHeight ?: 0)

    }


    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        Log.i(
            TAG,
            "onNestedPreScroll dy[$dy],dx[$dx],type[$type],target[${target is ViewPager2},${target is RecyclerView}]"
        )
//        if(type ==  ViewCompat.TYPE_TOUCH){
//        if (abs(dy) < abs(dx)) {
//            consumed[0] = 0
//            consumed[1] = 0
//        } else {
            if (dy > 0) {
                consumed[1] = when {
                    mScrollY + dy > MAX_SCROLL_DIS -> {
                        scrollBy(0, MAX_SCROLL_DIS - mScrollY)
                        MAX_SCROLL_DIS - mScrollY
                    }
                    else -> {
                        scrollBy(0, dy)
                        dy
                    }
                }
            } else if (!target.canScrollVertically(dy)) {
                consumed[1] = dy
                scrollBy(0, dy)
            }
//        }
//        }
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        mNestedScrollingParentHelper.onStopNestedScroll(target, type)
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean =
        axes == ViewCompat.SCROLL_AXIS_VERTICAL && type == ViewCompat.TYPE_TOUCH

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes, type)
    }


    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        if (dyUnconsumed < 0 && type == ViewCompat.TYPE_NON_TOUCH) {//fling
            scrollBy(0, dyUnconsumed)
        }
    }

    override fun getNestedScrollAxes(): Int {
        return mNestedScrollingParentHelper.nestedScrollAxes
    }

    override fun scrollBy(x: Int, y: Int) {
        when {
            mScrollY + y < 0 -> {
                super.scrollBy(x, -mScrollY)
                mScrollY = 0
            }
            mScrollY + y > MAX_SCROLL_DIS -> {
                super.scrollBy(x, MAX_SCROLL_DIS - mScrollY)
                mScrollY = MAX_SCROLL_DIS
            }
            else -> {
                super.scrollBy(x, y)
                mScrollY += y
            }
        }
        onScrollChangeListener?.onScrollChange(
            0,
            mScrollY,
            1 - mScrollY * 1.0 / if (MAX_SCROLL_DIS == 0) 1 else MAX_SCROLL_DIS
        )
    }

    private var startX = 0f
    private var startY = 0f

//    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
//       return  when(ev?.action){
//            MotionEvent.ACTION_DOWN->{
//                startX = ev.rawX
//                startY = ev.rawY
//                super.onInterceptTouchEvent(ev)
//            }
//
//            MotionEvent.ACTION_MOVE->{
//                abs(ev.rawY - startY) > abs(ev.rawX - startX)
//            }
//            else-> super.onInterceptTouchEvent(ev)
//        }
//    }

    interface OnScrollChangeListener {
        fun onScrollChange(scrollX: Int, scrollY: Int, scrollRatio: Double)
    }

}