package com.wyj.base

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

//隐藏软键盘
fun Activity.hideSoftKeyboard() {
    val inputMethodManager: InputMethodManager? =
        this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (inputMethodManager != null) {
        val localView = currentFocus
        if (localView != null && localView.windowToken != null) {
            val windowToken = localView.windowToken
            inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
        }
    }
}

fun Fragment.hideSoftKeyboard() {
    val inputMethodManager: InputMethodManager? =
        this.activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (inputMethodManager != null) {
        val localView = this.activity?.currentFocus
        if (localView != null && localView.windowToken != null) {
            val windowToken = localView.windowToken
            inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
        }
    }
}