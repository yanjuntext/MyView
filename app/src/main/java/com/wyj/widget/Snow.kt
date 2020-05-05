package com.wyj.widget

import android.graphics.Bitmap
import android.graphics.Matrix
import kotlin.math.sin
import kotlin.random.Random

/**
 *
 *@author abc
 *@time 2020/4/10 17:27
 */
data class Snow(
    var x: Float, var y: Float, val radius: Float, val speed: Int,
    val scale: Float, var angle: Double, val windSpeed: Int
) {

    var bitmap: Bitmap? = null
        set(value) {
            field = getBitmap(value)
        }

    fun move(total: Int) {
        y += speed
        x += (windSpeed * sin(angle)).toFloat()
        angle += (if (Random.nextBoolean()) -1 else 1) * Math.random() * 0.0025
        if (y > total) y = 0f
    }


    fun getBitmap(bitmap: Bitmap?): Bitmap? {
        if (bitmap == null) return null
        val width = bitmap.width
        val height = bitmap.height

        val newW = width * scale
        val newH = height * scale

        val matrix = Matrix()
        matrix.postScale(scale, scale)
        matrix.postRotate(Random.nextInt(90).toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
    }

}