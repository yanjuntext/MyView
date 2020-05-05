package com.wyj.widget.dialog

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.wyj.widget.R
import com.wyj.widget.dialog.base.SBuilder
import com.wyj.widget.dialog.base.SDialog
import com.base.utils.image.ImageLoader
import kotlinx.android.synthetic.main.dialog_image.*
import java.io.File

class ImageDialog private constructor(private var builder: Builder) : SDialog(builder) {

    override fun initView() {
        if (builder.isQrBitmap) {
            iv_image.visibility = View.GONE
            iv_qr_code.visibility = View.VISIBLE

            if (!builder.url.isNullOrEmpty()) {
                ImageLoader.with(context)
                    .load(builder.url ?: "")
                    .into(iv_qr_code)
            }
            if(builder.bitmap != null){
                iv_qr_code.setImageBitmap(builder.bitmap)
            }
        }else{
            if (!builder.url.isNullOrEmpty()) {
                ImageLoader.with(context)
                    .load(builder.url ?: "")
                    .into(iv_image)
            }

            if(builder.bitmap != null){
                iv_image.setImageBitmap(builder.bitmap)
            }
        }



    }

    open class Builder(activity: FragmentActivity) : SBuilder<Builder>(activity) {
        var url: String? = null
        var imgFile: File? = null
        var errorImg: Drawable? = null
        var isQrBitmap = false
        var bitmap: Bitmap? = null

        fun asQrBitmap(): Builder {
            isQrBitmap = true
            return this
        }

        fun setUrl(url: String?): Builder {
            this.url = url
            return this
        }

        fun setImageFile(imgFile: File?): Builder {
            this.imgFile = imgFile
            return this
        }

        fun setErroeDraw(errorImg: Drawable?): Builder {
            this.errorImg = errorImg
            return this
        }

        fun setBitmap(bitmap: Bitmap): Builder {
            this.bitmap = bitmap
            return this
        }

        override fun builder(): Builder {
            setContentView(R.layout.dialog_image)
            return this
        }

        override fun show(activity: FragmentActivity): SDialog? {
            val dialog = ImageDialog(this)
            dialog.show(activity)
            return dialog
        }
    }
}