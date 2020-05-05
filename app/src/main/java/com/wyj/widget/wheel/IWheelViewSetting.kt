package com.wyj.widget.wheel

import androidx.annotation.ColorInt

/**
 *
 *@author abc
 *@time 2019/10/24 14:34
 */
interface IWheelViewSetting {
    fun setTextSize(textSize: Float)
    fun setTestColor(@ColorInt textColor: Int)

    fun setSelectTextSize(textSize: Float)
    fun setSelectTextColor(@ColorInt textColor: Int)

    fun setShowCount(showCount: Int)
    fun setTotalOffsetX(totalOffsetX: Int)
    /**item数值间距*/
    fun setItemVerticalSpace(itemVerticalSpace: Int)

    fun setItems(items: MutableList<IWheel>)

    fun getSelectedIndex(): Int

    fun setSelectedIndex(targetIndexPosition: Int)

    fun setSelectedIndex(targetIndexPosition: Int, withAnimation: Boolean)

    fun setSelectedLabel(targerLabel: String)

    fun setSelectedLabel(targerLabel: String, withAnimation: Boolean)

    fun isScrolling(): Boolean

    fun OnSelectedListener(onSelectedListener: WheelItemView.OnSelectedListener?)
}