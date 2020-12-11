package com.wyj.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.min

/**
 * 作者：王颜军 on 2020/9/23 16:22
 * 邮箱：3183424727@qq.com
 */
class DottledLineArcView : View {
    /**默认大小*/
    private val DEFAULT_SIZE = 200F

    /**总刻度个数*/
    private val scaleCount = 60

    /**刻度宽度*/
    private var mScaleWidth = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        2f,
        resources.displayMetrics
    )

    /**刻度长度*/
    private var mScaleHeight = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        5f,
        resources.displayMetrics
    )

    //偏转角
    private var mSpaceRotate = 0f

    private lateinit var mScalePaint : Paint

    private var centX = 0f
    private var centY = 0f

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

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
        super.onSizeChanged(w, h, oldw, oldh)
        mSpaceRotate = 270f / scaleCount

        centX = width/2f
        centY = height/2f

        mScalePaint = Paint().apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context,R.color.colorButtonPressed)
            isAntiAlias = true
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            drawScale(it)
        }
    }

    private fun drawScale(canvas: Canvas){

        canvas.save()

        canvas.rotate(135f,centX,centY)
        (1..scaleCount).forEach {
            canvas.drawLine(centX,0f,centX,mScaleHeight,mScalePaint)
            canvas.rotate(mSpaceRotate,centX,centY)
        }

    }

}