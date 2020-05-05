package com.wyj.base.http.observer

import com.base.utils.MLog
import com.wyj.base.http.Constants
import com.wyj.base.http.Resource
import com.wyj.base.http.ResponseListener
import com.wyj.base.http.retrofit2.ApiException
import com.wyj.base.http.retrofit2.HttpResponse
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.net.SocketException
import java.net.UnknownHostException

abstract class BaseObserver<T> : Observer<HttpResponse<T>> {
    private val TAG by lazy { BaseObserver::class.java.simpleName }

    private var mListener: ResponseListener<Resource<T>>? = null

    constructor(mListener: ResponseListener<Resource<T>>?) {
        this.mListener = mListener
    }


    override fun onComplete() {
    }

    override fun onSubscribe(d: Disposable) {

    }

    override fun onNext(response: HttpResponse<T>) {
        when (response.code) {
            Constants.WEB_RESP_CODE_SUCCESS -> {
                val msg = response.msg
                if (msg == null) {
                    onSuccessed()
                } else {
                    onSuccessed(msg)
                }
            }
            Constants.WEB_RESP_CODE_FAILURE -> onSuccessed()
        }
    }

    override fun onError(e: Throwable) {
        e.printStackTrace()
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

    open fun onFailure(code: Int) {
        MLog.e(TAG,"onFailure[$code]")
        mListener?.onResponse(Resource.error(code))
    }
}