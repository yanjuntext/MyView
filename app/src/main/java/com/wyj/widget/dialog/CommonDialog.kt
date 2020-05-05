package com.wyj.widget.dialog

import android.content.DialogInterface
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.wyj.widget.R
import com.wyj.widget.dialog.base.SBuilder
import com.wyj.widget.dialog.base.SDialog
import com.base.utils.MLog
import com.base.utils.clickDelay
import kotlinx.android.synthetic.main.dialog_default.*
import kotlinx.android.synthetic.main.include_bottom.*
import kotlinx.android.synthetic.main.include_title.*


/**
 *对话框dialog
 *@author abc
 *@time 2019/11/12 17:08
 */
class CommonDialog private constructor(private var builder: Builder) : SDialog(builder) {


    private val TAG by lazy { CommonDialog::class.java.name }
    override fun initView() {

        tv_title.text = if (builder.title.isNullOrEmpty()) {
            tv_title.visibility = View.GONE
            ""
        } else {
            tv_title.visibility = View.VISIBLE
            builder.title
        }
        tv_title.setTextColor(builder.titleColor)

        MLog.e(TAG, "content[${builder.content}]")
        with(tv_content) {
            this.text = builder.content ?: ""
            this.setTextColor(builder.contentColor)
        }
        with(builder.cancle) {
            if (this.isNullOrEmpty()) {
                divider_ver.visibility = View.GONE
                tv_cancle.visibility = View.GONE
            } else {
                divider_ver.visibility = View.VISIBLE
                tv_cancle.visibility = View.VISIBLE
            }
            tv_cancle.text = this ?: ""
            tv_cancle.setTextColor(builder.cancleColor)
        }

        tv_confirm.text = builder.confirm ?: ""
        tv_confirm.setTextColor(builder.confirmColor)

        tv_cancle.clickDelay {
            this.dismissAllowingStateLoss()
            builder.onCancleListener?.onCancle(this)
        }

        tv_confirm.clickDelay {
            this.dismissAllowingStateLoss()
            builder.onConfirmListrener?.onCaonfirm(this, null)
        }

    }

    override fun onShow(dialog: DialogInterface?) {

    }


    open class Builder(activity: FragmentActivity) : SBuilder<Builder>(activity) {
        override fun builder(): Builder {
            setContentView(R.layout.dialog_default)
            return this
        }

        override fun show(activity: FragmentActivity) : SDialog?{
            val dialog = CommonDialog(this)
            dialog.show(activity)
            return dialog
        }
    }
}