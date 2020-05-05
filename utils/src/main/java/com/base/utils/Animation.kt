package com.base.utils

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.RotateAnimation

/**
 *
 *@author abc
 *@time 2019/9/12 10:34
 */

fun View.rotation(fromDegrees: Float, toDegress: Float, duration: Long): ObjectAnimator {
    return ObjectAnimator.ofFloat(this, "rotation", fromDegrees, toDegress).setDuration(duration)
}


fun View.alpha(fromAlpha: Float, toAlpha: Float, duration: Long): ObjectAnimator {
    return ObjectAnimator.ofFloat(this, "alpha", fromAlpha, toAlpha).setDuration(duration)
}

fun View.tanslateX(from:Float,to:Float,duration: Long):ObjectAnimator{
    return ObjectAnimator.ofFloat(this,"translationX",from,to).setDuration(duration)
}

fun View.tanslateY(from:Float,to:Float,duration: Long):ObjectAnimator{
    return ObjectAnimator.ofFloat(this,"translationY",from,to).setDuration(duration)
}