package com.wyj.widget.livedata

import androidx.lifecycle.MutableLiveData

/**
 * 作者：王颜军 on 2020/7/15 16:50
 * 邮箱：3183424727@qq.com
 */
object Test {
    val testData = MutableLiveData<MutableList<String>>()

    fun setData(list: MutableList<String>) {
        testData.value = list
    }

}