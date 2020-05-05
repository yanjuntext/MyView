package com.wyj.base.http

import com.wyj.base.logE
import com.wyj.base.util.WheelServiceUtils

class Resource<T>(
    val status: Status,
    val data: T?,
    val code: Int
) {

    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, 0)
        }

        fun <T> error(code: Int): Resource<T> {
            return Resource(Status.ERROR, null, code)
        }

        fun <T> retry(): Resource<T> {
            return Resource(Status.RETRY, null, -1)
        }
    }

    fun isSuccessed(): Boolean = status == Status.SUCCESS

    fun isRetry(index: Int): Boolean {
        val b = (status == Status.RETRY) && (index + 1 < WheelServiceUtils.MAX_BASE_URL_COUNT)
        logE("$b")
        return b
    }

    override fun toString(): String {
        return "Resource(status=$status, data=$data, code=$code)"
    }


    enum class Status {
        SUCCESS, ERROR, RETRY
    }
}