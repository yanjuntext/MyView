package com.wyj.widget.ruler

import java.text.SimpleDateFormat
import java.util.*

/**
 * 作者：王颜军 on 2020/8/15 10:10
 * 邮箱：3183424727@qq.com
 */
object DateUtil {

    val sdf = SimpleDateFormat("", Locale.CHINA)

    private fun format(pattern: String, time: Long): String {
        sdf.applyPattern(pattern)
        if (time < 8 * 60 * 60 * 1000) {
            sdf.timeZone = TimeZone.getTimeZone("GMT+0")
        } else {
            sdf.timeZone = TimeZone.getTimeZone("GMT+08:00")
        }
        return sdf.format(time)
    }

    fun formatHourMinute(time: Long) = format("HH:mm", time)


    fun getSecond(time: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        return calendar.get(Calendar.SECOND)
    }

    fun getMinute(time: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        return calendar.get(Calendar.MINUTE)
    }

    fun getHour(time: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        return calendar.get(Calendar.HOUR_OF_DAY)
    }

    fun getDay(time: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        return calendar.get(Calendar.DAY_OF_MONTH)
    }


    fun getCurrentDayStartTime(time: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        calendar.set(year, month, day, 0, 0,0)
        return calendar.timeInMillis / 1000
    }


    fun getDateLong(hour:Int,min:Int):Long{
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        calendar.set(year, month, day, hour, min,0)
        return calendar.timeInMillis / 1000
    }

}