package com.wyj.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View

/**
 * 作者：王颜军 on 2020/7/29 10:59
 * 邮箱：3183424727@qq.com
 */
class RingProgress : View {

    private val DEFAULT_SIZE = 200f

    private var Angle = 270

    private val paint by lazy {
        Paint().apply {
            isAntiAlias = true
            strokeWidth = 20f
            style = Paint.Style.STROKE
            color = Color.parseColor("#DDDDDD")
        }
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

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val space = (360 - Angle)/2f


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val c = 20f/2
            canvas?.drawArc(c,c,width - c,height-c,90+space,Angle.toFloat(),false,paint)
        }
    }
}