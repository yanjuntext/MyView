package com.wyj.widget.dialog

import androidx.fragment.app.FragmentActivity
import com.wyj.widget.R
import com.wyj.widget.dialog.base.SBuilder
import com.wyj.widget.dialog.base.SDialog
import com.base.utils.clickDelay
import kotlinx.android.synthetic.main.dialog_sure_dev.*

/**
 *
 *@author abc
 *@time 2020/1/8 16:09
 */
class SureDevDialog private constructor(private var builder: Builder) : SDialog(builder) {

    override fun initView() {
        with(iv_pet) {
            if (builder.mDevIconResource != 0) this.setImageResource(builder.mDevIconResource)
        }

        with(tv_cancle) {
            this.text = builder.cancle ?: ""
            this.clickDelay {
                this@SureDevDialog.dismissAllowingStateLoss()
            }
        }

        with(btn_sure) {
            this.text = builder.confirm ?: ""
            this.clickDelay {
                builder.onConfirmListrener?.onCaonfirm(this@SureDevDialog, builder.devType)
                this@SureDevDialog.dismissAllowingStateLoss()
            }
        }
    }

    open class Builder(activity: FragmentActivity) : SBuilder<Builder>(activity) {

        var mDevIconResource: Int = 0

        var devType:String? = null

        override fun isFullScreent(): Boolean = true

        override fun builder(): Builder {
            setContentView(R.layout.dialog_sure_dev)
            return this
        }

        fun setDevType(devType:String):Builder{
            this.devType = devType
            return this
        }

        fun setDevIconResource(devIconResource: Int): Builder {
            this.mDevIconResource = devIconResource
            return this
        }

        override fun show(activity: FragmentActivity) : SDialog?{
            val dialog = SureDevDialog(this)
            dialog.show(activity)
            return dialog
        }

    }
}