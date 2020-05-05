package com.wyj.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateInterpolator

/**
 *
 *@author abc
 *@time 2020/1/17 10:51
 */
class DownProgressView : View {
    enum class Down {
        downing, complete, error
    }

    private var MAX_PROGRESS = 100
    private val DEFAULT_SIZE = 120F
    private var sProgress: Int = 0
    private var strokWidth = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        2F,
        resources.displayMetrics
    )

    private var mRectF: RectF? = null

    private var mDefCircleColor = Color.argb(255, 173, 216, 230)
    private var mCompleteCircleColor = Color.argb(255, 173, 216, 230)
    private var mErrorCircleColor = Color.argb(255, 173, 216, 230)

    private var mDefCircleStyle: Int = 1
    private var mComCircleStyle: Int = 1
    private var mErrorCircleStyle: Int = 1
    private var mCirclePaint: Paint = Paint().also {
        it.isAntiAlias = true
        it.strokeWidth = strokWidth
        it.color = mDefCircleColor
        it.style = Paint.Style.STROKE
    }

    private var mProgressColor = Color.argb(255, 255, 215, 0)
    private var mProgressPaint: Paint = Paint().also {
        it.isAntiAlias = true
        it.strokeWidth = strokWidth
        it.color = mProgressColor
        it.style = Paint.Style.STROKE
    }

    private var mProgressTextColor = Color.argb(255, 255, 215, 0)
    private var mTextSize = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        14F,
        resources.displayMetrics
    )
    private var mTextPaint: TextPaint = TextPaint().also {
        it.color = mProgressTextColor
        it.textSize = mTextSize
        it.isAntiAlias = true
        it.textAlign = Paint.Align.CENTER
    }

    private var mErrorResultColor = Color.argb(255, 255, 215, 0)
    private var mCompleterResultColor = Color.argb(255, 255, 215, 0)
    private var mResultPaint: Paint = Paint().also {
        it.isAntiAlias = true
        it.strokeWidth = strokWidth
        it.color = mCompleterResultColor
        it.style = Paint.Style.STROKE
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

    private var mErrorPathMeasure: PathMeasure? = null
    private var mErrorPath: Path? = null
    private var mErrorPathMeasure2: PathMeasure? = null
    private var mErrorpath2: Path? = null

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

    private var sResult: Down = Down.downing

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0) {
        initAttr(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initAttr(context, attrs)
    }

    private fun initAttr(context: Context, attrs: AttributeSet?) {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        attrs?.let {
            val attr = context.obtainStyledAttributes(it, R.styleable.DownProgressView)

            strokWidth =
                attr.getDimensionPixelSize(
                    R.styleable.DownProgressView_strokeWidth,
                    strokWidth.toInt()
                ).toFloat()



            mDefCircleColor =
                attr.getColor(R.styleable.DownProgressView_defCircleColor, mDefCircleColor)
            mCompleteCircleColor = attr.getColor(
                R.styleable.DownProgressView_completeCircleColor,
                mCompleteCircleColor
            )
            mErrorCircleColor =
                attr.getColor(R.styleable.DownProgressView_errorCircleColor, mCompleteCircleColor)


            mErrorResultColor =
                attr.getColor(R.styleable.DownProgressView_errorResultColor, mErrorResultColor)
            mCompleterResultColor = attr.getColor(
                R.styleable.DownProgressView_completeResultColor,
                mCompleterResultColor
            )

            mDefCircleStyle =
                attr.getInt(R.styleable.DownProgressView_defCirclrStyle, mDefCircleColor)
            mComCircleStyle =
                attr.getInt(R.styleable.DownProgressView_completeCirclrStyle, mComCircleStyle)
            mErrorCircleStyle =
                attr.getInt(R.styleable.DownProgressView_errorCirclrStyle, mErrorCircleStyle)

            MAX_PROGRESS = attr.getInteger(R.styleable.DownProgressView_maxProgress, MAX_PROGRESS)
            sProgress = attr.getInteger(R.styleable.DownProgressView_downProgress, sProgress)
            if (sProgress > MAX_PROGRESS) sProgress = 0
            mTextSize =
                attr.getDimensionPixelSize(
                    R.styleable.DownProgressView_progessSize,
                    mTextSize.toInt()
                ).toFloat()
            mProgressTextColor =
                attr.getColor(R.styleable.DownProgressView_progressTextColor, mProgressTextColor)
            mProgressColor =
                attr.getColor(R.styleable.DownProgressView_progressColor, mProgressColor)

            attr.recycle()
        }
    }

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
        mRectF = RectF(
            0f + strokWidth / 2,
            0f + strokWidth / 2,
            width.toFloat() - strokWidth / 2,
            height.toFloat() - strokWidth / 2
        )
        //completed
        mCompletePath = Path().also {
            it.moveTo(width / 2 * 0.5f, height * 1.0f / 2)
            it.lineTo(width / 2 * 0.9f, height / 2 + 0.3f * width / 2)
            it.lineTo(1.5f * width / 2, height / 2 - 0.3f * width / 2)
        }
        mCompletePathMeasure = PathMeasure(mCompletePath, false)

        mErrorPath = Path().also {
            it.moveTo(0.7f * width / 2, height / 2 - 0.3f * width / 2)
            it.lineTo(1.3f * width / 2, height / 2 + 0.3f * width / 2)
        }
        mErrorPathMeasure = PathMeasure(mErrorPath, false)

        mErrorpath2 = Path().also {
            it.moveTo(1.3f * width / 2, height / 2 - 0.3f * width / 2)
            it.lineTo(0.7f * width / 2, height / 2 + 0.3f * width / 2)
        }
        mErrorPathMeasure2 = PathMeasure(mErrorpath2, false)

        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            drawCircle(it)
            drawLoadProgress(it)
            drawProgress(it)
            drawCompleted(it)
            drawError(it)
        }
    }

    override fun onDetachedFromWindow() {
        mCompleteAnimal.cancel()
        mErrorAnimal.cancel()
        super.onDetachedFromWindow()
    }

    private fun drawCircle(canvas: Canvas) {
        mRectF?.let {
            val mcomStyle = if (mComCircleStyle == 0) Paint.Style.FILL else Paint.Style.STROKE
            val merrStyle = if (mErrorCircleStyle == 0) Paint.Style.FILL else Paint.Style.STROKE
            val mdefStyle = if (mDefCircleStyle == 0) Paint.Style.FILL else Paint.Style.STROKE
            mCirclePaint.also {
                when (sResult) {
                    Down.complete -> {
                        it.style = mcomStyle
                        it.color = mCompleteCircleColor
                    }
                    Down.error -> {
                        it.style = merrStyle
                        it.color = mErrorCircleColor
                    }
                    else -> {
                        it.style = mdefStyle
                        it.color = mDefCircleColor
                    }
                }
            }
            canvas.drawArc(it, 0F, 360F, false, mCirclePaint)
        }
    }

    private fun drawLoadProgress(canvas: Canvas) {
        if (sProgress <= 0 || sResult != Down.downing) return
        mRectF?.let {
            mProgressPaint.color = mProgressColor
            canvas.drawArc(it, 270F, 360F * sProgress / MAX_PROGRESS, false, mProgressPaint)
        }
    }

    private fun drawProgress(canvas: Canvas) {
        if (sProgress <= 0 || sResult != Down.downing) return
        mTextPaint.textSize = mTextSize
        mTextPaint.color = mProgressTextColor
        val text = "${(sProgress * 1.0F / MAX_PROGRESS * 100).toInt()}%"
        val fontMetrics = mTextPaint.fontMetrics
        val distance = (fontMetrics.bottom - fontMetrics.top) * 1.0F / 2 - fontMetrics.bottom
        val baseline = height / 2 + distance
        canvas.drawText(text, width * 1.0F / 2, baseline, mTextPaint)
    }

    private fun drawCompleted(canvas: Canvas) {
        if (sResult != Down.complete) return
        mResultPaint.color = mCompleterResultColor
        mCompletePathMeasure?.let {
            val path = Path()
            it.getSegment(0F, mCompletePercent * it.length, path, true)
            path.rLineTo(0f, 0f)
            canvas.drawPath(path, mResultPaint)
        }
    }

    private fun drawError(canvas: Canvas) {
        if (sResult != Down.error) return
        if (mErrorPath == null || mErrorPathMeasure == null || mErrorpath2 == null || mErrorPathMeasure2 == null) return
        mResultPaint.color = mErrorResultColor
        mErrorPathMeasure?.let {
            val path = Path()
            it.getSegment(0F, mErrorPercent * it.length, path, true)
            path.rLineTo(0f, 0f)
            canvas.drawPath(path, mResultPaint)
        }

        mErrorPathMeasure2?.let {
            val path = Path()
            it.getSegment(0F, mErrorPercent * it.length, path, true)
            path.rLineTo(0f, 0f)
            canvas.drawPath(path, mResultPaint)
        }

    }

    /**设置进度*/
    fun setProgress(progress: Int) {
        this.sProgress = progress
        invalidate()
    }

    fun setMaxProgress(max: Int) {
        this.MAX_PROGRESS = max
        invalidate()
    }

    fun setDownResult(result: Down) {
        this.sResult = result
        when (result) {
            Down.complete -> {
                mErrorAnimal.cancel()
                if (!mCompleteAnimal.isRunning) {
                    mCompleteAnimal.start()
                }
            }
            Down.error -> {
                mCompleteAnimal.cancel()
                if (!mErrorAnimal.isRunning) {
                    mErrorAnimal.start()
                }
            }
            else -> {
                invalidate()
            }
        }

    }

    fun getDownResult() = sResult
}