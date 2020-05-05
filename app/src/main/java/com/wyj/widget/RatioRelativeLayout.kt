package com.wyj.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

/**
 *
 *@author abc
 *@time 2019/11/6 9:59
 */
class RatioRelativeLayout : RelativeLayout {

    private var mWidthRatio = 16f
    private var mHeightRatio = 9f

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttr(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        initAttr(context, attrs)
    }

    private fun initAttr(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatioRelativeLayout)?.also {
            val ratio = it.getString(R.styleable.RatioRelativeLayout_viewGroupDimentsRatio)?:""
            try {
                when {
                    ratio.toLowerCase().startsWith("h,") && ratio.length > 2 -> {
                        ratio.substring(2).also { value ->
                            if (value.contains(":")) {
                                val split = value.split(":")
                                mWidthRatio = split[1].toFloat()
                                mHeightRatio = split[0].toFloat()
                            }
                        }
                    }
                    ratio.toLowerCase().startsWith("w,") && ratio.length > 2 -> {
                        ratio.substring(2).also { value ->
                            if (value.contains(":")) {
                                val split = value.split(":")
                                mWidthRatio = split[0].toFloat()
                                mHeightRatio = split[1].toFloat()
                            }
                        }
                    }
                    ratio.length > 2 -> {
                        if (ratio.contains(":")) {
                            val split = ratio.split(":")
                            mWidthRatio = split[0].toFloat()
                            mHeightRatio = split[1].toFloat()
                        }
                    }
                    else -> {
                        mWidthRatio = 16f
                        mHeightRatio = 9f
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        typedArray?.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(setMeasureSpec(widthMeasureSpec, heightMeasureSpec, 1), setMeasureSpec(widthMeasureSpec, heightMeasureSpec, 2))
    }

    private fun setMeasureSpec(widthMeasureSpec: Int, heightMeasureSpec: Int, type: Int): Int {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        return if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
            if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
                if (type == 1) widthMeasureSpec else heightMeasureSpec
            } else {
                if (type == 1) widthMeasureSpec else MeasureSpec.makeMeasureSpec((widthSize * mHeightRatio / mWidthRatio).toInt(), MeasureSpec.EXACTLY)
            }
        } else if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            if (type == 1) MeasureSpec.makeMeasureSpec((heightSize * mWidthRatio / mHeightRatio).toInt(), MeasureSpec.EXACTLY) else heightMeasureSpec
        } else {
            if (type == 1) widthMeasureSpec else heightMeasureSpec
        }
    }
}