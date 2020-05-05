package com.wyj.widget
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

/**
 *
 *@author abc
 *@time 2019/9/12 9:41
 */
class ScaleImageView : AppCompatImageView {

    var scaleRatio: Float = 1.2f

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        attrs?.let {
            val array = context.obtainStyledAttributes(it, R.styleable.ScaleImageView)
            if (array.hasValue(R.styleable.ScaleImageView_scaleRatio)) {
                scaleRatio = array.getFloat(R.styleable.ScaleImageView_scaleRatio, scaleRatio)
            }
            array.recycle()
        }
    }


    override fun setPressed(pressed: Boolean) {
        super.setPressed(pressed)
        if (pressed) {
            scaleX = scaleRatio
            scaleY = scaleRatio
        } else {
            scaleX = 1.0f
            scaleY = 1.0f
        }
    }


}