package com.wyj.widget.livedata

import androidx.lifecycle.MutableLiveData

/**
 * 作者：王颜军 on 2020/7/15 16:41
 * 邮箱：3183424727@qq.com
 */
class TestLiveData private constructor(): MutableLiveData<MutableList<String>>() {

    companion object{
        @Volatile
        private var instance:TestLiveData? = null
        fun getInstance() = instance?: synchronized(this){
            instance?:TestLiveData().also {
                instance = it
            }
        }

    }

}