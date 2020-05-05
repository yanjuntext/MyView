package com.wyj.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random


/**
 *
 *@author abc
 *@time 2020/4/10 16:05
 */
class SnowView : View, Runnable {

    private val mSnowList = mutableListOf<Snow>()
    private var mSnowPaint: Paint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
        color = Color.WHITE
    }
    private var mSnowY = 0f
    private var mBitmap: Bitmap? = null
    private var TIME_SPACE = 10L

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    init {
        mBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_snow, null)
        post(this)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initSnowList()
    }

    private fun initSnowList() {
        val count = Random.nextInt(300)
        for (i in 0..count) {
            mSnowList.add(
                Snow(
                    Random.nextInt(width) * 1.0f,
                    Random.nextInt(height) - height + 0f,
                    Random.nextInt(15) * 1.0f,
                    Random.nextInt(30),
                    (Random.nextInt(10) + 1) * 0.1f,
                    getAngele(),
                    Random.nextInt(10)
                ).apply {
                    bitmap = mBitmap
                }
            )
        }
        invalidate()
    }

    private fun getAngele(): Double {
        val PI = Math.PI / 2
        val angle = (if (Random.nextBoolean()) -1 else 1) * Math.random() * Random.nextInt(10) / 50
        return when {
            angle > PI -> {
                PI
            }
            angle < -PI -> {
                -PI
            }
            else -> angle
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        mSnowList.forEach {
            it.bitmap?.let { bitmap ->
                canvas?.drawBitmap(bitmap, it.x, it.y, mSnowPaint)
            }
//            canvas?.drawCircle(it.x, it.y, it.radius, mSnowPaint)
        }
    }

    override fun onDetachedFromWindow() {
        removeCallbacks(this)
        super.onDetachedFromWindow()
    }

    override fun run() {
        mSnowList.forEach {
            it.move(height)
        }
//        mSnowY += 5
//        if (mSnowY > height) mSnowY = 0f
        invalidate()
        postDelayed(this, TIME_SPACE)
    }

}