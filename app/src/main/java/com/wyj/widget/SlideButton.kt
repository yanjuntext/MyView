package com.wyj.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import kotlin.math.sqrt

/**
 *
 *@author abc
 *@time 2020/5/5 17:04
 */
class SlideButton : View, View.OnTouchListener {

    private var mRadius = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        20F,
        resources.displayMetrics
    )

    private val mRadiusPaint: Paint = Paint()

    private var mStorkenWidth = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        1F,
        resources.displayMetrics
    )

    private val mMoveRadiusPaint = Paint()

    private var mRadiusPoints: PointF? = null

    private var mCanMove = false

    private var direction = 0
    private var distance = 0f
    private var time = 500L
    private var startX = 0f
    private var mBitmap: Bitmap? = null
    private var mBitmapWidth = 0f
    private var mBitmapHeight = 0f

    private val path = Path()
    private val drawable = GradientDrawable()

    private val mSlideDrawable = GradientDrawable()

    private val mTextPaint by lazy {
        TextPaint().also {
            it.textSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                14F,
                resources.displayMetrics
            )
            it.color = Color.BLUE
            it.isAntiAlias = true
        }
    }

    private val mBtimapPaint by lazy {
        Paint().also {
            it.isAntiAlias = true
        }
    }

    private val mAnimal: ValueAnimator by lazy {

        ValueAnimator.ofFloat(0f, 1f).also {
            it.duration = (time / (width / 2f) * distance).toLong()
            it.interpolator = AccelerateInterpolator()
            it.addUpdateListener {
                mRadiusPoints?.set(
                    startX + distance * (it.animatedValue as Float) * direction,
                    height / 2f
                )
                invalidate()
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


    init {
        setOnTouchListener(this)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.i("SlideButton", "onSizeChanged")
        BitmapFactory.decodeResource(resources, R.drawable.ic_clock, null)?.let {
            val curentH = height.toFloat() - 10f
            val scale = curentH / it.height
            Log.i(
                "SlideButton",
                "scale[$scale],h[${it.height}],w[${it.width}],ch[${curentH}],cw[${it.width * scale}]"
            )
            mBitmapWidth = it.width * scale
            mBitmapHeight = curentH
            val matrix = Matrix()
            matrix.postScale(scale, scale)
            mBitmap = Bitmap.createBitmap(it, 0, 0, it.width, it.height, matrix, true)
        }

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {

            with(mRadiusPaint) {
                style = Paint.Style.STROKE
                color = Color.BLUE
                strokeWidth = mStorkenWidth
                isAntiAlias = true
            }
            with(mMoveRadiusPaint) {
                style = Paint.Style.FILL
                color = Color.BLUE
                isAntiAlias = true
            }
            if (mRadiusPoints == null) {
                mRadiusPoints = PointF(mRadius, height / 2f)
            }
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    it.drawRoundRect(
//                        mStorkenWidth,
//                        mStorkenWidth,
//                        width.toFloat() - mStorkenWidth,
//                        height.toFloat() - mStorkenWidth,
//                        mRadius,
//                        mRadius,
//                        mRadiusPaint
//                    )
//                }
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    it.drawRoundRect(
//                        mStorkenWidth,
//                        mStorkenWidth,
//                        (mRadiusPoints?.x ?: mRadius) + mRadius,
//                        height.toFloat() - mStorkenWidth,
//                        mRadius,
//                        mRadius,
//                        mMoveRadiusPaint
//                    )
//                }
//            } else
//            run {





            drawable.shape = GradientDrawable.RECTANGLE
            drawable.cornerRadius = mRadius
            drawable.setStroke(this.mStorkenWidth.toInt(), Color.BLUE)
            drawable.setBounds(0, 0, width, height)
            drawable.draw(it)


            mSlideDrawable.shape = GradientDrawable.RECTANGLE
            mSlideDrawable.cornerRadius = mRadius
            mSlideDrawable.setColor(Color.BLUE)
            mSlideDrawable.setBounds(
                0, 0,
                ((mRadiusPoints?.x ?: mRadius) + mRadius).toInt(), height
            )
            mSlideDrawable.draw(it)
//            }


            it.drawCircle(
                mRadiusPoints?.x ?: mRadius,
                mRadiusPoints?.y ?: (height / 2f),
                mRadius,
                mMoveRadiusPaint
            )

            Log.e("SlideButton", "mBitmap [${mBitmap == null}]")
            drawBitmap(it)
            it.drawText(
                "滑动解锁",
                width / 2f - getTextWidth("滑动解锁", mTextPaint) / 2f,
                height / 2f + getTextDistance(mTextPaint),
                mTextPaint
            )
        }

    }

    private fun drawBitmap(it: Canvas) {


        mBitmap?.let { bitmap ->
            val px = mRadiusPoints?.x ?: 0f
            val py = mRadiusPoints?.y ?: 0f
            val rectF = RectF(
                px - mBitmapWidth / 2f,
                py - mBitmapHeight / 2f,
                px + mBitmapWidth / 2f,
                py + mBitmapHeight / 2f
            )
            it.drawBitmap(bitmap, null, rectF, mBtimapPaint)
        }
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {

        when (p1?.action) {
            MotionEvent.ACTION_DOWN -> {
                mAnimal.cancel()
                direction = 0
                distance = 0f
                startX = 0f

                mCanMove = sqrt(
                    (p1.x - (mRadiusPoints?.x ?: 0f)) * (p1.x - (mRadiusPoints?.x
                        ?: 0f)) + (p1.y - (mRadiusPoints?.y ?: 0f)) * (p1.y - (mRadiusPoints?.y
                        ?: 0f)) * 1.0
                ) < mRadius
            }
            MotionEvent.ACTION_MOVE -> {

                if (mCanMove) {

                    val distance = p1.x - (mRadiusPoints?.x ?: 0f)
                    when {
                        (mRadiusPoints?.x ?: 0f) + distance < mRadius -> mRadiusPoints?.set(
                            mRadius,
                            height / 2f
                        )
                        (mRadiusPoints?.x ?: 0f) + distance > width - mRadius -> mRadiusPoints?.set(
                            width - mRadius,
                            height / 2f
                        )
                        else -> mRadiusPoints?.set((mRadiusPoints?.x ?: 0f) + distance, height / 2f)
                    }
                    invalidate()

                }

            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                direction = if ((mRadiusPoints?.x ?: 0f) >= width / 2f) {
                    startX = mRadiusPoints?.x ?: 0f
                    distance = width - mRadius - mStorkenWidth - (mRadiusPoints?.x ?: 0f)
                    1
                } else {
                    startX = mRadiusPoints?.x ?: 0f
                    distance = (mRadiusPoints?.x ?: 0f) - mRadius - mStorkenWidth
                    -1
                }
                if (distance > 0) {
                    mAnimal.duration = (time / (width / 2f) * distance).toLong()
                    mAnimal.start()
                }
            }
        }

        return true
    }


    private fun getTextDistance(paint: TextPaint): Float {
        val fontMetrics = paint.fontMetrics
        return (fontMetrics.bottom - fontMetrics.top) * 1.0F / 2 - fontMetrics.bottom
    }

    private fun getTextWidth(text: String, paint: TextPaint) = paint.measureText(text)

    private fun getTextHeight(text: String, paint: TextPaint): Float {
        val rect = Rect()
        paint.getTextBounds(text, 0, text.length, rect)
        return rect.height().toFloat()
    }

}