package com.wyj.base.http.retrofit2

class HttpResponse<T>(
    var cmd: Int?,
    var code: Int?,
    var msg: T?
)