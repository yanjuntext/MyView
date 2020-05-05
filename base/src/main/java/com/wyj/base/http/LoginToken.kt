package com.wyj.base.http

object LoginToken {
    var mLogined: Boolean = false
    var mRandom: String? = null
    var mUser: String? = null
    var mPlatform: String? = null


    /**是否需要重启*/
    fun isRestart() =
        mLogined && (mRandom.isNullOrEmpty() || mUser.isNullOrEmpty() || mPlatform.isNullOrEmpty())

    fun release(){
        mLogined = false
        mRandom = null
        mUser = null
        mPlatform = null
    }
}