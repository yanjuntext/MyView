package com.wyj.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.google.android.material.appbar.AppBarLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView


/**
 *
 *@author abc
 *@time 2019/10/21 15:56
 */
class FlingBehavior : AppBarLayout.Behavior {
    constructor()
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private val TAG = FlingBehavior::class.java.name
    private val TOP_CHILD_FLING_THRESHOLD = 1
    private val OPTIMAL_FLING_VELOCITY = 3500f
    private val MIN_FLING_VELOCITY = 20f

    var shouldFling = false
    var flingVelocityY = 0f

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: AppBarLayout,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        if (dy > MIN_FLING_VELOCITY) {
            shouldFling = true
            flingVelocityY = dy.toFloat()
        } else {
            shouldFling = false
        }
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, abl: AppBarLayout, target: View, type: Int) {
        super.onStopNestedScroll(coordinatorLayout, abl, target, type)
        if (shouldFling) {
            Log.d(TAG, "onNestedPreScroll: running nested fling, velocityY is $flingVelocityY")
            onNestedFling(coordinatorLayout, abl, target, 0f, flingVelocityY, true)
        }
    }


    override fun onNestedFling(
        coordinatorLayout: CoordinatorLayout,
        child: AppBarLayout,
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        var velocityY = velocityY
        var consumed = consumed

        if (target is RecyclerView && velocityY < 0) {
            Log.d(TAG, "onNestedFling: target is recyclerView")
            val recyclerView = target as RecyclerView
            val firstChild = recyclerView.getChildAt(0)
            val childAdapterPosition = recyclerView.getChildAdapterPosition(firstChild)
            consumed = childAdapterPosition > TOP_CHILD_FLING_THRESHOLD
        }

        // prevent fling flickering when going up
        if (target is NestedScrollView && velocityY > 0) {
            consumed = true
        }

        if (Math.abs(velocityY) < OPTIMAL_FLING_VELOCITY) {
            velocityY = OPTIMAL_FLING_VELOCITY * if (velocityY < 0) -1 else 1
        }
        Log.d(TAG, "onNestedFling: velocityY - $velocityY, consumed - $consumed")

        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed)

    }
}