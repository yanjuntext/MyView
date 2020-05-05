package com.wyj.widget.wheel

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.ColorInt
import com.wyj.widget.R

/**
 * 分割线
 *@author abc
 *@time 2019/10/24 14:42
 */
class WheelMaskView : View {
    private val TAG by lazy { WheelMaskView::class.java.simpleName }
    private val mPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private var lineColor: Int = Color.BLUE
    private var lineTop: Int = 0
    private var lineBottom = 0

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs, defStyle)
    }

    fun init(context: Context, attrs: AttributeSet?, defStyle: Int) {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.WheelMaskView, defStyle, 0)
            lineColor = typedArray.getColor(R.styleable.WheelMaskView_wheelMaskLineColor, -0x70ffff01)
            typedArray.recycle()
        }
        mPaint.color = lineColor
        mPaint.strokeWidth = 1f
    }

    fun updateMask(heightCount: Int, itemHeight: Int) {
        if (heightCount > 0) {
            val centerIndex = heightCount / 2
            lineTop = centerIndex * itemHeight
            lineBottom = lineTop + itemHeight
        } else {
            lineTop = 0
            lineBottom = 0
        }
        invalidate()
    }

    fun setLineColor(@ColorInt lineColor: Int) {
        this.lineColor = lineColor
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        Log.d(TAG,"width[$width,$measuredWidth],height[$height,$measuredHeight],top[$lineTop],bottom[$bottom]")
        canvas?.let {
            mPaint.color = lineColor
            it.drawLine(0f, lineTop.toFloat(), width.toFloat(), lineTop.toFloat(), mPaint)
            it.drawLine(0f, lineBottom.toFloat(), width.toFloat(), lineBottom.toFloat(), mPaint)
        }
    }
}