package com.base.utils

/**
 *
 *@author abc
 *@time 2019/12/30 14:30
 */
object Format {

    private val MIN_AND_SEN = "%02d:%02d"

    fun formatMinAndSen(min: Int, sen: Int): String = String.format(MIN_AND_SEN, min, sen)

}