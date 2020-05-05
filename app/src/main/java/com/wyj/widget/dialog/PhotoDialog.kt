package com.wyj.widget.dialog

import android.view.Gravity
import androidx.fragment.app.FragmentActivity
import com.wyj.widget.R
import com.wyj.widget.dialog.base.SBuilder
import com.wyj.widget.dialog.base.SDialog
import com.base.utils.clickDelay
import kotlinx.android.synthetic.main.dialog_photo.*

class PhotoDialog private constructor(private var builder: Builder) : SDialog(builder) {

    override fun initView() {
        tv_camera.clickDelay {
            dismissAllowingStateLoss()
            builder.onConfirmListrener?.onCaonfirm(this, 1)
        }

        tv_media.clickDelay {
            dismissAllowingStateLoss()
            builder.onConfirmListrener?.onCaonfirm(this, 2)
        }
        tv_cancle.text = builder.cancle?:""
        tv_cancle.clickDelay {
            dismissAllowingStateLoss()
            builder.onCancleListener?.onCancle(this)
        }

    }

    open class Builder(activity: FragmentActivity) : SBuilder<Builder>(activity) {

        override fun setGravity() = Gravity.BOTTOM

        override fun builder(): Builder {
            setContentView(R.layout.dialog_photo)

            return this
        }

        override fun show(activity: FragmentActivity): SDialog? =
            PhotoDialog(this).also {
                it.show(activity)
            }
    }
}