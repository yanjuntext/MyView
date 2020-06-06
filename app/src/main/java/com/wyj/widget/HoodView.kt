package com.wyj.widget

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.base.utils.GlobalStatusBarUtil
import com.base.utils.image.ImageLoader

class HoodView : FrameLayout, HoodItemView.OnAnimatorEndListener {


    private var mHoodItemView: HoodItemView? = null
    private var mImageView: ImageView? = null


    private val mImageAnimator by lazy {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 200L
            addUpdateListener {
                mImageView?.alpha = it.animatedValue as Float
            }
        }
    }

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )


    fun startShow(activity: Activity, targetView: View) {
        if (mImageView != null) {
            removeView(mImageView)
            mImageView = null
        }
        val rootView = activity.window.decorView.findViewById<FrameLayout>(android.R.id.content)

        rootView.addView(
            this,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        mImageAnimator.cancel()

        val mLocals = IntArray(2)
        targetView.getLocationOnScreen(mLocals)
        mImageView = ImageView(context)
        mImageView?.scaleType = ImageView.ScaleType.FIT_XY
        val ivHeight =
            height - (mLocals[1] - GlobalStatusBarUtil.getStatusbarHeight(context) - targetView.width.coerceAtLeast(
                targetView.height
            ) / 2f * 2.5)
        Log.i("HoodVIew", "ivHeight[$ivHeight]")
        addView(
            mImageView, ViewGroup.LayoutParams.MATCH_PARENT,
            ivHeight.toInt()
        )

        mImageView?.let { imageView ->
            val layoutParams = imageView.layoutParams
            if (layoutParams is FrameLayout.LayoutParams) {
                layoutParams.gravity = Gravity.BOTTOM
            }
            imageView.alpha = 0f
//            imageView.visibility = View.INVISIBLE
            ImageLoader.with(context)
                .blur(30, 1)
                .load(R.drawable.ic_hodle_bg)
                .into(imageView)
        }


        if (mHoodItemView != null) {
            removeView(mHoodItemView)
            mHoodItemView = null
        }

        postInvalidate()

        mHoodItemView = HoodItemView(context)
        mHoodItemView?.setAnimatorEndListener(this)
        addView(
            mHoodItemView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        mHoodItemView?.bindView(targetView)?.startDraw()

    }

    fun remove(activity: Activity) {
        activity.window.decorView.findViewById<FrameLayout>(android.R.id.content).removeView(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

    override fun onAnimatorEnd() {
        mImageAnimator.start()

    }


}