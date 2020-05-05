package com.wyj.widget.wheel

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout

/**
 *
 *@author abc
 *@time 2019/10/24 16:55
 */
class WheelView : FrameLayout, IWheelViewSetting {
    private var wheelView: WheelItemView? = null
    private var wheelMaskView: WheelMaskView? = null

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs, defStyle)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyle: Int) {
        wheelView = WheelItemView(context)
        wheelView?.init(context, attrs, defStyle)
        wheelMaskView = WheelMaskView(context)
        wheelMaskView?.init(context, attrs, defStyle)
        addView(
            wheelView,
            FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        )
        addView(
            wheelMaskView,
            FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        wheelMaskView?.let {
            val params = it.layoutParams
            params.height = wheelView?.measuredHeight ?: 0
            it.layoutParams = params
            it.updateMask(wheelView?.getShowCount() ?: 0, wheelView?.getItemHeight() ?: 0)
        }

    }

    override fun setTextSize(textSize: Float) {
        wheelView?.setTextSize(textSize)
    }

    override fun setTestColor(textColor: Int) {
        wheelView?.setTestColor(textColor)
    }

    override fun setSelectTextSize(textSize: Float) {
        wheelView?.setSelectTextSize(textSize)
    }

    override fun setSelectTextColor(textColor: Int) {
        wheelView?.setSelectTextColor(textColor)
    }

    override fun setShowCount(showCount: Int) {
        wheelView?.setShowCount(showCount)
    }

    override fun setTotalOffsetX(totalOffsetX: Int) {
        wheelView?.setTotalOffsetX(totalOffsetX)
    }

    override fun setItemVerticalSpace(itemVerticalSpace: Int) {
        wheelView?.setItemVerticalSpace(itemVerticalSpace)
    }

    override fun setItems(items: MutableList<IWheel>) {
        wheelView?.setItems(items)
    }

    override fun getSelectedIndex(): Int = wheelView?.getSelectedIndex()?:0

    override fun setSelectedIndex(targetIndexPosition: Int) {
        wheelView?.setSelectedIndex(targetIndexPosition)
    }

    override fun setSelectedIndex(targetIndexPosition: Int, withAnimation: Boolean) {
        wheelView?.setSelectedIndex(targetIndexPosition)
    }

    override fun setSelectedLabel(targerLabel: String) {
        wheelView?.setSelectedLabel(targerLabel)
    }

    override fun setSelectedLabel(targerLabel: String, withAnimation: Boolean) {
        wheelView?.setSelectedLabel(targerLabel,withAnimation)
    }

    override fun isScrolling(): Boolean  = wheelView?.isScrolling()?:false

    override fun OnSelectedListener(onSelectedListener: WheelItemView.OnSelectedListener?) {
        wheelView?.OnSelectedListener(onSelectedListener)
    }
}