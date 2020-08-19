package com.wyj.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.base.utils.DisplayHelper
import java.lang.RuntimeException

/**
 * 作者：王颜军 on 2020/7/23 09:13
 * 邮箱：3183424727@qq.com
 */
class XProgressBar : View {

    private val DEFAULT_SIZE = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        50F,
        resources.displayMetrics
    )

    private var mBackGroundColor = Color.WHITE
    private var mBackGroundStrokenWidth = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        2F,
        resources.displayMetrics
    )
    private var mBackGroundStrokenColor = Color.BLUE
    private var mBackGroundHeight = 0F
    private val mBackgroundDrawable by lazy { GradientDrawable() }

    private var mProgressColor = Color.RED
    private var mProgressHeight = 0f
    private val mProgressDrawable by lazy { GradientDrawable() }

    private var mProgressTextColor = Color.WHITE
    private var mShowProgressText = true
    private var mCurrentProgressEndX = 0f
    private val mTextProgressPaint by lazy {
        TextPaint().apply {
            isAntiAlias = true
        }
    }

    private var currentProgress = -1
    private var MAX_PROGRESS = 100

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
        with(context.obtainStyledAttributes(attrs, R.styleable.XProgressBar)) {
            mBackGroundColor =
                getColor(R.styleable.XProgressBar_xpb_background_color, mBackGroundColor)
            mBackGroundHeight = getDimensionPixelSize(
                R.styleable.XProgressBar_xpb_background_height,
                mBackGroundHeight.toInt()
            ).toFloat()

            mBackGroundStrokenColor =
                getColor(
                    R.styleable.XProgressBar_xpb_background_stroken_color,
                    mBackGroundStrokenColor
                )
            mBackGroundStrokenWidth = getDimensionPixelSize(
                R.styleable.XProgressBar_xpb_background_stroken_width,
                mBackGroundStrokenWidth.toInt()
            ).toFloat()

            mProgressColor = getColor(R.styleable.XProgressBar_xpb_progress_color, mProgressColor)
            mProgressHeight =
                getDimensionPixelSize(
                    R.styleable.XProgressBar_xpb_progress_height,
                    mProgressHeight.toInt()
                ).toFloat()

            mProgressTextColor =
                getColor(R.styleable.XProgressBar_xpb_progress_text_color, mProgressTextColor)
            mShowProgressText =
                getBoolean(R.styleable.XProgressBar_xpb_progress_text_show, mShowProgressText)

            MAX_PROGRESS = getInt(R.styleable.XProgressBar_xpb_max_progress, MAX_PROGRESS)
            currentProgress = getInt(R.styleable.XProgressBar_xpb_progress, currentProgress)

            if (currentProgress > MAX_PROGRESS) {
                throw RuntimeException("current progress greater than max progress")
            }

            if (mProgressHeight > mBackGroundHeight) {
                throw RuntimeException("progress height greater than background height")
            }

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
                DEFAULT_SIZE.toInt(), MeasureSpec.EXACTLY
            )
        } else heightMeasureSpec
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            drawBackground(it)
            drawProgress(it)
            drawProgressText(it)
        }

    }


    private fun drawBackground(canvas: Canvas) {
        mBackgroundDrawable.shape = GradientDrawable.RECTANGLE
        mBackgroundDrawable.cornerRadius = mBackGroundHeight / 2f
        mBackgroundDrawable.setColor(mBackGroundColor)
        if (mBackGroundStrokenWidth > 0) {
            mBackgroundDrawable.setStroke(mBackGroundStrokenWidth.toInt(), mBackGroundStrokenColor)
        }
        val cH = height / 2f
        val bH = mBackGroundHeight / 2f
        mBackgroundDrawable.setBounds(0, (cH - bH).toInt(), width, (cH + bH).toInt())
        mBackgroundDrawable.draw(canvas)
    }


    private fun drawProgress(canvas: Canvas) {
        if (currentProgress <= 0) return
        mProgressDrawable.apply {
            val cH = height / 2f
            val cpH = mProgressHeight / 2f
            val cmW = (mBackGroundHeight - mProgressHeight) / 2
            shape = GradientDrawable.RECTANGLE
            cornerRadius = cpH
            setColor(mProgressColor)
            val end = calProgressWidth() + cmW

            mCurrentProgressEndX = end
            setBounds(cmW.toInt(), (cH - cpH).toInt(), end.toInt(), (cH + cpH).toInt())
            draw(canvas)
        }
    }


    private fun drawProgressText(canvas: Canvas) {
        if (currentProgress <= 0) return

        val progress = "$currentProgress%"

        mTextProgressPaint.textSize = mProgressHeight - 20
        mTextProgressPaint.color = mProgressTextColor
        val cmW = (mBackGroundHeight - mProgressHeight) / 2
        val distance = getTextDistance(mTextProgressPaint)
        val width = getTextWidth(progress, mTextProgressPaint) + 10
        val x = if (mCurrentProgressEndX - width < cmW) cmW else mCurrentProgressEndX - width
        canvas.drawText(progress,x,height/2f + distance,mTextProgressPaint)

        // mTextPaint.textSize = mTextSize
        //        mTextPaint.color = mProgressTextColor
        //        val text = "${(sProgress * 1.0F / MAX_PROGRESS * 100).toInt()}%"
        //        val fontMetrics = mTextPaint.fontMetrics
        //        val distance = (fontMetrics.bottom - fontMetrics.top) * 1.0F / 2 - fontMetrics.bottom
        //        val baseline = height / 2 + distance
        //        canvas.drawText(text, width * 1.0F / 2, baseline, mTextPaint)


    }


    fun setProgress(progress: Int) {
        currentProgress = progress
        invalidate()
    }


    private fun calProgressWidth(): Float {
        val cmW = (mBackGroundHeight - mProgressHeight) / 2
        val tW = width - 2 * cmW

        if (currentProgress <= 0) return 0F

        return currentProgress * tW / MAX_PROGRESS
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