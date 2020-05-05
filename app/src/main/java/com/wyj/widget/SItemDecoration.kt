package com.wyj.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.base.utils.DisplayHelper
import java.lang.ref.WeakReference

/**
 *
 *@author abc
 *@time 2019/11/13 17:10
 */
class SItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private var mContext: WeakReference<Context>? = null

    private var dividerHeight = 1
    private var dividerColor = Color.WHITE
    private var paint: Paint? = null
    private var coun = 2
    private val isShowHorizontalDivider = true
    private val isShowVerticalDivider = true
    private var mShowLastIndexLine = true

    init {
        mContext = WeakReference(context)
        paint = Paint()
        paint?.isAntiAlias = true
    }

    fun setDividerHeight(dividerHeight: Int): SItemDecoration {
        this.dividerHeight = DisplayHelper.dp2px(dividerHeight.toFloat())
        if (paint != null) {
            paint?.strokeWidth = this.dividerHeight.toFloat()
        }
        return this
    }

    fun setDividerHeightPx(dividerHeightPx: Int): SItemDecoration {
        this.dividerHeight = dividerHeightPx
        if (paint != null) {
            paint?.strokeWidth = this.dividerHeight.toFloat()
        }
        return this
    }

    fun setDividerColorResource(@ColorRes dividerColor: Int): SItemDecoration {
        this.dividerColor = dividerColor
        if (paint != null) {
            mContext?.get()?.let {
                paint?.setColor(ContextCompat.getColor(it, dividerColor))
            }
        }
        return this
    }

    fun setCoun(coun: Int): SItemDecoration {
        this.coun = coun
        return this
    }

    fun setShowLastIndexLine(show: Boolean): SItemDecoration {
        this.mShowLastIndexLine = show
        return this
    }


    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = dividerHeight
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)

        drawHorDivider(c, parent, state)
    }

    //水平分割线
    private fun drawHorDivider(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val count = if (mShowLastIndexLine) getChildCount(parent) else getChildCount(parent) - 1

        for (i in 0 until count) {
            val child = parent.getChildAt(i)
            if (child != null) {
                val params = child.layoutParams as RecyclerView.LayoutParams
                val left = getLeft(child, params)
                val right = getRight(child, params)
                val top = child.bottom + params.bottomMargin
                val bottom = top + dividerHeight
//                val bottom = child.top + params.topMargin
//                val top = bottom - dividerHeight
                paint?.let {
                    c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), it)
                }
            }
        }
    }

    private fun getLeft(child: View, params: RecyclerView.LayoutParams): Int =
        child.left - params.leftMargin

    private fun getTop(child: View, params: RecyclerView.LayoutParams): Int =
        child.top - params.topMargin

    private fun getRight(child: View, params: RecyclerView.LayoutParams): Int =
        child.right - params.rightMargin

    private fun getBottom(child: View, params: RecyclerView.LayoutParams): Int =
        child.bottom - params.bottomMargin

    private fun getChildCount(parent: RecyclerView): Int = parent.childCount

    private fun drawVerticalDivider(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams

            val top = child.top - params.topMargin
            val bottom = child.bottom + params.bottomMargin

            var left = 0
            var right = 0

            //左边第一列
            if (i % coun == 0) {
                //item左边分割线
                left = child.left
                right = left + dividerHeight

                if (paint != null) {
                    c.drawRect(
                        left.toFloat(),
                        top.toFloat(),
                        right.toFloat(),
                        bottom.toFloat(),
                        paint!!
                    )
                }
                //item右边分割线
                left = child.right + params.rightMargin - dividerHeight
                right = left + dividerHeight
            } else {
                //非左边第一列
                left = child.right + params.rightMargin - dividerHeight
                right = left + dividerHeight
            }
            //画分割线

            if (paint != null) {
                c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint!!)
            }

        }
    }

    private fun drawHorizontal(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams

            val left = child.left - params.leftMargin - dividerHeight
            val right = child.right + params.rightMargin
            var top = 0
            var bottom = 0

            // 最上面一行
            if (i / coun == 0) {
                //当前item最上面的分割线
                top = child.top
                //当前item下面的分割线
                bottom = top + dividerHeight

                if (paint != null) {
                    c.drawRect(
                        left.toFloat(),
                        top.toFloat(),
                        right.toFloat(),
                        bottom.toFloat(),
                        paint!!
                    )
                }
                top = child.bottom + params.bottomMargin
                bottom = top + dividerHeight
            } else {
                top = child.bottom + params.bottomMargin
                bottom = top + dividerHeight
            }
            //画分割线

            if (paint != null) {
                c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint!!)
            }
        }
    }
}
