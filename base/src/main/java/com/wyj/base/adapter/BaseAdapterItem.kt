package com.wyj.base.adapter

/**
 *
 *@author abc
 *@time 2019/9/16 9:39
 */
interface  BaseAdapterItem<T> {
     fun getDataModel(): T
     fun getViewType(): Int
}