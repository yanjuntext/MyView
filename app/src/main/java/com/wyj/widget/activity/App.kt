package com.wyj.widget.activity

import android.app.Application
import android.content.Context

/**
 * 作者：王颜军 on 2020/12/11 10:48
 * 邮箱：3183424727@qq.com
 */
class App:Application() {
    init {
        mContext = this
    }

    companion object {
        lateinit var mContext: Context
        fun getContext() = mContext
    }
}