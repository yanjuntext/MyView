package com.wyj.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import kotlin.math.min

/**
 *波浪球
 *@author abc
 *@time 2020/3/2 14:29
 */
class WaveBall : View {
    private val DEFAULT_SIZE = 120F

    /**波浪 画布 */
   private var mWaveBitamp: Bitmap? = null
    private var mWaveCanvas: Canvas? = null
    private val mWavePait by lazy {
        Paint().also {
            it.isAntiAlias = true
            it.color = mWaveColor
            it.style = Paint.Style.FILL
        }
    }
    private val mWavePath by lazy { Path() }
    private var mWaveColor = Color.argb(255, 89, 20, 21)
    private var mWaveCanvasPaint: Paint = Paint().also {
        it.isAntiAlias = true
//        it.color = Color.RED
        it.color = Color.argb(255, 255, 255, 255)
        it.style = Paint.Style.FILL
    }

    /**波浪高度*/
    private var mWaveHeight = 50F

    /**球画笔*/
    private val mBallPaint by lazy {
        Paint().also {
            it.isAntiAlias = true
            it.color = mBallColor
            it.style = Paint.Style.STROKE
        }
    }
    private var mBallColor = Color.argb(255, 255, 0, 0)

    private var mRectF: RectF? = null

    private var strokWidth = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        2F,
        resources.displayMetrics
    )


    private var mWavePercent: Float = 0f
    private val mWaveAnimal: ValueAnimator by lazy {
        ValueAnimator.ofFloat(0f, 1f).also {
            it.duration = 1000L
            it.repeatCount = Animation.INFINITE
            it.interpolator = AccelerateInterpolator()
            it.addUpdateListener {
                mWavePercent = it.animatedValue as Float
                invalidate()
            }
        }
    }


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
            val attr = context.obtainStyledAttributes(it, R.styleable.WaveBall)
            mBallColor = attr.getColor(R.styleable.WaveBall_ball_color, mBallColor)
            mWaveColor = attr.getColor(R.styleable.WaveBall_wave_color, mWaveColor)
            mWaveHeight =
                attr.getInt(R.styleable.WaveBall_wave_height, mWaveHeight.toInt()).toFloat()
            strokWidth =
                attr.getDimensionPixelSize(
                    R.styleable.DownProgressView_strokeWidth,
                    strokWidth.toInt()
                ).toFloat()
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
        setMeasuredDimension(min(width, height), min(width, height))
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

        mWaveBitamp = Bitmap.createBitmap(
            (width - strokWidth).toInt(),
            (height - strokWidth).toInt(), Bitmap.Config.ARGB_8888
        )
        mWaveBitamp?.let {
            mWaveCanvas = Canvas(it)
        }
        mWavePait.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

        mRectF = RectF(
            strokWidth / 2,
            strokWidth / 2,
            width.toFloat() - strokWidth / 2,
            height.toFloat() - strokWidth / 2
        )

        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas?) {

        canvas?.let {
            if (mWaveBitamp != null) it.drawBitmap(
                mWaveBitamp!!,
                strokWidth / 2,
                strokWidth / 2,
                mWaveCanvasPaint
            )
            mWaveCanvas?.drawCircle(
                (width / 2).toFloat(),
                (height / 2).toFloat(),
                (width - strokWidth) / 2,
                mWaveCanvasPaint
            )
            Log.e("WaveBall", "mWaveBitamp[${mWaveBitamp == null}]")
            drawBall(it)
            drawWave()
        }

    }

    private fun drawWave() {
        mWavePath.reset()
        val offset = mWavePercent * width
        val startx = -width + offset
        val ceny = height / 2f
        mWavePath.moveTo(startx, ceny)
        for (i in 0 until 2) {
            mWavePath.quadTo(
                -width * 3 / 4 + offset + i * width,
                ceny + mWaveHeight,
                -width / 2 + offset + i * width,
                ceny
            )
            mWavePath.quadTo(
                -width / 4 + offset + i * width,
                ceny - mWaveHeight,
                i * width + offset,
                ceny
            )
        }
        mWavePath.lineTo(width.toFloat(), height.toFloat())
        mWavePath.lineTo(0f, height.toFloat())
        mWavePath.close()
        mWaveCanvas?.drawPath(mWavePath, mWavePait)
    }

    private fun drawBall(canvas: Canvas) {
        mRectF?.let {
            canvas.drawArc(it, 0F, 360F, false, mBallPaint)
        }
    }

    override fun onDetachedFromWindow() {
        mWaveAnimal.cancel()
        super.onDetachedFromWindow()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mWaveAnimal.start()
    }

}