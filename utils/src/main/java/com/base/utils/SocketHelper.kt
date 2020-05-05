package com.base.utils

import java.lang.Exception
import java.net.Socket

/**
 *socket 辅助
 *@author abc
 *@time 2020/1/18 16:41
 */
object SocketHelper {

    /**获取端口号*/
    fun getLocalPort(ip: String?): Int {
        if (ip.isNullOrEmpty()) return 0
        while (true) {
            val port = 6000 + (Math.random() * 3000).toInt()
            if (isUsableLocalPort(ip, port)) return port
        }
    }

    /*8端口号是否被占用*/
    fun isUsableLocalPort(ip: String?, port: Int): Boolean {
        if (ip.isNullOrEmpty()) return false
        return try {
            // socket链接正常，说明这个端口正在使用
            Socket(ip, port).close()
            false
        } catch (e: Exception) {
            true
        }
    }
}