package com.base.utils.image

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.base.utils.R
import com.base.utils.image.transformation.BlurTransformation
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

/**
 *
 *@author abc
 *@time 2019/9/20 10:08
 */
class GlideStrategy : ImageStrategy {

    @SuppressLint("CheckResult")
    override fun load(loader: ImageLoader) {
        val view = loader.view ?: return
        val manager = getRequestManager(loader.context)

        if (loader.isGif) manager.asGif()

        val builder: RequestBuilder<Drawable> = when {
            !loader.url.isNullOrEmpty() -> manager.load(loader.baseUrl + loader.url)
            loader.uri != null -> manager.load(loader.uri)
            loader.isFile -> manager.load(loader.file)
            loader.resourceId != 0 -> manager.load(loader.resourceId)
            loader.errorRes != null -> manager.load(loader.errorRes)
            else -> manager.load(loader.error)
        }

        val pRes = loader.placeholderRes
        val eRes = loader.errorRes

        val options = when {
            pRes != null && eRes != null -> RequestOptions.placeholderOf(pRes).error(eRes)
            pRes != null -> RequestOptions.placeholderOf(pRes).error(loader.error)
            eRes != null -> RequestOptions.placeholderOf(loader.placeholder).error(eRes)
            else -> RequestOptions.placeholderOf(loader.placeholder).error(loader.error)
        }
        if (loader.circle != 0) {
            when {
                loader.circle == Int.MAX_VALUE -> options.circleCrop()
                loader.blur > 0 -> options.transform(
                    MultiTransformation(
                        BlurTransformation(
                            loader.blur,
                            loader.blurScal
                        ), RoundedCorners(loader.circle)
                    )
                )
                loader.isCenterCrop -> options.transform(
                    MultiTransformation(
                        CenterCrop(),
                        RoundedCorners(loader.circle)
                    )
                )
                else -> options.transform(RoundedCorners(loader.circle))
            }
        } else {

            if (loader.isCenterCrop) {
                options.transform(CenterCrop())
            } else if (loader.blur > 0) {
                options.transform(
                    jp.wasabeef.glide.transformations.BlurTransformation(
                        loader.blur,
                        loader.blurScal
                    )
                )
            }
        }

        builder.apply(options)
        if (loader.width != 0 && loader.height != 0) {
            builder.override(loader.width, loader.height)
        }
        if (loader.skipMemoryCache) {
            builder.diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
        }
        builder.into(view)
    }

    private fun getRequestManager(any: Any?): RequestManager {
        return if (any == null) {
            throw IllegalArgumentException("You cannot start a load on a null Context")
        } else {
            when (any) {
                is Context -> {
                    when (any) {
                        is FragmentActivity -> Glide.with(any)
                        is Activity -> Glide.with(any)
                        else -> Glide.with(any)
                    }
                }
                is Fragment -> Glide.with(any)
                else -> throw IllegalArgumentException("This object is illegal")
            }
        }
    }
}