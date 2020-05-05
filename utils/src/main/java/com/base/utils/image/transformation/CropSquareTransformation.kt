package com.base.utils.image.transformation

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import java.nio.charset.Charset
import java.security.MessageDigest


/**
 *
 *@author abc
 *@time 2019/9/20 11:42
 */
class CropSquareTransformation : BitmapTransformation() {

    private val VERSION = 1
    private val ID = "com.base.utils.image.transformation.CropSquareTransformation.$VERSION"
    val CHARSET = Charset.forName("UTF-8")
    private var size: Int = 0

    override fun transform(
        context: Context,
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        this.size = Math.max(outWidth, outHeight)
        return TransformationUtils.centerCrop(pool, toTransform, size, size)
    }

    override fun equals(o: Any?): Boolean {
        return o is CropSquareTransformation && o.size == size
    }

    override fun hashCode(): Int = ID.hashCode() + size * 10

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update((ID + size).toByteArray(CHARSET))
    }

    override fun toString(): String = "CropSquareTransformation(size=$size)"

}