package com.wyj.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment

//activity跳转
fun Activity.startAction(clazz: Class<*>,finish: Boolean = false) {
    this.startActivity(Intent(this, clazz))
    if(finish) this.finish()
}

fun Activity.startAction(clazz: Class<*>, bundle: Bundle,finish: Boolean = false) {
    startActivity(Intent(this, clazz).putExtra(clazz.simpleName, bundle))
    if(finish) this.finish()
}

fun Activity.startActionForResult(clazz: Class<*>, requestCode: Int,finish: Boolean = false) {
    this.startActivityForResult(Intent(this, clazz), requestCode)
    if(finish) this.finish()
}

fun Activity.startActionForResult(clazz: Class<*>, bundle: Bundle, requestCode: Int,finish: Boolean = false) {
    log("startActionForResult bundler[${bundle == null}],name[${clazz.simpleName}]")
    startActivityForResult(Intent(this, clazz).putExtra(clazz.simpleName, bundle), requestCode)
    if(finish) this.finish()
}

fun Activity.startAction(clazz: Class<*>, enterAnim: Int, exitAnim: Int, finish: Boolean = false) {
    this.startActivity(Intent(this, clazz))
    this.overridePendingTransition(enterAnim, exitAnim)
    if (finish) this.finish()
}

fun Activity.startAction(clazz: Class<*>, bundle: Bundle, enterAnim: Int, exitAnim: Int, finish: Boolean = false){
    startActivity(Intent(this, clazz).putExtra(clazz.simpleName, bundle))
    this.overridePendingTransition(enterAnim, exitAnim)
    if (finish) this.finish()
}

fun Activity.startActionForResult(clazz: Class<*>, requestCode: Int, enterAnim: Int, exitAnim: Int, finish: Boolean = false) {
    this.startActivityForResult(Intent(this, clazz), requestCode)
    this.overridePendingTransition(enterAnim, exitAnim)
    if (finish) this.finish()
}

fun Activity.startActionForResult(clazz: Class<*>, bundle: Bundle, requestCode: Int, enterAnim: Int, exitAnim: Int, finish: Boolean = false) {
    startActivityForResult(Intent(this, clazz).putExtra(clazz.simpleName, bundle), requestCode)
    this.overridePendingTransition(enterAnim, exitAnim)
    if (finish) this.finish()
}

//fragment跳转
fun Fragment.startAction(clazz: Class<*>) {
    this.activity?.let {
        it.startActivity(Intent(it, clazz))
    }
}

fun Fragment.startAction(clazz: Class<*>, bundle: Bundle) {
    this.activity?.let {
        it.startActivity(Intent(it, clazz).putExtra(clazz.simpleName, bundle))
    }
}

fun Fragment.startActionForResult(clazz: Class<*>, requestCode: Int) {
    this.activity?.let {
        it.startActivityForResult(Intent(it, clazz), requestCode)
    }
}

fun Fragment.startActionForResult(clazz: Class<*>, bundle: Bundle, requestCode: Int) {
    this.activity?.let {
        it.startActivityForResult(Intent(it, clazz).putExtra(clazz.simpleName, bundle), requestCode)
    }
}