package com.wyj.widget

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.animation.doOnEnd
import androidx.viewpager2.widget.ViewPager2
import com.base.utils.MLog
import kotlin.math.abs
import kotlin.math.max

/**
 *下滑退出当前界面
 *@author abc
 *@time 2020/5/10 16:41
 */
class SlideCloseLayout : FrameLayout {

    private var mPreviousX = 0f
    private var mPreviousY = 0f
    private var mView: View? = null
    private var transTotle = 0f
    private val mLayoutRecoverAnimator by lazy {
        ValueAnimator.ofFloat(1f, 0f).apply {
            duration = 300L
            addUpdateListener {
                mView?.translationY = transTotle * it.animatedValue as Float
                alpha = 1f -  (mView?.translationY?:0f) / height

                mView?.scaleX = 1f-(mView?.translationY?:0f) / height
                mView?.scaleY = 1f-(mView?.translationY?:0f) / height
            }
        }
    }
    private var mExitTrans = 0f
    private val mLaoutExitAnimator by lazy {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 300L
            addUpdateListener {
                mView?.translationY= mExitTrans * it.animatedValue as Float + transTotle
                alpha = 1f - (mView?.translationY?:0f) / height

                mView?.scaleX = 1f-(mView?.translationY?:0f) / height
                mView?.scaleY = 1f-(mView?.translationY?:0f) / height
            }
            doOnEnd {
                mOnLayoutCloseListener?.onLayoutClose()
            }
        }
    }

    private var mOnLayoutCloseListener: OnLayoutCloseListener? = null

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    override fun onFinishInflate() {
        super.onFinishInflate()
        if(childCount > 0){
            mView = getChildAt(0)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        Log.i("SlideCloseLayout", "onInterceptTouchEvent")
        ev?.pointerCount?.let {
            if (it > 1) return false
            val x = ev.rawX
            val y = ev.rawY
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    mPreviousX = x
                    mPreviousY = y
                }
                MotionEvent.ACTION_MOVE -> {
                    val diffY = y - mPreviousY
                    val diffX = x - mPreviousX
                    if (diffY < 0) return false
                    if (abs(diffX) + 50 < abs(diffY)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mLayoutRecoverAnimator.cancel()
                mPreviousX = event.rawX
                mPreviousY = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                val diffY = max(event.rawY - mPreviousY, 0f)
                mView?.translationY = diffY
//                translationY = diffY
                alpha = 1f - diffY / height
                mView?.scaleX = 1f-diffY / height
                mView?.scaleY = 1f-diffY / height
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                transTotle = mView?.translationY?:0f
                if (abs(mView?.translationY?:0f) > height / 5) {
                    //退出
                    mExitTrans = height - transTotle
                    mLaoutExitAnimator.start()
                } else {
                    //恢复
                    mLayoutRecoverAnimator.start()
                }
            }
        }
        return super.onTouchEvent(event)
    }




    fun setOnLayoutCloseListener(listener: OnLayoutCloseListener?) {
        this.mOnLayoutCloseListener = listener
    }

    override fun onDetachedFromWindow() {
        mLayoutRecoverAnimator.cancel()
        mLaoutExitAnimator.cancel()
        super.onDetachedFromWindow()
    }

    interface OnLayoutCloseListener {
        fun onLayoutClose()
    }
}