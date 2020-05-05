package com.wyj.base.http.okhttp

import io.reactivex.FlowableTransformer
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

object RxObservableUtils {

    fun <T> iomainSchedulers(): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream ->
            upstream
                .subscribeOn(Schedulers.io())//被观察着者在io线程执行
                .observeOn(AndroidSchedulers.mainThread())//观察着者在主线程执行
        }
    }

    fun <T> ioandioSchedulers(): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream ->
            upstream
                .subscribeOn(Schedulers.io())//被观察着者在io线程执行
                .observeOn(Schedulers.io());//观察着者在io线程执行
        }
    }

    fun <T> iomainFlowableSchedulers(): FlowableTransformer<T, T> {
        return FlowableTransformer { upstream ->
            upstream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }

    fun <T> ioandioFlowableSchedulers(): FlowableTransformer<T, T> {
        return FlowableTransformer { upstream ->
            upstream
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
        }
    }
}