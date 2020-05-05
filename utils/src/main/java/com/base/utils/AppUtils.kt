package com.base.utils

import android.content.Context
import android.os.Build

/**
 *
 *@author abc
 *@time 2019/10/25 9:58
 */
object AppUtils {

    fun getAppVersionName(context: Context): String =
        try {
            context.packageManager.getPackageInfo(context.packageName, 0)?.versionName ?: ""
        } catch (e: Exception) {
            ""
        }


    fun getAppVersionCode(context: Context): Long =
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.packageManager.getPackageInfo(context.packageName, 0)?.longVersionCode ?: 0.toLong()
            } else {
                (context.packageManager.getPackageInfo(context.packageName, 0)?.versionCode ?: 0).toLong()
            }
        } catch (e: Exception) {
            0L
        }

}