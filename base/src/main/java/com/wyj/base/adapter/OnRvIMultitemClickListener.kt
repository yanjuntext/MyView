package com.wyj.base.adapter

interface OnRvIMultitemClickListener<T> {
    fun onRvMultiItemClick(data: T?, position: Int, type: Int)
}