package com.wyj.widget

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.core.animation.doOnEnd

/**
 *选中View
 *@author abc
 *@time 2020/5/8 17:06
 */
class TickView : View, View.OnTouchListener {

    private val DEFAULT_SIZE = 80f
    //未选中状态
    private var mUnCheckColor = Color.parseColor("#E4E4E4")
    private var mCircleStrokenWidth = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        1F,
        resources.displayMetrics
    )
    private var mCheckMarkStrokenWidth = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        1F,
        resources.displayMetrics
    )

    //选中状态
    private var mCheckColor = Color.parseColor("#6BA539")
    private var mCheckMarkCheckColor = Color.WHITE

    private var mAnimalTime = 500L

    private var mRadius = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        25F,
        resources.displayMetrics
    )
    private var mScaleFactor = mRadius / 3


    private val mUnCheckPaint by lazy {
        Paint().apply { isAntiAlias = true }
    }

    private val mCheckPaint by lazy {
        Paint().apply { isAntiAlias = true }
    }

    private var mCheckMarkPath: Path? = null
    private var mCheckMarkPathMeasure: PathMeasure? = null

    private var mCircleStrokenSweepAngle = 0f
    private val mCircleStrokenAnimal by lazy {
        ValueAnimator.ofFloat(0f, 1f).apply {
            interpolator = AccelerateInterpolator()
            addUpdateListener {
                mCircleStrokenSweepAngle = 360f * it.animatedValue as Float
                invalidate()
            }
        }
    }
    //缩小的圆
    private var mWhiteCircleRadius = mRadius
    private val mScaleWhiteAnimal by lazy {
        ValueAnimator.ofFloat(1f, 0f).apply {
            interpolator = AccelerateInterpolator()
            addUpdateListener {
                mWhiteCircleRadius = mRadius * it.animatedValue as Float
                invalidate()
            }
            this.doOnEnd {
                mCheckMarkAnimalSet.start()
            }

        }
    }

    //打钩动画
    private var mCheckMarkPercent = 0f
    private var mCheckScale = 0f
    private val mCheckMarkAnimal by lazy {
        ValueAnimator.ofFloat(0f, 1f).apply {
            interpolator = AccelerateInterpolator()
            addUpdateListener {
                mCheckMarkPercent = it.animatedValue as Float
                invalidate()
            }
        }
    }
    private val mCheckScaleAnimal by lazy {
        ValueAnimator.ofFloat(0f, 1f, 0f).apply {
            interpolator = AccelerateInterpolator()
            addUpdateListener {
                mCheckScale = mScaleFactor * it.animatedValue as Float
            }
        }
    }

    private val mAnimalSet by lazy {
        AnimatorSet().apply {
            play(mCircleStrokenAnimal).before(mScaleWhiteAnimal)
        }
    }

    private val mCheckMarkAnimalSet by lazy {
        AnimatorSet().apply {
            play(mCheckMarkAnimal).with(mCheckScaleAnimal)
        }
    }


    private var isChecked = false

    private var mRect: RectF? = null

    private var mOnCheckChangeListener: OnCheckChangeListener? = null

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

    init {
        setOnTouchListener(this)
    }


    private fun initAttr(context: Context, attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.TickView).apply {
            mUnCheckColor = getColor(R.styleable.TickView_tiv_uncheck_color, mUnCheckColor)
            mCircleStrokenWidth =
                getDimensionPixelSize(
                    R.styleable.TickView_tiv_stroken_width,
                    mCircleStrokenWidth.toInt()
                ).toFloat()

            mCheckMarkStrokenWidth =
                getDimensionPixelSize(
                    R.styleable.TickView_tiv_check_mark_stroken_width,
                    mCheckMarkStrokenWidth.toInt()
                ).toFloat()


            mCheckColor = getColor(R.styleable.TickView_tiv_check_color, mCheckColor)
            mCheckMarkCheckColor =
                getColor(R.styleable.TickView_tiv_select_check_mark_color, mCheckMarkCheckColor)

            mRadius = getDimensionPixelSize(
                R.styleable.TickView_tiv_radius,
                mRadius.toInt()
            ).toFloat()
            mScaleFactor = getDimensionPixelSize(
                R.styleable.TickView_tiv_scale_factor,
                mScaleFactor.toInt()
            ).toFloat()

            mAnimalTime = getInt(R.styleable.TickView_tiv_animal_time, mAnimalTime.toInt()).toLong()
            recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = if (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY) {
            MeasureSpec.makeMeasureSpec(
                ((mRadius + mScaleFactor) * 2).toInt(), MeasureSpec.EXACTLY
            )
        } else widthMeasureSpec
        val height = if (MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY) {
            MeasureSpec.makeMeasureSpec(
                ((mRadius + mScaleFactor) * 2).toInt(), MeasureSpec.EXACTLY
            )
        } else heightMeasureSpec
        setMeasuredDimension(width, height)
    }


    override fun onDraw(canvas: Canvas?) {
//        super.onDraw(canvas)
        if (mCheckMarkPath == null) {
            mCheckMarkPath = Path().also {
                it.moveTo(width / 2 - mRadius / 2, height / 2f)
                it.lineTo(width / 2f, height / 2 + mRadius / 3)
                it.lineTo(width / 2 + mRadius / 2, height / 2 - mRadius / 3)
            }
            mCheckMarkPathMeasure = PathMeasure(mCheckMarkPath, false)
        }
        if (mRect == null) {
            mRect = RectF(
                width / 2f - mRadius,
                height / 2f - mRadius,
                width / 2f + mRadius,
                height / 2f + mRadius
            )
        }
        canvas?.let {
            drawDefauleView(it)
            drawCheckArc(it)
            drawScaleWhite(it)
            drawCheckMark(it)
        }
    }


    //绘制默认
    private fun drawDefauleView(canvas: Canvas) {
        with(mUnCheckPaint) {
            style = Paint.Style.STROKE
            color = mUnCheckColor
            strokeWidth = mCircleStrokenWidth
        }
        mRect?.let {
            canvas.drawArc(it, 90f, 360f, false, mUnCheckPaint)
        }

        mUnCheckPaint.strokeWidth = mCheckMarkStrokenWidth
        mCheckMarkPath?.let {
            canvas.drawPath(it, mUnCheckPaint)
        }
    }

    private fun drawCheckArc(canvas: Canvas) {
        if (!isChecked) return

        with(mCheckPaint) {
            color = mCheckColor
            strokeWidth = mCircleStrokenWidth
            style = Paint.Style.STROKE
        }
        mRect?.let {
            canvas.drawArc(it, 90f, mCircleStrokenSweepAngle, false, mCheckPaint)
        }
    }

    //白色缩小的圆
    private fun drawScaleWhite(canvas: Canvas) {
        if (!isChecked || mCircleStrokenSweepAngle != 360f) return
        with(mCheckPaint) {
            color = mCheckColor
            strokeWidth = 0f
            style = Paint.Style.FILL
        }
        canvas.drawCircle(width / 2f, height / 2f, mRadius, mCheckPaint)

        mCheckPaint.color = Color.WHITE
        canvas.drawCircle(width / 2f, height / 2f, mWhiteCircleRadius, mCheckPaint)


    }

    //打钩 和缩放
    private fun drawCheckMark(canvas: Canvas) {
        Log.i("TickView", "mCheckScale[$mCheckScale],mCheckMarkPercent[$mCheckMarkPercent]")
        if (!isChecked || mCircleStrokenSweepAngle != 360f || mWhiteCircleRadius != 0f) return

        with(mCheckPaint) {
            color = mCheckColor
            strokeWidth = 0f
            style = Paint.Style.FILL
        }
        canvas.drawCircle(width / 2f, height / 2f, mRadius + mCheckScale, mCheckPaint)

        with(mCheckPaint) {
            color = mCheckMarkCheckColor
            strokeWidth = mCheckMarkStrokenWidth
            style = Paint.Style.STROKE
        }
        mCheckMarkPathMeasure?.let {
            val path = Path()
            it.getSegment(0F, mCheckMarkPercent * it.length, path, true)
            path.rLineTo(0f, 0f)
            canvas.drawPath(path, mCheckPaint)
        }
    }

    override fun onDetachedFromWindow() {
        mAnimalSet.cancel()
        mCheckMarkAnimalSet.cancel()
        super.onDetachedFromWindow()
    }

    fun setCheck(check: Boolean, animal: Boolean = true) {
        isChecked = check
        if (check) {
            if (animal) {
                mCircleStrokenAnimal.duration = mAnimalTime
                mScaleWhiteAnimal.duration = mAnimalTime
                mCheckMarkAnimal.duration = mAnimalTime
                mCheckScaleAnimal.duration = mAnimalTime
                mAnimalSet.start()
            }
        } else {
            mAnimalSet.cancel()
            mCheckMarkAnimalSet.cancel()
            invalidate()
        }
        mOnCheckChangeListener?.onCheck(isChecked)
    }

    fun setOnCheckChangeListener(onCheckChangeListener: OnCheckChangeListener?) {
        this.mOnCheckChangeListener = onCheckChangeListener
    }


    override fun onTouch(p0: View?, event: MotionEvent?): Boolean {

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mAnimalSet.cancel()
                mCheckMarkAnimalSet.cancel()
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                setCheck(!isChecked, true)
            }


        }

        return true
    }

    interface OnCheckChangeListener {
        fun onCheck(check: Boolean)
    }

}