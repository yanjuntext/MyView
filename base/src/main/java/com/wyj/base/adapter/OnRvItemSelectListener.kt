package com.wyj.base.adapter

/**
 *
 *@author abc
 *@time 2019/9/16 9:58
 */
interface OnRvItemSelectListener<T> {
    fun onRvItemSelect(data: T?, position: Int, select: Boolean)
}