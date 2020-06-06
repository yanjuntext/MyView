package com.wyj.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import com.base.utils.GlobalStatusBarUtil

class HoodItemView : View {

    private var mBindView: View? = null
    private var mLocals = IntArray(2)
    private var mBindRadius = 0f
    private var mBindViewWidth = 0f
    private var mBindViewHeight = 0f

    private val mMinCircleColor by lazy {
        ContextCompat.getColor(context, R.color.holder_one)
    }

    private val mMaxCircleColor by lazy {
        ContextCompat.getColor(context, R.color.holder_two)
    }

    private val mRectColor by lazy {
        ContextCompat.getColor(context, R.color.holder_three)
    }

    private val mPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
    }


    private var mMinRadius = 0f
    private val mMinCircleAnimator by lazy {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 200L
            addUpdateListener {
                mMinRadius = mBindRadius * 2 * it.animatedValue as Float
                postInvalidate()
            }
            doOnEnd {
                mMaxRadius = mBindRadius * 2
                mMaxCircleAnimator.start()
            }
        }
    }

    private var mMaxRadius = 0f
    private val mMaxCircleAnimator by lazy {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 200L
            addUpdateListener {
                mMaxRadius = mBindRadius * 2 * (1 + it.animatedValue as Float)
                postInvalidate()
            }
            doOnEnd {
                mOnAnimatorEndListener?.onAnimatorEnd()
            }
        }
    }


    private var mOnAnimatorEndListener: OnAnimatorEndListener? = null

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    fun setAnimatorEndListener(listener: OnAnimatorEndListener?): HoodItemView {
        mOnAnimatorEndListener = listener
        return this
    }

    fun bindView(view: View?): HoodItemView {
        mBindView = view
        mBindView?.let {
            it.getLocationOnScreen(mLocals)
            mBindRadius = it.width.coerceAtLeast(it.height) / 2f
//            mLocals[0] = it.left
//            mLocals[1] = it.top
            mBindViewWidth = it.width.toFloat()
            mBindViewHeight = it.height.toFloat()

            mMinRadius = 0f
            mMaxRadius = 0f
        }

        postInvalidate()
        return this
    }

    fun startDraw(): HoodItemView {
        mMinRadius = 0f
        mMaxRadius = 0f
        mMinCircleAnimator.start()
        return this
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (mBindView == null) return

        canvas?.let {
            drawCircle(it)
            drawWhiteCircle(it)
        }

    }

    private fun drawCircle(canvas: Canvas) {
        val x = mLocals[0] + mBindViewWidth / 2f
        val y = mLocals[1] + mBindViewHeight / 2f - GlobalStatusBarUtil.getStatusbarHeight(context)

        mPaint.color = mMaxCircleColor
        canvas.drawCircle(x, y, mMaxRadius, mPaint)

        mPaint.color = mMinCircleColor
        canvas.drawCircle(x, y, mMinRadius, mPaint)

    }

    private fun drawWhiteCircle(canvas: Canvas) {
        val x = mLocals[0] + mBindViewWidth / 2f
        val y = mLocals[1] + mBindViewHeight / 2f - GlobalStatusBarUtil.getStatusbarHeight(context)

        mPaint.color = Color.WHITE
        canvas.drawCircle(x, y, mBindRadius, mPaint)


    }

    override fun onDetachedFromWindow() {
        mMinCircleAnimator.cancel()
        mMaxCircleAnimator.cancel()
        super.onDetachedFromWindow()
    }

    interface OnAnimatorEndListener {
        fun onAnimatorEnd()
    }

}