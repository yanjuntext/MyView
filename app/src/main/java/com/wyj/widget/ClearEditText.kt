package com.wyj.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import android.widget.EditText
import androidx.core.content.ContextCompat

/**
 *
 *@author abc
 *@time 2019/9/5 14:36
 */
class ClearEditText : EditText, View.OnFocusChangeListener, TextWatcher {
    private val TAG by lazy { ClearEditText::class.java.simpleName }
    private var hasFoucs = false
    private var mClearDrawable: Drawable? = null

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, android.R.attr.editTextStyle)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)


    init{
        Log.i(TAG, "init,${mClearDrawable == null}")
        mClearDrawable = compoundDrawables[2]
        if (mClearDrawable == null) {
            // throw new
            // NullPointerException("You can add drawableRight attribute in XML");
            mClearDrawable = ContextCompat.getDrawable(context,R.drawable.ic_input_delete)
            //			mClearDrawable = getResources().getDrawable(R.drawable.id_del_edit_text);

        }

        mClearDrawable?.let {
            it.setBounds(
                0, 0, it.intrinsicWidth, it.intrinsicHeight
            )
        }
        setClearIconVisible(false)

        onFocusChangeListener = this

        addTextChangedListener(this)
    }

    private fun setClearIconVisible(visible: Boolean) {
        Log.i(TAG, "$visible,${mClearDrawable == null}")
        val right = if (visible) mClearDrawable else null
        setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], right, compoundDrawables[3])
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            if (compoundDrawables[2] != null) {

                val touchable = event.x > (width - totalPaddingRight)
                        && (event.x < ((width - paddingRight)))

                if (touchable) {
                    this.setText("")
                }
            }
        }


        return super.onTouchEvent(event)
    }


    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        Log.i(TAG,"hasFoucs = $hasFoucs")
        this.hasFoucs = hasFocus
        if (hasFocus) {
            setClearIconVisible(text.isNotEmpty())
        } else {
            setClearIconVisible(false)
        }
    }

    override fun onTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        if (hasFoucs) {
            setClearIconVisible((s.toString().isNotEmpty()))
        }

    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    /**
     * 设置晃动动画
     */
    private fun setShakeAnimation() {
        this.animation = shakeAnimation(5)
    }


    /**
     * 晃动动画
     * @param counts 1秒钟晃动多少下
     * @return
     */
    private fun shakeAnimation(counts: Int): Animation {
        val translateAnimation = TranslateAnimation(0f, 10f, 0f, 0f)
        translateAnimation.interpolator = CycleInterpolator(counts.toFloat())
        translateAnimation.duration = 1000
        return translateAnimation
    }


}