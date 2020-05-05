package com.wyj.base

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Parcelable
import android.view.WindowManager
import com.base.utils.ActivityManager
import java.io.Serializable

//activity管理
fun Activity.addActivity() {
    ActivityManager.add(this)
}

fun Activity.removeActivity() {
    ActivityManager.remove(this)
}

fun Activity.finishAll() {
    ActivityManager.finishAll()
}

fun Bundle.put(name: String, data: Any?) {
    when (data) {
        is String -> this.putString(name, data)
        is Int -> putInt(name, data)
        is Float -> putFloat(name, data)
        is Double -> putDouble(name, data)
        is Long -> putLong(name, data)
        is Boolean -> putBoolean(name, data)
        is Char -> putChar(name, data)
        is CharArray -> putCharArray(name, data)
        is Byte -> putByte(name, data)
        is ByteArray -> putByteArray(name, data)
        is Serializable -> putSerializable(name, data)
        is Parcelable -> putParcelable(name, data)
    }
}


//横竖屏切换
fun Activity.changePortOrLands() {
    this.requestedOrientation =
        if (this.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

}

/**保持屏幕常亮*/
fun Activity.keepScreenOn(){
    this.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}