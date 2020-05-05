package com.base.utils.image

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import java.io.File

/**
 *
 *@author abc
 *@time 2019/9/20 10:27
 */
class ImageLoader {
    val baseUrl: String by lazy { sImageFactory?.getBaseUrl() ?: "" }
    var context: Any? = null
    var circle: Int = 0
    var file: File? = null
    var url: String? = null
    var uri: Uri? = null
    var isGif: Boolean = false
    var isFile: Boolean = false
    var isCenterCrop: Boolean = false
    var blur: Int = 0
    var blurScal: Int = 1

    var placeholder: Drawable? = sPlaceholder
    var error: Drawable? = sError
    var view: ImageView? = null

    var placeholderRes: Int? = null
    var errorRes:Int? = null

    @DrawableRes
    var resourceId: Int = 0

    var width: Int = 0
    var height: Int = 0

    var skipMemoryCache: Boolean = false


    companion object {
        @Volatile
        private var sImageFactory: ImageFactory<*>? = null

        @Volatile
        private var sImageStrategy: ImageStrategy? = null

        @Volatile
        private var sPlaceholder: Drawable? = null

        @Volatile
        private var sError: Drawable? = null

        fun init(application: Application) {
            init(application, GlideFactory(null, null))
        }

        fun init(application: Application, factory: ImageFactory<*>) {
            sImageFactory = factory
            sImageStrategy = factory.createImageStrategy()
            sPlaceholder = factory.createPlaceholder(application)
            sError = factory.createError(application)
        }

        fun clearCache(context: Context?) {
            if (context == null) return
            clearMemoryCache(context)
            clearDiskCache(context)
        }

        fun with(context: Any?): ImageLoader = ImageLoader(context)

        fun getCacheSize(context: Context): Double = sImageFactory?.getCacheSize(context) ?: 0.0

        private fun clearMemoryCache(context: Context) {
            sImageFactory?.clearMemoryCache(context)
        }

        private fun clearDiskCache(context: Context) {
            sImageFactory?.clearDiskCache(context)
        }
    }

    private constructor(context: Any?) {
        this.context = context
    }

    fun gif(): ImageLoader {
        this.isGif = true
        return this
    }

    fun skipMemoryCache(): ImageLoader {
        this.skipMemoryCache = true
        return this
    }

    private fun file(): ImageLoader {
        this.isFile = true
        return this
    }

    fun cenrerCrop(): ImageLoader {
        this.isCenterCrop = true
        return this
    }

    fun circle(): ImageLoader {
        circle(Int.MAX_VALUE)
        return this
    }

    fun circle(circle: Int): ImageLoader {
        this.circle = circle
        return this
    }

    /***/
    fun blur(blur: Int, blurScal: Int = 1): ImageLoader {
        this.blur = blur
        this.blurScal = blurScal
        return this
    }

    fun load(url: String): ImageLoader {
        this.url = url
        return this
    }

    fun load(file: File): ImageLoader {
//        this.url = Uri.fromFile(file).toString()
        this.file = file
        return file()
    }

    fun load(uri:Uri?):ImageLoader{
        this.uri = uri
        return this
    }


    fun load(@DrawableRes id: Int): ImageLoader {
        this.resourceId = id
        return this
    }

    fun placeholderRes(@DrawableRes placeholderRes: Int): ImageLoader {
        this.placeholderRes = placeholderRes
        return this
    }

    fun placeholder(placeholder: Drawable): ImageLoader {
        this.placeholder = placeholder
        return this
    }

    fun error(error: Drawable): ImageLoader {
        this.error = error
        return this
    }

    fun errorRes(@DrawableRes errorRes:Int):ImageLoader{
        this.errorRes = errorRes
        return this
    }

    fun override(width: Int, height: Int): ImageLoader {
        this.width = width
        this.height = height
        return this
    }

    fun into(view: ImageView) {
        this.view = view
        sImageStrategy?.load(this)
    }
}