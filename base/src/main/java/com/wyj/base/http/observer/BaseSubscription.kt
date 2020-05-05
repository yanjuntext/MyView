package com.wyj.base.http.observer

import com.base.utils.MLog
import com.wyj.base.http.Constants
import com.wyj.base.http.Resource
import com.wyj.base.http.ResponseListener
import com.wyj.base.http.retrofit2.ApiException
import com.wyj.base.http.retrofit2.HttpResponse
import io.reactivex.subscribers.DisposableSubscriber
import java.net.SocketException
import java.net.UnknownHostException

open class BaseSubscription<T>() : DisposableSubscriber<HttpResponse<T>>() {
    private val TAG by lazy { BaseSubscription::class.java.simpleName }

    private var mListener: ResponseListener<Resource<T>>? = null

    constructor(mListener: ResponseListener<Resource<T>>?) : this() {
        this.mListener = mListener
    }

    override fun onComplete() {
    }

    override fun onNext(t: HttpResponse<T>?) {
        t?.let {
            when (it.code) {
                Constants.WEB_RESP_CODE_SUCCESS -> {
                    val msg = it.msg
                    if (msg == null) {
                        onSuccessed()
                    } else {
                        onSuccessed(msg)
                    }
                }
                Constants.WEB_RESP_CODE_FAILURE -> onSuccessed()
            }
        }
    }

    override fun onError(e: Throwable?) {
        e?.printStackTrace()
        when (e) {
            is ApiException -> {
                if (e.code == Constants.WEB_RESP_CODE_FAILURE) {
                    onSuccessed()
                } else {
                    onFailure(e.code)
                }
            }
            is SocketException -> onFailure(Constants.WEB_REQ_CODE_TIMEOUT)
            is UnknownHostException -> onFailure(Constants.WEB_REQ_CODE_UnknownHost)
            else -> onFailure(Constants.WEB_RESP_CODE_DEFAULT_FAILURE)
        }

        MLog.e(TAG,e)
    }

    open fun onSuccessed() {
        mListener?.onResponse(Resource.success(null))
    }
    open fun onSuccessed(t: T) {
        mListener?.onResponse(Resource.success(t))
    }
    open fun onFailure(code: Int){
        mListener?.onResponse(Resource.error(code))
    }
}