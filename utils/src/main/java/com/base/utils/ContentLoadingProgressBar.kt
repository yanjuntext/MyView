package com.base.utils

import android.graphics.PorterDuff
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.FragmentActivity

/**
 * ContentLoadingProgressBar
 *@author abc
 *@time 2019/11/25 9:23
 */

fun ContentLoadingProgressBar?.setColor(activity: FragmentActivity,colorRes: Int){
    this?.indeterminateDrawable?.setColorFilter(ContextCompat.getColor(activity,colorRes), PorterDuff.Mode.MULTIPLY)
}