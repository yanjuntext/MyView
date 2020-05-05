package com.wyj.base.adapter

interface DiffContentsTheSame<T> {
    fun areContentsTheSame(oldData: T?, newData: T?):Boolean
}