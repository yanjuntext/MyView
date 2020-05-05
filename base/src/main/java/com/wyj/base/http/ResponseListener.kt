package com.wyj.base.http

interface ResponseListener<T> {
    fun onResponse(t: T)
}