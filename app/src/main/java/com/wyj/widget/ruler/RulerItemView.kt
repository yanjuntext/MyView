package com.wyj.widget.ruler

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.wyj.base.log

/**
 * 作者：王颜军 on 2020/8/15 08:55
 * 邮箱：3183424727@qq.com
 */
class RulerItemView : View {

    private val timeSpace = 60 * 60 * 2

    private val margin = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        10f,
        resources.displayMetrics
    )

    private val mTimeLineWidth by lazy {
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            10f,
            resources.displayMetrics
        )
    }

    private val mRulerTextPaint by lazy {
        TextPaint().apply {
            isAntiAlias = true
            textSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                10f,
                resources.displayMetrics
            )
            color = Color.parseColor("#DDDDDD")
        }
    }

    private val mTimeLinePaint by lazy {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = Color.parseColor("#DDDDDD")
        }
    }

    private val lineWidth = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        1f,
        resources.displayMetrics
    )
    private val mRulerPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = Color.parseColor("#DDDDDD")
            strokeWidth = lineWidth
        }
    }

    private val mTimeLineRectF by lazy { RectF() }

    private val mTextWidth by lazy { getTextWidth("24:00", mRulerTextPaint) }

    private val mTextHeight by lazy { getTextHeight("24:00", mRulerTextPaint) }

    var startTime = -1L
        set(value) {
            field = value
            endTime = field + timeSpace
        }
    private var endTime = 0L


    var ruleEnum = RulerEnum.HALF_HOUR

    var lineColor = Color.parseColor("#DDDDDD")

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
    }




    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {
            drawTimeLineRect(it)
            drawRulerText(canvas)
        }

    }


    private fun drawTimeLineRect(canvas: Canvas) {
        val left = margin * 2 + mTextWidth
        val top = 0f
        val right = left + mTimeLineWidth
        val bottom = height.toFloat()
        mTimeLinePaint.color = lineColor
        mTimeLineRectF.set(left, top, right, bottom)
        canvas.drawRect(mTimeLineRectF, mTimeLinePaint)
    }

    private fun drawRulerText(canvas: Canvas) {
        if (startTime < 0) return

        val sen: Int = DateUtil.getSecond(startTime * 1000)
        val rTime = startTime - sen

        val total = (endTime - rTime) / 60

        val h = sen / (timeSpace * 1.0f / height)

        val spaceH = 60f / (timeSpace * 1.0f / height)

        val sDay = DateUtil.getDay(startTime * 1000)

        (1..total).forEachIndexed { index, _ ->

            val start = startTime - sen - 60 * index
            val rminute = DateUtil.getMinute(start * 1000)

            val time = DateUtil.formatHourMinute(start * 1000)

            val cDay = DateUtil.getDay(start* 1000)
            val tH = getTextHeight(time,mRulerTextPaint)
            when (ruleEnum) {

                RulerEnum.ONE_MINUTER -> {
                    if (sDay == cDay) {
                        canvas.drawText(
                            time,
                            margin,
                            h + spaceH * index - tH/2f,
                            mRulerTextPaint
                        )
                        canvas.drawLine(margin + margin / 2 + mTextWidth,

                            h + spaceH * index - mTimeLineWidth / 2f,
                            margin * 2 + mTextWidth,
                            h + spaceH * index - mTimeLineWidth / 2f,
                            mRulerPaint
                        )
                    }
                }
                RulerEnum.TEN_MINUTER -> {
                    if (sDay == cDay) {
                        if (rminute % 10 == 0) {
                            canvas.drawLine(
                                margin + margin / 2 + mTextWidth,
                                h + spaceH * index - mTimeLineWidth / 2f,
                                margin * 2 + mTextWidth,
                                h + spaceH * index - mTimeLineWidth / 2f,
                                mRulerPaint
                            )
                        }


                        if (rminute % 10 == 0) {
                            canvas.drawText(
                                time,
                                margin,
                                h + spaceH * index,
                                mRulerTextPaint
                            )

                        }
                    }
                }
                RulerEnum.HALF_HOUR -> {
                    if (sDay == cDay) {
                        if (rminute % 10 == 0) {
                            canvas.drawLine(
                                if (rminute % 30 == 0) margin + margin / 2 + mTextWidth else margin + margin * 2 / 3 + mTextWidth,
                                h + spaceH * index - mTimeLineWidth / 2f,
                                margin * 2 + mTextWidth,
                                h + spaceH * index - mTimeLineWidth / 2f,
                                mRulerPaint
                            )
                        }



                        if (rminute % 30 == 0) {

                            canvas.drawText(
                                time,
                                margin,
                                h + spaceH * index,
                                mRulerTextPaint
                            )


                        }
                    }

                }
                RulerEnum.HOUR -> {
                    if (sDay == cDay) {
                        if (rminute % 10 == 0) {
                            canvas.drawLine(
                                if (rminute % 60 == 0) margin + margin / 2 + mTextWidth else margin + margin * 2 / 3 + mTextWidth,
                                h + spaceH * index - mTimeLineWidth / 2f,
                                margin * 2 + mTextWidth,
                                h + spaceH * index - mTimeLineWidth / 2f,
                                mRulerPaint
                            )
                        }

                        if (rminute % 60 == 0) {

                            canvas.drawText(
                                time,
                                margin,
                                h + spaceH * index,
                                mRulerTextPaint
                            )


                        }
                    }
                }
                else -> {
                    if (sDay == cDay) {
                        val hour = DateUtil.getHour(start * 1000)

                        if (rminute == 0) {
                            canvas.drawLine(
                                if (hour % 2 == 0) margin + margin / 2 + mTextWidth else margin + margin * 2 / 3 + mTextWidth,
                                h + spaceH * index - mTimeLineWidth / 2f,
                                margin * 2 + mTextWidth,
                                h + spaceH * index - mTimeLineWidth / 2f,
                                mRulerPaint
                            )

                        }

                        if (hour % 2 == 0 && rminute == 0) {

                            canvas.drawText(
                                time,
                                margin,
                                h + spaceH * index,
                                mRulerTextPaint
                            )


                        }
                    }
                }
            }


        }
    }

    private fun getTextWidth(text: String, paint: TextPaint) = paint.measureText(text)
    private fun getTextHeight(text: String, paint: TextPaint): Float {
        val rect = Rect()
        paint.getTextBounds(text, 0, text.length, rect)
        return rect.height().toFloat()
    }

    private fun getTextDistance( paint: TextPaint): Float {
        val fontMetrics = paint.fontMetrics
        return (fontMetrics.bottom - fontMetrics.top) * 1.0F / 2 - fontMetrics.bottom
    }
}