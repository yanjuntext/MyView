package com.wyj.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.base.utils.MLog

class PasswordEditText:AppCompatEditText, View.OnTouchListener, View.OnFocusChangeListener,
    TextWatcher {


    private val TAG = PasswordEditText::class.java.simpleName
    private val TYPE_VISIBLE = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
    private val TYPE_INVISIBLE = InputType.TYPE_TEXT_VARIATION_PASSWORD

    private var mCurrentDrawable: Drawable? = null
    private var mVisibleDrawable: Drawable? = null
    private var mInvisibleDrawable: Drawable? = null

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, android.R.attr.editTextStyle)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)



    init {
        ContextCompat.getDrawable(context, R.drawable.ic_input_show)?.also {
            mVisibleDrawable = DrawableCompat.wrap(it).also {
                it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)
            }
        }
        ContextCompat.getDrawable(context, R.drawable.ic_input_hide)?.also {
            mInvisibleDrawable = DrawableCompat.wrap(it).also {
                it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)
            }
        }
        mCurrentDrawable = mVisibleDrawable
        setDrawableVisible(true)
//        mCurrentDrawable?.setVisible(false,false)
//        mCurrentDrawable?.setVisible(true,true)
        // 密码不可见
        addInputType(TYPE_INVISIBLE)
        setOnTouchListener(this)
        onFocusChangeListener = this
        addTextChangedListener(this)
    }

    /**
     * 添加一个输入标记
     */
    fun addInputType(type: Int) {
        inputType = inputType or type
    }

    /**
     * 移除一个输入标记
     */
    fun removeInputType(type: Int) {
        inputType = inputType and type.inv()
    }


    private fun setDrawableVisible(visible: Boolean) {
//        if (mCurrentDrawable?.isVisible == visible) {
//            return
//        }
//
////        mCurrentDrawable?.setVisible(visible, false)
//        mCurrentDrawable?.setVisible(true, false)
        val drawables = compoundDrawables
//        setCompoundDrawables(
//            drawables[0],
//            drawables[1],
//            if (visible) mCurrentDrawable else null,
//            drawables[3])
        setCompoundDrawables(
            drawables[0],
            drawables[1],
            mCurrentDrawable ,
            drawables[3])
    }

    private fun refreshDrawableStatus() {
        val drawables = compoundDrawables
        setCompoundDrawables(
            drawables[0],
            drawables[1],
            mCurrentDrawable,
            drawables[3])
    }

    override fun onTouch(v: View?, motionEvent: MotionEvent?): Boolean {
        if (motionEvent == null) return false
        val x = motionEvent.x.toInt()
        if (mCurrentDrawable?.isVisible == true && x > width - paddingRight - (mCurrentDrawable?.intrinsicWidth
                ?: 0)) {
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                if (mCurrentDrawable === mVisibleDrawable) {
                    mCurrentDrawable = mInvisibleDrawable
                    // 密码可见
                    removeInputType(TYPE_INVISIBLE)
                    addInputType(TYPE_VISIBLE)
                    refreshDrawableStatus()
                } else if (mCurrentDrawable === mInvisibleDrawable) {
                    mCurrentDrawable = mVisibleDrawable
                    // 密码不可见
                    removeInputType(TYPE_VISIBLE)
                    addInputType(TYPE_INVISIBLE)
                    refreshDrawableStatus()
                }
                val editable = text
                if (editable != null) {
                    setSelection(editable.toString().length)
                }
            }
            return true
        }
        return false
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        MLog.e(TAG,"hasFocus[$hasFocus],text[${text.toString()},[${text.isNullOrEmpty()}]]")
        if (hasFocus && !text.isNullOrEmpty()) {
            setDrawableVisible(true)
        } else {
            setDrawableVisible(false)
        }
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        if (isFocused) {
            setDrawableVisible(!text.isNullOrEmpty())
        }
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }


}