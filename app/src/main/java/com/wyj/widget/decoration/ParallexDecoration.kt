package com.wyj.widget.decoration

import android.app.ActivityManager
import android.content.Context
import android.graphics.*
import androidx.recyclerview.widget.RecyclerView

/**
 * 作者：王颜军 on 2020/12/23 09:33
 * 邮箱：3183424727@qq.com
 */
class ParallexDecoration(private val context: Context) : RecyclerView.ItemDecoration() {

    //滚动视差系数
    var parallax = 1.0f

    //是否自动铺满
    var autoFill = false

    private var am: ActivityManager? = null
    private lateinit var bitmapOption: BitmapFactory.Options

    private var bitmapPool = mutableListOf<Bitmap>()
    private var bitmapPoolSize = 0
    private var mScreenWidth = 0
    private var mScreenHeight = 0
    private var mBitmapWidth = 0
    private var mBitmapHeight = 0
    private var mScaleBitmapWidth = 0
    private var mScaleBitmapHeight = 0
    private var mScale = 1f
    private var maxVisibleCount = 0
    private var minVisibleCount = 0

    private val mPaint by lazy {
        Paint()
            .apply {
                style = Paint.Style.FILL
                color = Color.RED
                isAntiAlias = true
            }
    }

    fun setUpBitmap(bitmaps: List<Bitmap>) {
        bitmapPool.clear()
        bitmapPool.addAll(bitmaps)
        updateConfig()
    }

    fun addBitmap(bitmap: Bitmap) {
        bitmapPool.add(bitmap)
        updateConfig()
    }

    fun setUpResorce(resources: List<Int>) {
        bitmapPool.clear()
        resources.forEach {
            bitmapPool.add(createBitmap(it))
        }
        updateConfig()
    }

    fun addResource(resource: Int) {
        bitmapPool.add(createBitmap(resource))
        updateConfig()
    }


    private fun createBitmap(resource: Int): Bitmap {
        if (am == null) {
            bitmapOption = BitmapFactory.Options()
            am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            if (am!!.isLowRamDevice) {
                bitmapOption.inPreferredConfig = Bitmap.Config.RGB_565
            }
        }
        return BitmapFactory.decodeResource(context.resources, resource, bitmapOption)
    }

    private fun updateConfig() {
        if (bitmapPool.isNullOrEmpty()) return
        bitmapPoolSize = bitmapPool.size
        mBitmapHeight = bitmapPool[0].height
        mBitmapWidth = bitmapPool[0].width
        mScaleBitmapWidth = mBitmapWidth
        mScaleBitmapHeight = mBitmapHeight
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        if (bitmapPool.isNullOrEmpty()) return
        parent.layoutManager?.let { lm ->
            val isHorizontal = lm.canScrollHorizontally()
            if (mScreenWidth == 0 || mScreenHeight == 0) {
                mScreenWidth = c.width
                mScreenHeight = c.height
                val allInScreen: Int
                val doubleOutOfScreen: Boolean
                if (isHorizontal) {
                    if (autoFill) {
                        mScale = mScreenHeight * 1f / mBitmapHeight
                        mScaleBitmapWidth = (mBitmapWidth * mScale).toInt()
                    }
                    allInScreen = mScreenWidth / mScaleBitmapWidth
                    doubleOutOfScreen = mScreenWidth % mScaleBitmapWidth > 1
                } else {
                    if (autoFill) {
                        mScale = mScreenWidth * 1f / mBitmapWidth
                        mScaleBitmapHeight = (mBitmapHeight * mScale).toInt()
                    }
                    allInScreen = mScreenHeight / mScaleBitmapHeight
                    doubleOutOfScreen = mScreenHeight % mScaleBitmapHeight > 1
                }
                minVisibleCount = allInScreen + 1
                maxVisibleCount = if (doubleOutOfScreen) allInScreen + 2 else minVisibleCount
            }

            val parallasOffset: Float
            val firstVisible: Int
            val firstVisibleOffset: Float
            if (isHorizontal) {
                parallasOffset = lm.computeHorizontalScrollOffset(state) * parallax
                firstVisible = (parallasOffset / mScaleBitmapWidth).toInt()
                firstVisibleOffset = parallasOffset % mScaleBitmapWidth
            } else {
                parallasOffset = lm.computeVerticalScrollOffset(state) * parallax
                firstVisible = (parallasOffset / mScaleBitmapHeight).toInt()
                firstVisibleOffset = parallasOffset % mScaleBitmapHeight
            }

            val bestDrawCount = maxVisibleCount
            c.save()
            if (isHorizontal) {
                c.translate(-firstVisibleOffset, 0f)
            } else {
                c.translate(0f, -firstVisibleOffset)
            }
            if (autoFill) c.scale(mScale, mScale)
            for ((i, index) in (firstVisible until firstVisible + bestDrawCount).withIndex()) {
                if (isHorizontal) {

                    c.drawBitmap(
                        bitmapPool[index % bitmapPoolSize],
                        i * mScaleBitmapWidth.toFloat(),
                        0f, null
                    )

                } else {
                    c.drawBitmap(
                        bitmapPool[index % bitmapPoolSize],
                        0f,
                        i * mScaleBitmapHeight.toFloat(),
                        null
                    )
                }
            }
            c.restore()
        }

    }
}