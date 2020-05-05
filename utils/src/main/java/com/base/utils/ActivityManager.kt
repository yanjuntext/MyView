package com.base.utils

import android.app.Activity

/**
 *
 *@author abc
 *@time 2019/9/9 13:59
 */
object ActivityManager {
    private val activitys: MutableList<Activity> by lazy { mutableListOf<Activity>() }


    fun add(activity: Activity) = activitys.add(activity)

    fun remove(activity: Activity) = activitys.remove(activity)

    fun fihishActivity(activity: Activity) {
        remove(activity)
        activity.finish()
    }

    fun finishAll() {
        for (info in activitys) {
            info.finish()
        }
        activitys.clear()
    }


}