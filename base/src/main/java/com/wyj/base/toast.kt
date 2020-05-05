package com.wyj.base

import android.content.Context
import androidx.annotation.IdRes
import com.hjq.toast.ToastUtils

//toast弹窗
fun Context.toast(content: String) {
    ToastUtils.show(content)
}

fun Context.toast(@IdRes id: Int) {
    ToastUtils.show(id)
}

fun Context.toast() {
    ToastUtils.show("")
}

