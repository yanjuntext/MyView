package com.base.utils

import android.util.Log
import android.view.View
import com.base.utils.ClickDelay.Companion.SPACE_TIME
import com.base.utils.ClickDelay.Companion.hash
import com.base.utils.ClickDelay.Companion.lastClickTime
import kotlin.math.log


/**
 * 点击防抖
 *@author abc
 *@time 2019/9/4 14:55
 */

class ClickDelay {
    companion object {
        var hash: Int = 0
        var lastClickTime: Long = 0
        var SPACE_TIME: Long = 500

        fun init(delayTime: Long) {
            SPACE_TIME = delayTime
        }
    }
}

infix fun View.clickDelay(clickAction: () -> Unit) {
    this.setOnClickListener {
        MLog.i("clickDelay","clickDelay")
        if (this.hashCode() != hash) {
            hash = this.hashCode()
            lastClickTime = System.currentTimeMillis()
            clickAction()
        } else {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime > SPACE_TIME) {
                lastClickTime = System.currentTimeMillis()
                clickAction()
            }
        }
    }
}