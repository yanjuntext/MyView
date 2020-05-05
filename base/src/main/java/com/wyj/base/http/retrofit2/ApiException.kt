package com.wyj.base.http.retrofit2

import java.lang.RuntimeException

class ApiException(var code: Int, msg: String?) : RuntimeException(msg)