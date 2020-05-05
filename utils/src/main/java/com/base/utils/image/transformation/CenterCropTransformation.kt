package com.base.utils.image.transformation

import android.graphics.Bitmap
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.bumptech.glide.util.Util
import java.security.MessageDigest

/**
 *
 *@author abc
 *@time 2019/9/20 14:03
 */
class CenterCropTransformation : BitmapTransformation() {
    companion object {
        private val VERSION = 1
        private val ID = "com.base.utils.image.transformation.CenterCropTransformation.$VERSION"
        private val ID_BYTES = ID.toByteArray(CHARSET)
    }


    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)
    }

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap =
        TransformationUtils.centerCrop(pool, toTransform, outWidth, outHeight)

    override fun equals(o: Any?): Boolean = o is CenterCropTransformation


    override fun hashCode(): Int = Util.hashCode(ID.hashCode())

}