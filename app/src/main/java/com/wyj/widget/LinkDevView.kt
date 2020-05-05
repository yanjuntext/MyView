package com.wyj.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateInterpolator
import kotlinx.coroutines.*

/**
 *
 *@author abc
 *@time 2020/1/17 11:12
 */
class LinkDevView : View {

    enum class Link {
        linking, complete, error
    }

    private var sJob: Job? = null
    /**默认半径*/
    private var DEFAULT_RADIUS = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        8F,
        resources.displayMetrics
    )
    /***/
    private var DEFAULT_SEL_RADIUS = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        10F,
        resources.displayMetrics
    )

    /**默认个数*/
    private var DEFAULT_COUNT = 5

    private var mCurrentSelIndex = -1

    private var mResult: Link = Link.linking

    private var mDuration = 200L

    private val mPaint by lazy {
        Paint().also {
            it.isAntiAlias = true
            it.style = Paint.Style.FILL
        }
    }

    private val mResultPaint by lazy {
        Paint().also {
            it.isAntiAlias = true
            it.style = Paint.Style.STROKE
            it.strokeWidth = 2F
        }
    }

    private var mCompletePath: Path? = null
    private var mCompletePathMeasure: PathMeasure? = null
    private var mCompletePercent = 0f
    private val mCompleteAnimal: ValueAnimator by lazy {
        ValueAnimator.ofFloat(0f, 1f).also {
            it.startDelay = 200L
            it.duration = 500L
            it.interpolator = AccelerateInterpolator()
            it.addUpdateListener {
                mCompletePercent = it.animatedValue as Float
                invalidate()
            }
        }
    }

    private var mErrorPath: Path? = null
    private var mErrorPathMeasure: PathMeasure? = null
    private var mErrorPath1: Path? = null
    private var mErrorPathMeasure1: PathMeasure? = null
    private var mErrorPercent = 0f
    private val mErrorAnimal: ValueAnimator by lazy {
        ValueAnimator.ofFloat(0f, 1f).also {
            it.startDelay = 200L
            it.duration = 500L
            it.interpolator = AccelerateInterpolator()
            it.addUpdateListener {
                mErrorPercent = it.animatedValue as Float
                invalidate()
            }
        }
    }
    private var mNext = false

    private var mDefaultColor = Color.argb(255, 25, 118, 114)
    private var mSelColor = Color.parseColor("#FFC0CB")

    //失败之后的颜色
    private var mErrorCenterColor = Color.argb(255, 255, 0, 0)
    private var mErrorColor = Color.argb(255, 25, 118, 114)

    //成功的颜色
    private var mCompleteCenterColor = Color.argb(255, 25, 118, 114)
    private var mCompleteColor = Color.argb(255, 25, 118, 114)
    //对号颜色
    private var mCompleterResultColor = Color.argb(255, 255, 255, 255)
    //叉号颜色
    private var mErrorResultColor = Color.argb(255, 255, 255, 255)

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
        attrs?.let {
            val array = context.obtainStyledAttributes(it, R.styleable.LinkDevView)

            DEFAULT_COUNT = array.getInteger(R.styleable.LinkDevView_linkCircleCount, DEFAULT_COUNT)
            mDefaultColor = array.getColor(R.styleable.LinkDevView_linkDefColor, mDefaultColor)
            mSelColor = array.getColor(R.styleable.LinkDevView_linkSelcolor, mSelColor)
            DEFAULT_RADIUS =
                array.getDimensionPixelSize(
                    R.styleable.LinkDevView_linkDefRadiius,
                    DEFAULT_RADIUS.toInt()
                ).toFloat()
            DEFAULT_SEL_RADIUS =
                array.getDimensionPixelSize(
                    R.styleable.LinkDevView_linkSelRadius,
                    DEFAULT_SEL_RADIUS.toInt()
                )
                    .toFloat()
            mDuration =
                array.getInteger(R.styleable.LinkDevView_duration, mDuration.toInt()).toLong()

            mErrorCenterColor =
                array.getColor(R.styleable.LinkDevView_errorCenterColor, mErrorCenterColor)
            mErrorColor = array.getColor(R.styleable.LinkDevView_errorColor, mErrorColor)
            mErrorResultColor =
                array.getColor(R.styleable.LinkDevView_linkErrorResultColor, mErrorResultColor)

            mCompleteCenterColor =
                array.getColor(R.styleable.LinkDevView_completeCenterColor, mCompleteCenterColor)
            mCompleteColor = array.getColor(R.styleable.LinkDevView_completeColor, mCompleteColor)
            mCompleterResultColor =
                array.getColor(
                    R.styleable.LinkDevView_linkCompleteResultColor,
                    mCompleterResultColor
                )

            array.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = if (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY) {
            MeasureSpec.makeMeasureSpec(
                (DEFAULT_SEL_RADIUS * 2 * (DEFAULT_COUNT * 2 - 1)).toInt(), MeasureSpec.EXACTLY
            )
        } else widthMeasureSpec
        val height = if (MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY) {
            MeasureSpec.makeMeasureSpec(
                (DEFAULT_SEL_RADIUS * 2).toInt(), MeasureSpec.EXACTLY
            )
        } else heightMeasureSpec
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mErrorPath = Path().also {
            it.moveTo(
                width / 2 - 0.5f * DEFAULT_SEL_RADIUS,
                height * 1.0F / 2 - 0.5f * DEFAULT_SEL_RADIUS
            )
            it.lineTo(
                width / 2 + 0.5f * DEFAULT_SEL_RADIUS,
                height * 1.0F / 2 + 0.5f * DEFAULT_SEL_RADIUS
            )
        }
        mErrorPathMeasure = PathMeasure(mErrorPath, false)
        mErrorPath1 = Path().also {
            it.moveTo(
                width / 2 + 0.5f * DEFAULT_SEL_RADIUS,
                height * 1.0F / 2 - 0.5f * DEFAULT_SEL_RADIUS
            )
            it.lineTo(
                width / 2 - 0.5f * DEFAULT_SEL_RADIUS,
                height * 1.0F / 2 + 0.5f * DEFAULT_SEL_RADIUS
            )
        }
        mErrorPathMeasure1 = PathMeasure(mErrorPath1, false)

        mCompletePath = Path().also {
            it.moveTo(width / 2 - 0.5f * DEFAULT_SEL_RADIUS, height * 1.0f / 2)
            it.lineTo(width / 2 - 0.1f * DEFAULT_SEL_RADIUS, height / 2 + 0.3f * DEFAULT_SEL_RADIUS)
            it.lineTo(width / 2 + 0.5f * DEFAULT_SEL_RADIUS, height / 2 - 0.3f * DEFAULT_SEL_RADIUS)
        }
        mCompletePathMeasure = PathMeasure(mCompletePath, false)
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            drawLinkCircle(it)
        }
    }

    override fun onDetachedFromWindow() {
        stopLink()
        mCompleteAnimal.cancel()
        mErrorAnimal.cancel()
        super.onDetachedFromWindow()
    }

    private fun drawLinkCircle(canvas: Canvas) {
        val center = DEFAULT_COUNT / 2
        for (i in center downTo 0) {
            if (i == center) {
                drawCenterCircle(canvas, center)
            } else {
                drawElseCircle(canvas, center, i)
            }
        }
    }

    private fun drawElseCircle(canvas: Canvas, center: Int, i: Int) {
        val lx = width / 2 - (center - i) * DEFAULT_SEL_RADIUS * 4
        mPaint.color = when {
            mResult == Link.error -> mErrorColor
            mResult == Link.complete -> mCompleteColor
            mCurrentSelIndex % DEFAULT_COUNT == i -> mSelColor
            else -> mDefaultColor
        }
//            if (mCurrentSelIndex % DEFAULT_COUNT == i) mSelColor else mDefaultColor
        canvas.drawCircle(
            lx,
            (height / 2).toFloat(),
            if (mCurrentSelIndex % DEFAULT_COUNT == i) DEFAULT_SEL_RADIUS else DEFAULT_RADIUS,
            mPaint
        )

        val rx = width / 2 + (center - i) * DEFAULT_SEL_RADIUS * 4
        mPaint.color = when {
            mResult == Link.error -> mErrorColor
            mResult == Link.complete -> mCompleteColor
            mCurrentSelIndex % DEFAULT_COUNT == center * 2 - i -> mSelColor
            else -> mDefaultColor
        }
        canvas.drawCircle(
            rx,
            (height / 2).toFloat(),
            if (mCurrentSelIndex % DEFAULT_COUNT == center * 2 - i) DEFAULT_SEL_RADIUS else DEFAULT_RADIUS,
            mPaint
        )
    }

    private fun drawCenterCircle(canvas: Canvas, center: Int) {
        when (mResult) {
            Link.linking -> {
                mPaint.color =
                    if (mCurrentSelIndex % DEFAULT_COUNT == center) mSelColor else mDefaultColor
                canvas.drawCircle(
                    (width / 2).toFloat(),
                    (height / 2).toFloat(),
                    if (mCurrentSelIndex % DEFAULT_COUNT == center) DEFAULT_SEL_RADIUS else DEFAULT_RADIUS,
                    mPaint
                )
            }
            Link.error -> {
                mPaint.color = mErrorCenterColor
                canvas.drawCircle(
                    (width / 2).toFloat(),
                    (height / 2).toFloat(),
                    DEFAULT_SEL_RADIUS,
                    mPaint
                )
                mResultPaint.color = mErrorResultColor
                val path = Path()
                mErrorPathMeasure?.let {
                    it.getSegment(0F, it.length * mErrorPercent, path, true)
                }
                val path1 = Path()
                mErrorPathMeasure1?.let {
                    it.getSegment(0F, it.length * mErrorPercent, path1, true)
                }

                canvas.drawPath(path, mResultPaint)
                canvas.drawPath(path1, mResultPaint)
//                canvas.drawPath(mErrorPath1, mResultPaint)
            }
            Link.complete -> {
                mPaint.color = mCompleteCenterColor
                canvas.drawCircle(
                    (width / 2).toFloat(),
                    (height / 2).toFloat(),
                    DEFAULT_SEL_RADIUS,
                    mPaint
                )
                mResultPaint.color = mCompleterResultColor
                val path = Path()
                mCompletePathMeasure?.let {
                    it.getSegment(0F, it.length * mCompletePercent, path, true)
                }
//                canvas.drawPath(mCompletePath, mResultPaint)
                canvas.drawPath(path, mResultPaint)
            }
        }
    }


    fun startLinck() {
        stopLink()
        sJob = GlobalScope.launch(Dispatchers.IO) {
            startLinkAnim()
        }
    }

    fun stopLink() {
        mCompletePercent = 0F
        mNext = false
        mErrorPercent = 0F
        mCurrentSelIndex = -1
        sJob?.cancel()
        sJob = null
    }

    fun setLinkResult(result: Link) {
        mResult = result
        if (mResult != Link.linking) {
            stopLink()
        }
        when (mResult) {
            Link.linking -> invalidate()
            Link.complete -> {

                mErrorAnimal.cancel()
                if (!mCompleteAnimal.isRunning) {
                    mCompletePercent = 0F
                    mCompleteAnimal.start()
                }
            }
            Link.error -> {
                mCompleteAnimal.cancel()
                if (!mErrorAnimal.isRunning) {
                    mNext = false
                    mErrorPercent = 0F
                    mErrorAnimal.start()
                }
            }
        }

    }

    private suspend fun startLinkAnim() {
        withContext(Dispatchers.Main) {
            mCurrentSelIndex++
            invalidate()
        }
        delay(mDuration)
        startLinkAnim()
    }
}