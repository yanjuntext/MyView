package com.wyj.base

import com.base.utils.MLog

fun Any.logD(content: String?) {
    MLog.d(this::class.java.simpleName, content ?: "")
}


fun Any.log(content: Any) {
    val TAG = if (this is String) this else this::class.java.simpleName
    when (content) {
        is Int -> MLog.i(TAG, content.toString())
        is Float -> MLog.i(TAG, content.toString())
        is Double -> MLog.i(TAG, content.toString())
        is String -> MLog.i(TAG, content.toString())
        else -> MLog.i(TAG, content.toString())
    }
}

fun Any.logList(data: MutableList<*>?) {
    data?.let {
        it.forEach {
            MLog.i(this::class.java.simpleName, it.toString())
        }
    }
}

fun Any.logList(tag: String, data: MutableList<*>?) {
    data?.let {
        it.forEach {
            MLog.i(tag, it.toString())
        }
    }
}


fun Any.logW(content: String?) {
    MLog.w(this::class.java.simpleName, content ?: "")
}

fun Any.logE(content: String?) {
    MLog.e(this::class.java.simpleName, content ?: "")
}

fun Any.log(TAG: String, content: String?, grade: Int) {
    when (grade) {
        0 -> MLog.d(TAG, content)
        1 -> MLog.i(TAG, content)
        2 -> MLog.w(TAG, content)
        3 -> MLog.e(TAG, content)
    }
}