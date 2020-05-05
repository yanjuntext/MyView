package com.base.utils

import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 *@author abc
 *@time 2019/10/18 17:13
 */
object TimeHelper {



    val FORMAT_DEFAULT = "yyyy-MM-dd HH:mm:ss"
    val FORMAT_HOUR_MIN = "HH:mm"


    fun getCurrentTime() = System.currentTimeMillis()

    fun getCurrenTime(): String {
        val format = SimpleDateFormat(FORMAT_DEFAULT, Locale.CHINA)
        val date = Date()
        return format.format(date)
    }

    fun getTimeStr(time: Long): String {
        return TimeHelper.getTimeStr(time, FORMAT_DEFAULT)
    }

    fun getTimeStr(time: Long, formatStr: String): String {
        val format = SimpleDateFormat(formatStr, Locale.CHINA)
        val date = Date(time)
        return format.format(date)
    }

    fun getCurrentHourAndMin(): String {
        return SimpleDateFormat(FORMAT_HOUR_MIN, Locale.CHINA).format(getCurrentTime())
    }

    fun compareTime(one: String, two: String): Int = one.compareTo(two)


    fun getCurrentYear(): Int {
        val calendar =
            Calendar.getInstance(TimeZone.getTimeZone("gmt"))
        return calendar.get(Calendar.YEAR)
    }

    fun getCurrentMonth(): Int {
        val calendar =
            Calendar.getInstance(TimeZone.getTimeZone("gmt"))
        return calendar.get(Calendar.MONTH)
    }

    fun getCurrentDay(): Int {
        val calendar =
            Calendar.getInstance(TimeZone.getTimeZone("gmt"))
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

    fun getCurrentHour(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.HOUR_OF_DAY)
    }

    fun getCurrentMin(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.MINUTE)
    }

    /**零时区偏移时间*/
    fun getCurrentDiffTimeZoneSec(): Int {
        val tz = TimeZone.getDefault().id
        val calendar =
            Calendar.getInstance(TimeZone.getTimeZone(tz))
        return TimeZone.getDefault().getOffset(calendar.timeInMillis) / 1000
    }


    fun getIndexDatOfTime(day:Int):Long{
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val calendar = Calendar.getInstance()
        calendar.time = Date()

        val sb = StringBuilder()
        sb.append(
            calendar.get(Calendar.YEAR))
            .append("-")
            .append(calendar.get(Calendar.MONTH) + 1)
            .append("-")
            .append(calendar.get(Calendar.DAY_OF_MONTH) - day)
        var date = Date()
        try {
            date = sdf.parse(sb.toString())
        } catch (e: Exception) {

            e.printStackTrace()
        }
        val mStartTimeStemp = date.time / 1000
        return mStartTimeStemp + 24 * 60 * 60
    }


    fun getTimeZoneStringByOffset(offset: Int): String {
        if (offset == 0) {
            return " "
        }
        val minCount = Math.abs(offset / 60)
        if (minCount == 0) {
            return " "
        }
        val hour = minCount / 60
        val min = minCount % 60
        val strMinus = if (offset < 0) "-" else "+"
        return "GMT " + strMinus + String.format("%02d", hour) + ":" + String.format("%02d", min)
    }


    private fun getGmtCalendar() = Calendar.getInstance(TimeZone.getTimeZone("gmt"))

    fun getGmfOffset(offset:Int,type:Int):Long{
        val calendar = getGmtCalendar()
        calendar.add(type,offset)
        return calendar.timeInMillis
    }


    fun secToTime(time:Int):String{
        var timeStr: String? = null
        var hour = 0
        var minute = 0
        var second = 0
        if (time <= 0)
            return "00:00"
        else {
            minute = time / 60
            if (minute < 60) {
                second = time % 60
                timeStr = unitFormat(minute) + ":" + unitFormat(second)
            } else {
                hour = minute / 60
                if (hour > 99)
                    return "99:59:59"
                minute %= 60
                second = time - hour * 3600 - minute * 60
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second)
            }
        }
        return timeStr
    }

    private fun unitFormat(i:Int) = String.format("%02d",i)


    fun getImgName():String{
        val sdf = SimpleDateFormat("yyyyMMddHHmmss")
        val date = Date(System.currentTimeMillis())
        return sdf.format(date) + ".jpg"
    }

}