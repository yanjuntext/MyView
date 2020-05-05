package com.base.utils

import android.content.res.Resources

/**
 *  dp sp px 转换
 *@author abc
 *@time 2019/9/4 17:31
 */
object DisplayHelper {

    private val density by lazy { Resources.getSystem().displayMetrics.density }

    private val fontScale by lazy { Resources.getSystem().displayMetrics.scaledDensity }

    fun dp2px(dp: Float): Int = (dp * density + 0.5f).toInt()

    fun px2dp(px: Float): Int = (px / density + 0.5f).toInt()

    fun sp2px(sp: Float): Int = (sp * fontScale + 0.5f).toInt()

    fun px2sp(px: Float): Int = (px / fontScale + 0.5f).toInt()

    fun screenWidth(): Int = Resources.getSystem().displayMetrics.widthPixels

    fun screenHeight(): Int = Resources.getSystem().displayMetrics.heightPixels
}