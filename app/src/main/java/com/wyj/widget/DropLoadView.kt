package com.wyj.widget

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.doOnEnd

/**
 *下落加载View
 *@author abc
 *@time 2020/5/9 18:03
 */
class DropLoadView : View {

    private val DEFAULT_SIZE = 100

    private var mAnimalTime = 1500L
    private var mRadius = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        10F,
        resources.displayMetrics
    )

    private var mTransY = 0f
    private var mRotation = 0f

    private var mAngle = 90f

    private var mPath: Path? = null

    private var mPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = Color.parseColor("#6BA539")
    }

    private var mCircleColor = Color.parseColor("#6BA539")
    private var mRectColor = Color.parseColor("#6BA539")
    private var mTrianglesColor = Color.parseColor("#6BA539")

    private val mColors by lazy {
        mutableListOf(mTrianglesColor, mCircleColor, mRectColor)
    }

    private val mPaths by lazy { mutableListOf<Path>() }

    private var index = 0

    private val mDropAnimal by lazy {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = mAnimalTime
            //越来越快
            interpolator = AccelerateInterpolator()
            addUpdateListener {
                mTransY = (this@DropLoadView.height - mRadius * 2) * it.animatedValue as Float
                mRotation = 180f * it.animatedValue as Float
                invalidate()
            }
            doOnEnd {
                index++
            }

        }
    }

    private val mRiseAnimal by lazy {
        ValueAnimator.ofFloat(1f, 0f).apply {
            duration = mAnimalTime
            //越来越慢
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                mTransY = (this@DropLoadView.height - mRadius * 2) * it.animatedValue as Float
                mRotation = 180f * it.animatedValue as Float
                invalidate()
            }
            doOnEnd {
                index++
            }

        }
    }

    private val mAnimatorSet by lazy {
        AnimatorSet().apply {
            play(mDropAnimal).before(mRiseAnimal)
        }.also {
            it.doOnEnd {
                start()
            }
        }
    }

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0) {
        initAttr(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initAttr(context, attrs)
    }

    private fun initAttr(context: Context, attrs: AttributeSet?) {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        context.obtainStyledAttributes(attrs, R.styleable.DropLoadView).apply {

            mRadius = getDimensionPixelSize(
                R.styleable.DropLoadView_dlv_radius,
                mRadius.toInt()
            ).toFloat()
            mAngle = getFloat(R.styleable.DropLoadView_dlv_rotate_angle, mAngle)
            mAnimalTime =
                getInt(R.styleable.DropLoadView_dlv_drop_time, mAnimalTime.toInt()).toLong()

            mCircleColor = getColor(R.styleable.DropLoadView_dlv_circle_color, mCircleColor)
            mRectColor = getColor(R.styleable.DropLoadView_dlv_rect_color, mRectColor)
            mTrianglesColor =
                getColor(R.styleable.DropLoadView_dlv_triangles_color, mTrianglesColor)

            recycle()
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = if (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY) {
            MeasureSpec.makeMeasureSpec(
                (mRadius * 3).toInt(), MeasureSpec.EXACTLY
            )
        } else widthMeasureSpec
        val height = if (MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY) {
            MeasureSpec.makeMeasureSpec(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    DEFAULT_SIZE.toFloat(),
                    resources.displayMetrics
                ).toInt(), MeasureSpec.EXACTLY
            )
        } else heightMeasureSpec
        setMeasuredDimension(width, height)
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        Log.i("DropLoadView", "onDraw[$mTransY],[$mRotation]")
        canvas?.let {
            it.save()
            if (mPath == null) {
                mPath = Path().apply {
                    moveTo(width / 2f - mRadius, 0f)
                    lineTo(width / 2f + mRadius, 0f)
                    lineTo(width / 2f, mRadius * 2)
                    lineTo(width / 2f - mRadius, 0f)
                }
                mPaths.clear()
                mPaths.add(mPath!!)
                mPaths.add(Path().also {
                    it.addCircle(width / 2f, mRadius, mRadius, Path.Direction.CW)
                })
                mPaths.add(Path().apply {
                    moveTo(width / 2f - mRadius, 0f)
                    lineTo(width / 2f + mRadius, 0f)
                    lineTo(width / 2f + mRadius, mRadius * 2)
                    lineTo(width / 2f - mRadius, mRadius * 2)
                    lineTo(width / 2f - mRadius, 0f)
                })
            }
            it.translate(0f, mTransY)
            it.rotate(mRotation, width / 2f, mRadius)
            mPaint.color = mColors[index % 3]
            it.drawPath(mPaths[index % 3], mPaint)
            it.restore()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        start()
    }

    fun start() {
        mAnimatorSet.start()
    }

    override fun onDetachedFromWindow() {
        mAnimatorSet.cancel()
//        mRiseAnimal.cancel()
        super.onDetachedFromWindow()
    }


}