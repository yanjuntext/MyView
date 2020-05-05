package com.wyj.widget

import android.content.Context
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.base.utils.DisplayHelper
import kotlin.math.min

/**
 *
 *@author abc
 *@time 2019/11/13 18:04
 */
class AutoLinearLayoutManager(context: Context, var maxShowItem: Int) :
    LinearLayoutManager(context) {

    private val MaxHeight by lazy { DisplayHelper.screenHeight() / 2 }
    private val TAG by lazy { AutoLinearLayoutManager::class.java.simpleName }

    override fun onMeasure(
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State,
        widthSpec: Int,
        heightSpec: Int
    ) {

        super.onMeasure(
            recycler,
            state,
            widthSpec,
            View.MeasureSpec.makeMeasureSpec( min(View.MeasureSpec.getSize(heightSpec),MaxHeight), View.MeasureSpec.EXACTLY)
        )
//        var realHeight = 0
//        val count = min(maxShowItem, state.itemCount)
//        if (count > 0) {
//            for (i in 0 until count) {
//                val child = recycler.getViewForPosition(0)
//                if (child != null) {
////                    measureChild(child, widthSpec, heightSpec)
//                    realHeight += child.measuredHeight
//                }
//                super.onMeasure(
//                    recycler,
//                    state,
//                    widthSpec,
//                    View.MeasureSpec.makeMeasureSpec( min(realHeight,MaxHeight), View.MeasureSpec.EXACTLY)
//                )
//            }
//        } else {
//            super.onMeasure(recycler, state, widthSpec, heightSpec)
//        }

        Log.e(
            TAG,
            "onMeasure[${state.itemCount}],show[${maxShowItem}],height[${View.MeasureSpec.getSize(
                heightSpec
            )}],MaxHeight[$MaxHeight]"
        )
    }


    private fun setMeasureSpec(
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State,
        widthSpec: Int,
        heightSpec: Int
    ): Int {
        val mode = View.MeasureSpec.getMode(heightSpec)
        return when {
//            mode == View.MeasureSpec.EXACTLY -> heightSpec
            state.itemCount > maxShowItem -> {
                val child = recycler.getViewForPosition(0)
                measureChild(child, widthSpec, heightSpec)
                val height = child.measuredHeight * maxShowItem
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
                heightSpec
            }
            else -> heightSpec
        }

    }

}