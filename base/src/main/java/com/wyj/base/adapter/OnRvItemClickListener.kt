package com.wyj.base.adapter

/**
 *
 *@author abc
 *@time 2019/9/16 9:54
 */
interface OnRvItemClickListener<T> {
    fun onRvItemClick(data: T?, position: Int)
}