package com.wyj.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.animation.doOnEnd
import kotlin.math.abs

/**
 *
 *@author abc
 *@time 2020/5/10 11:22
 */
class LoadButton : View {

    private val drawable = GradientDrawable()
    private var mStorkenWidth = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        1F,
        resources.displayMetrics
    )

    private val mTextPaint by lazy {
        TextPaint().apply {
            isAntiAlias = true
            textSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                14F,
                resources.displayMetrics
            )
            color = Color.BLUE
        }
    }

    private var mCurrentW = 0f
    private var mAlpha = 255f
    private val mChangeToLoadAnimal by lazy {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 800L
            addUpdateListener {
                mCurrentW = (width / 2f - height / 2f) * it.animatedValue as Float
                mAlpha = 255 - 255 * it.animatedValue as Float
                invalidate()
            }
            doOnEnd {
                mLoadPathAnimator.start()
            }
        }
    }

    private val mChangeToButtonAnimal by lazy {
        ValueAnimator.ofFloat(1f, 0f).apply {
            duration = 800L
            addUpdateListener {
                mCurrentW = (width / 2f - height / 2f) * it.animatedValue as Float
                mAlpha = 255 - 255 * it.animatedValue as Float
                invalidate()
            }
        }
    }

    private var mPathPercent = 0f
    private val mLoadPathAnimator by lazy {
        ValueAnimator.ofFloat(0f,1f).apply {
            duration = 1000L
            repeatCount = -1
            addUpdateListener {
                mPathPercent = it.animatedValue as Float
                invalidate()
            }
        }
    }
    private val mPathPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = mStorkenWidth
            color = Color.BLUE
        }
    }
    private val mLoadPath = Path()

    private var mCirclePath: Path? = null
    private var mCirclePathMeasure: PathMeasure? = null
    private var mRect: RectF? = null

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )



    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (mCirclePath == null) {
            mRect = RectF(width / 2f - height / 2f , 0f +  mStorkenWidth, width / 2f + height / 2f, height.toFloat() - mStorkenWidth)
            mCirclePath = Path().apply {
                addArc(mRect!!, 0F, 360F)
            }
            mCirclePathMeasure = PathMeasure(mCirclePath, false)
        }

        canvas?.let {
            if(mPathPercent == 0f){
                drawable.shape = GradientDrawable.RECTANGLE
                drawable.cornerRadius = height / 2f
                drawable.setStroke(this.mStorkenWidth.toInt(), Color.BLUE)
                drawable.setBounds(mCurrentW.toInt(), 0, (width - mCurrentW).toInt(), height)
                drawable.draw(it)

                mTextPaint.alpha = mAlpha.toInt()
                it.drawText(
                    "加载",
                    width / 2f - getTextWidth("加载", mTextPaint) / 2f,
                    height / 2f + getTextDistance(mTextPaint),
                    mTextPaint
                )
            }

            if(mPathPercent != 0f){
                mCirclePathMeasure?.let {
                    mLoadPath.reset()
                    val stop = it.length * mPathPercent
                    val start = stop - (0.5f - abs(mPathPercent - 0.5f)) * it.length
                    it.getSegment(start, stop, mLoadPath, true)
                    canvas.drawPath(mLoadPath, mPathPaint)
                }
            }
        }


    }

    private var isLoad = false
    fun startLoad() {
        mLoadPathAnimator.cancel()
        mPathPercent = 0f
        if (isLoad) {
            mChangeToButtonAnimal.start()
        } else {
            mChangeToLoadAnimal.start()
        }
        isLoad = !isLoad
    }

    private fun getTextDistance(paint: TextPaint): Float {
        val fontMetrics = paint.fontMetrics
        return (fontMetrics.bottom - fontMetrics.top) * 1.0F / 2 - fontMetrics.bottom
    }

    private fun getTextWidth(text: String, paint: TextPaint) = paint.measureText(text)
}