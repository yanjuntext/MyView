package com.wyj.base

import android.view.View
import androidx.fragment.app.FragmentActivity
import com.base.utils.GlobalStatusBarUtil
import com.wyj.base.util.WheelServiceUtils

fun View?.setStatusBarHeight(activity: FragmentActivity) {
    this?.let {
        val params = it.layoutParams
        params.height = GlobalStatusBarUtil.getStatusbarHeight(activity)
        it.layoutParams = params
    }
}

//获取service url
fun Int.getUrl() = WheelServiceUtils.getBaseUrl(this)