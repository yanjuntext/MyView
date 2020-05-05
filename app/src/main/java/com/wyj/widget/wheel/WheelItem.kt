package com.wyj.widget.wheel

/**
 *
 *@author abc
 *@time 2019/10/24 14:31
 */
data class WheelItem(private var label: String) : IWheel {
    override fun getShowText(): String = label
}