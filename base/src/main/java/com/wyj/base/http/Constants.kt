package com.wyj.base.http

object Constants {
    val WEB_RESP_CODE_SUCCESS = 20000//请求成功
    val WEB_RESP_CODE_FAILURE = 0//请求成功，数据有错
    val WEB_RESP_CODE_DEFAULT_FAILURE = -1//默认失败返回参数
    val WEB_REQ_CODE_TIMEOUT = -1//网络请求超时，请检查网络
    val WEB_REQ_CODE_UnknownHost = 600//无可用网络，请检查网络
}