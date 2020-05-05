package com.wyj.widget

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.ActionBar
import android.content.Context
import android.graphics.*
import android.os.Build
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import com.base.utils.DisplayHelper
import kotlin.math.abs
import kotlin.math.sqrt

/**
 *
 *@author abc
 *@time 2020/4/9 16:14
 */
class DegreeView : View, View.OnTouchListener {

    /**进度条颜色*/
    private var mProgressBgColor = Color.parseColor("#B6B6B6")
    /**进度条选中颜色*/
    private var mProgressColor = ContextCompat.getColor(context, R.color.colorAccent)
    /**进度条高度*/
    private var mProgressHeight = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        2F,
        resources.displayMetrics
    )
    /**滑块颜色*/
    private var mSlideColor = Color.WHITE
    /**滑块半径大小*/
    private var mSlideRadius = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        10F,
        resources.displayMetrics
    )
    /**进度颜色*/
    private var mProgressTextColor = ContextCompat.getColor(context, R.color.colorAccent)
    /**刻度颜色*/
    private var mScaleTextColor = ContextCompat.getColor(context, R.color.colorAccent)
    /**最小刻度*/
    private var mMinProgress = 0
    /**最大刻度*/
    private var mMaxProgress = 42

    private var mProgressPercent = true

    private lateinit var mSlidePoint: PointF


    private var mProgressText = 0
    private var mProgressTextSize = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        14F,
        resources.displayMetrics
    )

    private var mTextHeight = 0f
    private var mLimit = 0f

    private lateinit var mTextPaint: TextPaint
    private lateinit var mProgressPaint: Paint
    private lateinit var mSlidePaint: Paint

    private var mCanMove = false
    private var mStartX = 0f

    private var mIDegreeProgress: DegreeView.IDegreeProgress? = null


    init {
        setOnTouchListener(this)
    }

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0) {
        initAttrs(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initAttrs(context, attrs)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        with(context.obtainStyledAttributes(attrs, R.styleable.DegreeView)) {

            mProgressBgColor =
                getColor(R.styleable.DegreeView_cat_progress_bg_color, mProgressBgColor)
            mProgressColor = getColor(R.styleable.DegreeView_cat_progress_color, mProgressColor)
            mProgressTextColor =
                getColor(R.styleable.DegreeView_cat_progress_text_color, mProgressTextColor)
            mSlideColor = getColor(R.styleable.DegreeView_cat_slide_color, mSlideColor)

            mProgressHeight =
                getDimensionPixelSize(
                    R.styleable.DegreeView_cat_progress_height,
                    mProgressHeight.toInt()
                ).toFloat()
            mProgressTextSize = getDimensionPixelSize(
                R.styleable.DegreeView_cat_progress_text_size,
                mProgressTextSize.toInt()
            ).toFloat()
            mSlideRadius =
                getDimensionPixelSize(
                    R.styleable.DegreeView_cat_slide_radius,
                    mSlideRadius.toInt()
                ).toFloat()

            mMinProgress = getInt(R.styleable.DegreeView_cat_min_progress, mMinProgress)
            mMaxProgress = getInt(R.styleable.DegreeView_cat_max_progress, mMaxProgress)
            mProgressText = getInt(R.styleable.DegreeView_cat_current_progress, mProgressText)

            recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = if (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY) {
            MeasureSpec.makeMeasureSpec(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    DisplayHelper.screenWidth().toFloat(),
                    resources.displayMetrics
                ).toInt(), MeasureSpec.EXACTLY
            )
        } else widthMeasureSpec
        val height = if (MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY) {
            MeasureSpec.makeMeasureSpec(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    70F,
                    resources.displayMetrics
                ).toInt(), MeasureSpec.EXACTLY
            )
        } else heightMeasureSpec
        setMeasuredDimension(width, height)

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)


        mTextPaint = TextPaint().apply {
            isAntiAlias = true
            textSize = mProgressTextSize
        }

        mTextHeight = getTextHeight(
            if (mProgressPercent) "${(mProgressText * 1.0 / (mMaxProgress - mMinProgress) * 100).toInt()}%" else "$mProgressText℃",
            mTextPaint
        )

        mProgressPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        mSlidePaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL

            setShadowLayer(mSlideRadius + 1, 0f, 0f, Color.parseColor("#906BA539"))
        }


        mLimit = (width - mSlideRadius * 4f) / (mMaxProgress - mMinProgress)

        mSlidePoint = PointF(calPointX(mProgressText), mTextHeight + mSlideRadius * 2f)


    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {
            drawProgressText(it)
            drawProgress(it)
            drawScaleText(it)
            drawSlide(it)
        }

    }

    private fun drawProgressText(canvas: Canvas) {
        with(mTextPaint) {
            textSize = mProgressTextSize
            color = mProgressTextColor
        }

        mProgressText = ((mSlidePoint.x - mSlideRadius * 2) / mLimit + 0.5f).toInt()
        mProgressText = (mProgressText * 1.0 / (mMaxProgress - mMinProgress) * 100).toInt()
        val text = if (mProgressPercent) "$mProgressText%" else "$mProgressText℃"
        val width = getTextWidth(text, mTextPaint)
        val distance = getTextDistance(text, mTextPaint)
        canvas.drawText(text, mSlidePoint.x - width / 2f, mTextHeight / 2 + distance, mTextPaint)

        mIDegreeProgress?.onDegreeProgress(mProgressText)
    }

    private fun drawProgress(canvas: Canvas) {

        with(mProgressPaint) {
            color = mProgressBgColor
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mProgressPaint.strokeWidth = mProgressHeight / 2f
            canvas.drawRoundRect(
                mSlideRadius * 2f,
                mSlidePoint.y - mProgressHeight / 2.0f,
                width - mSlideRadius * 2f,
                mSlidePoint.y + mProgressHeight / 2.0f,
                mProgressHeight / 2.0f,
                mProgressHeight / 2.0f,
                mProgressPaint
            )
        } else {
            mProgressPaint.strokeWidth = mProgressHeight.toFloat()
            canvas.drawLine(
                mSlideRadius * 2f,
                mSlidePoint.y,
                width - mSlideRadius * 2f,
                mSlidePoint.y,
                mProgressPaint
            )
        }


        with(mProgressPaint) {
            color = mProgressColor
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mProgressPaint.strokeWidth = mProgressHeight / 2f
            canvas.drawRoundRect(
                mSlideRadius * 2f,
                mSlidePoint.y - mProgressHeight / 2.0f,
                mSlidePoint.x,
                mSlidePoint.y + mProgressHeight / 2.0f,
                mProgressHeight / 2.0f,
                mProgressHeight / 2.0f,
                mProgressPaint
            )
        } else {
            mProgressPaint.strokeWidth = mProgressHeight
            canvas.drawLine(
                mSlideRadius * 2f,
                mSlidePoint.y,
                mSlidePoint.x,
                mSlidePoint.y,
                mProgressPaint
            )
        }


    }

    private fun drawSlide(canvas: Canvas) {
        with(mSlidePaint) {
            color = mSlideColor
        }

        canvas.drawCircle(mSlidePoint.x, mSlidePoint.y, mSlideRadius, mSlidePaint)
    }

    private fun drawScaleText(canvas: Canvas) {


        with(mTextPaint) {
            textSize = mProgressTextSize
            color = mProgressTextColor
        }

        val min = if (mProgressPercent) "$mMinProgress" else "${mMinProgress}℃"
        val distance = getTextDistance(min, mTextPaint)
        canvas.drawText(
            min,
            mSlideRadius * 2f,
            mSlidePoint.y + mSlideRadius + 10f + mTextHeight / 2 + distance,
            mTextPaint
        )


        val max = if (mProgressPercent) "100" else "${mMaxProgress}℃"
        val width = getTextWidth(max, mTextPaint)
        val maxDistance = getTextDistance(max, mTextPaint)
        canvas.drawText(
            max,
            this.width - mSlideRadius * 2f - width,
            mSlidePoint.y + mSlideRadius + mTextHeight / 2 + maxDistance + 10f,
            mTextPaint
        )
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                calCanMove(event.x, event.y)
                mStartX = event.x
            }
            MotionEvent.ACTION_MOVE -> {
                calMoveX(event.x)
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                mCanMove = false
                mStartX = 0f
                mIDegreeProgress?.onDownUpListener(mProgressText)
            }
        }
        return true
    }

    private fun calCanMove(x: Float, y: Float) {
        mCanMove =
            sqrt((x - mSlidePoint.x) * (x - mSlidePoint.x) + (y - mSlidePoint.y) * (y - mSlidePoint.y)) <= mSlideRadius * 4
    }

    private fun calMoveX(x: Float) {
        if (!mCanMove) return
        val distance = x - mStartX
        if (abs(distance) > 3) {
            when {
                mSlidePoint.x + distance < mSlideRadius * 2 -> mSlidePoint.offset(
                    mSlideRadius * 2 - mSlidePoint.x,
                    0f
                )
                mSlidePoint.x + distance > width - mSlideRadius * 2 -> mSlidePoint.offset(
                    width - mSlideRadius * 2 - mSlidePoint.x,
                    0f
                )
                else -> mSlidePoint.offset(distance, 0f)
            }
            mStartX = x
            invalidate()
        }
    }

    private fun calPointX(progress: Int): Float = mSlideRadius * 2f + mLimit * progress

    fun setProgress(progress: Int) {
        mProgressText =
            if (progress < mMinProgress) mMinProgress else if (progress > mMaxProgress) mMaxProgress else progress
        mSlidePoint.x = calPointX(mProgressText)
        invalidate()
    }

    fun setDegreeProgressListener(listener: IDegreeProgress?) {
        mIDegreeProgress = listener
    }

    fun show() {
        visibility = VISIBLE
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

    interface IDegreeProgress {
        fun onDegreeProgress(progress: Int)
        fun onDownUpListener(progress: Int)
    }


    private fun getTextDistance(text: String? = null, paint: TextPaint): Float {
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