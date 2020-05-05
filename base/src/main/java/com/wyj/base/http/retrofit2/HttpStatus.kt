package com.wyj.base.http.retrofit2

import com.wyj.base.http.Constants

class HttpStatus(
    var cmd: Int?,
    var code: Int?
) {

    fun isCodeInvalid(): Boolean = code == null || code != Constants.WEB_RESP_CODE_SUCCESS
    override fun toString(): String {
        return "HttpStatus(cmd=$cmd, code=$code)"
    }
}