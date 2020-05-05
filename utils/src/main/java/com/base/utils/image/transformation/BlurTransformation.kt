package com.base.utils.image.transformation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.base.utils.image.internal.FastBlur
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import java.security.MessageDigest

/**
 *
 *@author abc
 *@time 2020/1/21 15:27
 */
class BlurTransformation(var radius: Int = MAX_RADIUS, var sampling: Int = DEFAULT_DOWN_SAMPLING) :
    BitmapTransformation() {
    companion object {
        val MAX_RADIUS = 25
        val DEFAULT_DOWN_SAMPLING = 1
        private val VERSION = 1
        private val ID = "com.base.utils.image.transformation.BlurTransformation.$VERSION"
    }

    override fun transform(
        context: Context,
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        val width = toTransform.width
        val height = toTransform.height
        val scaleWidth = width / sampling
        val scaleHeight = height / sampling
        var bitmap = pool.get(scaleWidth, scaleHeight, Bitmap.Config.ARGB_8888)
        setCanvasBitmapDensity(toTransform, bitmap)
        val canvas = Canvas(bitmap)
        canvas.scale(1f/sampling,1f/sampling)
        val paint = Paint()
        paint.flags = Paint.FILTER_BITMAP_FLAG
        canvas.drawBitmap(toTransform,0f,0f,paint)
        bitmap = FastBlur.blur(bitmap, radius, true)?:bitmap

        return bitmap
    }

    override fun equals(o: Any?): Boolean =
        o is BlurTransformation && o.radius == radius && o.sampling == sampling

    override fun hashCode(): Int = ID.hashCode() + radius * 1000 + sampling * 10

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update("$ID$radius$sampling".toByteArray(Charsets.UTF_8))
    }

    override fun toString(): String {
        return "BlurTransformation(radius=$radius, sampling=$sampling)"
    }


}