package com.base.utils.image

import android.content.Context
import android.graphics.drawable.Drawable

/**
 *
 *@author abc
 *@time 2019/9/20 10:05
 */
interface ImageFactory<T : ImageStrategy> {

    /**
     * 创建一个图片加载策略
     */
    fun createImageStrategy(): T

    /**
     * 创建加载占位图
     */
    fun createPlaceholder(context: Context): Drawable?

    /**
     * 创建加载错误占位图
     */
    fun createError(context: Context): Drawable?

    /**
     * 清除内存缓存
     */
    fun clearMemoryCache(contex: Context)

    /**
     * 清除磁盘缓存
     */
    fun clearDiskCache(context: Context)

    /**
     * 获取缓存大小
     */
    fun getCacheSize(contex: Context): Double

    /**
     * 设置base url
     */
    fun setBaseUrl(baseUrl: String): ImageFactory<T>

    /**
     * 获取base url
     */
    fun getBaseUrl():String
}