package com.wyj.base.util

import com.wyj.base.log

object WheelServiceUtils {
    val MAX_BASE_URL_COUNT = 1

    private var DEFAULT_URLS: MutableList<String>? = null

    private var defaultFastService: String? = "http://39.108.79.96:80/"

    private var mBaseUrls: MutableList<String>? = null

    /**初始化服务器*/
    fun init(vararg urls: String) {
        if (DEFAULT_URLS.isNullOrEmpty()) {
            DEFAULT_URLS = mutableListOf()
            urls.forEach {
                DEFAULT_URLS?.add(it)
            }

        }
    }

    fun setFastService(service: String) {
        defaultFastService = service
    }

    fun getBaseUrl(index: Int): String {
        if (mBaseUrls == null) {
            mBaseUrls = mutableListOf()
            mBaseUrls?.let { it ->
                it.add(defaultFastService ?: "")
                log("url${it}")
                DEFAULT_URLS?.forEach {
                    if (it != defaultFastService) {
                        mBaseUrls?.add(it)
                    }
                }
            }
        }
        return mBaseUrls?.get(index % MAX_BASE_URL_COUNT) ?: ""
    }

}