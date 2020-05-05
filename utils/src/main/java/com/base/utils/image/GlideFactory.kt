package com.base.utils.image

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Looper
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import java.io.File
import java.lang.Exception

/**
 *
 *@author abc
 *@time 2019/9/20 10:08
 */
class GlideFactory(val placeholder: Drawable?, val error: Drawable?) : ImageFactory<GlideStrategy> {
    private var baseUrl: String? = null
    override fun getBaseUrl(): String = baseUrl ?: ""

    override fun setBaseUrl(baseUrl: String): ImageFactory<GlideStrategy> {
        this.baseUrl = baseUrl
        return this
    }

    override fun createImageStrategy(): GlideStrategy = GlideStrategy()

    override fun createPlaceholder(context: Context): Drawable? = placeholder

    override fun createError(context: Context): Drawable? = error

    override fun clearMemoryCache(contex: Context) {
        // 清除内存缓存（必须在主线程）
        Glide.get(contex).clearMemory()
    }

    override fun clearDiskCache(context: Context) {
        // 清除本地缓存（必须在子线程）
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Thread(Runnable {
                    Glide.get(context).clearDiskCache()
                }).start()
            } else {
                Glide.get(context).clearDiskCache()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getCacheSize(contex: Context): Double = getFolderSize(getCacheFile(contex)).toDouble()

    private fun getCacheFile(context: Context): File =
        File(context.cacheDir.toString() + "/" + InternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR)


    private fun getFolderSize(file: File): Long {
        var size: Long = 0
        try {
            val fileList = file.listFiles()
            for (aFileList in fileList!!) {
                size += if (aFileList.isDirectory) {
                    getFolderSize(aFileList)
                } else {
                    aFileList.length()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return size
    }
}