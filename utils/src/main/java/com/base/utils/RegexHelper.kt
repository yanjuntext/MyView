package com.base.utils

import java.util.regex.Pattern

/**
 *
 *@author abc
 *@time 2020/3/5 14:20
 */
object RegexHelper {
    /** 手机号（只能以 1 开头）  */
    const val REGEX_MOBILE = "[1]\\d{0,10}"
    /** 中文（普通的中文字符）  */
    const val REGEX_CHINESE = "[\\u4e00-\\u9fa5]*"
    /** 英文（大写和小写的英文）  */
    const val REGEX_ENGLISH = "[a-zA-Z]*"
    /** 计数（非 0 开头的数字）  */
    const val REGEX_COUNT = "[1-9]\\d*"
    /** 用户名（中文、英文、数字）  */
    const val REGEX_NAME = "[$REGEX_CHINESE|$REGEX_ENGLISH|\\d*]*"
    /** 非空格的字符（不能输入空格）  */
    const val REGEX_NONNULL = "\\S+"

    const val REGEX_EMAIL = "^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+\$"

    fun regexPhone(phone: String?): Boolean {
        if (phone.isNullOrEmpty()) return false
        val pattern = Pattern.compile(REGEX_MOBILE)
        return pattern.matcher(phone).matches()
    }

    fun regexEmail(email:String?):Boolean{
        if (email.isNullOrEmpty()) return false
        val pattern = Pattern.compile(REGEX_EMAIL)
        return pattern.matcher(email).matches()
    }

}