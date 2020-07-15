package com.wyj.widget

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import kotlin.random.Random

class AudioView : View {

    private val mHandler by lazy {
        object : MyHandler(context as Activity, Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                val activity = activityWeakReference.get()
                activity?.let {
                    when (msg.what) {
                        1 -> {

                            if (System.currentTimeMillis() - startTime > 5 * 1000) {
                                startTime = System.currentTimeMillis()
                                count += 10
                                if (count > 35) count = 35
                                mOnAddCountListener?.onAdded()
                            }

                            isStarting = true
                            this@AudioView.postInvalidate()
                            sendEmptyMessageDelayed(1, 500L)
                        }
                        else -> {
                        }
                    }
                }
            }
        }
    }

    private var count = 25

    private var startTime = 0L

    private var mWidth = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        2F,
        resources.displayMetrics
    )

    private var mMaxHeight = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        35F,
        resources.displayMetrics
    )

    private var mCurrentHeight = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        10F,
        resources.displayMetrics
    )

    private var mMinHeight = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        5F,
        resources.displayMetrics
    )

    private val mRectF by lazy {
        RectF()
    }

    private val mPain by lazy {
        Paint().apply {
            style = Paint.Style.FILL
            color = Color.BLUE

        }
    }

    private var isStarting = false

    var mOnAddCountListener:OnAddCountListener? = null

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            drawView(it)
        }
    }

    private fun drawView(canvas: Canvas) {
        val centerY = height / 2f
        val centerX = width / 2f
        val center = count / 2 + count % 2
        for (i in center downTo 0) {
            val height =
                if (isStarting) (mMaxHeight - mMinHeight) * Random.nextFloat() + mMinHeight else mCurrentHeight

            val x = (center - i) * 2 * mWidth //离中心点的距离

            mRectF.set(
                centerX - x - mWidth / 2f,
                centerY - height / 2f,
                centerX - x + mWidth / 2f,
                centerY + height / 2f
            )

            canvas.drawRect(mRectF, mPain)

            mRectF.set(
                centerX + x - mWidth / 2f,
                centerY - height / 2f,
                centerX + x + mWidth / 2f,
                centerY + height / 2f
            )

            canvas.drawRect(mRectF, mPain)

        }
    }


    fun start() {
        startTime = System.currentTimeMillis()
        mHandler.removeCallbacksAndMessages(null)
        mHandler.sendEmptyMessage(1)

    }


    override fun onDetachedFromWindow() {
        isStarting = false
        mHandler.removeCallbacksAndMessages(null)
        super.onDetachedFromWindow()
    }


    interface OnAddCountListener{
        fun onAdded()
    }

}