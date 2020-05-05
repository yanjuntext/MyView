package com.wyj.base.http.okhttp

import com.base.utils.MLog
import com.wyj.base.http.retrofit2.ApiException
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.Function
import org.reactivestreams.Publisher
import java.io.IOException
import java.util.concurrent.TimeUnit

class RxFunctionUtils {
    val TAG: String by lazy { RxFunctionUtils::class.java.simpleName }

    var mCurrentRetryCount = 0
    var mMaxRetryCount = 2
    var mWaitRetryTime = 2

    constructor()
    constructor(mMaxRetryCount: Int, mWaitRetryTime: Int) {
        this.mMaxRetryCount = mMaxRetryCount
        this.mWaitRetryTime = mWaitRetryTime
    }

    companion object {
        fun getInstance(): RxFunctionUtils = RxFunctionUtils()
        fun getInstance(mMaxRetryCount: Int, mmWaitRetryTime: Int): RxFunctionUtils =
            RxFunctionUtils(mMaxRetryCount, mmWaitRetryTime)
    }

    fun getFunction(): io.reactivex.functions.Function<Observable<Throwable>, ObservableSource<*>> {
        return Function {
            return@Function it.flatMap {throws->
                return@flatMap when (throws) {
                    is IOException -> {
                        if (mCurrentRetryCount < mMaxRetryCount) {
                            // 记录重试次数
                            mCurrentRetryCount++
                            MLog.i(TAG, "重试次数 = $mCurrentRetryCount")
                            mWaitRetryTime = 200
                            MLog.i(TAG, "等待时间 =$mWaitRetryTime")
                            Observable.just(1).delay(mWaitRetryTime.toLong(), TimeUnit.MILLISECONDS)
                        } else {
                            Observable.error<ApiException>(ApiException(-1, ""))
                        }
                    }
                    is ApiException->{
                        Observable.error<ApiException>(throws)
                    }
                    else-> Observable.error<ApiException>(ApiException(-1, ""))
                }
            }
        }

    }

    fun getFlowableFunction(): io.reactivex.functions.Function<Flowable<Throwable>, Publisher<*>> {
        return Function {
            return@Function it.flatMap { throws ->
                return@flatMap when (throws) {
                    is ApiException -> {
                        Flowable.error<ApiException>(throws)
                    }
                    is IOException -> {
                        if (mCurrentRetryCount < mMaxRetryCount) {
                            // 记录重试次数
                            mCurrentRetryCount++
                            MLog.i(TAG, "重试次数 = $mCurrentRetryCount")
                            mWaitRetryTime = 200
                            MLog.i(TAG, "等待时间 =$mWaitRetryTime")
                            Flowable.just(1).delay(mWaitRetryTime.toLong(), TimeUnit.MILLISECONDS)
                        } else {
                            Flowable.error<ApiException>(ApiException(-1, ""))
                        }
                    }
                    else -> Flowable.error<ApiException>(ApiException(-1, ""))
                }
            }
        }
    }
}