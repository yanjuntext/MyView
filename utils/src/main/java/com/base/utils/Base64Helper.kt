package com.base.utils

import android.util.Base64
import java.lang.Exception

object Base64Helper {

    fun encode(data:String):String{
        return try {
            Base64.encodeToString(data.toByteArray(Charsets.UTF_8), Base64.DEFAULT)
        }catch (e:Exception){
            e.printStackTrace()
            ""
        }
    }
}