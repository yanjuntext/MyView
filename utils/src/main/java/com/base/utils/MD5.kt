package com.base.utils

import java.security.MessageDigest

/**
 *
 *@author abc
 *@time 2019/10/18 16:36
 */
object MD5 {

    private var appMdr: String? = null

    fun setAppMd5(appMdr: String?) {
        this.appMdr = appMdr
    }

    fun getAppMd5() = appMdr

    fun MD5(s: String): String? {
        val hexDigits = charArrayOf(
            '0',
            '1',
            '2',
            '3',
            '4',
            '5',
            '6',
            '7',
            '8',
            '9',
            'a',
            'b',
            'c',
            'd',
            'e',
            'f'
        )
        try {
            val btInput = s.toByteArray()
            val mdInst = MessageDigest.getInstance("MD5")
            mdInst.update(btInput)
            val md = mdInst.digest()
            val j = md.size
            val str = CharArray(j * 2)
            var k = 0
            for (i in 0 until j) {
                val byte0 = md[i].toInt()
                str[k++] = hexDigits[byte0.ushr(0x4) and 0xf]
                str[k++] = hexDigits[(byte0 and 0xf)]
            }
            return String(str)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    //随机码加密
    fun MD5Scode(random: String): String? {
        val sorce = "${random}${if (appMdr.isNullOrEmpty()) "iM4#C.NcE_6kJ0Nx" else appMdr}"
        return MD5(sorce)
    }

    //密码加密
    fun MD5Passwd(passwd: String, random: String): String? {
        return MD5Scode(passwd)?.let {
            val source = "$it$random"
            MD5(source)
        }
    }
}