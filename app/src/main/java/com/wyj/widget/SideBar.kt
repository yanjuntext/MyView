package com.wyj.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.TextView

/**
 * 作者：王颜军 on 2020/7/9 15:26
 * 邮箱：3183424727@qq.com
 */
class SideBar : View {

    private val mTextPaint by lazy {
        Paint().apply {
            isAntiAlias = true
        }
    }
    private var mTipTextView: TextView? = null
    private var singleHeight = 0f

    private val mLables by lazy {
        mutableListOf(
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
            "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"
        )
    }
    private var mChooseIndex = -1
    private var mDefaultColor = Color.rgb(23, 122, 216)
    private var mSelectColor = Color.parseColor("#c60000")

    private var onTouchingLetterChangedListener: OnTouchingLetterChangedListener? = null

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0) {
        init(context, attrs)
    }


    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        with(context.obtainStyledAttributes(attrs, R.styleable.SideBar)) {
            mDefaultColor = getColor(R.styleable.SideBar_sb_default_color, mDefaultColor)
            mSelectColor = getColor(R.styleable.SideBar_sb_select_color, mSelectColor)
            recycle()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // 获取每一个字母的高度
        singleHeight = height * 1f / mLables.size
        singleHeight = (height * 1f - singleHeight / 2) / mLables.size


        mLables.forEachIndexed { index, s ->
            mTextPaint.color = mDefaultColor
            // paint.setColor(Color.WHITE);
            mTextPaint.typeface = Typeface.DEFAULT_BOLD
            mTextPaint.isAntiAlias = true
            mTextPaint.textSize = 25f

            if (index == mChooseIndex) {
                mTextPaint.color = mSelectColor
                mTextPaint.isFakeBoldText = true
            }
            val xPos = width * 1f / 2 - mTextPaint.measureText(s) / 2f
            val yPos = singleHeight * index + singleHeight
            canvas?.drawText(s, xPos, yPos, mTextPaint)
            mTextPaint.reset()
        }

    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            val oldChoose = mChooseIndex
            // 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.
            val c: Int = (it.y / height * mLables.size).toInt()
            when (it.action) {
                MotionEvent.ACTION_CANCEL,
                MotionEvent.ACTION_UP -> {
                    mChooseIndex = -1
                    mTipTextView?.visibility = View.INVISIBLE
                    postInvalidate()
                }
                else -> {
                    if (oldChoose != c) {
                        if (c in 0 until mLables.size) {
                            onTouchingLetterChangedListener?.onTouchingLetterChanged(mLables[c])

                            mTipTextView?.apply {
                                text = mLables[c]
                                visibility = View.VISIBLE
                                x = left * 1f / 2 * 3

                                y = singleHeight * c
                            }

                            mChooseIndex = c
                            postInvalidate()
                        }
                    }
                }
            }
        }

        return true
    }


    fun setOnTouchingLetterChangedListener(onTouchingLetterChangedListener: OnTouchingLetterChangedListener?) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener
    }

    interface OnTouchingLetterChangedListener {
        fun onTouchingLetterChanged(value: String)
    }
}