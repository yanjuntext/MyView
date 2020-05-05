package com.wyj.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

/**
 *
 *@author abc
 *@time 2019/10/22 10:28
 */
class RatioImageView : AppCompatImageView {
    private var mWidthRatio = 16
    private var mHeightRatio = 9

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttr(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        initAttr(context, attrs)
    }

    private fun initAttr(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatioImageView)?.also {
            val ratio = it.getString(R.styleable.RatioImageView_imageViewdimensRatio)?:""
            try {
                when {
                    ratio.toLowerCase().startsWith("h,") && ratio.length > 2 -> {
                        ratio.substring(2).also { value ->
                            if (value.contains(":")) {
                                val split = value.split(":")
                                mWidthRatio = split[1].toInt()
                                mHeightRatio = split[0].toInt()
                            }
                        }
                    }
                    ratio.toLowerCase().startsWith("w,") && ratio.length > 2 -> {
                        ratio.substring(2).also { value ->
                            if (value.contains(":")) {
                                val split = value.split(":")
                                mWidthRatio = split[0].toInt()
                                mHeightRatio = split[1].toInt()
                            }
                        }
                    }
                    ratio.length > 2 -> {
                        if (ratio.contains(":")) {
                            val split = ratio.split(":")
                            mWidthRatio = split[0].toInt()
                            mHeightRatio = split[1].toInt()
                        }
                    }
                    else -> {
                        mWidthRatio = 16
                        mHeightRatio = 9
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        typedArray?.recycle()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        when {
            MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY -> {
                val widthSize = MeasureSpec.getSize(widthMeasureSpec)
                setMeasuredDimension(widthSize, widthSize * mHeightRatio / mWidthRatio)
            }
            MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY -> {
                val heightSize = MeasureSpec.getSize(heightMeasureSpec)
                setMeasuredDimension(heightSize * mWidthRatio / mHeightRatio, heightSize)
            }
            else -> super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}