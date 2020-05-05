package com.base.utils

import android.app.Activity
import android.view.View
import androidx.annotation.IdRes

/**
 *
 *@author abc
 *@time 2020/1/3 9:04
 */
object ViewFindUtils {

    fun <T : View> find(activity: Activity, @IdRes id: Int): T {
        return activity.window.decorView.findViewById<T>(id)
    }

}