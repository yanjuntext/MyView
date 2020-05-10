package com.wyj.widget

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.animation.doOnEnd

/**
 *
 *@author abc
 *@time 2020/5/10 14:18
 */
class VideoShinyView : View {

    private val DEFAULT_SIZE = 80f

    private var mBitmap: Bitmap? = null
    private var mBitmapCanvas: Canvas? = null
    private val mPaint by lazy {
        Paint().also {
            it.isAntiAlias = true
//        it.color = Color.RED
            it.color = Color.parseColor("#E4E4E4")
            it.style = Paint.Style.FILL
        }

    }
    private val msPaint by lazy {
        Paint().also {
            it.isAntiAlias = true
//        it.color = Color.RED
            it.color = Color.argb(255, 255, 255, 255)
            it.style = Paint.Style.FILL
        }

    }
    private val mCPaint by lazy {
        Paint().also {
            it.isAntiAlias = true
            it.color = Color.WHITE
            it.style = Paint.Style.FILL
        }
    }

    private var mRatate = 0f
    private var mTrans = 0f
    private val mRectAnimator by lazy {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1500L
            addUpdateListener {
                mRatate = 180 * it.animatedValue as Float

                invalidate()
            }
        }
    }
    private val mAddRotateAnimator by lazy {
        ValueAnimator.ofFloat(1f, 0f).apply {
            duration = 1500L
            addUpdateListener {
                mRatate = 180 * it.animatedValue as Float
                invalidate()
            }
        }
    }

    private val mAnimatorSet by lazy {
        AnimatorSet().apply {
            play(mRectAnimator).before(mAddRotateAnimator)
        }.apply {
            doOnEnd {
                start()
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

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = if (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY) {
            MeasureSpec.makeMeasureSpec(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    DEFAULT_SIZE,
                    resources.displayMetrics
                ).toInt(), MeasureSpec.EXACTLY
            )
        } else widthMeasureSpec
        val height = if (MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY) {
            MeasureSpec.makeMeasureSpec(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    DEFAULT_SIZE,
                    resources.displayMetrics
                ).toInt(), MeasureSpec.EXACTLY
            )
        } else heightMeasureSpec
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).also {
            mBitmapCanvas = Canvas(it)
        }
        mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {


            it.drawCircle(width / 2f, height / 2f, width / 2f, mCPaint)
            it.save()
            it.rotate(180f - mRatate, width.toFloat(), height / 2f)
            mBitmap?.let { bitmap ->
                it.drawBitmap(bitmap, 0f, 0f, msPaint)
            }
            mBitmapCanvas?.drawCircle(width / 2f, height / 2f, width / 2f, msPaint)
            mBitmapCanvas?.drawCircle(width / 2f, height / 2f, width / 2f, mPaint)
            it.restore()

        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mAnimatorSet.start()
    }

    private fun start() {
        mAnimatorSet.start()
    }

}