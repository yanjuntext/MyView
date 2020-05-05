package com.base.utils

import android.util.Log

/**
 *
 *@author abc
 *@time 2019/10/18 16:38
 */
object MLog {
    var DEBUG: Boolean = true

    fun init(DEBUG: Boolean) {
        this.DEBUG = DEBUG
    }

    fun getDebug():Boolean = DEBUG

    fun d(TAG: String, msg: Any?) {
        if (DEBUG) {
            when (msg) {
                is Int,
                is Long,
                is Float,
                is Double,
                is Byte -> Log.d(TAG, "$msg")
                is String -> Log.d(TAG, msg)
                is Throwable -> Log.d(TAG, msg.message)
                else -> Log.d(TAG, msg?.toString() ?: "")
            }
        }
    }

    fun w(TAG: String, msg: Any?) {
        if (DEBUG) {
            when (msg) {
                is Int,
                is Long,
                is Float,
                is Double,
                is Byte -> Log.w(TAG, "$msg")
                is String -> Log.w(TAG, msg)
                is Throwable -> Log.w(TAG, msg.message)
                else -> Log.w(TAG, msg?.toString() ?: "")
            }
        }
    }

    fun i(TAG: String, msg: Any?) {
        if (DEBUG) {
            when (msg) {
                is Int,
                is Long,
                is Float,
                is Double,
                is Byte -> Log.i(TAG, "$msg")
                is String -> Log.i(TAG, msg)
                is Throwable -> Log.i(TAG, msg.message)
                else -> Log.i(TAG, msg?.toString() ?: "")
            }
        }
    }

    fun e(TAG: String, msg: Any?) {
        if (DEBUG) {
            when (msg) {
                is Int,
                is Long,
                is Float,
                is Double,
                is Byte -> Log.e(TAG, "$msg")
                is String -> Log.e(TAG, msg)
                is Throwable -> Log.e(TAG, msg.message)
                else -> Log.e(TAG, msg?.toString() ?: "")
            }
        }
    }
}