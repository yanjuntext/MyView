package com.wyj.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.base.utils.DisplayHelper


abstract class StickeItemDecoration(
    val context: Context
) : RecyclerView.ItemDecoration() {

    private var headerHeight = DisplayHelper.dp2px(40F)

    private var isGridLayout = false


    private val headPaint by lazy {
        Paint().also {
            it.isAntiAlias = true
            it.color = Color.parseColor("#E4E4E4")
        }
    }

    private val textPaint by lazy {
        TextPaint().also {
            it.isAntiAlias = true
            it.textSize = DisplayHelper.sp2px(16F).toFloat()
            it.color = Color.parseColor("#80000000")
        }
    }

    fun asGridLayout(): StickeItemDecoration {
        this.isGridLayout = true
        return this
    }

    fun setHeaderHeight(height: Int): StickeItemDecoration {
        this.headerHeight = height
        return this
    }

    fun setHeaderColor(color: Int): StickeItemDecoration {
        headPaint.color = color
        return this
    }

    fun setHeaderTitleColor(color: Int): StickeItemDecoration {
        this.textPaint.color = color
        return this
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        val left = if (!isGridLayout) parent.paddingLeft else 0
        val right =
            if (!isGridLayout) parent.width - parent.paddingRight else DisplayHelper.screenWidth()

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i) ?: continue
            val adapterPosition = parent.getChildAdapterPosition(child)

            if (i != 0) {
                Log.e("StickeItemDecoration", "isStickHead[${isStickHead(adapterPosition)}]")
                if (isStickHead(adapterPosition)) {
                    val top = child.top - headerHeight
                    val bottom = child.top
                    drawStickHead(c, adapterPosition, left, top, right, bottom)
                }
            } else {
                //当 ItemView 是屏幕上第一个可见的View 时，不管它是不是组内第一个View
                //它都需要绘制它对应的 StickyHeader。
                var top = parent.paddingTop
                if (isLastHead(adapterPosition)) {
                    val suggestTop = child.bottom - headerHeight
                    // 当 ItemView 与 Header 底部平齐的时候，判断 Header 的顶部是否小于
                    // parent 顶部内容开始的位置，如果小于则对 Header.top 进行位置更新，
                    //否则将继续保持吸附在 parent 的顶部
                    if (suggestTop < top) {
                        top = suggestTop
                    }
                }
                val bottom = top + headerHeight
                drawStickHead(c, adapterPosition, left, top, right, bottom)
            }

        }
    }

    private fun drawStickHead(
        canvas: Canvas,
        position: Int,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        val rect = Rect(left, top, right, bottom)
        canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), headPaint)


        val fontMetrics = textPaint.fontMetrics
        val distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        val baseLine = rect.centerY() + distance
        val title = getStickHeadTitle(position)
        val width = textPaint.measureText(title)
        val x = rect.centerX() - width / 2
        val y = bottom - fontMetrics.descent
        canvas.drawText(title, x, baseLine, textPaint)
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.childCount == 0) return
        val childAdapterPosition = parent.getChildAdapterPosition(view)
        if (isStickHead(childAdapterPosition) || (childAdapterPosition - 1 >= 0 && isStickHead((childAdapterPosition - 1))) || (childAdapterPosition - 2 >= 0 && isStickHead (childAdapterPosition - 2)) ) {
            outRect.top = headerHeight
        } else if (!isGridLayout) outRect.top = 1
    }



    abstract fun isStickHead(position: Int): Boolean
    abstract fun getStickHeadTitle(position: Int): String
    abstract fun isLastHead(position: Int): Boolean

}