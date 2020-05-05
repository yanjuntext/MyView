package com.wyj.widget.dialog.base

import com.wyj.widget.R

/**
 *
 *@author abc
 *@time 2019/11/12 15:03
 */
object AnimStyle {
    /** 没有动画效果  */
    val NO_ANIM = 0

    /** 默认动画效果  */
    internal val DEFAULT = R.style.anim_one_dialog

    /** 缩放动画  */
    val SCALE = R.style.ScaleAnimStyle

    /** IOS 动画  */
    val IOS = R.style.IOSAnimStyle

    /** 吐司动画  */
    val TOAST = android.R.style.Animation_Toast

    /** 顶部弹出动画  */
    val TOP = R.style.TopAnimStyle

    /** 底部弹出动画  */
    val BOTTOM = R.style.BottomAnimStyle

    /** 左边弹出动画  */
    val LEFT = R.style.LeftAnimStyle

    /** 右边弹出动画  */
    val RIGHT = R.style.RightAnimStyle

    /** 左边弹出缩放动画  */
    val LEFT_SCALE = R.style.LeftScaleAnimStyle

    /** 右边弹出缩放动画  */
    val RIGHT_SCALE = R.style.RightScaleAnimStyle
}