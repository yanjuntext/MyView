package com.wyj.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

/**
 * 作者：王颜军 on 2020/7/16 11:29
 * 邮箱：3183424727@qq.com
 * 内凹底部导航栏
 */
class ConcavityTabItem : View {

    private val path by lazy { Path() }

    private var radius = -1f
    private var mRa = -1f
    private var mLeftRectF: RectF? = null

    private val mPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = Color.WHITE
//            strokeWidth = 5f

        }
    }

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

    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (radius < 0) {
            radius = height / 4f
            mRa = width / 4f
        }



        path.moveTo(0f, 0f)
////        path.lineTo(width/4f, height/2f)
//        path.quadTo(mRa, 0f, mRa, radius)
//        if (mLeftRectF == null) {
//            mLeftRectF = RectF(mRa, 0f, mRa + radius * 2, radius * 2)
//        }
//        mLeftRectF?.let {
//            path.addArc(it, 0f, 180f)
//        }
////
//        path.moveTo(width.toFloat(),0f)
//        path.quadTo(width.toFloat() - mRa,0f,width - mRa,radius)
////        path.quadTo(width.toFloat() - mRa,0f,width.toFloat(),0f)
//        path.moveTo(width.toFloat(),0f)
//

        path.cubicTo(width * 0.4f, height / 6f, 0f, height * 5f / 9, width / 2f, height * 2f / 3)
        path.cubicTo(
            width.toFloat(),
            height * 5f / 9,
            width * 0.6f,
            height / 6f,
            width.toFloat(),
            0f
        )
        path.lineTo(width.toFloat(), height.toFloat())
        path.lineTo(0f, height.toFloat())
        path.lineTo(0f, 0f)
//        path.quadTo(width / 4f, 0f, width / 4f, height / 3f)
//        path.quadTo(width / 2f, height * 2f / 3, width * 3f / 4, height / 3f)
//        path.quadTo(width * 3f / 4, 0f, width.toFloat(), 0f)
        canvas?.drawPath(path, mPaint)
    }


}